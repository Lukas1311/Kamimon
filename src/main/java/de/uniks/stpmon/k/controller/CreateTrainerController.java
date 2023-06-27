package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.popup.ModalCallback;
import de.uniks.stpmon.k.controller.popup.PopUpController;
import de.uniks.stpmon.k.controller.popup.PopUpScenario;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.MainWindow;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.world.TextureSetService;
import de.uniks.stpmon.k.utils.Direction;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class CreateTrainerController extends PortalController {

    @FXML
    public TextField createTrainerInput;
    @FXML
    public Label trainerNameInfo;
    @FXML
    public Button closeButton;
    @FXML
    public ImageView trainerSprite;
    @FXML
    public Button createSpriteButton;
    @FXML
    public Button createTrainerButton;
    @FXML
    public AnchorPane createTrainerContent;
    @FXML
    public StackPane spriteContainer;
    @FXML
    public ImageView spriteBackground;

    @Inject
    RegionService regionService;
    @Inject
    ChooseSpriteController chooseSpriteController;
    @Inject
    Provider<PopUpController> popUpControllerProvider;
    @Inject
    Provider<HybridController> hybridControllerProvider;
    @Inject
    TextureSetService textureService;

    private Region chosenRegion;
    private String chosenSprite = "Premade_Character_01.png";
    private final BooleanProperty isPopUpShown = new SimpleBooleanProperty(false);
    private final SimpleStringProperty trainerName = new SimpleStringProperty();

    @Inject
    public CreateTrainerController() {
    }

    public void setChosenRegion(Region region) {
        this.chosenRegion = region;
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        loadImage(spriteBackground, "spriteBackground.png");
        loadImage(trainerSprite, "spritePlacehoder.png");

        BooleanBinding trainerNameTooLong = trainerName.length().greaterThan(32);
        BooleanBinding trainerNameInvalid = trainerName.isEmpty().or(trainerNameTooLong);

        createTrainerInput.textProperty().bindBidirectional(trainerName);

        trainerNameInfo.textProperty().bind(
                Bindings.when(trainerNameTooLong)
                        .then(translateString("trainername.too.long"))
                        .otherwise("")
        );

        loadSprite(chosenSprite);

        // these three elements have to be disabled when pop up is shown
        trainerSprite.disableProperty().bind(isPopUpShown);
        createSpriteButton.disableProperty().bind(isPopUpShown);
        createTrainerButton.disableProperty().bind(isPopUpShown.or(trainerNameInvalid));

        createTrainerButton.setOnAction(click -> createTrainer());
        closeButton.setOnAction(click -> closeWindow());

        return parent;
    }

    public void loadSprite(String selectedCharacter) {
        subscribe(
                setSpriteImage(spriteContainer, trainerSprite, Direction.BOTTOM, selectedCharacter, textureService, 120, 120),
                this::handleError
        );
    }

    public void trainerSprite() {
        createSprite();
    }

    public void createSprite() {
        chooseSpriteController.setCreationMode(true);
        createTrainerContent.getChildren().clear();
        createTrainerContent.getChildren().addAll(chooseSpriteController.render());
    }

    // set file ID of the chosen sprite
    public void setTrainerImage(String id) {
        chosenSprite = id;
        loadSprite(chosenSprite);
    }

    public void createTrainer() {
        showPopUp(PopUpScenario.CREATE_TRAINER, result -> {
            if (!result) return;
            disposables.add(regionService
                    .createTrainer(chosenRegion._id(), trainerName.get(), chosenSprite)
                    .observeOn(FX_SCHEDULER)
                    .subscribe(trainer -> enterRegion(chosenRegion), err -> {
                                err.printStackTrace();
                                handleError(err);
                            }
                    )
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

    public void closeWindow() {
        hybridControllerProvider.get().openMain(MainWindow.LOBBY);
    }

}

