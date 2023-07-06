package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.service.MonsterService;
import de.uniks.stpmon.k.service.PresetService;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ActionFieldChangeMonsterControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);

    @Mock
    MonsterService monsterService;
    @Mock
    PresetService presetService;

    @InjectMocks
    ActionFieldChangeMonsterController actionFieldChangeMonsterController;


    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        app.show(actionFieldChangeMonsterController);

        stage.requestFocus();
    }

    @Test
    void testRender() {
        Platform.runLater(() -> actionFieldChangeMonsterController.addActionOption("monster", false));
        Label back = lookup("#user_monster_label_0").query();
        assertTrue(back.getText().endsWith("Back"));

        Label mon= lookup("#user_monster_label_1").query();
        assertTrue(mon.getText().endsWith("monster"));
    }
}
