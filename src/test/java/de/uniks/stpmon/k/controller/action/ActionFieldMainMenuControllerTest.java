package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.App;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;

@ExtendWith(MockitoExtension.class)
public class ActionFieldMainMenuControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);

    @InjectMocks
    ActionFieldMainMenuController actionFieldMainMenuController;


    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        app.show(actionFieldMainMenuController);
        stage.requestFocus();
    }

    @Test
    void testRender() {
        Label fightLabel = lookup("#main_menu_label_0").query();
        Label changeMonLabel = lookup("#main_menu_label_1").query();

        verifyThat(fightLabel, hasText("Fight"));
        verifyThat(changeMonLabel, hasText("Change Mon"));
    }
}
