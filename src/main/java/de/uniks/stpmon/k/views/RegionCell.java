package de.uniks.stpmon.k.views;

import javax.inject.Provider;

import de.uniks.stpmon.k.controller.HybridController;
import de.uniks.stpmon.k.controller.RegionController;
import de.uniks.stpmon.k.dto.Region;
import javafx.scene.control.ListCell;

public class RegionCell extends ListCell<Region> {

    private final Provider<HybridController> hybridControllerProvider;

    public RegionCell(Provider<HybridController> hybridControllerProvider) {
        this.hybridControllerProvider = hybridControllerProvider;
    }

    @Override
    protected void updateItem(Region item, boolean empty) {
        super.updateItem(item, empty);
        if(empty || item == null) {
            setGraphic(null);
        } else {
            final RegionController regionController = new RegionController(item, hybridControllerProvider);
            setGraphic(regionController.render());
        }
    }
}
