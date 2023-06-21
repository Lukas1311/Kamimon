package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.AbilityMove;
import de.uniks.stpmon.k.dto.ChangeMonsterMove;
import de.uniks.stpmon.k.models.Encounter;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.models.Result;
import de.uniks.stpmon.k.rest.EncounterApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EncounterServiceTest {
    @Mock
    EncounterApiService encounterApiService;
    @InjectMocks
    EncounterService encounterService;

    private Encounter getDummyEncounters() {
        return new Encounter(
                "0",
                "regionId",
                false);
    }

    private List<Opponent> getDummyOpponents() {
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
                null,
                new Result(
                        "ability-success",
                        0,
                        "effective"
                ),
                0);
        Opponent opponent2 = new Opponent(
                "1",
                "encounter2Id",
                "trainer2Id",
                true,
                true,
                "monster2Id",
                null,
                new ChangeMonsterMove(
                        "change-monster",
                        "monster3Id"
                ),
                new Result(
                        "monster-changed",
                        null,
                        null
                ),
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
                .getEncounters("regionId")
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

        //action
        final Encounter returnEncounter = encounterService
                .getEncounter("regionId", "id")
                .blockingFirst();

        //check values
        assertEquals("0", returnEncounter._id());
        assertEquals("regionId", returnEncounter.region());
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
                .getTrainerOpponents("regionId", "trainerId")
                .blockingFirst();

        //check values
        assertEquals(1, returnOpponents.size());
        assertEquals("0", returnOpponents.get(0)._id());
        assertEquals("encounter1Id", returnOpponents.get(0).encounter());
        assertEquals("trainer1Id", returnOpponents.get(0).trainer());
        assertEquals(false, returnOpponents.get(0).isAttacker());
        assertEquals(false, returnOpponents.get(0).isNPC());
        assertEquals("monster1Id", returnOpponents.get(0).monster());
        assertEquals(new AbilityMove("ability", 0, "targetId"), returnOpponents.get(0).abilityMove());
        assertNull(returnOpponents.get(0).changeMonsterMove());
        assertEquals(new Result("ability-success", 0, "effective"), returnOpponents.get(0).result());
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

        //action
        final List<Opponent> returnOpponents = encounterService
                .getEncounterOpponents("regionId", "encounterId")
                .blockingFirst();

        //check values
        assertEquals(1, returnOpponents.size());
        assertEquals("1", returnOpponents.get(0)._id());
        assertEquals("encounter2Id", returnOpponents.get(0).encounter());
        assertEquals("trainer2Id", returnOpponents.get(0).trainer());
        assertEquals(true, returnOpponents.get(0).isAttacker());
        assertEquals(true, returnOpponents.get(0).isNPC());
        assertEquals("monster2Id", returnOpponents.get(0).monster());
        assertNull(returnOpponents.get(0).abilityMove());
        assertEquals(new ChangeMonsterMove("change-monster", "monster3Id"), returnOpponents.get(0).changeMonsterMove());
        assertEquals(new Result("monster-changed", null, null), returnOpponents.get(0).result());
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

        //action
        final Opponent returnOpponent = encounterService
                .getEncounterOpponent("regionId", "encounterId", "id")
                .blockingFirst();

        //check value
        assertEquals("0", returnOpponent._id());
        assertEquals("encounter1Id", returnOpponent.encounter());
        assertEquals("trainer1Id", returnOpponent.trainer());
        assertEquals(false, returnOpponent.isAttacker());
        assertEquals(false, returnOpponent.isNPC());
        assertEquals("monster1Id", returnOpponent.monster());
        assertEquals(new AbilityMove("ability", 0, "targetId"), returnOpponent.abilityMove());
        assertNull(returnOpponent.changeMonsterMove());
        assertEquals(new Result("ability-success", 0, "effective"), returnOpponent.result());
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
        Monster attacker = new Monster("0", null, null, null, null, sortedMap, null, null);
        Monster target = new Monster("1", null, null, null, null, null, null, null);

        //define mock
        when(encounterApiService.makeMove(any(), any(), any(), any()))
                .thenReturn(Observable.just(opponent));

        //action
        final Opponent returnOpponent = encounterService
                .makeAbilityMove("regionId", "encounterId", "id", attacker, attacker.abilities().get("Tackle"), target)
                .blockingFirst();

        //check value
        assertEquals("0", returnOpponent._id());
        assertEquals("encounter1Id", returnOpponent.encounter());
        assertEquals("trainer1Id", returnOpponent.trainer());
        assertEquals(false, returnOpponent.isAttacker());
        assertEquals(false, returnOpponent.isNPC());
        assertEquals("monster1Id", returnOpponent.monster());
        assertEquals(new AbilityMove("ability", 0, "targetId"), returnOpponent.abilityMove());
        assertNull(returnOpponent.changeMonsterMove());
        assertEquals(new Result("ability-success", 0, "effective"), returnOpponent.result());
        assertEquals(0, returnOpponent.coins());

        //check mock
        verify(encounterApiService).makeMove(any(), any(), any(), any());
    }

    @Test
    void makeChangeMonsterMove() {
        Opponent opponent = getDummyOpponents().get(1);

        // Create new monsters
        Monster attacker = new Monster("0", null, null, null, null, null, null, null);
        Monster nextMonster = new Monster("1", null, null, null, null, null, null, null);

        //define mock
        when(encounterApiService.makeMove(any(), any(), any(), any()))
                .thenReturn(Observable.just(opponent));

        //action
        final Opponent returnOpponent = encounterService
                .makeChangeMonsterMove("regionId", "encounterId", "id", attacker, nextMonster)
                .blockingFirst();

        //check value
        assertEquals("1", returnOpponent._id());
        assertEquals("encounter2Id", returnOpponent.encounter());
        assertEquals("trainer2Id", returnOpponent.trainer());
        assertEquals(true, returnOpponent.isAttacker());
        assertEquals(true, returnOpponent.isNPC());
        assertEquals("monster2Id", returnOpponent.monster());
        assertNull(returnOpponent.abilityMove());
        assertEquals(new ChangeMonsterMove("change-monster", "monster3Id"), returnOpponent.changeMonsterMove());
        assertEquals(new Result("monster-changed", null, null), returnOpponent.result());
        assertEquals(1, returnOpponent.coins());

        //check mock
        verify(encounterApiService).makeMove(any(), any(), any(), any());
    }

    @Test
    void fleeEncounter() {
        Opponent opponent = getDummyOpponents().get(1);

        //define mock
        when(encounterApiService.fleeEncounter(any(), any(), any()))
                .thenReturn(Observable.just(opponent));

        //action
        final Opponent returnOpponent = encounterService
                .fleeEncounter("regionId", "encounterId", "id")
                .blockingFirst();

        //check values
        assertEquals("1", returnOpponent._id());
        assertEquals("encounter2Id", returnOpponent.encounter());
        assertEquals("trainer2Id", returnOpponent.trainer());
        assertEquals(true, returnOpponent.isAttacker());
        assertEquals(true, returnOpponent.isNPC());
        assertEquals("monster2Id", returnOpponent.monster());
        assertNull(returnOpponent.abilityMove());
        assertEquals(new ChangeMonsterMove("change-monster", "monster3Id"), returnOpponent.changeMonsterMove());
        assertEquals(new Result("monster-changed", null, null), returnOpponent.result());
        assertEquals(1, returnOpponent.coins());

        //check mock
        verify(encounterApiService).fleeEncounter(any(), any(), any());
    }
}
