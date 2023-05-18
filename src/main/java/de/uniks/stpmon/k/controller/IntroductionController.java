package de.uniks.stpmon.k.controller;


import de.uniks.stpmon.k.controller.sidebar.HybridController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Objects;



public class IntroductionController extends Controller{


    @FXML
    public ImageView imageIntroduction;
    @FXML
    public Button further;
    @FXML
    public Button back;
    @Inject
    Provider<HybridController> hybridControllerProvider;

    @Inject
    public IntroductionController(){

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        final Image imageOne = loadImage("introductionSheetOne.png");
        imageIntroduction.setImage(imageOne);
        if (imageIntroduction.getImage() == imageOne) {
            back.setVisible(false);
        }
        return parent;
    }

    public void nextSheet(ActionEvent event) {
        back.setVisible(true);
        final Image imageTwo = loadImage("introductionSheetTwo.png");
        final Image imageThree = loadImage("introductionSheetThree.png");
        imageIntroduction.setImage(imageTwo);
        further.setOnAction(event1 -> {
            imageIntroduction.setImage(imageThree);
            further.setOnAction(event2 -> {
                app.show(hybridControllerProvider.get());
            });
        });
    }

    private Image loadImage(String image) {
        return new Image(Objects.requireNonNull(LoadingScreenController.class.getResource(image)).toString());
    }

    public void previousSheet(ActionEvent event) {
        final Image previousImageOne = loadImage("introductionSheetOne.png");
        final Image previousImageTwo = loadImage("introductionSheetTwo.png");
        imageIntroduction.setImage(previousImageTwo);
        back.setOnAction(event1 -> {
            imageIntroduction.setImage(previousImageOne);
        });
        /*Image currentImage = imageIntroduction.getImage();
        final Image previousImageOne = loadImage("introductionSheetOne.png");
        final Image previousImageTwo = loadImage("introductionSheetTwo.png");
        final Image previousImageThree = loadImage("introductionSheetThree.png");
        if(Objects.equals(currentImage, previousImageTwo)) {
            imageIntroduction.setImage(previousImageOne);
            back.setVisible(false);
        } else if(Objects.equals(currentImage, previousImageThree)) {
            imageIntroduction.setImage(previousImageTwo);
        }

         */
    }
}
