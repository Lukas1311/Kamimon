package de.uniks.stpmon.k;

import de.uniks.stpmon.k.di.DaggerTestComponent;
import de.uniks.stpmon.k.di.TestComponent;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.NPCInfo;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.service.dummies.EventDummy;
import de.uniks.stpmon.k.service.dummies.MessageApiDummy;
import de.uniks.stpmon.k.service.dummies.MonsterDummy;
import de.uniks.stpmon.k.service.dummies.MovementDummy;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.MonsterCache;
import de.uniks.stpmon.k.service.storage.cache.TrainerCache;
import de.uniks.stpmon.k.utils.Direction;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.List;

import static java.util.function.Predicate.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

class AppTest extends ApplicationTest {

    private final App app = new App(null);
    private final TestComponent component = (TestComponent) DaggerTestComponent.builder().mainApp(app).build();
    private final MessageApiDummy messageApi = component.messageApi();

    @Override
    public void start(Stage stage) {
        app.setMainComponent(component);
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

        sleep(1000);
        //open friend list
        type(KeyCode.F);
        waitForFxEvents();

        ListView<User> list = lookup("#userListView").query();
        ObservableList<User> users = list.getItems();

        //user has no friends
        assertEquals(0, users.size());

        clickOn("#checkBox");
        clickOn("#searchButton");
        waitForFxEvents();

        //show all users
        assertEquals(3, users.size());

        //add a new friend
        clickOn("#searchFriend");
        write("TestUser1");
        verifyThat("#searchFriend", hasText("TestUser1"));
        type(KeyCode.ENTER);

        //one search result is shown
        assertEquals(1, users.size());

        //TestUser1 is shown
        assertEquals("TestUser1", users.get(0).name());
        assertNotNull(lookup("#TestUser1").query());

        //add friend
        clickOn("#removeFriendButton");
        waitForFxEvents();

        clickOn("#checkBox");
        clickOn("#searchFriend");
        type(KeyCode.BACK_SPACE, "TestUser1".length());
        clickOn("#searchButton");

        //check if user is added to friend list
        assertEquals(1, users.size());

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

        //close friends sidebar
        type(KeyCode.F);
        waitForFxEvents();

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
        EventDummy eventDummy = component.eventDummy();
        eventDummy.ensureMock();
        app.addInputHandler(component);
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

        MonsterDummy.addMonsterDummy(component.trainerStorage(), eventDummy);

        CacheManager cacheManager = component.cacheManager();
        TrainerCache trainerCache = cacheManager.trainerCache();

        Trainer me = component.trainerStorage().getTrainer();
        Trainer prof = TrainerBuilder.builder()
                .setId("prof")
                .setX(4)
                .setRegion("id0")
                .setArea("id0_0")
                .setDirection(Direction.LEFT)
                .setNpc(new NPCInfo(false, false, false, List.of("0", "1", "2"), null))
                .create();

        trainerCache.addValue(prof);

        //shortcut tests
        type(KeyCode.C);
        waitForFxEvents();
        verifyThat("#chatList", Node::isVisible);
        type(KeyCode.C);

        type(KeyCode.B);
        waitForFxEvents();
        verifyThat("#backpackMenuHBox", Node::isVisible);
        type(KeyCode.B);

        type(KeyCode.M);
        waitForFxEvents();
        verifyThat("#mapOverviewContent", Node::isVisible);
        type(KeyCode.M);

        type(KeyCode.N);
        waitForFxEvents();
        verifyThat("#monsterListVBox", Node::isVisible);
        type(KeyCode.N);

        type(KeyCode.P);
        waitForFxEvents();
        verifyThat("#shortcutPane", Node::isVisible);
        type(KeyCode.P);

        type(KeyCode.O);
        waitForFxEvents();
        verifyThat("#settingsScreen", Node::isVisible);
        type(KeyCode.O);


        clickOn("#monsterBar");
        waitForFxEvents();
        // verify that the monster list is empty
        assertThat(me.team()).isEmpty();

        // walk to the right
        type(KeyCode.D, 3);
        // talk to prof
        type(KeyCode.ENTER);
        type(KeyCode.ENTER);
        type(KeyCode.ENTER);
        type(KeyCode.ENTER);
        type(KeyCode.ENTER);
        waitForFxEvents();

        clickOn("#monster_label_0");
        verifyThat("#monsterInformation", Node::isVisible);

        clickOn("#monsterBar");


        //check backpack
        clickOn("#backpackImage");

        verifyThat("#backpackMenuHBox", Node::isVisible);

        //add Monsters to inventory
        Monster teamMonster = MonsterBuilder.builder().setId("monster1")
                .setTrainer(me._id()).create();
        Monster storageMonster = MonsterBuilder.builder().setId("monster2")
                .setTrainer(me._id()).create();
        MonsterCache monsterCache = cacheManager.requestMonsters(me._id());
        monsterCache.addValue(storageMonster);
        monsterCache.addValue(teamMonster);
        trainerCache.updateValue(TrainerBuilder.builder(me).addTeam(teamMonster._id()).create());

        //open MonBox
        clickOn("#backpackMenuLabel_0");
        waitForFxEvents();
        verifyThat("#monBoxStackPane", Node::isVisible);

        GridPane teamGrid = lookup("#monTeam").query();
        Node teamMon = teamGrid.getChildren().get(0);
        clickOn(teamMon);
        verifyThat("#mainPane", Node::isVisible);

        type(KeyCode.B);
        HBox ingameWrappingHbox = lookup("#ingameWrappingHBox").query();
        assertEquals(1, ingameWrappingHbox.getChildren().size());

    }

}
