package de.uniks.stpmon.k.controller.encounter;


import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.Encounter;
import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.MonsterAttributes;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.SessionService;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
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
    final
    App app = new App(null);
    @Mock
    PresetService presetService;
    @Mock
    SessionService sessionService;
    @Mock
    EncounterStorage encounterStorage;
    @Spy
    @SuppressWarnings("unused")
    EffectContext effectContext = new EffectContext().setSkipLoadImages(true);

    @InjectMocks
    @Spy
    StatusController statusController;

    final MonsterAttributes attributes1 = new MonsterAttributes(10f, 8f, 6f, 4f);
    final MonsterAttributes currentAttributes1 = new MonsterAttributes(5f, 4f, 3f, 2f);
    final Monster dummyMonster1 = MonsterBuilder.builder()
            .setId("id1")
            .setTrainer("id")
            .setLevel(1)
            .setExperience(2)
            .setAttributes(attributes1)
            .setCurrentAttributes(currentAttributes1)
            .create();

    @Override
    public void start(Stage stage) {
        app.start(stage);

        MonsterTypeDto monsterTypeDto = new MonsterTypeDto(1, "monster", null, null, null);
        when(presetService.getMonster(anyString())).thenReturn(Observable.just(monsterTypeDto));

        when(sessionService.getMonster(EncounterSlot.PARTY_FIRST)).thenReturn(dummyMonster1);
        // No monster updates
        when(sessionService.listenMonster(any())).thenReturn(Observable.empty());
        when(sessionService.isSelf(EncounterSlot.PARTY_FIRST)).thenReturn(true);

        statusController.setSlot(EncounterSlot.PARTY_FIRST);
        app.show(statusController);
        stage.requestFocus();
    }


    @Test
    void testRender() {
        when(sessionService.isSelf(EncounterSlot.ENEMY_FIRST)).thenReturn(false);
        when(encounterStorage.getEncounter()).thenReturn(new Encounter("0", "0", false));
        doNothing().when(statusController).loadMonsterInformation();

        statusController.setSlot(EncounterSlot.PARTY_FIRST);
        statusController.render();

        statusController.setSlot(EncounterSlot.ENEMY_FIRST);
        statusController.render();

        // one time for app start (because monster has to be initially set), two times for invocation
        verify(statusController, times(3)).loadMonsterInformation();
    }



    @Test
    void testLoadUserMonsterInformation() {
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
        statusController.loadMonsterDto("1");
        waitForFxEvents();

        assertEquals("monster", statusController.monsterName.getText());
    }

}