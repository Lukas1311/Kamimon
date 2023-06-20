package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.Dialogue;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class InteractionStorage {
    private Dialogue dialogue = null;

    @Inject
    public InteractionStorage() {
    }

    public void setDialogue(Dialogue dialogue) {
        this.dialogue = dialogue;
    }

    public Dialogue getDialogue() {
        return dialogue;
    }
}
