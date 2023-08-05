package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.inventory.InventoryController;
import de.uniks.stpmon.k.controller.inventory.ItemController;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.PresetService;
import javafx.scene.control.ListCell;

public class ItemCell extends ListCell<Item> {
    private final InventoryController inventoryController;
    private final IResourceService resourceService;
    private final PresetService presetService;

    public ItemCell(InventoryController inventoryController, IResourceService resourceService, PresetService presetService) {
        this.inventoryController = inventoryController;
        this.resourceService = resourceService;
        this.presetService = presetService;
    }

    @Override
    protected void updateItem(Item item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            final ItemController itemController = new ItemController(item, resourceService, presetService);
            if (inventoryController != null) {
                this.setOnMouseClicked(e -> inventoryController.triggerDetail(item));
            }
            setGraphic(itemController.render());
        }
    }
}
