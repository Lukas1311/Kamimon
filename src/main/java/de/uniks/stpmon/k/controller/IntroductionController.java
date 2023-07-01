package de.uniks.stpmon.k.controller;


import de.uniks.stpmon.k.controller.sidebar.HybridController;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class IntroductionController extends Controller {

    @FXML
    public ImageView imageIntroduction;
    @FXML
    public Button further;
    @FXML
    public Button back;
    @Inject
    Provider<HybridController> hybridControllerProvider;
    final Image[] images = new Image[8];
    private final IntegerProperty indexProperty = new SimpleIntegerProperty(0);

    @Inject
    public IntroductionController() {

    }

    @Override
    public void init() {
        super.init();
        loadImage(images, 0, "introduction/intro_00.png");
        loadImage(images, 1, "introduction/intro_01.png");
        loadImage(images, 2, "introduction/intro_02.png");
        loadImage(images, 3, "introduction/intro_03.png");
        loadImage(images, 4, "introduction/intro_04.png");
        loadImage(images, 5, "introduction/intro_05.png");
        loadImage(images, 6, "introduction/intro_06.png");
        loadImage(images, 7, "introduction/intro_07.png");
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        imageIntroduction.setImage(images[0]);
        if (imageIntroduction.getImage() == images[0]) {
            back.setVisible(false);
        }
        back.visibleProperty()
                .bind(indexProperty.greaterThan(0));
        further.visibleProperty()
                .bind(indexProperty.lessThan(images.length));
        return parent;
    }

    public void nextSheet() {
        openSheet(true);
    }

    public void previousSheet() {
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
