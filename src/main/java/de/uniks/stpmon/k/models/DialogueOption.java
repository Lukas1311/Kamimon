package de.uniks.stpmon.k.models;

import java.util.Objects;

public class DialogueOption {
    private final String text;
    private final Runnable action;
    private DialogueOption next;
    private Dialogue dialogue;

    public DialogueOption(String text) {
        this(text, null);
    }

    public DialogueOption(Runnable action) {
        this("", action);
    }

    public DialogueOption(String text, Runnable action) {
        Objects.requireNonNull(text);
        this.text = text;
        this.action = action;
    }

    public void setup(Dialogue dialogue, DialogueOption next) {
        this.dialogue = dialogue;
        this.next = next;
    }

    public Runnable getAction() {
        return action;
    }

    public String getText() {
        return text;
    }

    public Dialogue getDialogue() {
        return dialogue;
    }

    public DialogueOption getNext() {
        return next;
    }
}
