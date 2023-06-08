package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.WorldController;
import de.uniks.stpmon.k.di.DaggerTestComponent;
import de.uniks.stpmon.k.di.TestComponent;
import de.uniks.stpmon.k.map.WorldSet;
import de.uniks.stpmon.k.service.storage.WorldStorage;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.MeshView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class WorldViewTest extends ApplicationTest {

    private final App app = new App(null);
    private final TestComponent component = (TestComponent) DaggerTestComponent.builder().mainApp(app).build();

    WorldStorage worldStorage = component.worldStorage();

    @InjectMocks
    public WorldController controller = component.worldController();

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        BufferedImage images = new BufferedImage(1, 1,
                BufferedImage.TYPE_INT_RGB);
        worldStorage.setWorld(new WorldSet(images, images, new ArrayList<>(), Collections.emptyMap()));

        // show app
        app.start(stage);
        app.show(controller);
        stage.requestFocus();
    }

    @Test
    public void moveCharacter() {
        SubScene node = lookup("#worldScene").queryAs(SubScene.class);
        Parent root = node.getRoot();
        MeshView character = (MeshView) root.lookup("#character");
        assertEquals(0, character.getTranslateX());

        // test move up
        type(KeyCode.W);
        waitForFxEvents();

        // check if char moved up by 8
        assertEquals(8, character.getTranslateZ());

        // test move down
        type(KeyCode.S);
        waitForFxEvents();

        // check if char moved down by 8
        assertEquals(0, character.getTranslateZ());

        // test move left
        type(KeyCode.A);
        waitForFxEvents();

        // check if char moved left by 8
        assertEquals(-8, character.getTranslateX());

        // test move right
        type(KeyCode.D);
        waitForFxEvents();

        // check if char moved right by 8
        assertEquals(0, character.getTranslateX());
    }
}