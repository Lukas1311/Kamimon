package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.service.MonsterService;
import de.uniks.stpmon.k.service.PresetService;
import javafx.application.Platform;
import javafx.scene.text.Text;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ActionFieldChangeMonsterControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);
    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;

    @Mock
    MonsterService monsterService;
    @Mock
    PresetService presetService;

    @InjectMocks
    ActionFieldChangeMonsterController actionFieldChangeMonsterController;


    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);

        when(resourceBundleProvider.get()).thenReturn(resources);

        app.show(actionFieldChangeMonsterController);
        stage.requestFocus();
    }

    @Test
    void testRender() {
        Platform.runLater(() -> actionFieldChangeMonsterController.addActionOption("monster", false));
        Text back = lookup("#user_monster_0").query();
        assertTrue(back.getText().endsWith("Back"));

        Text mon= lookup("#user_monster_1").query();
        assertTrue(mon.getText().endsWith("monster"));
    }
}
