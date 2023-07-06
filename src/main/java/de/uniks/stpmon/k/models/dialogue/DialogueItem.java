package de.uniks.stpmon.k.models.dialogue;

import java.util.Objects;

public class DialogueItem {

    private final String text;
    private final Runnable action;
    private final DialogueOption[] options;
    private DialogueItem next;
    private Dialogue parent;

    DialogueItem(String text, Runnable action, DialogueOption[] options) {
        Objects.requireNonNull(text);
        this.text = text;
        this.action = action;
        this.options = options;
    }

    public void setup(Dialogue parent, DialogueItem next) {
        this.parent = parent;
        this.next = next;
    }

    public DialogueOption[] getOptions() {
        return options;
    }

    public Runnable getAction() {
        return action;
    }

    public String getText() {
        return text;
    }

    public DialogueItem getNext() {
        return next;
    }

}
