package de.uniks.stpmon.k.controller.encounter;


import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.MonsterAttributes;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.TrainerService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import io.reactivex.rxjava3.core.Observable;
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
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;


@ExtendWith(MockitoExtension.class)
public class StatusControllerTest extends ApplicationTest {
    @Spy
    final App app = new App(null);
    @Mock
    PresetService presetService;
    @Mock
    RegionService regionService;
    @Mock
    RegionStorage regionStorage;
    @Mock
    TrainerService trainerService;
    @Spy
    @SuppressWarnings("unused")
    EffectContext effectContext = new EffectContext().setSkipLoadImages(true);

    @InjectMocks
    StatusController statusController;

    final Region dummyRegion = new Region("1", "reg", null, null);

    final MonsterAttributes attributes1 = new MonsterAttributes(10, 8, 6, 4);
    final MonsterAttributes currentAttributes1 = new MonsterAttributes(5, 4, 3, 2);
    final Monster dummyMonster1 = MonsterBuilder.builder()
            .setId("id1")
            .setTrainer("id")
            .setLevel(1)
            .setExperience(2)
            .setAttributes(attributes1)
            .setCurrentAttributes(currentAttributes1)
            .create();

    final Trainer dummyTrainer = TrainerBuilder.builder()
            .setId("id")
            .addTeam(dummyMonster1._id())
            .create();

    // create a dummy monster with some values for the second trainer
    final MonsterAttributes attributes2 = new MonsterAttributes(24, 9, 7, 5);
    final MonsterAttributes currentAttributes2 = new MonsterAttributes(6, 5, 4, 3);
    final Monster dummyMonster2 = MonsterBuilder.builder()
            .setId("id2")
            .setTrainer("otherTrainerId")
            .setLevel(2)
            .setExperience(3)
            .setAttributes(attributes2)
            .setCurrentAttributes(currentAttributes2)
            .create();


    @Override
    public void start(Stage stage) {
        app.start(stage);

        statusController.setMonster(dummyMonster1);

        when(trainerService.getMe()).thenReturn(dummyTrainer);
        when(regionStorage.getRegion()).thenReturn(dummyRegion);

        when(regionService.getMonster(anyString(), eq("id1"))).thenReturn(Observable.just(dummyMonster1));

        app.show(statusController);
        stage.requestFocus();
    }


    @Test
    void testRender() {
        StatusController statusControllerSpy = spy(statusController);
        doNothing().when(statusControllerSpy).loadMonsterInformation();
        when(trainerService.getMe()).thenReturn(dummyTrainer);

        statusControllerSpy.setMonster(dummyMonster1);
        statusControllerSpy.render();

        statusControllerSpy.setMonster(dummyMonster2);
        statusControllerSpy.render();

        // one time for app start (because monster has to be initially set), two times for invocation
        verify(statusControllerSpy, times(2)).loadMonsterInformation();
    }

    @Test
    void testLoadUserMonsterInformation() {
        // Mock the regionService.getMonster() method to return the dummy monster
        when(regionService.getMonster(anyString(), anyString())).thenReturn(Observable.just(dummyMonster1));

        // Call the loadMonsterInformation method
        statusController.loadMonsterInformation();

        assertNotNull(statusController.fullBox);
        assertNotNull(statusController.hpBar);
        assertNotNull(statusController.monsterHp);
        assertNotNull(statusController.monsterLevel);
        assertNotNull(statusController.experienceBar);

        assertEquals("5 / 10", statusController.monsterHp.getText());
        assertEquals("Lvl. 1", statusController.monsterLevel.getText());
        assertEquals(0.5, statusController.hpBar.getProgress());
        assertEquals(2.0, statusController.experienceBar.getProgress());
    }

    @Test
    void testLoadMonsterDto() {
        MonsterTypeDto monsterTypeDto = new MonsterTypeDto(1, "monster", null, null, null);

        when(presetService.getMonster(anyString())).thenReturn(Observable.just(monsterTypeDto));

        statusController.loadMonsterDto(monsterTypeDto.id().toString());
        waitForFxEvents();

        assertEquals("monster", statusController.monsterName.getText());
    }
}