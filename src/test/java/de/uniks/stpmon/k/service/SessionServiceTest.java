package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.models.Encounter;
import de.uniks.stpmon.k.models.EncounterMember;
import de.uniks.stpmon.k.models.Event;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.models.builder.OpponentBuilder;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
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

import static org.junit.jupiter.api.Assertions.*;
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
    @Mock
    EventListener listener;
    @InjectMocks
    SessionService sessionService;

    @Test
    void listenEncounter() {
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
                        .setAttacker(true).create()
        );
        when(encounterService.getEncounterOpponents()).thenReturn(Observable.just(opponents));
        Encounter dummyEncounter = new Encounter("encounter_0", "", false);
        when(encounterService.getEncounter("encounter_0"))
                .thenReturn(Observable.just(dummyEncounter));
        // Mock opponent cache
        OpponentCache cache = Mockito.mock(OpponentCache.class);
        when(cache.getCurrentValues()).thenReturn(opponents);
        when(cache.onInitialized()).thenReturn(Completable.complete());
        when(opponentCacheProvider.get()).thenReturn(cache);
        // Mock single monster cache
        SingleMonsterCache monsterCache = Mockito.mock(SingleMonsterCache.class);
        when(monsterCache.onInitialized()).thenReturn(Completable.complete());
        when(monsterCacheProvider.get()).thenReturn(monsterCache);

        when(listener.listen(Socket.WS,
                "encounters.*.trainers.0.opponents.*.created", Opponent.class))
                .thenReturn(Observable.just(new Event<>("", battleOpponent)));

        // Check if encounter is null at start
        assertNull(encounterStorage.getEncounter());
        assertNull(encounterStorage.getSession());

        sessionService.listenForEncounter().blockingAwait();
        // Check if encounter is set after loading
        assertEquals(dummyEncounter, encounterStorage.getEncounter());

        EncounterSession session = encounterStorage.getSession();
        assertEquals(List.of("0"), session.getOwnTeam());
        assertEquals(List.of("1"), session.getAttackerTeam());
        assertIterableEquals(List.of(EncounterMember.SELF, EncounterMember.ATTACKER_FIRST),
                session.getMembers());
    }

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
        when(encounterService.getEncounterOpponents()).thenReturn(Observable.just(opponents));
        when(encounterService.getTrainerOpponents("0")).thenReturn(Observable.just(List.of(battleOpponent)));
        Encounter dummyEncounter = new Encounter("encounter_0", "", false);
        when(encounterService.getEncounter("encounter_0"))
                .thenReturn(Observable.just(dummyEncounter));
        // Mock opponent cache
        OpponentCache cache = Mockito.mock(OpponentCache.class);
        when(cache.getCurrentValues()).thenReturn(opponents);
        when(cache.onInitialized()).thenReturn(Completable.complete());
        when(opponentCacheProvider.get()).thenReturn(cache);
        // Mock single monster cache
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
        assertIterableEquals(List.of(EncounterMember.SELF, EncounterMember.ATTACKER_FIRST,
                EncounterMember.TEAM_FIRST, EncounterMember.ATTACKER_SECOND), session.getMembers());
    }

}