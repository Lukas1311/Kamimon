package de.uniks.stpmon.k.controller.inventory;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.IngameController;
import de.uniks.stpmon.k.dto.ItemTypeDto;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.ItemService;
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
    @FXML
    public Label nameLabel;
    @FXML
    public Button useButton;

    @Inject
    IResourceService resourceService;
    @Inject
    PresetService presetService;
    @Inject
    ItemService itemService;
    @Inject
    Provider<IngameController> ingameControllerProvider;


    public Item item;
    public ItemTypeDto itemTypeDto;

    private boolean isEncounter = false;

    @Inject
    public ItemInformationController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        parent.setId("itemInformationNode");
        loadBgImage(fullBox, getResourcePath() + "InventoryBox_v1.1.png");
        useButton.setVisible(false);

        subscribe(presetService.getItem(item.type()), item -> {
            itemTypeDto = item;
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
        if(itemTypeDto == null) {
            return;
        }
        switch(itemTypeDto.use()) {
            case ITEM_BOX -> {
                //TODO
            }
            case MONSTER_BOX -> {
                //TODO
            }
            case BALL -> {
                //TODO
            }
            case EFFECT -> {
                if(itemService == null || ingameControllerProvider == null) {
                    return;
                }
                itemService.setActiveItem(itemTypeDto.id());
                if(!isEncounter) {
                    ingameControllerProvider.get().removeChildren(2);
                    ingameControllerProvider.get().openMonsterInventory();
                }
            }
        }

    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setInEncounter(boolean isEncounter){
        this.isEncounter = isEncounter;
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
