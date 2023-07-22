package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.inventory.InventoryController;
import de.uniks.stpmon.k.controller.inventory.ItemController;
import de.uniks.stpmon.k.models.Item;
import javafx.scene.control.ListCell;

public class ItemCell extends ListCell<Item> {

    private final InventoryController inventoryController;

    public ItemCell(InventoryController inventoryController) {
        this.inventoryController = inventoryController;
    }

    @Override
    protected void updateItem(Item item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            final ItemController itemController = new ItemController();
            setGraphic(itemController.render());
        }
    }
}
