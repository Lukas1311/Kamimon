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
            protected void updateItem(String entry, boolean empty) {
                super.updateItem(entry, empty);

                if (empty || entry == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    final IngameSettingsEntryController entryController =
                            new IngameSettingsEntryController(ingameSettingsController, entry);
                    setGraphic(entryController.render());
                }
            }
        };
    }
}
