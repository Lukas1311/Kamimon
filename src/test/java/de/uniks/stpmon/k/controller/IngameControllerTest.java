package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class IngameControllerTest extends ApplicationTest {

    @Spy
    Provider<HybridController> hybridControllerProvider;
    @Spy
    @SuppressWarnings("unused")
    MonsterBarController monsterBarController;
    @Spy
    @SuppressWarnings("unused")
    MinimapController minimapController;
    @Spy
    @SuppressWarnings("unused")
    BackpackController backpackController;
    @Mock
    Provider<BackpackMenuController> backpackMenuControllerProvider;
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
        app.show(ingameController);
        stage.requestFocus();

        final HybridController hybridController = Mockito.mock(HybridController.class);
        when(hybridControllerProvider.get()).thenReturn(hybridController);
    }

    @Test
    void testShow() {
        BorderPane ingame = lookup("#ingame").query();
        assertNotNull(ingame);
    }

    @Test
    void showBackPackMenu() {
        //mock
        BackpackMenuController backpackMenuController = Mockito.mock(BackpackMenuController.class);
        when(backpackMenuControllerProvider.get()).thenReturn(backpackMenuController);

        when(backpackMenuController.render()).thenReturn(new HBox());

        //check if backpackController appears
        assertEquals(1, ingameController.ingameWrappingHBox.getChildren().size());
        //action
        clickOn("#backpackImage");
        assertEquals(2, ingameController.ingameWrappingHBox.getChildren().size());

        //check for visibility
        HBox backMenuHbox = (HBox) ingameController.ingameWrappingHBox.getChildren().get(0);
        doAnswer(invocation -> {
            backMenuHbox.setVisible(false);
            return null;
        }).when(backpackMenuController).setVisability(anyBoolean());

        clickOn("#backpackImage");

        assertFalse(backMenuHbox.isVisible());

    }
}
