package de.uniks.stpmon.k;

import de.uniks.stpmon.k.di.DaggerTestComponent;
import de.uniks.stpmon.k.di.TestComponent;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.service.dummies.MessageApiDummy;
import de.uniks.stpmon.k.service.dummies.MovementDummy;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;

import static java.util.function.Predicate.not;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

class AppTest extends ApplicationTest {

    private final App app = new App(null);
    private final TestComponent component = (TestComponent) DaggerTestComponent.builder().mainApp(app).build();
    private final MessageApiDummy messageApi = component.messageApi();

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        stage.requestFocus();
    }

    @AfterEach
    void afterEach() {
        // Remove used input handler
        app.removeInputHandler(component);
    }

    @Test
    void criticalPathV1() {
        MovementDummy.addMovementDummy(component.eventListener());

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
        clickOn(messagefield);
        write("t");
        waitForFxEvents();

        type(KeyCode.ENTER);
        //chatlist first entry is message with t
        verifyThat("#bodyText", hasText("t"));
        //potential invite here
        //...

        //close friends sidebar
        clickOn("#friends");

        //check that there are two test regions
        GridPane regionListGridPane = lookup("#regionListGridPane").query();
        assertThat(regionListGridPane.getColumnCount()).isEqualTo(1);

        clickOn("#regionImage");
        waitForFxEvents();

        // create a new trainer
        clickOn("#createTrainerInput");
        write("Tom");
        clickOn("#createTrainerButton");
        // popup pops up
        clickOn("#approveButton");
        waitForFxEvents();

        verifyThat("#pause", Node::isVisible);
        clickOn("#pause");
        verifyThat("#pauseScreen", Node::isVisible);
        clickOn("#logoutButton");
        verifyThat(regionListGridPane, Node::isVisible);

        clickOn("#regionImage");

        // enter with already created trainer
        verifyThat("#ingame", Node::isVisible);
    }

    @Test
    void criticalPathV2() {
        MovementDummy.addMovementDummy(component.eventListener());

        app.show(component.hybridController());

        //set User
        ArrayList<String> friends = new ArrayList<>();
        friends.add("id1");
        User user = new User(
                "00",
                "T",
                "online",
                null,
                friends
        );
        component.userStorage().setUser(user);

        //open Settings
        clickOn("#settings");
        waitForFxEvents();
        //edit User
        clickOn("#editUserButton");

        clickOn("#usernameInput");
        write("TT");
        clickOn("#saveChangesButton");
        clickOn("#approveButton");

        clickOn("#settings");
        clickOn("#settings");

        Text username = lookup("#usernameValue").query();
        verifyThat(username, hasText("TT"));

        clickOn("#settings");


        //join region
        clickOn("#regionVBox");
        waitForFxEvents();

        clickOn("#monsterBar");
        verifyThat("#monsterList", Node::isVisible);

        clickOn("#monster_label_0");
        verifyThat("#monsterInformation", Node::isVisible);

        clickOn("#monsterBar");
        verifyThat("#monsterList", not(Node::isVisible));

        //check minimap
        clickOn("#miniMap");
        verifyThat("#mapOverviewContent", Node::isVisible);
        clickOn("#closeButton");

        //check backpack
        clickOn("#backpackImage");
        verifyThat("#backpackMenuHBox", Node::isVisible);
        clickOn("#backpackImage");

        clickOn("#settings");
        clickOn("#editTrainerButton");
        clickOn("#trainerNameInput");
        write("ttt");
        clickOn("#saveChangesButton");
        clickOn("#approveButton");
        clickOn("#settings");
        clickOn("#settings");
        verifyThat("#userTrainerValue", hasText("ttt"));

        clickOn("#editTrainerButton");
        clickOn("#deleteTrainerButton");
        clickOn("#approveButton");
        clickOn("#approveButton");
        verifyThat("#lobbyPane", Node::isVisible);
        clickOn("#settings");

        clickOn("#editUserButton");
        clickOn("#deleteUserButton");
        clickOn("#approveButton");

        verifyThat("#registerButton", Node::isVisible);
    }

    @Test
    void criticalPathV3() {
        MovementDummy.addMovementDummy(component.eventListener());

        app.show(component.hybridController());

        //set User
        User user = new User(
                "01",
                "T",
                "online",
                null,
                null
        );
        component.userStorage().setUser(user);

        //join region
        clickOn("#regionVBox");
        waitForFxEvents();

        // create a new trainer
        clickOn("#createTrainerInput");
        write("T");
        clickOn("#createTrainerButton");
        // popup pops up
        clickOn("#approveButton");
        waitForFxEvents();

        clickOn("#monsterBar");
        clickOn("#monsterBar");
    }
}
