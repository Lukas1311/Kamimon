package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.backpack.BackpackController;
import de.uniks.stpmon.k.controller.backpack.BackpackMenuController;
import de.uniks.stpmon.k.controller.interaction.DialogueController;
import de.uniks.stpmon.k.controller.map.MapOverviewController;
import de.uniks.stpmon.k.controller.map.MinimapController;
import de.uniks.stpmon.k.controller.monsters.MonsterBarController;
import de.uniks.stpmon.k.controller.overworld.WorldTimerController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.service.InputHandler;
import de.uniks.stpmon.k.service.SoundService;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
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
    @SuppressWarnings("unused")
    MinimapController minimapController;
    @Mock
    @SuppressWarnings("unused")
    MapOverviewController mapOverviewController;
    @Spy
    @InjectMocks
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
    @SuppressWarnings("unused")
    StarterController starterController;
    @Mock
    @SuppressWarnings("unused")
    WorldTimerController worldTimerController;
    @Mock
    @SuppressWarnings("unused")
    SoundService soundService;
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;
    @Spy
    InputHandler inputHandler;
    @Spy
    final App app = new App(null);
    @Spy
    final ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);

    @InjectMocks
    IngameController ingameController;

    @Override
    public void start(Stage stage) {
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        app.show(ingameController);
        app.addInputHandler(inputHandler);
        stage.requestFocus();
    }

    @AfterEach
    void afterEach() {
        // Remove event handlers
        app.removeInputHandler(inputHandler);
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
