package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.di.DaggerTestComponent;
import de.uniks.stpmon.k.di.TestComponent;
import de.uniks.stpmon.k.models.*;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.models.builder.OpponentBuilder;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.SessionService;
import de.uniks.stpmon.k.service.dummies.EncounterApiDummy;
import de.uniks.stpmon.k.service.dummies.EventDummy;
import de.uniks.stpmon.k.service.dummies.RegionApiDummy;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TextMatchers.hasText;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class EncounterTest extends ApplicationTest {

    @Spy
    @SuppressWarnings("unused")
    Provider<HybridController> hybridControllerProvider;

    @Spy
    final
    App app = new App(null);
    private final TestComponent component = (TestComponent) DaggerTestComponent.builder().mainApp(app).build();
    @Spy
    @SuppressWarnings("unused")
    EffectContext effectContext = new EffectContext().setSkipLoadImages(true).setEncounterAnimationSpeed(1);

    @Spy
    public final EncounterOverviewController controller = component.encounterController();

    final RegionStorage regionStorage = component.regionStorage();
    final SessionService sessionService = component.sessionService();
    final TrainerStorage trainerStorage = component.trainerStorage();
    final EncounterApiDummy encounterApiDummy = component.encounterApi();
    final EventDummy eventDummy = component.eventDummy();
    final RegionApiDummy regionApiDummy = component.regionApi();

    @SuppressWarnings("unused")
    final HybridController hybridController = Mockito.mock(HybridController.class);

    @Override
    public void start(Stage stage) {
        app.start(stage);

        regionApiDummy.addTrainer(DummyConstants.TRAINER);
        regionApiDummy.addTrainer(DummyConstants.TRAINER_OTHER);
        regionApiDummy.addMonster("0", "0", true);
        regionApiDummy.addMonster("attacker", "1", true);

        trainerStorage.setTrainer(DummyConstants.TRAINER);
        regionStorage.setRegion(new Region("id0", "", null, DummyConstants.EMPTY_MAP_DATA));
        regionStorage.setArea(DummyConstants.AREA);
        encounterApiDummy.startEncounter(true, false);
        sessionService.tryLoadEncounter().blockingAwait();

        app.show(controller);
        stage.requestFocus();
    }

    @Test
    void changeMonster() {

        // First monster should be selected
        verifyThat("#0_party #monsterHp", hasText("1 / 20"));
        // Send event for updating selected monster
        eventDummy.sendEvent(new Event<>("encounters.%s.trainers.%s.opponents.%s.%s".formatted("0", "0", "0", "updated"),
                OpponentBuilder.builder().setEncounter("0")
                        .setMonster("1")
                        .setId("0")
                        .setAttacker(true)
                        .setTrainer("0").create()));
        waitForFxEvents();

        // New monster should be selected
        verifyThat("#0_party #monsterHp", hasText("2 / 12"));
    }

    @Test
    void updateMonster() {
        // First monster should be selected
        verifyThat("#0_party #monsterHp", hasText("1 / 20"));
        Monster monster = sessionService.getMonster(EncounterSlot.PARTY_FIRST);
        // Send event for updating selected monster
        eventDummy.sendEvent(new Event<>("trainers.%s.monsters.%s.updated"
                .formatted("0", "0"),
                MonsterBuilder.builder(monster)
                        .setCurrentAttributes(new MonsterAttributes(1f, 10f, 10f, 10f))
                        .setAttributes(new MonsterAttributes(12f, 10f, 10f, 10f))
                        .create()));
        waitForFxEvents();
        sleep(200);

        // New monster should be selected
        verifyThat("#0_party #monsterHp", hasText("1 / 12"));
    }

    @AfterEach
    void closeAll() {
        controller.destroy();

    }

}
