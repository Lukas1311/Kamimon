package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import javafx.scene.control.Button;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChooseSpriteControllerTest extends ApplicationTest {

    @Spy
    App app = new App(null);

    @InjectMocks
    ChooseSpriteController chooseSpriteController;
    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        app.show(chooseSpriteController);
        stage.requestFocus();
    }

    @Test
    public void testGUI() {
        Text chooseTrainer = lookup("#chooseTrainer").query();
        assertNotNull(chooseTrainer);
        assertEquals("Choose your trainer!", chooseTrainer.getText());

        Button saveSprite = lookup("#saveSprite").query();
        assertNotNull(saveSprite);
        assertEquals("Save changes", saveSprite.getText());

        Button spriteLeft = lookup("#spriteLeft").query();
        assertNotNull(spriteLeft);

        Button spriteRight = lookup("#spriteRight").query();
        assertNotNull(spriteRight);

        clickOn(saveSprite);
    }
}
