package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.encounter.EncounterOverviewController;
import de.uniks.stpmon.k.controller.inventory.InventoryController;
import de.uniks.stpmon.k.models.Encounter;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ActionFieldMainMenuControllerTest extends ApplicationTest {

    @Spy
    final App app = new App(null);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;
    @Spy
    final ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);

    @Mock
    Provider<InventoryController> inventoryControllerProvider;
    @Mock
    Provider<EncounterOverviewController> encounterOverviewControllerProvider;
    @Mock
    EncounterStorage encounterStorage;
    @Mock
    Provider<ActionFieldController> actionFieldControllerProvider;
    @InjectMocks
    ActionFieldMainMenuController actionFieldMainMenuController;

    @Spy
    @SuppressWarnings("unused")
    EffectContext effectContext = new EffectContext().setSkipLoadImages(true);

    private final VBox actionFieldWrapperBox = new VBox();

    @Override
    public void start(Stage stage) {
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);

        Encounter encounter = new Encounter(
                "id",
                "region",
                true
        );
        when(encounterStorage.getEncounter()).thenReturn(encounter);

        app.show(actionFieldMainMenuController);
        stage.requestFocus();
    }

    @Test
    void testGUI() {
        HBox mainMenuBox = lookup("#mainMenuBox").queryAs(HBox.class);
        VBox leftContainer = lookup("#leftContainer").queryAs(VBox.class);
        VBox rightContainer = lookup("#rightContainer").queryAs(VBox.class);
        assertNotNull(mainMenuBox);
        assertNotNull(leftContainer);
        assertNotNull(rightContainer);
    }

    @Test
    void openAction() {
        EncounterOverviewController encounterOverviewController = mock(EncounterOverviewController.class);
        when(encounterOverviewControllerProvider.get()).thenReturn(encounterOverviewController);
        ActionFieldController actionFieldController = mock(ActionFieldController.class);
        when(actionFieldControllerProvider.get()).thenReturn(actionFieldController);
        when(actionFieldController.isMonInfoOpen()).thenReturn(false);
        encounterOverviewController.actionFieldWrapperBox = actionFieldWrapperBox;

        InventoryController inventoryController = new InventoryController();
        when(inventoryControllerProvider.get()).thenReturn(inventoryController);
        clickOn("#main_menu_inventory");
    }

    @Test
    void openMonInfo() {
        ActionFieldController actionFieldController = mock(ActionFieldController.class);
        when(actionFieldControllerProvider.get()).thenReturn(actionFieldController);
        EncounterOverviewController encounterOverviewController = mock(EncounterOverviewController.class);
        when(encounterOverviewControllerProvider.get()).thenReturn(encounterOverviewController);

        clickOn("#main_menu_showInfo");
    }
}