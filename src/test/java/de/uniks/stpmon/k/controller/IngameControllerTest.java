package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.interaction.DialogueController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class IngameControllerTest extends ApplicationTest {

    @Mock
    Provider<HybridController> hybridControllerProvider;
    @Spy
    @SuppressWarnings("unused")
    MonsterBarController monsterBarController;
    @Mock
    MinimapController minimapController;
    @Mock
    MapOverviewController mapOverviewController;
    @Mock
    RegionStorage regionStorage;
    @Spy
    BackpackController backpackController;
    @Spy
    Provider<BackpackMenuController> backpackMenuControllerProvider;
    @Mock
    Provider<IngameController> ingameControllerProvider;
    @Mock
    @SuppressWarnings("unused")
    DialogueController dialogueController;
    @Mock
    @SuppressWarnings("unused")
    WorldController worldController;
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;
    @Spy
    App app = new App(null);
    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);

    @InjectMocks
    IngameController ingameController;

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        regionStorage = minimapController.regionStorage;
        mapOverviewController.closeButton = new Button("");
        app.show(ingameController);
        stage.requestFocus();
    }

    @Test
    void showBackPackMenu() {
        final HybridController hybridController = Mockito.mock(HybridController.class);
        when(hybridControllerProvider.get()).thenReturn(hybridController);

        backpackController.backpackMenuControllerProvider = backpackMenuControllerProvider;
        backpackController.ingameControllerProvider = ingameControllerProvider;

        BackpackMenuController backpackMenuController = Mockito.mock(BackpackMenuController.class);
        when(backpackController.backpackMenuControllerProvider.get()).thenReturn(backpackMenuController);

        when(backpackMenuController.render()).thenReturn(new HBox());

        when(backpackController.ingameControllerProvider.get()).thenReturn(ingameController);


        //check if backpackController appears
        assertEquals(1, ingameController.ingameWrappingHBox.getChildren().size());
        //action
        clickOn("#backpackImage");
        assertEquals(2, ingameController.ingameWrappingHBox.getChildren().size());


        clickOn("#backpackImage");

        assertEquals(1, ingameController.ingameWrappingHBox.getChildren().size());

    }

    @Test
    void testShow() {
        BorderPane ingame = lookup("#ingame").query();
        assertNotNull(ingame);
    }

}
