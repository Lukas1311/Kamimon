package de.uniks.stpmon.k.controller.encounter;


import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.MonsterAttributes;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.TrainerService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatusControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);
    @Mock
    PresetService presetService;
    @Mock
    RegionService regionService;
    @Mock
    RegionStorage regionStorage;
    @Mock
    TrainerService trainerService;
    @InjectMocks
    StatusController statusController;

    Region dummyRegion = new Region("1", "reg", null, null);
    Trainer dummytrainer = TrainerBuilder.builder().setId("1").create();
    Monster dummyMonster;


    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);

        when(trainerService.getMe()).thenReturn(dummytrainer);
        when(regionStorage.getRegion()).thenReturn(dummyRegion);

        MonsterAttributes attributes = new MonsterAttributes(10, 8, 6, 4);
        MonsterAttributes currentAttributes = new MonsterAttributes(5, 4, 3, 2);
        dummyMonster = MonsterBuilder.builder()
                .setId("id")
                .setTrainer(trainerService.getMe()._id())
                .setLevel(1)
                .setExperience(2)
                .setAttributes(attributes)
                .setCurrentAttributes(currentAttributes)
                .create();

        statusController.setMonster(dummyMonster);

        when(regionService.getMonster(anyString(), anyString())).thenReturn(Observable.just(dummyMonster));

        app.show(statusController);
        stage.requestFocus();
    }

    @Test
    void testLoadUserMonsterInformation() {
        // Mock UI elements
        VBox fullBox = mock(VBox.class);
        ProgressBar hpBar = mock(ProgressBar.class);
        Text monsterHp = mock(Text.class);
        Text monsterLevel = mock(Text.class);
        ProgressBar experienceBar = mock(ProgressBar.class);

        // Set UI elements in the statusController
        statusController.fullBox = fullBox;
        statusController.hpBar = hpBar;
        statusController.monsterHp = monsterHp;
        statusController.monsterLevel = monsterLevel;
        statusController.experienceBar = experienceBar;

        // Create a dummy monster with the expected values
        MonsterAttributes attributes = new MonsterAttributes(10, 8, 6, 4);
        MonsterAttributes currentAttributes = new MonsterAttributes(5, 4, 3, 2);
        Monster dummyMonster = MonsterBuilder.builder()
                .setId("id")
                .setTrainer(trainerService.getMe()._id())
                .setLevel(1)
                .setExperience(2)
                .setAttributes(attributes)
                .setCurrentAttributes(currentAttributes)
                .create();

        // Mock the regionService.getMonster() method to return the dummy monster
        when(regionService.getMonster(anyString(), anyString())).thenReturn(Observable.just(dummyMonster));

        // Call the loadMonsterInformation method
        statusController.loadMonsterInformation();


        // Verify that the UI elements are updated correctly
        verify(monsterHp).setText("5 / 10");
        verify(monsterLevel).setText("Lvl. 1");
        verify(hpBar).setProgress(0.5);

        assertNotNull(fullBox);
        assertNotNull(hpBar);
        assertNotNull(monsterHp);
        assertNotNull(monsterLevel);
        assertNotNull(experienceBar);
    }

    @Test
    void testLoadOpponentMonsterInformation() {

    }

}
