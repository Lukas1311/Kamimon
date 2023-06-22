package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.Encounter;

import javax.inject.Inject;

public class EncounterStorage {

    private Encounter encounter;

    @Inject
    public EncounterStorage() {}

    public Encounter getEncounter() {
        return encounter;
    }

    public void setEncounter(Encounter encounter) {
        this.encounter = encounter;
    }
}
