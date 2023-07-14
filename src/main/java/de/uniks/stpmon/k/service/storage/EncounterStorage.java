package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.Encounter;
import de.uniks.stpmon.k.service.storage.cache.SingleCache;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EncounterStorage extends SingleCache<Encounter> {

    private EncounterSession encounterSession;

    @Inject
    public EncounterStorage() {
    }

    public Encounter getEncounter() {
        return asNullable();
    }

    public void setEncounter(Encounter encounter) {
        setValue(encounter);
    }

    public void setSession(EncounterSession encounterSession) {
        this.encounterSession = encounterSession;
    }

    public EncounterSession getSession() {
        return encounterSession;
    }
}
