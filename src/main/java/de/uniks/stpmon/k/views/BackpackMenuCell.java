package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.BackpackMenuController;
import de.uniks.stpmon.k.controller.BackpackMenuEntryController;
import de.uniks.stpmon.k.controller.BackpackMenuOption;
import javafx.scene.control.ListCell;

public class BackpackMenuCell extends ListCell<BackpackMenuOption> {
    private final BackpackMenuController backpackMenuController;

    public BackpackMenuCell(BackpackMenuController backpackMenuController) {
        this.backpackMenuController = backpackMenuController;
    }

    @Override
    protected void updateItem(BackpackMenuOption entry, boolean empty) {
        super.updateItem(entry, empty);

        if (empty || entry == null) {
            setText(null);
            setGraphic(null);
        } else {
            final BackpackMenuEntryController entryController =
                    new BackpackMenuEntryController(backpackMenuController, entry);
            setGraphic(entryController.render());
            this.setOnMouseEntered(event -> {
                if (!this.isEmpty()) {
                    entryController.setArrow();
                }
            });

            this.setOnMouseExited(event -> {
                if (!this.isEmpty()) {
                    entryController.removeArrow();
                }
            });

        }


    }


}
