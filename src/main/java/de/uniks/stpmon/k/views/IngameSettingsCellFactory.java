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

                if (empty || entryText == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    final IngameSettingsEntryController entryController =
                            new IngameSettingsEntryController(ingameSettingsController, entryText);
                    setGraphic(entryController.render());

                }

                ListView<String> listView = getListView();
                if (listView != null && isSelected()) {
                    // Set the style or update the text for the selected cell
                    setText("Selected: " + getText());
                }
            }
        };
    }


}
