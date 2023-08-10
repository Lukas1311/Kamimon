package de.uniks.stpmon.k.controller.inventory;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.IngameController;
import de.uniks.stpmon.k.controller.encounter.EncounterOverviewController;
import de.uniks.stpmon.k.dto.ItemTypeDto;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.models.ItemUse;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
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
    @Inject
    Provider<EncounterOverviewController> encounterOverviewControllerProvider;
    @Inject
    EventListener eventListener;


    public Item item;
    public ItemTypeDto itemTypeDto;
    private boolean isOpen = false;

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
                if (item.use().equals(ItemUse.BALL) && !isEncounter || (
                        item.use().equals(ItemUse.ITEM_BOX) || item.use().equals(ItemUse.MONSTER_BOX)) && isEncounter) {
                    useButton.setDisable(true);
                }
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

    public void useItem() {
        if (itemTypeDto == null) {
            return;
        }
        switch (itemTypeDto.use()) {
            case ITEM_BOX -> {
                if (itemService == null || ingameControllerProvider == null) {
                    return;
                }

                subscribe(eventListener.listen(Socket.WS, "%s.%s.items.*.*".formatted("trainers", item.trainer()), Item.class), itemEvent -> {
                    switch (itemEvent.suffix()) {
                        case "deleted" -> {
                        }
                        case "created", "updated" ->
                                subscribe(presetService.getItem(itemEvent.data().type()), itemTypeDto1 -> {
                                    if (!itemTypeDto1.name().contains("Mystery box") && isOpenBox()) {
                                        ingameControllerProvider.get().openBox(itemEvent.data());
                                    }
                                });
                    }
                });
                subscribe(itemService.useItem(itemTypeDto.id(), 1, null));
            }
            case MONSTER_BOX -> {
                if (itemService == null || ingameControllerProvider == null) {
                    return;
                }

                subscribe(eventListener.listen(Socket.WS, "%s.%s.monsters.*.*".formatted("trainers", item.trainer()), Monster.class), monsterEvent -> {
                    switch (monsterEvent.suffix()) {
                        case "deleted" -> {
                        }
                        case "created", "updated" ->
                                subscribe(presetService.getMonster(monsterEvent.data().type()), monsterTypeDto -> {
                                    if (!monsterTypeDto.name().contains("Monbox") && isOpenBox()) {
                                        ingameControllerProvider.get().openBox(monsterEvent.data());
                                    }
                                });
                    }
                });
                subscribe(itemService.useItem(itemTypeDto.id(), 1, null));
            }
            case BALL -> {
                //TODO
            }
            case EFFECT -> {
                if (itemService == null || ingameControllerProvider == null) {
                    return;
                }
                itemService.setActiveItem(itemTypeDto.id());
                if (!isEncounter) {
                    ingameControllerProvider.get().removeChildren(2);
                    ingameControllerProvider.get().openMonsterInventory();
                } else {
                    encounterOverviewControllerProvider.get().openController("monsterSelection", item);
                }
            }
        }

    }

    public boolean isOpenBox() {
        return !isOpen;
    }

    public void setOpen(boolean open) {
        this.isOpen = open;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setInEncounter(boolean isEncounter) {
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
        setOpen(false);
    }
}
