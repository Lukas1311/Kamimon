package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.controller.WorldController;
import de.uniks.stpmon.k.di.DaggerTestComponent;
import de.uniks.stpmon.k.di.TestComponent;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.dummies.TestHelper;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.WorldRepository;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class WorldViewTest extends ApplicationTest {

    private final App app = new App(null);
    private final TestComponent component = (TestComponent) DaggerTestComponent.builder().mainApp(app).build();

    final WorldRepository worldRepository = component.worldStorage();

    @InjectMocks
    public final WorldController controller = component.worldController();
    public final TrainerStorage trainerStorage = component.trainerStorage();
    public final RegionStorage regionStorage = component.regionStorage();
    public final RegionService regionService = component.regionService();

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        trainerStorage.setTrainer(regionService.createTrainer("id0", "Uwe", "t.png").blockingFirst());
        TestHelper.addWorldDummy(worldRepository);
        component.eventDummy().ensureMock();
        regionStorage.setRegion(DummyConstants.REGION);
        regionStorage.setArea(regionService.getArea("id0", "id0_0").blockingFirst());


        // show app
        app.start(stage);
        app.addInputHandler(component);
        app.show(controller);
        stage.requestFocus();
    }

    @AfterEach
    void afterEach() {
        // Remove event handlers
        app.removeInputHandler(component);
    }

    @Test
    public void moveCharacter() {
        SubScene node = lookup("#worldScene").queryAs(SubScene.class);
        Parent root = node.getRoot();
        Group character = (Group) root.lookup("#character");
        assertEquals(0, character.getTranslateX());
        // -16 because character is rendered from the center
        assertEquals(-16, character.getTranslateZ());
        // test move up
        type(KeyCode.W, 2);
        waitForFxEvents(10);

        // check if char moved up by -16
        assertEquals(0, character.getTranslateZ());
        waitForFxEvents(10);

        // test move down
        type(KeyCode.S, 2);
        waitForFxEvents();

        // check if char moved down by 16
        assertEquals(-16, character.getTranslateZ());

        // test move left
        type(KeyCode.A, 2);
        waitForFxEvents();

        // check if char moved left by 16
        assertEquals(-16, character.getTranslateX());

        // test move right
        type(KeyCode.D, 2);
        waitForFxEvents();

        // check if char moved right by -16
        assertEquals(0, character.getTranslateX());
    }

}
