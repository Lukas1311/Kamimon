package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.controller.WorldController;
import de.uniks.stpmon.k.di.DaggerTestComponent;
import de.uniks.stpmon.k.di.TestComponent;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.dummies.MovementDummy;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.WorldStorage;
import javafx.scene.Node;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class WorldViewTest extends ApplicationTest {

    private final App app = new App(null);
    private final TestComponent component = (TestComponent) DaggerTestComponent.builder().mainApp(app).build();

    WorldStorage worldStorage = component.worldStorage();

    @InjectMocks
    public WorldController controller = component.worldController();
    public TrainerStorage trainerStorage = component.trainerStorage();
    public RegionStorage regionStorage = component.regionStorage();
    public RegionService regionService = component.regionService();

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        MovementDummy.addMovementDummy(component.eventListener());
        trainerStorage.setTrainer(DummyConstants.TRAINER);
        worldStorage.setWorld(DummyConstants.WORLD);
        regionStorage.setRegion(DummyConstants.REGION);
        regionStorage.setArea(regionService.getArea("id0", "id0_0").blockingFirst());


        // show app
        app.start(stage);
        app.addInputHandler(component);
        app.show(controller);
        stage.requestFocus();
    }

    @Test
    public void moveCharacter() {
        SubScene node = lookup("#worldScene").queryAs(SubScene.class);
        Parent root = node.getRoot();
        MeshView character = (MeshView) root.lookup("#character");
        assertEquals(0, character.getTranslateX());
        // -16 because character is rendered from the center
        assertEquals(-16, character.getTranslateZ());

        // test move up
        type(KeyCode.W);
        waitForFxEvents();

        // check if char moved up by -16
        assertEquals(0, character.getTranslateZ());

        // test move down
        type(KeyCode.S);
        waitForFxEvents();

        // check if char moved down by 16
        assertEquals(-16, character.getTranslateZ());

        // test move left
        type(KeyCode.A);
        waitForFxEvents();

        // check if char moved left by 16
        assertEquals(-16, character.getTranslateX());

        // test move right
        type(KeyCode.D);
        waitForFxEvents();

        // check if char moved right by -16
        assertEquals(0, character.getTranslateX());
    }

    @Test
    public void renderAllTrainer() {
        SubScene node = lookup("#worldScene").queryAs(SubScene.class);
        Parent root = node.getRoot();
        Node npcGroup = root.lookup("#npcGroup");
        assertNotNull(npcGroup);
    }

}