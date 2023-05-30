package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.views.world.WorldView;
import javafx.scene.layout.BorderPane;
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
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class IngameControllerTest extends ApplicationTest {

    @Spy
    App app = new App(null);

    @Mock
    @SuppressWarnings("unused")
    RegionService regionService;

    @Mock
    @SuppressWarnings("unused")
    WorldView worldView;

    @InjectMocks
    IngameController ingameController;
    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;
    @Spy
    @SuppressWarnings("unused")
    MonsterBarController monsterBarController = new MonsterBarController();

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        app.show(ingameController);
        stage.requestFocus();
    }

    @Test
    void testShow() {
        BorderPane ingame = lookup("#ingame").query();
        assertNotNull(ingame);
    }
}
