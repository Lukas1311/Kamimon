package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.AbilityMove;
import de.uniks.stpmon.k.dto.ChangeMonsterMove;
import de.uniks.stpmon.k.models.Encounter;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.models.builder.ResultBuilder;
import de.uniks.stpmon.k.rest.EncounterApiService;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EncounterServiceTest {

    @Mock
    EncounterApiService encounterApiService;
    @Mock
    SessionService sessionService;
    @Mock
    Provider<SessionService> sessionServiceProvider;
    @Spy
    RegionStorage regionStorage;
    @Spy
    EncounterStorage encounterStorage;
    @InjectMocks
    EncounterService encounterService;

    private void initRegion() {
        regionStorage.setRegion(new Region(
                "0",
                null,
                null,
                null
        ));
    }

    private Encounter getDummyEncounters() {
        initRegion();
        return new Encounter(
                "0",
                regionStorage.getRegion()._id(),
                false);
    }

    private List<Opponent> getDummyOpponents() {
        initRegion();
        Opponent opponent1 = new Opponent(
                "0",
                "encounter1Id",
                "trainer1Id",
                false,
                false,
                "monster1Id",
                new AbilityMove(
                        "ability",
                        0,
                        "targetId"
                ),
                List.of(ResultBuilder.builder("ability-success")
                        .setAbility(0)
                        .setEffectiveness("effective")
                        .create()),
                0);
        Opponent opponent2 = new Opponent(
                "1",
                "encounter2Id",
                "trainer2Id",
                true,
                true,
                "monster2Id",
                new ChangeMonsterMove(
                        "change-monster",
                        "monster3Id"
                ),
                List.of(ResultBuilder.builder("monster-changed").create()),
                1);
        return List.of(opponent1, opponent2);
    }

    //---------------- Region Encounters ----------------------------

    @Test
    void getEncounters() {
        Encounter encounter = getDummyEncounters();

        //define mock
        List<Encounter> encounterList = new ArrayList<>();
        encounterList.add(encounter);
        when(encounterApiService.getEncounters(any()))
                .thenReturn(Observable.just(encounterList));

        //action
        final List<Encounter> returnEncounters = encounterService
                .getEncounters()
                .blockingFirst();

        //check values
        assertEquals(1, returnEncounters.size());
        assertEquals("0", returnEncounters.get(0)._id());
        assertEquals(false, returnEncounters.get(0).isWild());

        //check mock
        verify(encounterApiService).getEncounters(any());
    }

    @Test
    void getEncounter() {
        Encounter encounter = getDummyEncounters();

        //define mock
        when(encounterApiService.getEncounter(any(), any()))
                .thenReturn(Observable.just(encounter));

        when(encounterStorage.getEncounter()).thenReturn(encounter);

        encounterService.encounterStorage = encounterStorage;

        //action
        final Encounter returnEncounter = encounterService
                .getCurrentEncounter()
                .blockingFirst();

        //check values
        assertEquals("0", returnEncounter._id());
        assertEquals("0", returnEncounter.region());
        assertEquals(false, returnEncounter.isWild());

        // check mock
        verify(encounterApiService).getEncounter(any(), any());
    }

    //---------------- Encounters Opponents ----------------------------

    @Test
    void getTrainerOpponents() {
        Opponent trainerOpponent = getDummyOpponents().get(0);

        //define mock
        List<Opponent> opponentList = new ArrayList<>();
        opponentList.add(trainerOpponent);
        when(encounterApiService.getTrainerOpponents(any(), any()))
                .thenReturn(Observable.just(opponentList));

        //action
        final List<Opponent> returnOpponents = encounterService
                .getTrainerOpponents("trainerId")
                .blockingFirst();

        //check values
        assertEquals(1, returnOpponents.size());
        assertEquals("0", returnOpponents.get(0)._id());
        assertEquals("encounter1Id", returnOpponents.get(0).encounter());
        assertEquals("trainer1Id", returnOpponents.get(0).trainer());
        assertEquals(false, returnOpponents.get(0).isAttacker());
        assertEquals(false, returnOpponents.get(0).isNPC());
        assertEquals("monster1Id", returnOpponents.get(0).monster());
        assertEquals(new AbilityMove("ability", 0, "targetId"), returnOpponents.get(0).move());
        assertEquals(ResultBuilder.builder("ability-success")
                .setAbility(0)
                .setEffectiveness("effective")
                .create(), returnOpponents.get(0).results().get(0));
        assertEquals(0, returnOpponents.get(0).coins());

        //check mock
        verify(encounterApiService).getTrainerOpponents(any(), any());
    }

    @Test
    void getEncounterOpponents() {
        Opponent encounterOpponent = getDummyOpponents().get(1);

        //define mock
        List<Opponent> opponentList = new ArrayList<>();
        opponentList.add(encounterOpponent);
        when(encounterApiService.getEncounterOpponents(any(), any()))
                .thenReturn(Observable.just(opponentList));

        Encounter encounter = getDummyEncounters();
        when(encounterStorage.getEncounter()).thenReturn(encounter);

        //action
        final List<Opponent> returnOpponents = encounterService
                .getEncounterOpponents()
                .blockingFirst();

        //check values
        assertEquals(1, returnOpponents.size());
        assertEquals("1", returnOpponents.get(0)._id());
        assertEquals("encounter2Id", returnOpponents.get(0).encounter());
        assertEquals("trainer2Id", returnOpponents.get(0).trainer());
        assertEquals(true, returnOpponents.get(0).isAttacker());
        assertEquals(true, returnOpponents.get(0).isNPC());
        assertEquals("monster2Id", returnOpponents.get(0).monster());
        assertEquals(new ChangeMonsterMove("change-monster", "monster3Id"), returnOpponents.get(0).move());
        assertEquals(ResultBuilder.builder("monster-changed").create(), returnOpponents.get(0).results().get(0));
        assertEquals(1, returnOpponents.get(0).coins());

        //check mock
        verify(encounterApiService).getEncounterOpponents(any(), any());
    }

    @Test
    void getEncounterOpponent() {
        Opponent opponent = getDummyOpponents().get(0);

        //define mock
        when(encounterApiService.getEncounterOpponent(any(), any(), any()))
                .thenReturn(Observable.just(opponent));

        Encounter encounter = getDummyEncounters();
        when(encounterStorage.getEncounter()).thenReturn(encounter);

        when(sessionService.getOpponent(any())).thenReturn(opponent);

        when(sessionServiceProvider.get()).thenReturn(sessionService);

        //action
        final Opponent returnOpponent = encounterService.getEncounterOpponent().blockingFirst();

        //check value
        assertEquals("0", returnOpponent._id());
        assertEquals("encounter1Id", returnOpponent.encounter());
        assertEquals("trainer1Id", returnOpponent.trainer());
        assertEquals(false, returnOpponent.isAttacker());
        assertEquals(false, returnOpponent.isNPC());
        assertEquals("monster1Id", returnOpponent.monster());
        assertEquals(new AbilityMove("ability", 0, "targetId"), returnOpponent.move());
        assertEquals(ResultBuilder.builder("ability-success")
                .setAbility(0)
                .setEffectiveness("effective")
                .create(), returnOpponent.results().get(0));
        assertEquals(0, returnOpponent.coins());

        // check mock
        verify(encounterApiService).getEncounterOpponent(any(), any(), any());
    }

    @Test
    void makeAbilityMove() {
        Opponent opponent = getDummyOpponents().get(0);

        //ability for attacker
        SortedMap<String, Integer> sortedMap = new TreeMap<>();
        sortedMap.put("Tackle", 10);

        // Create new monsters
        Monster attacker = MonsterBuilder.builder()
                .setId("0")
                .setAbilities(sortedMap)
                .create();
        Monster target = MonsterBuilder.builder()
                .setId("1")
                .create();

        //define mock
        when(encounterApiService.makeMove(any(), any(), any(), any()))
                .thenReturn(Observable.just(opponent));

        Encounter encounter = getDummyEncounters();
        when(encounterStorage.getEncounter()).thenReturn(encounter);

        when(sessionService.getOpponent(any())).thenReturn(opponent);

        when(sessionServiceProvider.get()).thenReturn(sessionService);


        //action
        final Opponent returnOpponent = encounterService
                .makeAbilityMove(null, attacker.abilities().get("Tackle"), target._id())
                .blockingFirst();

        //check value
        assertEquals("0", returnOpponent._id());
        assertEquals("encounter1Id", returnOpponent.encounter());
        assertEquals("trainer1Id", returnOpponent.trainer());
        assertEquals(false, returnOpponent.isAttacker());
        assertEquals(false, returnOpponent.isNPC());
        assertEquals("monster1Id", returnOpponent.monster());
        assertEquals(new AbilityMove("ability", 0, "targetId"), returnOpponent.move());
        assertEquals(ResultBuilder.builder("ability-success")
                .setAbility(0)
                .setEffectiveness("effective")
                .create(), returnOpponent.results().get(0));
        assertEquals(0, returnOpponent.coins());

        //check mock
        verify(encounterApiService).makeMove(any(), any(), any(), any());
    }

    @Test
    void makeChangeMonsterMove() {
        Opponent opponent = getDummyOpponents().get(1);

        // Create new monsters
        Monster nextMonster = MonsterBuilder.builder().setId("1").create();

        //define mock
        when(sessionServiceProvider.get()).thenReturn(sessionService);

        when(sessionService.getOpponent(any())).thenReturn(opponent);

        when(encounterApiService.makeMove(any(), any(), any(), any()))
                .thenReturn(Observable.just(opponent));

        Encounter encounter = getDummyEncounters();
        when(encounterStorage.getEncounter()).thenReturn(encounter);

        //action
        final Opponent returnOpponent = encounterService
                .makeChangeMonsterMove(null, nextMonster)
                .blockingFirst();

        //check value
        assertEquals("1", returnOpponent._id());
        assertEquals("encounter2Id", returnOpponent.encounter());
        assertEquals("trainer2Id", returnOpponent.trainer());
        assertEquals(true, returnOpponent.isAttacker());
        assertEquals(true, returnOpponent.isNPC());
        assertEquals("monster2Id", returnOpponent.monster());
        assertEquals(new ChangeMonsterMove("change-monster", "monster3Id"), returnOpponent.move());
        assertEquals(ResultBuilder.builder("monster-changed").create(), returnOpponent.results().get(0));
        assertEquals(1, returnOpponent.coins());

        //check mock
        verify(encounterApiService).makeMove(any(), any(), any(), any());
    }

    @Test
    void fleeEncounter() {
        Opponent opponent = getDummyOpponents().get(1);

        //define mock
        when(sessionServiceProvider.get()).thenReturn(sessionService);
        when(sessionService.getOpponent(any())).thenReturn(opponent);

        when(encounterApiService.fleeEncounter(any(), any(), any()))
                .thenReturn(Observable.just(opponent));

        Encounter encounter = getDummyEncounters();
        when(encounterStorage.getEncounter()).thenReturn(encounter);

        //action
        final Opponent returnOpponent = encounterService
                .fleeEncounter()
                .blockingFirst();

        //check values
        assertEquals("1", returnOpponent._id());
        assertEquals("encounter2Id", returnOpponent.encounter());
        assertEquals("trainer2Id", returnOpponent.trainer());
        assertEquals(true, returnOpponent.isAttacker());
        assertEquals(true, returnOpponent.isNPC());
        assertEquals("monster2Id", returnOpponent.monster());
        assertEquals(new ChangeMonsterMove("change-monster", "monster3Id"), returnOpponent.move());
        assertEquals(ResultBuilder.builder("monster-changed").create(), returnOpponent.results().get(0));
        assertEquals(1, returnOpponent.coins());

        //check mock
        verify(encounterApiService).fleeEncounter(any(), any(), any());
    }

}
