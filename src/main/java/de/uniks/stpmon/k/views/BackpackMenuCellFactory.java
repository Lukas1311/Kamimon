package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.BackpackMenuController;
import de.uniks.stpmon.k.controller.BackpackMenuEntryController;
import de.uniks.stpmon.k.controller.BackpackMenuOption;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class BackpackMenuCellFactory implements Callback<ListView<BackpackMenuOption>, ListCell<BackpackMenuOption>> {

    private final BackpackMenuController backpackMenuController;

    public BackpackMenuCellFactory(BackpackMenuController backpackMenuController) {
        this.backpackMenuController = backpackMenuController;
    }

    @Override
    public ListCell<BackpackMenuOption> call(ListView<BackpackMenuOption> param) {
        return new ListCell<>() {
            @Override
            protected void updateItem(BackpackMenuOption entry, boolean empty) {
                super.updateItem(entry, empty);
                final BackpackMenuEntryController entryController =
                        new BackpackMenuEntryController(backpackMenuController, entry);
                if (empty || entry == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setGraphic(entryController.render());
                }

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
        };
    }


}
