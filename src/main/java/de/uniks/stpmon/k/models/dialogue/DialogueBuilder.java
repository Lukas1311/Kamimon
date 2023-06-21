package de.uniks.stpmon.k.models.dialogue;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class DialogueBuilder {

    public static DialogueBuilder create() {
        return new DialogueBuilder();
    }

    private final List<DialogueItem> items = new LinkedList<>();

    private DialogueBuilder() {
    }

    private void add(DialogueItem item) {
        items.add(item);
    }

    public ItemBuilder addItem() {
        return new ItemBuilder(this);
    }

    public Dialogue build() {
        return Dialogue.create(items.toArray(new DialogueItem[0]));
    }

    private static class ItemBuilder {
        private final DialogueBuilder parent;
        private final List<DialogueOption> options = new LinkedList<>();
        private String text = "";
        /**
         * Action performed on activation of the option.
         */
        private Runnable action;

        public ItemBuilder(DialogueBuilder parent) {
            this.parent = parent;
        }

        public ItemBuilder addAction(Runnable action) {
            this.action = action;
            return this;
        }

        public ItemBuilder setText(String text) {
            Objects.requireNonNull(text, "Text muss not be null!");
            this.text = text;
            return this;
        }

        public OptionBuilder addOption() {
            return new OptionBuilder(this);
        }

        public DialogueBuilder build() {
            parent.add(new DialogueItem(text, action, options.toArray(new DialogueOption[0])));
            return parent;
        }

        void add(DialogueOption option) {
            options.add(option);
        }
    }

    private static class OptionBuilder {
        private final ItemBuilder parent;

        private String text = "";
        /**
         * Action performed on selection of the option
         */
        private Runnable selection;
        /**
         * Action performed on activation of the option.
         */
        private Runnable action;

        public OptionBuilder(ItemBuilder parent) {
            this.parent = parent;
        }

        public OptionBuilder addAction(Runnable action) {
            this.action = action;
            return this;
        }

        public OptionBuilder addSelection(Runnable selection) {
            this.selection = selection;
            return this;
        }

        public OptionBuilder setText(String text) {
            Objects.requireNonNull(text, "Text muss not be null!");
            this.text = text;
            return this;
        }

        public ItemBuilder create() {
            parent.add(new DialogueOption(text, action, selection));
            return parent;
        }
    }
}
