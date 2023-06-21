package de.uniks.stpmon.k.models.dialogue;

import java.util.Arrays;

public final class Dialogue {
    private final DialogueItem[] items;

    public static Dialogue create(DialogueItem... items) {
        Dialogue dialogue = new Dialogue(items);
        for (int i = 0; i < items.length - 1; i++) {
            items[i].setup(dialogue, items[i + 1]);
        }
        return dialogue;
    }

    private Dialogue(DialogueItem[] items) {
        this.items = items;
    }

    public DialogueItem[] getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "Dialogue[options=" + Arrays.toString(items) + ']';
    }

    public boolean isEmpty() {
        return getItems().length == 0;
    }
}
