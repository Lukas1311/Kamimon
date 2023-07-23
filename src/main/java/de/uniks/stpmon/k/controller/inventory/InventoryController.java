package de.uniks.stpmon.k.controller.inventory;

import de.uniks.stpmon.k.controller.ToastedController;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.service.ItemService;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.ResourceService;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
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
    TrainerStorage trainerStorage;
    @Inject
    PresetService presetService;
    @Inject
    Provider<ResourceService> resourceServiceProvider;
    @Inject
    ItemService itemService;
    @FXML
    public AnchorPane fullPane;
    @FXML
    public ListView<Item> itemListView;
    @FXML
    public HBox coinBox;
    @FXML
    public ImageView coinView;
    @FXML
    public Text coinAmount;
    private final ObservableList<Item> userItems = FXCollections.observableArrayList();

    @Inject
    public InventoryController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        loadBgImage(fullPane, "inventory/inv_coins.png");
        loadImage(coinView, "inventory/coin.png");

        setCoins();

        subscribe(itemService.getItems(), items -> {
            if (!items.isEmpty()) {
                userItems.setAll(items);
                itemListView.setCellFactory(param -> new ItemCell(resourceServiceProvider, presetService));
                itemListView.setItems(userItems);
            } else {
                System.out.println("Liste leer");
            }
            System.out.println(userItems);
        });


        return parent;
    }

    private void setCoins() {
        subscribe(trainerStorage.onTrainer(), trainer ->
                trainer.ifPresent(value -> coinAmount.setText(value.coins().toString())));
    }

    @Override
    public String getResourcePath() {
        return "inventory/";
    }
}
