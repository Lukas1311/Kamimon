package de.uniks.stpmon.k.controller.inventory;

import de.uniks.stpmon.k.controller.IngameController;
import de.uniks.stpmon.k.controller.ToastedController;
import de.uniks.stpmon.k.controller.encounter.EncounterOverviewController;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.ItemService;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.TrainerService;
import de.uniks.stpmon.k.views.ItemCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class InventoryController extends ToastedController {

    @Inject
    Provider<TrainerService> trainerServiceProvider;
    @Inject
    PresetService presetService;
    @Inject
    IResourceService resourceService;
    @Inject
    ItemService itemService;
    @Inject
    Provider<IngameController> ingameControllerProvider;
    @Inject
    Provider<ItemInformationController> itemInformationControllerProvider;
    @Inject
    Provider<EncounterOverviewController> encounterOverviewControllerProvider;

    @FXML
    public AnchorPane inventoryPane;
    @FXML
    public ListView<Item> itemListView;
    @FXML
    public HBox coinBox;
    @FXML
    public ImageView coinView;
    @FXML
    public Text coinAmount;
    private final ObservableList<Item> userItems = FXCollections.observableArrayList();

    public boolean isInEncounter = false;

    public Item currentItem;
    
    @Inject
    public InventoryController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        if (isInEncounter) {
            loadBgImage(inventoryPane, getResourcePath() + "InventoryBox_v1.1.png");
            inventoryPane.getChildren().remove(coinBox);
            AnchorPane.setBottomAnchor(itemListView, 8.0);
        } else {
            loadBgImage(inventoryPane, getResourcePath() + "InventoryBox_w_coins.png");
            AnchorPane.setBottomAnchor(itemListView, 39.0);
        }

        loadImage(coinView, getResourcePath() + "coin.png");

        setCoins();

        subscribe(itemService.getItems(), items -> {
            if (!items.isEmpty()) {
                items.removeIf(item -> item.amount() == 0);
                userItems.setAll(items);
                itemListView.setCellFactory(param -> new ItemCell(this, resourceService, presetService));
                itemListView.setItems(userItems);
            }
        });

        return parent;
    }

    @Override
    public void destroy() {
        super.destroy();
        itemInformationControllerProvider.get().setItem(null);
        itemInformationControllerProvider.get().setOpen(false);
        itemService.resetActiveItem();
        itemListView.setItems(null);
        inventoryPane = null;
        itemListView = null;
        currentItem = null;
        coinAmount = null;
        coinView = null;
        coinBox = null;
    }

    private void setCoins() {
        if (trainerServiceProvider != null) {
            subscribe(trainerServiceProvider.get().onTrainer(), trainer ->
                    trainer.ifPresent(value -> coinAmount.setText(value.coins().toString())));
        }
    }

    public void triggerDetail(Item item) {
        if (currentItem == null) {
            openDetail(item);
        } else {
            if (currentItem == item) {
                closeDetail();
            } else {
                closeDetail();
                openDetail(item);
            }
        }
    }

    private void openDetail(Item item) {
        currentItem = item;
        itemService.resetActiveItem();
        if (isInEncounter()) {
            encounterOverviewControllerProvider.get().openController("itemInfo", item);
        } else {
            ingameControllerProvider.get().openItemInformation(item);
        }
    }

    private void closeDetail() {
        ingameControllerProvider.get().removeChildren(2);
        currentItem = null;
        itemService.resetActiveItem();
    }

    @Override
    public String getResourcePath() {
        return "inventory/";
    }


    public boolean isInEncounter() {
        return isInEncounter;
    }

    public void setInEncounter(boolean inEncounter) {
        isInEncounter = inEncounter;
    }


}
