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


public class ItemController extends Controller {

    @FXML
    public ImageView itemView;
    @FXML
    public Text itemName;
    @FXML
    public Text itemAmount;

    private final IResourceService resourceService;
    private final PresetService presetService;

    private Item item;

    public ItemController(Item item, IResourceService resourceService, PresetService presetService) {
        this.item = item;
        this.resourceService = resourceService;
        this.presetService = presetService;
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        subscribe(resourceService.getItemImage(String.valueOf(item.type())), imageUrl ->
                subscribe(presetService.getItem(item.type()), item1 -> {
                    //image
                    Image itemImage = ImageUtils.scaledImageFX(imageUrl, 3.0);
                    itemView.setImage(itemImage);

                    //text
                    itemName.setText(item1.name());
                    parent.setId("item_" + item1.name());
                    if (item.amount() != -1) {
                        itemAmount.setText(" x " + item.amount());
                    }
                }));

        return parent;
    }

    @Override
    public void destroy() {
        super.destroy();
        itemAmount = null;
        itemName = null;
        itemView = null;
        item = null;
    }

    @Override
    public String getResourcePath() {
        return "inventory/";
    }
}
