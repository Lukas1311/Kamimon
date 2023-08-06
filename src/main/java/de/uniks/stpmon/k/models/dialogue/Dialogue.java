package de.uniks.stpmon.k.models.dialogue;

public final class Dialogue {
    public static final Dialogue EMPTY = new Dialogue("", "", new DialogueItem[0]);

    private final DialogueItem[] items;
    private final String trainerId;
    private final String trainerName;

    public static Dialogue create(String trainerId, String trainerName, DialogueItem... items) {
        Dialogue dialogue = new Dialogue(trainerId, trainerName, items);
        for (int i = 0; i < items.length - 1; i++) {
            items[i].setup(items[i + 1]);
        }
        return dialogue;
    }

    public static DialogueBuilder builder() {
        return new DialogueBuilder();
    }

    private Dialogue(String trainerId, String trainerName, DialogueItem[] items) {
        this.items = items;
        this.trainerId = trainerId;
        this.trainerName = trainerName;
    }

    public DialogueItem[] getItems() {
        return items;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public boolean isEmpty() {
        return getItems().length == 0;
    }

}
