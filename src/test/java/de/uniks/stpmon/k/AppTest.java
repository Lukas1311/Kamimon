package de.uniks.stpmon.k;

import de.uniks.stpmon.k.controller.LoadingScreenController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.di.DaggerTestComponent;
import de.uniks.stpmon.k.di.TestComponent;
import de.uniks.stpmon.k.service.dummies.MessageApiDummy;
import de.uniks.stpmon.k.service.storage.WorldStorage;
import de.uniks.stpmon.k.service.world.World;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

class AppTest extends ApplicationTest {

    private final App app = new App(null);
    private final TestComponent component = (TestComponent) DaggerTestComponent.builder().mainApp(app).build();
    private final MessageApiDummy messageApi = component.messageApi();
    private final HybridController hybridController = component.hybridController();
    private final LoadingScreenController loadingScreen = component.loadingScreenController();
    private final WorldStorage worldStorage = component.worldStorage();

    @Override
    public void start(Stage stage) throws Exception {
        hybridController.setPlayAnimations(false);
        //TODO: remove if we find a way to mock the tilemap for testing
        loadingScreen.setSkipLoading(true);
        BufferedImage images = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        //--------------------
        worldStorage.setWorld(new World(images, images, new ArrayList<>()));
        app.start(stage);
        stage.requestFocus();
    }

    @Test
    void criticalPathV1() {
        app.show(component.loginController());

        //put in username and password
        clickOn("#usernameInput");
        write("T\t");
        verifyThat("#usernameInput", hasText("T"));
        write("Password");
        verifyThat("#passwordInput", hasText("Password"));
        // tab 4 times to go on the login button -> is faster than click on button (no mouse movement)
        write("\t\t\t\t");
        //check if registerButton is focused
        Button selectedButton = lookup("#registerButton").queryButton();
        assertThat(selectedButton).isFocused();

        //register
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        waitForFxEvents();

        //introduction
        clickOn("#further");
        clickOn("#further");
        clickOn("#further");
        clickOn("#further");
        //open friend list
        clickOn("#friends");


        ScrollPane scrollPane = lookup("#scrollPane").query();
        VBox userList = (VBox) scrollPane.getContent();
        VBox friendView = (VBox) userList.lookup("#friendSection");
        VBox userView = (VBox) userList.lookup("#userSection");
        ObservableList<Node> friendChildren = friendView.getChildren();
        ObservableList<Node> userChildren = userView.getChildren();
        //check if friend list is empty
        assertThat(friendChildren).isEmpty();
        assertThat(userChildren).isEmpty();

        //add a new friend
        clickOn("#searchFriend");
        write("TestUser1");
        verifyThat("#searchFriend", hasText("TestUser1"));
        type(KeyCode.ENTER);

        assertThat(friendChildren).isEmpty();
        assertThat(userChildren).isNotEmpty();

        //add friend
        clickOn("#removeFriendButton");
        waitForFxEvents();
        //check if user is added to friend list
        assertThat(friendChildren).hasSize(1);
        assertThat(userChildren).isEmpty();

        messageApi.mockEvents("0");

        //write user a message
        clickOn("#messageButton");
        waitForFxEvents();
        Text groupname = lookup("#groupName").query();
        assertThat(!groupname.getText().isEmpty()).isTrue();

        TextField messagefield = lookup("#messageField").query();
        clickOn("#messageField");
        write("t");
        waitForFxEvents();

        type(KeyCode.ENTER);
        //chatlist first entry is message with t
        verifyThat("#bodyText", hasText("t"));
        //potential invite here
        //...


        //close friends sidebar
        clickOn("#friends");
        BorderPane regionList = lookup("#regionsBorderPane").query();
        ListView<?> regionsListView = (ListView<?>) regionList.getChildren().get(0);
        assertThat(regionsListView.getItems().size()).isEqualTo(2);

        clickOn("#regionButton");
        waitForFxEvents();
        verifyThat("#pause", Node::isVisible);
        clickOn("#pause");
        verifyThat("#pauseScreen", Node::isVisible);
        clickOn("#logoutButton");
        verifyThat(regionsListView, Node::isVisible);

        clickOn("#regionButton");
        verifyThat("#ingame", Node::isVisible);
    }
}
