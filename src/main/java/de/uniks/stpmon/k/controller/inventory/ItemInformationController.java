package de.uniks.stpmon.k.controller.inventory;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import javax.inject.Inject;

public class ItemInformationController extends Controller {
    @FXML
    public AnchorPane fullBox;
    @FXML
    public ImageView itemView;
    @FXML
    public Text itemInformation;
    @FXML
    public Text amountText;
    @FXML
    public Label nameLabel;
    @FXML
    public Button useButton;

    @Inject
    IResourceService resourceService;
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

        subscribe(presetService.getItem(item.type()), item -> {
            if (item.use() != null) {
                useButton.setVisible(true);
                useButton.setText(translateString("useItemButton"));
                useButton.setOnAction(e -> useItem());
            } else {
                useButton.setVisible(false);
            }

            nameLabel.setText(item.name());
            itemInformation.setText(item.description());
        });
        subscribe(resourceService.getItemImage(item.type().toString()), imageUrl -> {
            Image itemImage = ImageUtils.scaledImageFX(imageUrl, 4.0);
            itemView.setImage(itemImage);
        });

        amountText.setText("Amount: " + item.amount().toString());

        return parent;
    }

    private void useItem() {
        //TODO
        System.out.println("TODO");
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public String getResourcePath() {
        return "inventory/";
    }

    @Override
    public void destroy() {
        super.destroy();
        item = null;
        fullBox = null;
        itemView = null;
        itemInformation = null;
        amountText = null;
        nameLabel = null;
        useButton = null;
    }
}