package de.uniks.stpmon.k.controller.shop;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.PresetService;
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
    PresetService presetService;
    @Inject
    IResourceService resourceService;
    @Inject
    ShopOptionController shopOptionController;

    @FXML
    public AnchorPane itemListPane;
    @FXML
    public ListView<Item> itemListView;

    private final ObservableList<Item> availableItems = FXCollections.observableArrayList();

    @Inject
    public ShopOverviewController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        parent.setId("shopOverview");
        loadBgImage(itemListPane, "inventory/InventoryBox_v1.1.png");


        itemListView.setCellFactory(param -> new ItemCell(null, resourceService, presetService));
        itemListView.setItems(availableItems);

        listen(itemListView.getSelectionModel().selectedItemProperty(), ((observable, oldValue, newValue) ->
        {
            if (newValue != null) {
                shopOptionController.setItem(availableItems.stream().filter(item ->
                                item.type().equals(newValue.type()))
                        .toList().get(0));
            }
        }));

        return parent;
    }

    public void setTrainer(Trainer npc) {
        List<Integer> items = npc.npc().sells();
        availableItems.setAll(items.stream().map(id -> new Item(null, null, id, -1)).toList());
    }

    public void initSelection() {
        if (!availableItems.isEmpty()) {
            shopOptionController.setItem(availableItems.get(0));
        }
    }

    @Override
    public String getResourcePath() {
        return "shop/";
    }

    @Override
    public void destroy() {
        super.destroy();
        availableItems.clear();
    }

}
