package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.di.DaggerTestComponent;
import de.uniks.stpmon.k.di.TestComponent;
import de.uniks.stpmon.k.models.*;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.models.builder.OpponentBuilder;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.SessionService;
import de.uniks.stpmon.k.service.dummies.EncounterApiDummy;
import de.uniks.stpmon.k.service.dummies.EventDummy;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TextMatchers.hasText;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class EncounterTest extends ApplicationTest {
    @Spy
    App app = new App(null);
    private final TestComponent component = (TestComponent) DaggerTestComponent.builder().mainApp(app).build();
    @Spy
    @SuppressWarnings("unused")
    EffectContext effectContext = new EffectContext().setSkipLoadImages(true).setEncounterAnimationSpeed(1);

    @Spy
    public EncounterOverviewController controller = component.encounterController();

    RegionStorage regionStorage = component.regionStorage();
    SessionService sessionService = component.sessionService();
    TrainerStorage trainerStorage = component.trainerStorage();
    EncounterApiDummy encounterApiDummy = component.encounterApi();
    EventDummy eventDummy = component.eventDummy();

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);

        trainerStorage.setTrainer(DummyConstants.TRAINER);
        regionStorage.setRegion(new Region("id0", "", null, DummyConstants.EMPTY_MAP_DATA));
        regionStorage.setArea(DummyConstants.AREA);
        encounterApiDummy.startEncounter(true);
        sessionService.tryLoadEncounter().blockingAwait();

        app.show(controller);
        stage.requestFocus();
    }

    @Test
    void changeMonster() {
        // First monster should be selected
        verifyThat("#0_party #monsterHp", hasText("10 / 20"));
        // Send event for updating selected monster
        eventDummy.sendEvent(new Event<>("encounters.%s.trainers.%s.opponents.%s.%s".formatted("0", "0", "0", "updated"),
                OpponentBuilder.builder().setEncounter("0")
                        .setMonster("1")
                        .setId("0")
                        .setAttacker(true)
                        .setTrainer("0").create()));
        waitForFxEvents();

        // New monster should be selected
        verifyThat("#0_party #monsterHp", hasText("5 / 12"));
    }

    @Test
    void updateMonster() {
        // First monster should be selected
        verifyThat("#0_party #monsterHp", hasText("10 / 20"));
        Monster monster = sessionService.getMonster(EncounterSlot.PARTY_FIRST);
        // Send event for updating selected monster
        eventDummy.sendEvent(new Event<>("trainers.%s.monsters.%s.created"
                .formatted("0", "0"),
                MonsterBuilder.builder(monster)
                        .setCurrentAttributes(new MonsterAttributes(1, 10, 10, 10))
                        .setAttributes(new MonsterAttributes(12, 10, 10, 10))
                        .create()));
        waitForFxEvents();

        // New monster should be selected
        verifyThat("#0_party #monsterHp", hasText("1 / 12"));
    }

}
