package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.models.*;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.models.builder.OpponentBuilder;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.storage.EncounterSession;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.cache.EncounterMember;
import de.uniks.stpmon.k.service.storage.cache.EncounterMonsters;
import de.uniks.stpmon.k.service.storage.cache.OpponentCache;
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
import static org.mockito.ArgumentMatchers.any;
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
    Provider<EncounterMember> monsterCacheProvider;
    @Mock
    Provider<EncounterMonsters> monstersCacheProvider;
    @Mock
    EventListener listener;
    @InjectMocks
    SessionService sessionService;

    private void mockCaches(List<Opponent> opponents) {
        // Mock opponent cache
        OpponentCache cache = Mockito.mock(OpponentCache.class);
        when(cache.getCurrentValues()).thenReturn(opponents);
        when(cache.onInitialized()).thenReturn(Completable.complete());
        when(cache.listenValue(any())).thenReturn(Observable.empty());
        when(cache.onDeletion()).thenReturn(Observable.empty());
        when(cache.onCreation()).thenReturn(Observable.empty());
        when(opponentCacheProvider.get()).thenReturn(cache);
        EncounterMonsters monsters = Mockito.mock(EncounterMonsters.class);
        when(monsters.onInitialized()).thenReturn(Completable.complete());
        when(monstersCacheProvider.get()).thenReturn(monsters);
        // Mock single monster cache
        EncounterMember monsterCache = Mockito.mock(EncounterMember.class);
        when(monsterCache.getTrainerId()).thenReturn("0");
        when(monsterCacheProvider.get()).thenReturn(monsterCache);
    }

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
        mockCaches(opponents);

        when(listener.listen(Socket.WS,
                "encounters.*.trainers.0.opponents.*.created", Opponent.class))
                .thenReturn(Observable.just(new Event<>("", battleOpponent)));

        // Check if encounter is null at start
        assertNull(encounterStorage.getEncounter());
        assertNull(encounterStorage.getSession());

        sessionService.listenForEncounter().blockingAwait();
        // Check if encounter is set after loading
        assertEquals(dummyEncounter, encounterStorage.getEncounter());
        assertTrue(sessionService.isSelf(EncounterSlot.PARTY_FIRST));

        EncounterSession session = encounterStorage.getSession();
        assertEquals(List.of("0"), session.getOwnTeam());
        assertEquals(List.of("1"), session.getEnemyTeam());
        assertIterableEquals(List.of(EncounterSlot.PARTY_FIRST, EncounterSlot.ENEMY_FIRST),
                session.getSlots());
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
        mockCaches(opponents);

        // Check if encounter is null at start
        assertNull(encounterStorage.getEncounter());
        assertNull(encounterStorage.getSession());
        assertTrue(sessionService.hasNoEncounter());

        sessionService.tryLoadEncounter().blockingAwait();
        // Check if encounter is set after loading
        assertEquals(dummyEncounter, encounterStorage.getEncounter());
        assertFalse(sessionService.hasNoEncounter());
        assertTrue(sessionService.isSelf(EncounterSlot.PARTY_FIRST));

        EncounterSession session = encounterStorage.getSession();
        assertEquals(List.of("0", "2"), session.getOwnTeam());
        assertEquals(List.of("1", "3"), session.getEnemyTeam());
        assertIterableEquals(List.of(EncounterSlot.PARTY_FIRST, EncounterSlot.ENEMY_FIRST,
                EncounterSlot.PARTY_SECOND, EncounterSlot.ENEMY_SECOND), session.getSlots());
    }

    @Test
    public void testFacade() {
        assertTrue(sessionService.hasNoEncounter());
        // Every getter method should throw an error if session is null
        assertThrows(IllegalStateException.class, () -> sessionService.getMonster(EncounterSlot.PARTY_FIRST));
        // List should just be empty
        assertEquals(List.of(), sessionService.getSlots());
        assertEquals(List.of(), sessionService.getEnemyTeam());
        assertEquals(List.of(), sessionService.getOwnTeam());

        EncounterSession session = Mockito.mock(EncounterSession.class);
        encounterStorage.setSession(session);

        when(session.getOwnTeam()).thenReturn(List.of("10"));

        assertEquals(List.of("10"), sessionService.getOwnTeam());

        when(session.getEnemyTeam()).thenReturn(List.of("10"));

        assertEquals(List.of("10"), sessionService.getEnemyTeam());

        Opponent opponent = OpponentBuilder.builder().create();
        when(session.getOpponent(EncounterSlot.PARTY_FIRST)).thenReturn(opponent);
        assertEquals(opponent, sessionService.getOpponent(EncounterSlot.PARTY_FIRST));

        when(session.listenOpponent(EncounterSlot.PARTY_SECOND)).thenReturn(Observable.empty());
        // Check if listen is empty
        sessionService.listenOpponent(EncounterSlot.PARTY_SECOND).test()
                .assertValues();


        Monster monster = MonsterBuilder.builder().create();
        when(session.getMonster(EncounterSlot.PARTY_FIRST)).thenReturn(monster);
        assertEquals(monster, sessionService.getMonster(EncounterSlot.PARTY_FIRST));

        when(session.listenMonster(EncounterSlot.PARTY_SECOND)).thenReturn(Observable.empty());
        // Check if listen is empty
        sessionService.listenMonster(EncounterSlot.PARTY_SECOND).test()
                .assertValues();

        when(session.hasSlot(EncounterSlot.PARTY_FIRST)).thenReturn(true);
        when(session.hasSlot(EncounterSlot.PARTY_SECOND)).thenReturn(false);
        assertTrue(sessionService.hasSlot(EncounterSlot.PARTY_FIRST));
        assertFalse(sessionService.hasSlot(EncounterSlot.PARTY_SECOND));
    }

}