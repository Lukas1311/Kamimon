package de.uniks.stpmon.k.controller.inventory;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.views.ItemCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class InventoryController extends Controller {
    @Inject
    TrainerStorage trainerStorage;
    @Inject
    PresetService presetService;
    @Inject
    IResourceService resourceService;
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

    @Inject
    public InventoryController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        loadBgImage(fullPane, "inventory/inv_coins.png");
        loadImage(coinView, "inventory/coin.png");

        setCoins();

        ObservableList<Item> observableList = FXCollections.observableArrayList(dummyItems());
        itemListView.setItems(observableList);
        //itemListView.setCellFactory(param -> new ItemCell(this));

        itemListView.setCellFactory(param -> new ItemCell(this, resourceService, presetService));

        return parent;
    }

    private List<Item> dummyItems() {
        return List.of(new Item("20",
                        trainerStorage.getTrainer()._id(),
                        1,
                        1),
                new Item("15",
                        trainerStorage.getTrainer()._id(),
                        1,
                        12));
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
