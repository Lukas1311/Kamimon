package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.Encounter;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Opponent;

import javax.inject.Inject;
import java.util.List;

public class EncounterStorage {

    private Encounter encounter;
    private List<Opponent> opponentList;
    private List<Monster> monsterList;


    @Inject
    public EncounterStorage() {}

    public Encounter getEncounter() {
        return encounter;
    }

    public void setEncounter(Encounter encounter) {
        this.encounter = encounter;
    }

    public List<Opponent> getOpponentList() {
        //opponnentList.get(0) is representing the user
        return opponentList;
    }

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
