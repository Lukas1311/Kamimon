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
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ItemInformationController extends Controller {
    @FXML
    public AnchorPane fullBox;
    @FXML
    public ImageView itemView;
    @FXML
    public Text itemInformation;
    @FXML
    public Text amountText;

    @Inject
    Provider<ResourceService> resourceServiceProvider;
    @Inject
    PresetService presetService;

    public Item item;

    @Inject
    public ItemInformationController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        loadBgImage(fullBox, "inventory/InventoryBox.png");

        subscribe(resourceServiceProvider.get().getItemImage(String.valueOf(item._id())), bufferedImage -> {
            Image image = ImageUtils.scaledImageFX(bufferedImage, 1.0);
            itemView.setImage(image);
        });

        return parent;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public String getResourcePath() {
        return "inventory/";
    }
}
