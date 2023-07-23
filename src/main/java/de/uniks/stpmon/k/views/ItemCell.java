package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.inventory.ItemController;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.ResourceService;
import javafx.scene.control.ListCell;

import javax.inject.Provider;

public class ItemCell extends ListCell<Item> {

    private final Provider<ResourceService> resourceServiceProvider;
    private final PresetService presetService;

    public ItemCell(Provider<ResourceService> resourceServiceProvider, PresetService presetService) {
        this.resourceServiceProvider = resourceServiceProvider;
        this.presetService = presetService;
    }

    @Override
    protected void updateItem(Item item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            final ItemController itemController = new ItemController(item, resourceServiceProvider, presetService);
            setGraphic(itemController.render());
        }
    }
}
