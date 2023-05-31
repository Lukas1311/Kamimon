package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import javafx.scene.control.Button;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateTrainerControllerTest extends ApplicationTest {
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;

    @Spy
    App app = new App(null);
    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);

    @InjectMocks
    CreateTrainerController createTrainerController;

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        app.show(createTrainerController);
        stage.requestFocus();
    }

    /**
     * Verify the functionality of a graphical user interface (GUI) related to creating trainers and sprites.
     */
    @Test
    public void testGUI() {
        clickOn("#createTrainerInput");
        write("Tom\t");

        Button createSprite = lookup("#createSpriteButton").query();
        assertNotNull(createSprite);
        assertEquals("Create Sprite", createSprite.getText());

        Button createTrainer = lookup("#createTrainerButton").query();
        assertNotNull(createTrainer);
        assertEquals("Create Trainer", createTrainer.getText());

        clickOn(createTrainer);
    }
}
