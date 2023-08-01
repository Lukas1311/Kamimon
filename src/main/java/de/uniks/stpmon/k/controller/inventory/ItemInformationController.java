package de.uniks.stpmon.k.controller.inventory;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Item;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import javax.inject.Inject;

public class ItemInformationController extends Controller {

    @FXML
    public ImageView itemView;
    @FXML
    public Text itemInformation;
    @FXML
    public Text amountText;

    public Item currentItem;

    @Inject
    public ItemInformationController() {
    }

    @Override
    public Parent render() {
        return super.render();
    }

    public void setCurrentItem(Item currentItem) {
        this.currentItem = currentItem;
    }

}
