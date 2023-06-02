package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;

import de.uniks.stpmon.k.controller.popup.ModalCallback;
import de.uniks.stpmon.k.controller.popup.PopUpController;
import de.uniks.stpmon.k.controller.popup.PopUpScenario;
import de.uniks.stpmon.k.service.RegionService;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import javax.inject.Provider;

import static de.uniks.stpmon.k.controller.sidebar.MainWindow.INGAME;

public class CreateTrainerController extends Controller {
    @FXML
    public TextField createTrainerInput;
    @FXML
    public ImageView trainerSprite;
    @FXML
    public Button createSpriteButton;
    @FXML
    public Button createTrainerButton;

    @Inject
    RegionService regionService;
    @Inject
    Provider<PopUpController> popUpControllerProvider;
    @Inject
    Provider<IngameController> ingameControllerProvider;
    @Inject
    Provider<HybridController> hybridControllerProvider;

    private BooleanProperty isPopUpShown = new SimpleBooleanProperty(false);


    @Inject
    public CreateTrainerController() {}

    @Override
    public Parent render() {
        final Parent parent = super.render();

        // these three elements have to be disabled when pop up is shown
        trainerSprite.disableProperty().bind(isPopUpShown);
        createSpriteButton.disableProperty().bind(isPopUpShown);
        createTrainerButton.disableProperty().bind(isPopUpShown);

        return parent;
    }

    public void trainerSprite() {
    }

    public void createSprite() {
    }

    public void createTrainer() {
        showPopUp(PopUpScenario.CREATE_TRAINER, result -> {
            if (!result) return;
            // TODO: get values regionId, name and image
            disposables.add(regionService
                .createTrainer(null, null, null)
                .observeOn(FX_SCHEDULER)
                .subscribe(trainer -> {
                    hybridControllerProvider.get().openMain(INGAME);
                })
            );
        });
    }

    public void showPopUp(PopUpScenario scenario, ModalCallback callback) {
        isPopUpShown.set(true);
        PopUpController popUp = popUpControllerProvider.get();
        popUp.setScenario(scenario);
        popUp.showModal(callback);
        isPopUpShown.set(false);
    }
}

