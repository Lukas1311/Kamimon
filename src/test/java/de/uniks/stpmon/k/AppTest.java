package de.uniks.stpmon.k;

import de.uniks.stpmon.k.di.DaggerTestComponent;
import de.uniks.stpmon.k.di.TestComponent;
import de.uniks.stpmon.k.dto.AbilityMove;
import de.uniks.stpmon.k.dto.UpdateOpponentDto;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.service.dummies.EncounterApiDummy;
import de.uniks.stpmon.k.service.dummies.EventDummy;
import de.uniks.stpmon.k.service.dummies.MessageApiDummy;
import de.uniks.stpmon.k.service.dummies.TestHelper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;

import static java.util.function.Predicate.not;
import static org.junit.jupiter.api.Assertions.*;
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
        TestHelper.addMovementDummy(component.eventListener());

        Platform.runLater(() -> app.show(component.loginController()));
        waitForFxEvents();

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
        FlowPane regionListFlowPane = lookup("#regionsFlowPane").query();
        assertThat(regionListFlowPane.getChildren().size()).isEqualTo(2);

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
        verifyThat(regionListFlowPane, Node::isVisible);

        clickOn("#regionImage");

        // enter with already created trainer
        verifyThat("#ingame", Node::isVisible);
    }

    @Test
    void criticalPathV2() {
        EventDummy eventDummy = component.eventDummy();
        eventDummy.ensureMock();

        Platform.runLater(() -> app.show(component.hybridController()));
        waitForFxEvents();

        //set User
        User user = new User(
                "01",
                "T",
                "online",
                null,
                new ArrayList<>()
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


        // create a new trainer
        clickOn("#createTrainerInput");
        write("Tom");
        clickOn("#createTrainerButton");
        // popup pops up
        clickOn("#approveButton");
        waitForFxEvents();

        component.regionApi().addMonster("0", "0", true);

        clickOn("#monsterBar");
        verifyThat("#monsterList", Node::isVisible);

        clickOn("#monster_label_0");
        verifyThat("#monsterInformation", Node::isVisible);

        clickOn("#monsterBar");
        verifyThat("#monsterList", not(Node::isVisible));

        //check minimap
        clickOn("#miniMap");
        verifyThat("#mapOverviewHolder", Node::isVisible);
        // close minimap
        clickOn("#miniMap");

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
        TestHelper.addMovementDummy(component.eventListener());
        EventDummy eventDummy = component.eventDummy();
        eventDummy.ensureMock();
        app.addInputHandler(component);
        Platform.runLater(() -> app.show(component.hybridController()));
        waitForFxEvents();

        //set User
        User user = new User(
                "01",
                "T",
                "online",
                null,
                null
        );
        component.userStorage().setUser(user);
        waitForFxEvents();

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

        TestHelper.listenStarterMonster(component.trainerStorage(), component);
        TestHelper.addTestNpcsV3(component);

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
        verifyThat("#mapOverviewHolder", Node::isVisible);
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

        Trainer me = component.trainerStorage().getTrainer();
        // verify that the monster list is empty
        assertThat(me.team()).isEmpty();

        // walk to the right
        type(KeyCode.D, 3);
        // talk to prof
        type(KeyCode.E, 5);
        waitForFxEvents();

        clickOn("#monster_label_0");
        verifyThat("#monsterInformation", Node::isVisible);

        clickOn("#monsterBar");

        //check backpack
        clickOn("#backpackImage");

        verifyThat("#backpackMenuHBox", Node::isVisible);

        //open MonBox
        component.regionApi().addMonster("0", "1", false);
        clickOn("#backpackMenuLabel_0");
        waitForFxEvents();
        verifyThat("#monsterInventory", Node::isVisible);

        GridPane teamGrid = lookup("#monTeam").query();
        Node teamMon = teamGrid.getChildren().get(0);


        clickOn(teamMon);
        verifyThat("#mainPane", Node::isVisible);

        type(KeyCode.B);
        HBox ingameWrappingHbox = lookup("#ingameWrappingHBox").query();
        assertEquals(1, ingameWrappingHbox.getChildren().size());

        type(KeyCode.S, 2);
        // start encounter
        type(KeyCode.E);
        type(KeyCode.RIGHT);
        type(KeyCode.E);
        waitForFxEvents();

        verifyThat("#userMonsters", Node::isVisible);

        // open fight menu
        clickOn("#main_menu_fight");
        // attack
        clickOn("#ability_1");
        waitForFxEvents();
        clickOn("#battleLog");

        // Check if won and left encounter
        verifyThat("#monsterBar", Node::isVisible);
        type(KeyCode.D, 1);
        type(KeyCode.S, 1);

        // start 2v2 encounter
        type(KeyCode.E);
        type(KeyCode.RIGHT);
        type(KeyCode.E);
        // Check if encounter is started
        verifyThat("#userMonsters", Node::isVisible);

        EncounterApiDummy encounterApi = component.encounterApi();
        encounterApi.addMove("attacker", new UpdateOpponentDto(null,
                new AbilityMove("ability", 2, "0")));

        clickOn("#main_menu_changeMon");
        clickOn("#user_monster_1");
        waitForFxEvents();

        verifyThat("#battleLog", Node::isVisible);
        clickOn("#battleLog");
        clickOn("#battleLog");

        verifyThat("#changeMonBox", Node::isVisible);
        // no back button, monster is option 0
        clickOn("#user_monster_0");
        waitForFxEvents();

        encounterApi.addMove("attacker", new UpdateOpponentDto(null,
                new AbilityMove("ability", 2, "0")));
        encounterApi.addMove("attacker1", new UpdateOpponentDto(null,
                new AbilityMove("ability", 2, "0")));
        clickOn("#battleLog");

        // open fight menu
        clickOn("#main_menu_fight");
        // attack with ability 1
        clickOn("#ability_1");
        // attack opponent 0
        clickOn("#user_monster_1");
        waitForFxEvents();
        clickOn("#battleLog");
        clickOn("#battleLog");
        clickOn("#battleLog");

        // Check if lost and left encounter
        verifyThat("#monsterBar", Node::isVisible);
        // Check if hp is 0
        verifyThat("#monsterBar #slot_0_zero", Node::isVisible);
        verifyThat("#monsterBar #slot_1_zero", Node::isVisible);
        // Walk to nurse
        type(KeyCode.D, 2);
        type(KeyCode.S, 5);
        type(KeyCode.A, 5);
        type(KeyCode.W, 2);

        // Talk to nurse
        type(KeyCode.E, 4);
        waitForFxEvents();

        // Check if hp is full again
        verifyThat("#monsterBar #slot_0_normal", Node::isVisible);
        verifyThat("#monsterBar #slot_1_normal", Node::isVisible);

        type(KeyCode.D, 5);
        type(KeyCode.W, 2);
        type(KeyCode.A);

        // Start encounter wild encounter
        type(KeyCode.E);
        type(KeyCode.RIGHT);
        type(KeyCode.E);
        waitForFxEvents();

        verifyThat("#userMonsters", Node::isVisible);
        clickOn("#main_menu_flee");
        waitForFxEvents();
        clickOn("#battleLog");

        // Check if left encounter
        verifyThat("#backpackImage", Node::isVisible);
    }

    @Test
    void criticalPathV4() {
        TestHelper.addMovementDummy(component.eventListener());
        EventDummy eventDummy = component.eventDummy();
        eventDummy.ensureMock();
        app.addInputHandler(component);
        Platform.runLater(() -> app.show(component.hybridController()));
        waitForFxEvents();

        //set User
        User user = new User(
                "01",
                "T",
                "online",
                null,
                null
        );

        component.userStorage().setUser(user);

        waitForFxEvents();
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

        TestHelper.listenStarterMonster(component.trainerStorage(), component);
        TestHelper.addEncounteredMonsters(component.trainerStorage(), component);
        TestHelper.addTestNpcsV4(component);
        TestHelper.addMonster(component.trainerStorage(), component);

        //test monDex
        clickOn("#backpackImage");
        waitForFxEvents();
        verifyThat("#backpackMenuListView", Node::isVisible);
        waitForFxEvents();
        clickOn("#backpackMenuLabel_2");
        waitForFxEvents();

        AnchorPane monDexPain = lookup("#monDexPain").query();
        assertNotNull(monDexPain);
        waitForFxEvents();
        assertTrue(monDexPain.isVisible());
        waitForFxEvents();
        verifyThat(monDexPain, Node::isVisible);
        waitForFxEvents();

        waitForFxEvents();

        clickOn("#type0Label");
        verifyThat("#monDexDetailBox", Node::isVisible);
        clickOn("#type2Label");
        waitForFxEvents();
        clickOn("#backpackImage");
        waitForFxEvents();

        //test inventory and item usage
        clickOn("#backpackImage");
        clickOn("#backpackMenuLabel_1");
        waitForFxEvents();
        verifyThat("#inventoryPane", Node::isVisible);
        clickOn("#inventoryPane #item_Item_1");
        waitForFxEvents();
        verifyThat("#itemInformationNode", Node::isVisible);
        clickOn("#useButton");
        waitForFxEvents();
        sleep(1000);
        verifyThat("#monsterInventory", Node::isVisible);
        clickOn("#team_0");
        clickOn("#backpackImage");

        //test itemBox
        clickOn("#backpackImage");
        clickOn("#backpackMenuLabel_1");
        waitForFxEvents();
        verifyThat("#inventoryPane", Node::isVisible);
        clickOn("#inventoryPane #item_Item_2");
        waitForFxEvents();
        verifyThat("#itemInformationNode", Node::isVisible);
        clickOn("#useButton");
        waitForFxEvents();
        verifyThat("#monsterNameLabel", Node::isVisible);
        clickOn("#backpackImage");


        //test monBox
        clickOn("#backpackImage");
        clickOn("#backpackMenuLabel_1");
        waitForFxEvents();
        verifyThat("#inventoryPane", Node::isVisible);
        clickOn("#inventoryPane #item_Item_3");
        waitForFxEvents();
        verifyThat("#itemInformationNode", Node::isVisible);
        clickOn("#useButton");
        waitForFxEvents();
        verifyThat("#descriptionText", Node::isVisible);
        clickOn("#backpackImage");

        //test sound
        clickOn("#settings");
        clickOn("#mdmzSettings");
        final Slider musicSlider = lookup("#musicSlider").query();
        assertThat(musicSlider.getValue()).isEqualTo(0);
        clickOn("#settings");

        //go to clerk
        type(KeyCode.S, 2);
        type(KeyCode.D, 3);
        type(KeyCode.W);

        //talk to clerk
        type(KeyCode.E, 2);

        verifyThat("#shopOverview", Node::isVisible);
        verifyThat("#shopOption", Node::isVisible);
        verifyThat("#itemNameLabel", hasText("Item_0"));
        verifyThat("#buyButton", Node::isDisabled);
        verifyThat("#sellButton", Node::isDisabled);

        component.regionApi().updateTrainerCoins(component.trainerStorage().getTrainer()._id(), 100, false);
        waitForFxEvents();
        verifyThat("#coinsLabel", hasText("100 Coins"));
        component.regionApi().updateTrainerCoins(component.trainerStorage().getTrainer()._id(), 95, false);
        waitForFxEvents();
        verifyThat("#coinsLabel", hasText("95 Coins"));
        waitForFxEvents();
        verifyThat("#buyButton", not(Node::isDisabled));
        clickOn("#item_Item_1");
        waitForFxEvents();
        verifyThat("#itemNameLabel", hasText("Item_1"));
        verifyThat("#sellButton", not(Node::isDisabled));

        //close shop
        type(KeyCode.E);
        waitForFxEvents();

        //test evolution of mon
        // walk to the right and start Encounter
        type(KeyCode.A);
        type(KeyCode.UP);
        type(KeyCode.E);
        type(KeyCode.RIGHT);
        type(KeyCode.E);
        waitForFxEvents();

        // open fight menu
        clickOn("#main_menu_fight");
        //set evolution
        EncounterApiDummy encounterApi = component.encounterApi();
        encounterApi.setEvolves(true);
        // attack
        clickOn("#ability_1");
        waitForFxEvents();
        clickOn("#battleLog");
        clickOn("#battleLog");
        clickOn("#battleLog");
        clickOn("#battleLog");

        //test effect item in encounter
        type(KeyCode.E);
        type(KeyCode.RIGHT);
        type(KeyCode.E);
        waitForFxEvents();
        clickOn("#main_menu_inventory");
        waitForFxEvents();
        clickOn("#item_Item_1");
        waitForFxEvents();
        clickOn("#useButton");
        waitForFxEvents();
        FlowPane flowPane = lookup("#monsterSelectionFlow").queryAs(FlowPane.class);
        clickOn(flowPane.getChildren().get(0));
        waitForFxEvents();
        clickOn("#battleLog");

        //start wild encounter where monBall is used
        component.presetApi().getMonBall();
        waitForFxEvents();
        type(KeyCode.S);
        type(KeyCode.E);
        type(KeyCode.RIGHT);
        type(KeyCode.E);
        waitForFxEvents();
        clickOn("#main_menu_inventory");
        waitForFxEvents();
        //in the test case, item4 is the MonBall
        clickOn("#item_Item_4");
        waitForFxEvents();
        clickOn("#useButton");
        waitForFxEvents();
        clickOn("#battleLog");

    }

}