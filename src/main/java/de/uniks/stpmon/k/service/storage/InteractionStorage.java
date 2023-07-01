package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.dialogue.Dialogue;
import de.uniks.stpmon.k.service.storage.cache.SingleCache;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class InteractionStorage {

    private final SingleCache<String> selectedStarter = new SingleCache<>();
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

    public SingleCache<String> selectedStarter() {
        return selectedStarter;
    }

}
