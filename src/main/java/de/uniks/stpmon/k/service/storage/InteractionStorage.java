package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.Dialogue;
import de.uniks.stpmon.k.models.DialogueOption;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class InteractionStorage {
    private Dialogue dialogue = Dialogue.create(new DialogueOption("Hello Sven"),
            new DialogueOption("How are you doing?"));

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
