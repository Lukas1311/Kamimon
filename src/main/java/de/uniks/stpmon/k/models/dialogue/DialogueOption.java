package de.uniks.stpmon.k.models.dialogue;

import java.util.Objects;

public class DialogueOption {
    private final String text;
    /**
     * Action performed on selection of the option
     */
    private final Runnable selection;
    /**
     * Action performed on activation of the option.
     */
    private final Runnable action;
    private DialogueItem parent;

    DialogueOption(String text, Runnable action, Runnable selection) {
        this.selection = selection;
        this.text = text;
        this.action = action;
    }

    public void setup(DialogueItem parent) {
        this.parent = parent;
    }

    public Runnable getAction() {
        return action;
    }

    public Runnable getSelection() {
        return selection;
    }

    public String getText() {
        return text;
    }

    public DialogueItem getParent() {
        return parent;
    }

}
