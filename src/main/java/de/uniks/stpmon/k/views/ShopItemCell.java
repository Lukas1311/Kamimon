package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.inventory.ItemController;
import de.uniks.stpmon.k.controller.shop.ShopItemController;
import de.uniks.stpmon.k.dto.ItemTypeDto;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.PresetService;
import javafx.scene.control.ListCell;

public class ShopItemCell extends ListCell<Item> {
        private final IResourceService resourceService;
        private final PresetService presetService;

        public ShopItemCell(IResourceService resourceService, PresetService presetService) {
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
                setGraphic(itemController.render());
            }
        }
}
