package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.Encounter;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.service.storage.cache.SingleCache;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class EncounterStorage extends SingleCache<Encounter> {

    private EncounterSession encounterSession;
    private List<Opponent> opponentList;
    private List<Monster> monsterList;

    @Inject
    public EncounterStorage() {
    }

    public Encounter getEncounter() {
        return asNullable();
    }

    public void setEncounter(Encounter encounter) {
        setValue(encounter);
    }

    public void setEncounterSession(EncounterSession encounterSession) {
        this.encounterSession = encounterSession;
    }

    public EncounterSession getEncounterSession() {
        return encounterSession;
    }

    public List<Opponent> getOpponentList() {
        return opponentList;
    }

    /**
     * @param opponentList: is the list of all opponents, the user trainer should always be the first element
     */
    public void setOpponentList(List<Opponent> opponentList) {
        this.opponentList = opponentList;
    }

    public void setMonsterList(List<Monster> monsterList) {
        this.monsterList = monsterList;
    }

    public List<Monster> getMonsterList() {
        return monsterList;
    }

}
