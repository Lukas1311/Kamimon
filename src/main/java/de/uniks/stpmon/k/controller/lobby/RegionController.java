package de.uniks.stpmon.k.controller.lobby;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Region;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class RegionController extends Controller {

    @FXML
    public Text regionNameText;
    @FXML
    public ImageView regionImage;
    @FXML
    public BorderPane imageWrappingPane;
    @FXML
    public VBox regionVBox;

    private final Region region;
    private final RegionListController listController;
    private final Image image;

    public RegionController(Region region, RegionListController listController, Image image) {
        this.region = region;
        this.listController = listController;
        this.effectContext = listController.getEffectContext();
        this.image = image;
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        int vboxWidth = 330;
        int borderSize = 10;
        regionVBox.setMaxWidth(vboxWidth);
        regionNameText.setText(region.name());
        regionVBox.setSpacing(10);
        regionImage.setImage(image);
        regionVBox.setOnMouseClicked(event -> {
            listController.removeLettering();
            listController.createNewTrainer(region);
        });
        regionImage.setFitWidth(vboxWidth - borderSize * 2);
        imageWrappingPane.setMinWidth(regionImage.getFitWidth() + borderSize * 2);
        imageWrappingPane.setMinHeight(regionImage.getFitHeight() + borderSize * 2);

        return parent;
    }

}
