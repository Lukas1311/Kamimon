package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.di.DaggerTestComponent;
import de.uniks.stpmon.k.di.TestComponent;
import de.uniks.stpmon.k.service.dummies.MovementDummy;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.WorldStorage;
import de.uniks.stpmon.k.world.WorldSet;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class WorldControllerTest extends ApplicationTest {
    private final App app = new App(null);
    private final TestComponent component = (TestComponent) DaggerTestComponent.builder().mainApp(app).build();
    WorldController worldController = component.worldController();
    WorldStorage worldStorage = component.worldStorage();
    TrainerStorage trainerStorage = component.trainerStorage();

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        MovementDummy.addMovementDummy(component.eventListener());
        trainerStorage.setTrainer(DummyConstants.TRAINER);

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
        worldStorage.setWorld(new WorldSet(images, images, new ArrayList<>(), Collections.emptyMap()));
        app.show(worldController);

        SubScene node = lookup("#worldScene").queryAs(SubScene.class);
        Parent world = node.getRoot();
        assertFalse(world.getChildrenUnmodifiable().isEmpty());
    }
}
