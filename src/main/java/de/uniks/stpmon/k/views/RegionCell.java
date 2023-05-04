package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.RegionController;
import de.uniks.stpmon.k.dto.Region;
import javafx.scene.control.ListCell;

public class RegionCell extends ListCell<Region> {

    private final App app;

    public RegionCell(App app) {
        this.app = app;
    }

    @Override
    protected void updateItem(Region item, boolean empty) {
        super.updateItem(item, empty);
        if(empty || item == null) {
            setGraphic(null);
        } else {
            final RegionController regionController = new RegionController(item, app);
            setGraphic(regionController.render());
        }
    }
}
