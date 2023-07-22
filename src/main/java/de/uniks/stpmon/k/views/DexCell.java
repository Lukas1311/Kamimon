package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.MonDexController;
import de.uniks.stpmon.k.controller.MonDexEntryController;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import javafx.scene.control.ListCell;

public class DexCell extends ListCell<MonsterTypeDto> {

    private final MonDexController monDexController;

    public DexCell(MonDexController monDexController) {
        this.monDexController = monDexController;
    }

    @Override
    protected void updateItem(MonsterTypeDto entry, boolean empty) {
        super.updateItem(entry, empty);
        if (empty || entry == null) {
            setText(null);
            setGraphic(null);
        } else {
            final MonDexEntryController entryController = new MonDexEntryController(monDexController, entry);
            setGraphic(entryController.render());
        }
    }
}
