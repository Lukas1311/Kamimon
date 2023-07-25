package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.TrainerService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ShopOverviewController extends Controller {

    @FXML
    public AnchorPane itemListPane;

    @FXML
    public ListView itemListView;


    private Parent parent;
    private Trainer npc;

    @Inject
    public ShopOverviewController() {

    }

    @Override
    public Parent render() {
        parent = super.render();
        loadBgImage(itemListPane, "shop/InventoryBox.png");

        //parent.setVisible(false)
        return parent;
    }

    public void setTrainer(Trainer npc) {
        this.npc = npc;
    }

    @Override
    public String getResourcePath() {
        return "shop/";
    }

}
