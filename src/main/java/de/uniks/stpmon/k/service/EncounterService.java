package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.AbilityMove;
import de.uniks.stpmon.k.dto.ChangeMonsterMove;
import de.uniks.stpmon.k.dto.UpdateOpponentDto;
import de.uniks.stpmon.k.models.Encounter;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.rest.EncounterApiService;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
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
    EncounterStorage encounterStorage;

    @Inject
    public EncounterService() {
    }

    //---------------- Region Encounters ----------------------------

    public Observable<List<Encounter>> getEncounters() {
        return encounterApiService.getEncounters(regionStorage.getRegion()._id());
    }

    public Observable<Encounter> getEncounter() {
        return encounterApiService.getEncounter(
                regionStorage.getRegion()._id(),
                encounterStorage.getEncounter()._id()
        );
    }

    //---------------- Encounters Opponents ----------------------------

    public Observable<List<Opponent>> getTrainerOpponents(String trainerId) {
        return encounterApiService.getTrainerOpponents(regionStorage.getRegion()._id(), trainerId);
    }

    public Observable<List<Opponent>> getEncounterOpponents() {
        return encounterApiService.getEncounterOpponents(
                regionStorage.getRegion()._id(),
                encounterStorage.getEncounter()._id()
        );
    }

    public Observable<Opponent> getEncounterOpponent() {
        return encounterApiService.getEncounterOpponent(
                regionStorage.getRegion()._id(),
                encounterStorage.getEncounter()._id(),
                encounterStorage.getOpponentList().get(0)._id()
        );
    }

    public Observable<Opponent> makeAbilityMove(Monster attacker, int ability, Monster target) {
        UpdateOpponentDto dto = new UpdateOpponentDto(attacker._id(), new AbilityMove(
                Moves.ABILITY.toString(),
                ability,
                target._id())
        );
        return encounterApiService.makeMove(
                regionStorage.getRegion()._id(),
                encounterStorage.getEncounter()._id(),
                encounterStorage.getOpponentList().get(0)._id(),
                dto
        );
    }

    public Observable<Opponent> makeChangeMonsterMove(Monster currentMonster, Monster nextMonster) {
        UpdateOpponentDto dto = new UpdateOpponentDto(currentMonster._id(), new ChangeMonsterMove(
                Moves.CHANGE_MONSTER.toString(),
                nextMonster._id())
        );
        return encounterApiService.makeMove(
                regionStorage.getRegion()._id(),
                encounterStorage.getEncounter()._id(),
                encounterStorage.getOpponentList().get(0)._id(),
                dto
        );
    }

    public Observable<Opponent> fleeEncounter() {
        return encounterApiService.fleeEncounter(
                regionStorage.getRegion()._id(),
                encounterStorage.getEncounter()._id(),
                encounterStorage.getOpponentList().get(0)._id()
        );
    }

}
