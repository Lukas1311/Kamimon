package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.backpack.BackpackMenuController;
import de.uniks.stpmon.k.controller.inventory.InventoryController;
import de.uniks.stpmon.k.controller.mondex.MonDexController;
import de.uniks.stpmon.k.controller.monsters.MonsterInventoryController;
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
    @Mock
    Provider<MonsterInventoryController> monBoxControllerProvider;
    @Mock
    Provider<InventoryController> inventoryControllerProvider;
    @Mock
    Provider<MonDexController> monDexControllerProvider;
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
    void clickOnMonster_List() {
        MonsterInventoryController monsterInventoryController = Mockito.mock(MonsterInventoryController.class);
        when(monBoxControllerProvider.get()).thenReturn(monsterInventoryController);
        MonDexController monsterDexController = Mockito.mock(MonDexController.class);
        when(monDexControllerProvider.get()).thenReturn(monsterDexController);
        InventoryController inventoryController = Mockito.mock(InventoryController.class);
        when(inventoryControllerProvider.get()).thenReturn(inventoryController);

        IngameController ingameController = Mockito.mock(IngameController.class);
        when(ingameControllerProvider.get()).thenReturn(ingameController);

        //monsterList
        Label label = lookup("#backpackMenuLabel_0").query();
        clickOn(label);
        waitForFxEvents();
        verify(ingameController).pushController(any());

        Label label2 = lookup("#backpackMenuLabel_0").query();
        clickOn(label2);
        waitForFxEvents();

        verify(ingameController).removeChildren(anyInt());

        clickOn("#backpackMenuLabel_1");
        waitForFxEvents();
        clickOn("#backpackMenuLabel_3");
        clickOn("#backpackMenuLabel_3");
        when(ingameController.isMapOpen()).thenReturn(true);
        clickOn("#backpackMenuLabel_2");
    }
}