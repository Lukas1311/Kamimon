package de.uniks.stpmon.k.models.dialogue;

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
    private final Dialogue nextDialogue;

    DialogueOption(String text, Runnable action, Runnable selection, Dialogue nextDialogue) {
        this.selection = selection;
        this.text = text;
        this.action = action;
        this.nextDialogue = nextDialogue;
    }

    public Runnable getAction() {
        return action;
    }

    public Runnable getSelection() {
        return selection;
    }

    public Dialogue getNext() {
        return nextDialogue;
    }

    public String getText() {
        return text;
    }

    public boolean hasNext() {
        return nextDialogue != null;
    }

}
