package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.IngameSettingsController;
import de.uniks.stpmon.k.controller.IngameSettingsEntryController;
import de.uniks.stpmon.k.controller.IngameSettingsOption;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class IngameSettingsCellFactory implements Callback<ListView<IngameSettingsOption>, ListCell<IngameSettingsOption>> {

    private final IngameSettingsController ingameSettingsController;

    public IngameSettingsCellFactory(IngameSettingsController ingameSettingsController) {
        this.ingameSettingsController = ingameSettingsController;
    }

    @Override
    public ListCell<IngameSettingsOption> call(ListView<IngameSettingsOption> param) {
        return new ListCell<>() {
            @Override
            protected void updateItem(IngameSettingsOption entry, boolean empty) {
                super.updateItem(entry, empty);
                final IngameSettingsEntryController entryController =
                        new IngameSettingsEntryController(ingameSettingsController, entry);
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
