package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.di.DaggerTestComponent;
import de.uniks.stpmon.k.di.TestComponent;
import de.uniks.stpmon.k.service.dummies.MovementDummy;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.WorldStorage;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class WorldControllerTest extends ApplicationTest {
    private final App app = new App(null);
    private final TestComponent component = (TestComponent) DaggerTestComponent.builder().mainApp(app).build();
    WorldController worldController = component.worldController();
    WorldStorage worldStorage = component.worldStorage();
    TrainerStorage trainerStorage = component.trainerStorage();

    RegionStorage regionStorage = component.regionStorage();

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        MovementDummy.addMovementDummy(component.eventListener());
        trainerStorage.setTrainer(DummyConstants.TRAINER);
        // Needed for trainer cache
        regionStorage.setRegion(DummyConstants.REGION);
        regionStorage.setArea(DummyConstants.AREA);

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
        worldStorage.setWorld(DummyConstants.WORLD);
        app.show(worldController);

        SubScene node = lookup("#worldScene").queryAs(SubScene.class);
        Parent world = node.getRoot();
        assertFalse(world.getChildrenUnmodifiable().isEmpty());
    }
}
