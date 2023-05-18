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
    final Image imageOne = loadImage("introductionSheetOne.png");
    final Image imageTwo = loadImage("introductionSheetTwo.png");
    final Image imageThree = loadImage("introductionSheetThree.png");

    @Inject
    public IntroductionController(){

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        imageIntroduction.setImage(imageOne);
        if (imageIntroduction.getImage() == imageOne) {
            back.setVisible(false);
        }
        return parent;
    }

    public void nextSheet(ActionEvent event) {
        if (imageIntroduction.getImage() == imageOne) {
            back.setVisible(false);
        }else {
            back.setVisible(true);
        }
        openSheet(true);
    }

    private Image loadImage(String image) {
        return new Image(Objects.requireNonNull(LoadingScreenController.class.getResource(image)).toString());
    }

    public void previousSheet(ActionEvent event) {
        if (imageIntroduction.getImage() == imageTwo) {
            back.setVisible(false);
        }else {
            back.setVisible(true);
        }
        openSheet(false);
    }
    private void openSheet(boolean forward) {
        if(forward) {
            if(imageIntroduction.getImage() == imageOne) {
                imageIntroduction.setImage(imageTwo);
                back.setVisible(true);
            } else if (imageIntroduction.getImage() == imageTwo) {
                imageIntroduction.setImage(imageThree);
            } else if (imageIntroduction.getImage() == imageThree) {
                app.show(hybridControllerProvider.get());
            }
        }else {
            if(imageIntroduction.getImage() == imageTwo) {
                imageIntroduction.setImage(imageOne);
            } else if (imageIntroduction.getImage() == imageThree) {
                imageIntroduction.setImage(imageTwo);
            }
        }
    }
}
