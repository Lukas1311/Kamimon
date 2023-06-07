package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.IngameSettingsController;
import de.uniks.stpmon.k.controller.IngameSettingsEntryController;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class IngameSettingsCellFactory implements Callback<ListView<String>, ListCell<String>> {

    private final IngameSettingsController ingameSettingsController;

    public IngameSettingsCellFactory(IngameSettingsController ingameSettingsController) {
        this.ingameSettingsController = ingameSettingsController;
    }

    @Override
    public ListCell<String> call(ListView<String> param) {
        return new ListCell<>() {
            @Override
            protected void updateItem(String entryText, boolean empty) {
                super.updateItem(entryText, empty);
                final IngameSettingsEntryController entryController =
                        new IngameSettingsEntryController(ingameSettingsController, entryText);
                if (empty || entryText == null) {
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
