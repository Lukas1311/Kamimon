package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.AbilityMove;
import de.uniks.stpmon.k.dto.ChangeMonsterMove;
import de.uniks.stpmon.k.dto.UpdateOpponentDto;
import de.uniks.stpmon.k.models.Encounter;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.rest.EncounterApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class EncounterService {

    public enum Moves {

        CHANGE_MONSTER("change-monster"),
        ABILITY("ability");

        private final String move;

        Moves(final String move) {
            this.move = move;
        }

        @Override
        public String toString() {
            return move;
        }
    }

    @Inject
    EncounterApiService encounterApiService;

    @Inject
    public EncounterService() {
    }

    //---------------- Region Encounters ----------------------------

    public Observable<List<Encounter>> getEncounters(String regionId) {
        return encounterApiService.getEncounters(regionId);
    }

    public Observable<Encounter> getEncounter(String regionId, String encounterId) {
        return encounterApiService.getEncounter(regionId, encounterId);
    }

    //---------------- Encounters Opponents ----------------------------

    public Observable<List<Opponent>> getTrainerOpponents(String regionId, String trainerId) {
        return encounterApiService.getTrainerOpponents(regionId, trainerId);
    }

    public Observable<List<Opponent>> getEncounterOpponents(String regionId, String encounterId) {
        return encounterApiService.getEncounterOpponents(regionId, encounterId);
    }

    public Observable<Opponent> getEncounterOpponent(String regionId, String encounterId, String id) {
        return encounterApiService.getEncounterOpponent(regionId, encounterId, id);
    }

    public Observable<Opponent> makeAbilityMove(String regionId, String encounterId, String id, Monster attacker, int ability, Monster target) {
        UpdateOpponentDto dto = new UpdateOpponentDto(attacker._id(), new AbilityMove(Moves.ABILITY.toString(), ability , target._id()),null);
        return encounterApiService.makeMove(regionId, encounterId, id, dto);
    }

    public Observable<Opponent> makeChangeMonsterMove(String regionId, String encounterId, String id, Monster currentMonster, Monster nextMonster) {
        UpdateOpponentDto dto = new UpdateOpponentDto(currentMonster._id(), null, new ChangeMonsterMove(Moves.CHANGE_MONSTER.toString(), nextMonster._id()));
        return encounterApiService.makeMove(regionId, encounterId, id, dto);
    }

    public Observable<Opponent> fleeEncounter(String regionId, String encounterId, String id) {
        return encounterApiService.fleeEncounter(regionId, encounterId, id);
    }
}
