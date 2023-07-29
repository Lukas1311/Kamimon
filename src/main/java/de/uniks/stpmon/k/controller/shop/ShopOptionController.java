package de.uniks.stpmon.k.controller.shop;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.TrainerService;
import io.reactivex.rxjava3.core.Observable;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class ShopOptionController extends Controller {

    @FXML
    public ImageView itemImage;
    @FXML
    public Label itemNameLabel;
    @FXML
    public Label itemDescriptionLabel;
    @FXML
    public Label amountLabel;
    @FXML
    public Label buyPriceLabel;
    @FXML
    public Label sellPriceLabel;
    @FXML
    public Button buyButton;
    @FXML
    public Button sellButton;
    @FXML
    public ImageView coinsImage;
    @FXML
    public Label coinsLabel;
    @FXML
    public Label coinsDifferenceLabel;
    @FXML
    public AnchorPane backgroundPane;


    @Inject
    TrainerService trainerService;


    private Observable<Item> item;
    private Trainer npc;

    private Parent parent;

    @Inject
    public ShopOptionController() {

    }

    @Override
    public Parent render() {
        parent = super.render();
        loadBgImage(backgroundPane, "shop/inv_coins.png");
        loadImage(coinsImage, "shop/coin.png");


        if(trainerService != null) {
            subscribe(trainerService.onTrainer(), trainer ->
                    trainer.ifPresent(value -> coinsLabel.setText("Coins: " + trainerService.getMe().coins())));
        }

        return parent;
    }


    public void setTrainer(Trainer npc) {
        this.npc = npc;
    }

    public void setItem(Item item) {

    }

    @Override
    public String getResourcePath() {
        return "shop/";
    }

}
