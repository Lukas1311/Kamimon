package de.uniks.stpmon.k.controller.encounter;


import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.EncounterMember;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.MonsterAttributes;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.SessionService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;


@ExtendWith(MockitoExtension.class)
public class StatusControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);
    @Mock
    PresetService presetService;
    @Mock
    SessionService sessionService;
    @Spy
    @SuppressWarnings("unused")
    EffectContext effectContext = new EffectContext().setSkipLoadImages(true);

    @InjectMocks
    @Spy
    StatusController statusController;

    MonsterAttributes attributes1 = new MonsterAttributes(10, 8, 6, 4);
    MonsterAttributes currentAttributes1 = new MonsterAttributes(5, 4, 3, 2);
    Monster dummyMonster1 = MonsterBuilder.builder()
            .setId("id1")
            .setTrainer("id")
            .setLevel(1)
            .setExperience(2)
            .setAttributes(attributes1)
            .setCurrentAttributes(currentAttributes1)
            .create();

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);

        MonsterTypeDto monsterTypeDto = new MonsterTypeDto(1, "monster", null, null, null);
        when(presetService.getMonster(anyString())).thenReturn(Observable.just(monsterTypeDto));

        when(sessionService.getMonster(EncounterMember.SELF)).thenReturn(dummyMonster1);
        // No monster updates
        when(sessionService.listenMonster(any())).thenReturn(Observable.empty());

        statusController.setMember(EncounterMember.SELF);
        app.show(statusController);
        stage.requestFocus();
    }


    @Test
    void testRender() {
        doNothing().when(statusController).loadMonsterInformation();

        statusController.setMember(EncounterMember.SELF);
        statusController.render();

        statusController.setMember(EncounterMember.ATTACKER_FIRST);
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