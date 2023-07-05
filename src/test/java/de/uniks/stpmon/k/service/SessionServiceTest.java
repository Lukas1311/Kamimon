package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.models.Encounter;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.models.builder.OpponentBuilder;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.service.storage.EncounterSession;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.cache.OpponentCache;
import de.uniks.stpmon.k.service.storage.cache.SingleMonsterCache;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Provider;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Spy
    TrainerStorage trainerStorage;
    @Spy
    EncounterStorage encounterStorage;
    @Mock
    EncounterService encounterService;
    @Mock
    Provider<OpponentCache> opponentCacheProvider;
    @Mock
    Provider<SingleMonsterCache> monsterCacheProvider;
    @InjectMocks
    SessionService sessionService;

    @Test
    void tryLoadEncounter() {
        // Mock encounter
        trainerStorage.setTrainer(TrainerBuilder.builder()
                .setId("0").create());
        Opponent battleOpponent = OpponentBuilder.builder()
                .setId("0")
                .setTrainer("0")
                .setEncounter("encounter_0").create();
        List<Opponent> opponents = List.of(
                battleOpponent,
                OpponentBuilder.builder(battleOpponent)
                        .setId("1")
                        .setTrainer("o_1")
                        .setAttacker(true).create(),
                OpponentBuilder.builder(battleOpponent)
                        .setId("2")
                        .setTrainer("o_2").create(),
                OpponentBuilder.builder(battleOpponent)
                        .setId("3")
                        .setAttacker(true)
                        .setTrainer("o_3").create()
        );
        OpponentCache cache = Mockito.mock(OpponentCache.class);
        when(cache.getCurrentValues()).thenReturn(opponents);
        when(cache.onInitialized()).thenReturn(Completable.complete());
        when(encounterService.getEncounterOpponents()).thenReturn(Observable.just(opponents));
        when(encounterService.getTrainerOpponents("0")).thenReturn(Observable.just(List.of(battleOpponent)));
        Encounter dummyEncounter = new Encounter("encounter_0", "", false);
        when(encounterService.getEncounter("encounter_0"))
                .thenReturn(Observable.just(dummyEncounter));
        when(opponentCacheProvider.get()).thenReturn(cache);
        SingleMonsterCache monsterCache = Mockito.mock(SingleMonsterCache.class);
        when(monsterCache.onInitialized()).thenReturn(Completable.complete());
        when(monsterCacheProvider.get()).thenReturn(monsterCache);

        // Check if encounter is null at start
        assertNull(encounterStorage.getEncounter());
        assertNull(encounterStorage.getSession());

        sessionService.tryLoadEncounter().blockingAwait();
        // Check if encounter is set after loading
        assertEquals(dummyEncounter, encounterStorage.getEncounter());

        EncounterSession session = encounterStorage.getSession();
        assertEquals(List.of("0", "2"), session.getOwnTeam());
        assertEquals(List.of("1", "3"), session.getAttackerTeam());
    }

}