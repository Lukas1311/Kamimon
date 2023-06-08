package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.di.DaggerTestComponent;
import de.uniks.stpmon.k.di.TestComponent;
import de.uniks.stpmon.k.service.storage.WorldStorage;
import de.uniks.stpmon.k.service.world.World;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class WorldControllerTest extends ApplicationTest {
    private final App app = new App(null);
    private final TestComponent component = (TestComponent) DaggerTestComponent.builder().mainApp(app).build();
    WorldController worldController = component.worldController();
    WorldStorage worldStorage = component.worldStorage();

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        // show app
        app.start(stage);
        stage.requestFocus();
    }

    @Test
    public void worldEmpty() {
        app.show(worldController);

        SubScene node = lookup("#worldScene").queryAs(SubScene.class);
        Parent world = node.getRoot();
        assertTrue(world.getChildrenUnmodifiable().isEmpty());
    }

    @Test
    public void worldExists() {
        BufferedImage images = new BufferedImage(1, 1,
                BufferedImage.TYPE_INT_RGB);
        worldStorage.setWorld(new World(images, images, new ArrayList<>()));
        app.show(worldController);

        SubScene node = lookup("#worldScene").queryAs(SubScene.class);
        Parent world = node.getRoot();
        assertFalse(world.getChildrenUnmodifiable().isEmpty());
    }
}