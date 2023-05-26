package de.uniks.stpmon.k.controller;

import javax.inject.Inject;
import javax.inject.Provider;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.service.RegionService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

public class TrainerManagementController extends Controller {
    @FXML
    public VBox trainerManagementScreen;
    @FXML
    public TextField trainerNameInput;
    @FXML
    public Button deleteTrainerButton;
    @FXML
    public Button saveChangesButton;
    @FXML
    public Button backButton;
    @FXML
    public ImageView trainerSprite;

    @Inject
    RegionService regionService;
    @Inject
    Provider<HybridController> hybridControllerProvider;
    @Inject
    Provider<LoginController> loginControllerProvider;

    @Inject
    public TrainerManagementController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        trainerManagementScreen.prefHeightProperty().bind(app.getStage().heightProperty().subtract(35));

        // TODO: all ui functionality here

        backButton.setOnAction(click -> backToSettings());
        deleteTrainerButton.setOnAction(click -> deleteTrainer());
        return parent;
    }

    public void backToSettings() {
        // TODO: add pop confirmation only when unsaved settings
        hybridControllerProvider.get().popTab();
    }

    public void changeTrainerName() {
        // TODO: change the trainer name -> region service
    }

    public void openTrainerSpriteEditor() {
        // TODO: open trainer sprite controller
    }

    public void saveChanges() {
        // TODO: replace this with real modal pop pop up
        new Alert(Alert.AlertType.CONFIRMATION, "save changes?").showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                // TODO: save changes
            } else if (buttonType == ButtonType.CANCEL) {
                // do nothing
            }
        });
    }

    public void deleteTrainer() {
        // TODO: do some delete logic here -> region service
        
    }
}
