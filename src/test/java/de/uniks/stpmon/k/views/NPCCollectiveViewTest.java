package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.controller.WorldController;
import de.uniks.stpmon.k.di.DaggerTestComponent;
import de.uniks.stpmon.k.di.TestComponent;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.dummies.MovementDummy;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.WorldStorage;
import io.reactivex.rxjava3.core.Observable;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NPCCollectiveViewTest extends ApplicationTest {

    private final App app = new App(null);
    private final TestComponent component = (TestComponent) DaggerTestComponent.builder().mainApp(app).build();

    WorldStorage worldStorage = component.worldStorage();

    @InjectMocks
    public WorldController controller = component.worldController();
    public TrainerStorage trainerStorage = component.trainerStorage();
    public RegionStorage regionStorage = component.regionStorage();
    @Mock
    RegionService regionService;

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        MovementDummy.addMovementDummy(component.eventListener());
        trainerStorage.setTrainer(DummyConstants.TRAINER);
        worldStorage.setWorld(DummyConstants.WORLD);
        regionStorage.setRegion(DummyConstants.REGION);

        Trainer trainer = new Trainer("1", "1", "1", "1", "Premade_Character_01.png", 0, "1", 16, 16, 1, null);
        //define mocks
        List<Trainer> trainers = new ArrayList<>();
        trainers.add(trainer);
        when(regionService.getAllTrainer(any())).thenReturn(Observable.just(trainers));

        // show app
        app.start(stage);
        app.show(controller);
        stage.requestFocus();
    }

    @Test
    public void renderAllTrainer() {
        Node node = lookup("#npcGroup").query();

        assertNotNull(node);
    }

}
