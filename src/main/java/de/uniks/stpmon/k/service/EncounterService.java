package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.AbilityMove;
import de.uniks.stpmon.k.dto.ChangeMonsterMove;
import de.uniks.stpmon.k.dto.UpdateOpponentDto;
import de.uniks.stpmon.k.dto.UseItemMove;
import de.uniks.stpmon.k.models.Encounter;
import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.rest.EncounterApiService;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;

public class EncounterService {

    public enum Moves {

        CHANGE_MONSTER("change-monster"),
        ABILITY("ability"),
        USE_ITEM("use-item");

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
    Provider<SessionService> sessionServiceProvider;
    @Inject
    EncounterStorage encounterStorage;

    @Inject
    public EncounterService() {
    }


    //---------------- Region Encounters ----------------------------

    public Observable<List<Encounter>> getEncounters() {
        return encounterApiService.getEncounters(regionStorage.getRegion()._id());
    }


    public Observable<Encounter> getEncounter(String encounterId) {
        return encounterApiService.getEncounter(
                regionStorage.getRegion()._id(),
                encounterId
        );
    }

    public Observable<Encounter> getCurrentEncounter() {
        return getEncounter(
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

    public Observable<List<Opponent>> getEncounterOpponents(String encounterId) {
        return encounterApiService.getEncounterOpponents(
                regionStorage.getRegion()._id(),
                encounterId
        );
    }

    public Observable<Opponent> getEncounterOpponent() {
        return encounterApiService.getEncounterOpponent(
                regionStorage.getRegion()._id(),
                encounterStorage.getEncounter()._id(),
                sessionServiceProvider.get().getOpponent(EncounterSlot.PARTY_FIRST)._id()
        );
    }

    public Observable<Opponent> makeAbilityMove(EncounterSlot slot, int abilityId, String targetId) {
        UpdateOpponentDto dto = new UpdateOpponentDto(null, new AbilityMove(
                Moves.ABILITY.toString(),
                abilityId,
                targetId)
        );

        if (sessionServiceProvider.get().hasNoEncounter()) {
            throw new IllegalStateException("There is no encounter o_O");
        }

        return encounterApiService.makeMove(
                regionStorage.getRegion()._id(),
                encounterStorage.getEncounter()._id(),
                sessionServiceProvider.get().getOpponent(slot)._id(),
                dto
        );
    }

    public Observable<Opponent> makeChangeMonsterMove(EncounterSlot slot, Monster nextMonster) {
        UpdateOpponentDto dto = new UpdateOpponentDto(null, new ChangeMonsterMove(
                Moves.CHANGE_MONSTER.toString(),
                nextMonster._id())
        );
        return encounterApiService.makeMove(
                regionStorage.getRegion()._id(),
                encounterStorage.getEncounter()._id(),
                sessionServiceProvider.get().getOpponent(slot)._id(),
                dto
        );
    }

    public Observable<Opponent> makeItemMove(EncounterSlot slot, int itemId, String monsterId) {
        UpdateOpponentDto dto = new UpdateOpponentDto(null, new UseItemMove(
                Moves.USE_ITEM.toString(),
                itemId,
                monsterId)
        );

        if (sessionServiceProvider.get().hasNoEncounter()) {
            throw new IllegalStateException("There is no encounter o_O");
        }

        return encounterApiService.makeMove(
                regionStorage.getRegion()._id(),
                encounterStorage.getEncounter()._id(),
                sessionServiceProvider.get().getOpponent(slot)._id(),
                dto
        );
    }

    public Observable<Opponent> changeDeadMonster(EncounterSlot slot, Monster nextMonster) {
        UpdateOpponentDto dto = new UpdateOpponentDto(nextMonster._id(), null);

        return encounterApiService.makeMove(
                regionStorage.getRegion()._id(),
                encounterStorage.getEncounter()._id(),
                sessionServiceProvider.get().getOpponent(slot)._id(),
                dto
        );
    }

    public Observable<Opponent> fleeEncounter() {
        return encounterApiService.fleeEncounter(
                regionStorage.getRegion()._id(),
                encounterStorage.getEncounter()._id(),
                sessionServiceProvider.get().getOpponent(EncounterSlot.PARTY_FIRST)._id()
        );
    }

}
