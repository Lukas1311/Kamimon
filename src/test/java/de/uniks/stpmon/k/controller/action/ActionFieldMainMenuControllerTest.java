package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.App;
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

import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;

@ExtendWith(MockitoExtension.class)
public class ActionFieldMainMenuControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);
    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;

    @InjectMocks
    ActionFieldMainMenuController actionFieldMainMenuController;


    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);

        when(resourceBundleProvider.get()).thenReturn(resources);

        app.show(actionFieldMainMenuController);
        stage.requestFocus();
    }

    @Test
    void testRender() {
        Text fight = lookup("#main_menu_0").query();
        Text changeMon = lookup("#main_menu_1").query();

        verifyThat(fight, hasText("Fight"));
        verifyThat(changeMon, hasText("Change Mon"));
    }
}
