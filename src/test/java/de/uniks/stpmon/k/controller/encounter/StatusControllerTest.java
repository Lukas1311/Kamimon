package de.uniks.stpmon.k.controller.encounter;


import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.Encounter;
import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.MonsterAttributes;
import de.uniks.stpmon.k.models.MonsterStatus;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.SessionService;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.ParentMatchers.hasChildren;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;


@ExtendWith(MockitoExtension.class)
public class StatusControllerTest extends ApplicationTest {

    @Spy
    final App app = new App(null);
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

    @Override
    public void start(Stage stage) {
        app.start(stage);

        MonsterTypeDto monsterTypeDto = new MonsterTypeDto(1, "monster", null, null, null);
        when(presetService.getMonster(anyString())).thenReturn(Observable.just(monsterTypeDto));
        when(sessionService.listenOpponentDeletion(any())).thenReturn(Observable.empty());

        // No monster updates
        when(sessionService.listenMonster(any())).thenReturn(Observable.empty());
    }

    @Test
    void testOpponentStatus() {
        when(sessionService.getMonster(EncounterSlot.ENEMY_FIRST)).thenReturn(MonsterBuilder.builder()
                .setId("id1")
                .setTrainer("id")
                .setLevel(2)
                .setAttributes(attributes1)
                .setCurrentAttributes(currentAttributes1)
                .setStatus(List.of(MonsterStatus.CONFUSED, MonsterStatus.ASLEEP, MonsterStatus.FROZEN)).create());
        when(sessionService.isSelf(EncounterSlot.ENEMY_FIRST)).thenReturn(false);
        when(encounterStorage.getEncounter()).thenReturn(new Encounter("0", "0", true));
        Platform.runLater(() -> {
            statusController.setSlot(EncounterSlot.ENEMY_FIRST);
            app.show(statusController);
            app.getStage().requestFocus();
        });
        waitForFxEvents();

        assertNotNull(statusController.fullBox);
        assertNotNull(statusController.hpBar);
        assertNotNull(statusController.monsterLevel);
        assertNull(statusController.monsterHp);
        assertNull(statusController.experienceBar);

        assertEquals("Lvl. 2", statusController.monsterLevel.getText());
        verifyThat("#effectContainer", hasChildren(3));
        verifyThat("#effectContainer #effect_confused", Node::isVisible);
        verifyThat("#effectContainer #effect_frozen", Node::isVisible);
        verifyThat("#effectContainer #effect_asleep", Node::isVisible);
        assertEquals(0.5, statusController.hpBar.getProgress());
    }

    @Test
    void testSelfStatus() {
        when(sessionService.getMonster(EncounterSlot.PARTY_FIRST)).thenReturn(MonsterBuilder.builder()
                .setId("id1")
                .setTrainer("id")
                .setLevel(1)
                .setExperience(2)
                .setAttributes(attributes1)
                .setCurrentAttributes(currentAttributes1)
                .addStatus(MonsterStatus.BURNED, MonsterStatus.PARALYZED, MonsterStatus.POISONED)
                .create());
        when(sessionService.isSelf(EncounterSlot.PARTY_FIRST)).thenReturn(true);

        Platform.runLater(() -> {
            statusController.setSlot(EncounterSlot.PARTY_FIRST);
            app.show(statusController);
            app.getStage().requestFocus();
        });
        waitForFxEvents();

        assertNotNull(statusController.fullBox);
        assertNotNull(statusController.hpBar);
        assertNotNull(statusController.monsterHp);
        assertNotNull(statusController.monsterLevel);
        assertNotNull(statusController.experienceBar);

        assertEquals("5 / 10", statusController.monsterHp.getText());
        assertEquals("Lvl. 1", statusController.monsterLevel.getText());
        verifyThat("#effectContainer", hasChildren(3));
        verifyThat("#effectContainer #effect_burned", Node::isVisible);
        verifyThat("#effectContainer #effect_paralysed", Node::isVisible);
        verifyThat("#effectContainer #effect_poisoned", Node::isVisible);
        assertEquals(0.5, statusController.hpBar.getProgress());
        assertEquals(2.0, statusController.experienceBar.getProgress());
    }

}