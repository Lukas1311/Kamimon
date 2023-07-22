package de.uniks.stpmon.k.controller.inventory;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import javax.inject.Inject;

public class ItemController extends Controller {

    @FXML
    public ImageView itemView;
    @FXML
    public Text itemName;
    @FXML
    public Text itemAmount;

    private final IResourceService resourceService;
    private final PresetService presetService;

    private final InventoryController inventoryController;
    private final Item item;

    public ItemController(Item item, InventoryController inventoryController, IResourceService resourceService, PresetService presetService) {
        this.item = item;
        this.inventoryController = inventoryController;
        this.resourceService = resourceService;
        this.presetService = presetService;
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        subscribe(resourceService.getItemImage(item._id()), imageUrl -> {
            subscribe(presetService.getItem(item._id()), item1 -> {
                //image
                Image itemImage = ImageUtils.scaledImageFX(imageUrl, 2.0);
                itemView.setImage(itemImage);

                //text
                itemName.setText(item1.name());
                itemAmount.setText(" x " + item.amount());
            });

        });

       return parent;
    }

    @Override
    public String getResourcePath() {
        return "inventory/";
    }
}
