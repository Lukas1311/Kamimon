package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.AbilityMove;
import de.uniks.stpmon.k.dto.ChangeMonsterMove;
import de.uniks.stpmon.k.dto.UpdateOpponentDto;
import de.uniks.stpmon.k.models.Encounter;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.rest.EncounterApiService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
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
    RegionStorage regionStorage;
    
    @Inject
    public EncounterService() {
    }

    //---------------- Region Encounters ----------------------------

    public Observable<List<Encounter>> getEncounters() {
        return encounterApiService.getEncounters(regionStorage.getRegion()._id());
    }

    public Observable<Encounter> getEncounter(String encounterId) {
        return encounterApiService.getEncounter(regionStorage.getRegion()._id(), encounterId);
    }

    //---------------- Encounters Opponents ----------------------------

    public Observable<List<Opponent>> getTrainerOpponents(String trainerId) {
        return encounterApiService.getTrainerOpponents(regionStorage.getRegion()._id(), trainerId);
    }

    public Observable<List<Opponent>> getEncounterOpponents(String encounterId) {
        return encounterApiService.getEncounterOpponents(regionStorage.getRegion()._id(), encounterId);
    }

    public Observable<Opponent> getEncounterOpponent(String encounterId, String id) {
        return encounterApiService.getEncounterOpponent(regionStorage.getRegion()._id(), encounterId, id);
    }

    public Observable<Opponent> makeAbilityMove(String encounterId, String id, Monster attacker, int ability, Monster target) {
        UpdateOpponentDto dto = new UpdateOpponentDto(attacker._id(), new AbilityMove(Moves.ABILITY.toString(), ability , target._id()));
        return encounterApiService.makeMove(regionStorage.getRegion()._id(), encounterId, id, dto);
    }

    public Observable<Opponent> makeChangeMonsterMove(String encounterId, String id, Monster currentMonster, Monster nextMonster) {
        UpdateOpponentDto dto = new UpdateOpponentDto(currentMonster._id(), new ChangeMonsterMove(Moves.CHANGE_MONSTER.toString(), nextMonster._id()));
        return encounterApiService.makeMove(regionStorage.getRegion()._id(), encounterId, id, dto);
    }

    public Observable<Opponent> fleeEncounter(String encounterId, String id) {
        return encounterApiService.fleeEncounter(regionStorage.getRegion()._id(), encounterId, id);
    }
}
