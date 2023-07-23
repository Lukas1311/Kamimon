package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.monDex.MonDexController;
import de.uniks.stpmon.k.controller.monDex.MonDexEntryController;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.service.ResourceService;
import de.uniks.stpmon.k.service.TrainerService;
import javafx.scene.control.ListCell;

import javax.inject.Provider;

public class DexCell extends ListCell<MonsterTypeDto> {

    private final MonDexController monDexController;
    private final Provider<ResourceService> resServiceProvider;
    private final Provider<TrainerService> trainerServiceProvider;

    public DexCell(MonDexController monDexController, Provider<ResourceService> resourceServiceProvider,
                   Provider<TrainerService> trainerServiceProvider) {
        this.monDexController = monDexController;
        this.resServiceProvider = resourceServiceProvider;
        this.trainerServiceProvider = trainerServiceProvider;
    }

    @Override
    protected void updateItem(MonsterTypeDto entry, boolean empty) {
        super.updateItem(entry, empty);
        if (empty || entry == null) {
            setText(null);
            setGraphic(null);
        } else {
            final MonDexEntryController entryController =
                    new MonDexEntryController(resServiceProvider, entry, trainerServiceProvider);

            this.setOnMouseClicked(e -> monDexController.triggerDetail(entry));
            setGraphic(entryController.render());
        }
    }
}
