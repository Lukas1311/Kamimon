package de.uniks.stpmon.k.controller;


import de.uniks.stpmon.k.controller.sidebar.HybridController;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Objects;


public class IntroductionController extends Controller {

    @FXML
    public ImageView imageIntroduction;
    @FXML
    public Button further;
    @FXML
    public Button back;
    @Inject
    Provider<HybridController> hybridControllerProvider;
    final Image imageOne = loadImage("introductionSheetOne.png");
    final Image imageTwo = loadImage("introductionSheetTwo.png");
    final Image imageThree = loadImage("introductionSheetThree.png");
    final Image imageFour = loadImage("introductionSheetFour.png");
    final Image[] images = {imageOne, imageTwo, imageThree, imageFour};
    private final IntegerProperty indexProperty = new SimpleIntegerProperty(0);

    @Inject
    public IntroductionController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        imageIntroduction.setImage(imageOne);
        if (imageIntroduction.getImage() == imageOne) {
            back.setVisible(false);
        }
        back.visibleProperty()
                .bind(indexProperty.greaterThan(0));
        further.visibleProperty()
                .bind(indexProperty.lessThan(images.length));
        return parent;
    }

    private Image loadImage(String image) {
        return new Image(Objects.requireNonNull(LoadingScreenController.class.getResource(image)).toString());
    }

    public void nextSheet(ActionEvent event) {
        openSheet(true);
    }

    public void previousSheet(ActionEvent event) {
        openSheet(false);
    }

    private void openSheet(boolean forward) {
        int index = indexProperty.get();
        if (forward) {
            index++;
            if (index == images.length) {
                app.show(hybridControllerProvider.get());
                return;
            }
        } else {
            index--;
        }
        index = Math.min(Math.max(index, 0), images.length - 1);
        imageIntroduction.setImage(images[index]);
        indexProperty.set(index);
    }
}
