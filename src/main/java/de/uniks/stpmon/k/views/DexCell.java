package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.monDex.MonDexController;
import de.uniks.stpmon.k.controller.monDex.MonDexEntryController;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.service.ResourceService;
import javafx.scene.control.ListCell;

import javax.inject.Provider;

public class DexCell extends ListCell<MonsterTypeDto> {

    private final MonDexController monDexController;
    private final Provider<ResourceService> resServiceProvider;

    public DexCell(MonDexController monDexController, Provider<ResourceService> resourceServiceProvider) {
        this.monDexController = monDexController;
        this.resServiceProvider = resourceServiceProvider;
    }

    @Override
    protected void updateItem(MonsterTypeDto entry, boolean empty) {
        super.updateItem(entry, empty);
        if (empty || entry == null) {
            setText(null);
            setGraphic(null);
        } else {
            final MonDexEntryController entryController = new MonDexEntryController(monDexController, resServiceProvider, entry);
            setGraphic(entryController.render());
        }
    }
}
