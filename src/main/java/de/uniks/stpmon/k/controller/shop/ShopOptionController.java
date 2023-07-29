package de.uniks.stpmon.k.controller.shop;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.ItemService;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.ResourceService;
import de.uniks.stpmon.k.service.TrainerService;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Objects;


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
    @Inject
    ResourceService resourceService;
    @Inject
    PresetService presetService;
    @Inject
    ItemService itemService;

    private Trainer npc;

    private int availableCoins = 0;
    private int neededCoins = 1;
    private int itemAmount = 0;


    @Inject
    public ShopOptionController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        loadBgImage(backgroundPane, "inventory/inv_coins.png");
        loadImage(coinsImage, "inventory/coin.png");

        if (trainerService != null) {
            subscribe(trainerService.onTrainer(), trainer ->
                    trainer.ifPresent(value -> {
                        availableCoins = value.coins();
                        coinsLabel.setText(availableCoins + " Coins");
                        updateTradeButtons();
                    }));
        }

        return parent;
    }


    public void setTrainer(Trainer npc) {
        this.npc = npc;
    }

    public void setItem(Item item) {
        subscribe(itemService.getItem(item.type().toString()), newItem -> {
            if(newItem.isPresent()) {
                itemAmount = newItem.get().amount();
            } else {
                itemAmount = 0;
            }
            amountLabel.setText("Amount: " + itemAmount);
            updateTradeButtons();
        });

        subscribe(resourceService.getItemImage((String.valueOf(item.type()))), imagerUrl -> {
            subscribe(presetService.getItem(item.type()), item1 -> {
                Image itemImage = ImageUtils.scaledImageFX(imagerUrl, 2.0);
                this.itemImage.setImage(itemImage);
                //text
                itemNameLabel.setText(item1.name());
                buyPriceLabel.setText("Buy Price: " + item1.price().toString());
                sellPriceLabel.setText("Sell Price: " + item1.price() / 2);
                itemDescriptionLabel.setText(item1.description());

                buyButton.setTooltip(new Tooltip(translateString("shop.buyItem", item1.name())));
                sellButton.setTooltip(new Tooltip(translateString("shop.sellItem", item1.name())));

                //buttons
                neededCoins = item1.price();
                updateTradeButtons();

            });
        });

        buyButton.setOnAction(ac -> {
            subscribe(itemService.tradeItem(item.type(), 1, npc._id(), false));
        });

        sellButton.setOnAction(ac -> {
            subscribe(itemService.tradeItem(item.type(), 1, npc._id(), true));
        });


    }

    @Override
    public String getResourcePath() {
        return "shop/";
    }

    private void updateTradeButtons() {
        // neededCoins > 0 is used to distinguish between tradeable items and non tradeable items
        buyButton.setDisable(!(neededCoins > 0 && availableCoins >= neededCoins));
        sellButton.setDisable(!(neededCoins > 0 && itemAmount > 0));
    }

    @Override
    public void destroy() {
        super.destroy();
        itemImage = null;
        coinsImage = null;
        backgroundPane = null;
    }

}
