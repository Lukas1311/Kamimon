package de.uniks.stpmon.k.controller.inventory;

import de.uniks.stpmon.k.controller.ToastedController;
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
import javax.inject.Singleton;

@Singleton
public class InventoryController extends ToastedController {

    @Inject
    TrainerService trainerService;

    @Inject
    PresetService presetService;
    @Inject
    IResourceService resourceService;
    @Inject
    ItemService itemService;
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

    @Inject
    public InventoryController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        if (isInEncounter) {
            loadBgImage(inventoryPane, "inventory/InventoryBox.png");
            inventoryPane.getChildren().remove(coinBox);
            AnchorPane.setBottomAnchor(itemListView, 3.5);
        } else {
            loadBgImage(inventoryPane, "inventory/inv_coins.png");
            AnchorPane.setBottomAnchor(itemListView, 32.0);
        }

        loadImage(coinView, "inventory/coin.png");

        setCoins();

        subscribe(itemService.getItems(), items -> {
            if (!items.isEmpty()) {
                userItems.setAll(items);
                itemListView.setCellFactory(param -> new ItemCell(resourceService, presetService));
                itemListView.setItems(userItems);
            }
        });

        return parent;
    }

    @Override
    public void destroy() {
        super.destroy();
        itemListView.setItems(null);
        coinView = null;
        inventoryPane = null;
        itemListView = null;
        coinBox = null;
        coinAmount = null;
    }

    private void setCoins() {
        if (trainerService != null) {
            subscribe(trainerService.onTrainer(), trainer ->
                    trainer.ifPresent(value -> coinAmount.setText(value.coins().toString())));
        }
    }

    @Override
    public String getResourcePath() {
        return "inventory/";
    }

}
