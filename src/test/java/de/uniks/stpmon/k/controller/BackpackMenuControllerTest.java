package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.monsters.MonsterBarController;
import de.uniks.stpmon.k.controller.monsters.MonsterInventoryController;
import de.uniks.stpmon.k.controller.backpack.BackpackMenuController;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class BackpackMenuControllerTest extends ApplicationTest {

    @Spy
    final App app = new App(null);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;
    @Spy
    final ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    @Spy
    Provider<MonsterBarController> monsterBarControllerProvider;
    @Spy
    Provider<MonsterInventoryController> monBoxControllerProvider;
    @Mock
    Provider<IngameController> ingameControllerProvider;

    @InjectMocks
    BackpackMenuController backpackMenuController;

    @Override
    public void start(Stage stage) {
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        app.show(backpackMenuController);
        stage.requestFocus();
    }

    @Test
    void hoverOver() {
        Label label = lookup("#backpackMenuSelectedLabel_0").query();
        moveTo(label);
        assertEquals(">", label.getText());
        Label label2 = lookup("#backpackMenuSelectedLabel_1").query();
        moveTo(label2);
        assertEquals("", label.getText());
    }

    @Test
    void clickOnMonsters() {
        MonsterBarController monsterBarController = Mockito.mock(MonsterBarController.class);
        when(monsterBarControllerProvider.get()).thenReturn(monsterBarController);
        doNothing().when(monsterBarController).showMonsters();

        Label label = lookup("#backpackMenuLabel_1").query();
        clickOn(label);
        verify(monsterBarController).showMonsters();

    }

    @Test
    void clickOnMonster_List() {
        MonsterInventoryController monsterInventoryController = Mockito.mock(MonsterInventoryController.class);
        when(monBoxControllerProvider.get()).thenReturn(monsterInventoryController);
        IngameController ingameController = Mockito.mock(IngameController.class);
        when(ingameControllerProvider.get()).thenReturn(ingameController);

        Label label = lookup("#backpackMenuLabel_0").query();
        clickOn(label);
        waitForFxEvents();
        verify(ingameController).pushController(any());

        Label label2 = lookup("#backpackMenuLabel_0").query();
        clickOn(label2);
        waitForFxEvents();

        verify(ingameController).removeChildren(anyInt());
    }
}