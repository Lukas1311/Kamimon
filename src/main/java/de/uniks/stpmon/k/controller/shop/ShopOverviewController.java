package de.uniks.stpmon.k.controller.shop;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.shop.ShopOptionController;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.ItemService;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.views.ItemCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class ShopOverviewController extends Controller {

    @Inject
    TrainerStorage trainerStorage;
    @Inject
    PresetService presetService;
    @Inject
    IResourceService resourceService;
    @Inject
    ItemService itemService;

    @Inject
    ShopOptionController shopOptionController;

    @FXML
    public AnchorPane itemListPane;

    @FXML
    public ListView<Item> itemListView;


    private Parent parent;
    private Trainer npc;

    private final ObservableList<Item> availableItems = FXCollections.observableArrayList();

    @Inject
    public ShopOverviewController() {

    }

    @Override
    public Parent render() {
        parent = super.render();
        loadBgImage(itemListPane, "shop/InventoryBox.png");

        itemListView.setCellFactory(param -> new ItemCell(resourceService, presetService));
        itemListView.setItems(availableItems);

        listen(itemListView.getSelectionModel().selectedItemProperty(), ((observable, oldValue, newValue) ->
        {
            shopOptionController.setItem(newValue);
        }));
        return parent;
    }

    public void setTrainer(Trainer npc) {
        this.npc = npc;
        List<Integer> items = npc.npc().sells();
        availableItems.setAll(items.stream().map(id-> new Item(null, null, id, -1)).toList());
    }

    @Override
    public String getResourcePath() {
        return "shop/";
    }

    @Override
    public void destroy(){
        super.destroy();
    }

}
