package de.uniks.stpmon.k.models.dialogue;

public final class Dialogue {

    private final DialogueItem[] items;
    private final String trainerId;

    public static Dialogue create(String trainerId, DialogueItem... items) {
        Dialogue dialogue = new Dialogue(trainerId, items);
        for (int i = 0; i < items.length - 1; i++) {
            items[i].setup(dialogue, items[i + 1]);
        }
        return dialogue;
    }

    public static DialogueBuilder builder() {
        return new DialogueBuilder();
    }

    private Dialogue(String trainerId, DialogueItem[] items) {
        this.items = items;
        this.trainerId = trainerId;
    }

    public DialogueItem[] getItems() {
        return items;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public boolean isEmpty() {
        return getItems().length == 0;
    }

}
