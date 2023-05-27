package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.RegionController;
import de.uniks.stpmon.k.controller.RegionListController;
import de.uniks.stpmon.k.models.Region;
import javafx.scene.control.ListCell;

public class RegionCell extends ListCell<Region> {

    private final RegionListController listController;

    public RegionCell(RegionListController listController) {
        this.listController = listController;
    }

    @Override
    protected void updateItem(Region item, boolean empty) {
        super.updateItem(item, empty);
        setStyle("-fx-background-color: transparent;");
        if (empty || item == null) {
            setGraphic(null);
        } else {
            final RegionController regionController = new RegionController(item, listController);
            setGraphic(regionController.render());
        }
    }
}
