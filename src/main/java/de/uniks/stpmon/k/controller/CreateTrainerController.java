package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.controller.popup.ModalCallback;
import de.uniks.stpmon.k.controller.popup.PopUpController;
import de.uniks.stpmon.k.controller.popup.PopUpScenario;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.storage.RegionStorage;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import javax.inject.Provider;

import static de.uniks.stpmon.k.controller.sidebar.MainWindow.INGAME;

public class CreateTrainerController extends Controller {
    @FXML
    public TextField createTrainerInput;
    @FXML
    public Label trainerNameInfo;
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
    @Inject
    RegionStorage regionStorage;

    private Region currentRegion;
    private BooleanProperty isPopUpShown = new SimpleBooleanProperty(false);
    private final SimpleStringProperty trainerName = new SimpleStringProperty();
    private BooleanBinding trainerNameTooLong;
    private BooleanBinding trainerNameInvalid;
    


    @Inject
    public CreateTrainerController() {}

    @Override
    public Parent render() {
        final Parent parent = super.render();

        currentRegion = regionStorage.getRegion();

        trainerNameTooLong = trainerName.length().greaterThan(32);
        trainerNameInvalid = trainerName.isEmpty().or(trainerNameTooLong);

        createTrainerInput.textProperty().bindBidirectional(trainerName);

        trainerNameInfo.textProperty().bind(
            Bindings.when(trainerNameTooLong)
            .then(translateString("trainername.too.long"))
            .otherwise("")
        );

        // these three elements have to be disabled when pop up is shown
        trainerSprite.disableProperty().bind(isPopUpShown);
        createSpriteButton.disableProperty().bind(isPopUpShown);
        createTrainerButton.disableProperty().bind(isPopUpShown.or(trainerNameInvalid));

        return parent;
    }

    public void trainerSprite() {
    }

    public void createSprite() {
    }

    public void createTrainer() {
        showPopUp(PopUpScenario.CREATE_TRAINER, result -> {
            if (!result) return;
            // TODO: get image id string of the sprite
            disposables.add(regionService
                .createTrainer(currentRegion._id(), trainerName.get(), "string")
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

