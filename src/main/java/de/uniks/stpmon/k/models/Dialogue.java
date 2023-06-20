package de.uniks.stpmon.k.models;

public final class Dialogue {
    private final DialogueOption[] options;

    public static Dialogue create(DialogueOption... options) {
        Dialogue dialogue = new Dialogue(options);
        for (int i = 0; i < options.length - 1; i++) {
            options[i].setup(dialogue, options[i + 1]);
        }
        return dialogue;
    }

    private Dialogue(DialogueOption[] options) {
        this.options = options;
    }

    public DialogueOption[] options() {
        return options;
    }

    @Override
    public String toString() {
        return "Dialogue[" +
                "options=" + options + ']';
    }

}
