package de.uniks.stpmon.k.controller.inventory;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.ResourceService;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import javax.inject.Provider;


public class ItemController extends Controller {

    @FXML
    public ImageView itemView;
    @FXML
    public Text itemName;
    @FXML
    public Text itemAmount;

    private final Provider<ResourceService> resourceServiceProvider;
    private final PresetService presetService;

    private final Item item;

    public ItemController(Item item, Provider<ResourceService> resourceServiceProvider, PresetService presetService) {
        this.item = item;
        this.resourceServiceProvider = resourceServiceProvider;
        this.presetService = presetService;
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        subscribe(resourceServiceProvider.get().getItemImage(String.valueOf(item.type())), imageUrl ->
                subscribe(presetService.getItem(String.valueOf(item.type())), item1 -> {
                    //image
                    Image itemImage = ImageUtils.scaledImageFX(imageUrl, 2.0);
                    itemView.setImage(itemImage);

                    //text
                    itemName.setText(item1.name());
                    itemAmount.setText(" x " + item.amount());
                }));

        return parent;
    }

    @Override
    public String getResourcePath() {
        return "inventory/";
    }
}
