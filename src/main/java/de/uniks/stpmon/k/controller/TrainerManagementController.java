package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.popup.ModalCallback;
import de.uniks.stpmon.k.controller.popup.PopUpController;
import de.uniks.stpmon.k.controller.popup.PopUpScenario;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.TrainerService;
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
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

import static de.uniks.stpmon.k.controller.sidebar.SidebarTab.CHOOSE_SPRITE;

public class TrainerManagementController extends Controller {
    @FXML
    public VBox trainerManagementScreen;
    @FXML
    public TextField trainerNameInput;
    @FXML
    public Label trainerNameInfo;
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
    Provider<PopUpController> popUpControllerProvider;
    @Inject
    TrainerService trainerService;
    @Inject
    Provider<LoginController> loginControllerProvider;
    @Inject
    Provider<LobbyController> lobbyControllerProvider;

    private Trainer currentTrainer;
    private final BooleanProperty isPopUpShown = new SimpleBooleanProperty(false);
    private final SimpleStringProperty trainerName = new SimpleStringProperty();
    private BooleanBinding trainerNameTooLong;
    private BooleanBinding trainerNameInvalid;
    private Boolean changesSaved = false;
    private BooleanBinding changesMade;

    private final BooleanProperty disablEdit = new SimpleBooleanProperty(false);

    @Inject
    public TrainerManagementController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        currentTrainer = trainerService.getMe();
        trainerManagementScreen.prefHeightProperty().bind(app.getStage().heightProperty().subtract(35));

        trainerNameTooLong = trainerName.length().greaterThan(32);
        trainerNameInvalid = trainerName.isEmpty().or(trainerNameTooLong);
        changesMade = trainerNameInvalid.not();
        changesMade.addListener((observable, oldValue, newValue) -> {
            // if changes are made again, then changesSaved should update again to false
            if (newValue) {
                changesSaved = false;
            }
        });

        trainerNameInput.textProperty().bindBidirectional(trainerName);
        trainerNameInfo.textProperty().bind(
                Bindings.when(trainerNameTooLong)
                        .then(translateString("trainername.too.long"))
                        .otherwise(""));

        // set bindings to buttons that should be disabled after the popup is shown
        saveChangesButton.disableProperty().bind(changesMade.not().or(isPopUpShown));
        deleteTrainerButton.disableProperty().bind(isPopUpShown);

        backButton.setOnAction(click -> backToSettings());
        deleteTrainerButton.setOnAction(click -> deleteTrainer());
        trainerSprite.setOnMouseClicked(click -> openTrainerSpriteEditor());
        saveChangesButton.setOnMouseClicked((click -> saveChanges()));

        deleteTrainerButton.disableProperty().bind(disablEdit);
        saveChangesButton.disableProperty().bind(disablEdit);

        return parent;
    }

    public void backToSettings() {
        if (hasUnsavedChanges()) {
            showPopUp(PopUpScenario.UNSAVED_CHANGES, result -> {
                if (!result)
                    return;
                saveSettings();
            });
        }
        hybridControllerProvider.get().popTab();
    }

    public void tryChangeTrainerName() {
        String newName = trainerNameInput.getText();
        if (newName == null || newName.equals("") || newName.length() > 32) {
            trainerNameInput.setText(currentTrainer.name());
            return;
        }
        disposables.add(trainerService
                .setTrainerName(newName)
                .observeOn(FX_SCHEDULER)
                .subscribe(trainer -> {
                    currentTrainer = trainer;
                }, err -> {
                    trainerNameInput.setText(currentTrainer.name());
                })
        );
    }

    public Boolean hasUnsavedChanges() {
        return changesMade.get() && !changesSaved;
    }

    public void openTrainerSpriteEditor() {
        disablEdit.set(true);
        hybridControllerProvider.get().pushTab(CHOOSE_SPRITE);
        // TODO Enable Buttons after focus returend
    }

    public void saveChanges() {
        showPopUp(PopUpScenario.SAVE_CHANGES, result -> {
            if (!result)
                return;
            saveSettings();
            changesSaved = true;
        });
    }

    public void saveSettings() {
        if (!trainerNameInvalid.get()) {
            saveTrainerName(trainerName.get());
        }
        // TODO: change sprite here
    }

    private void saveTrainerName(String newTrainerName) {
        if (trainerNameInvalid.get())
            return;
        disposables.add(
                trainerService.setTrainerName(newTrainerName).observeOn(FX_SCHEDULER).subscribe(trainer -> {
                    // set this to retrieve the newly set trainerName
                    currentTrainer = trainer;
                }, err -> {
                    // nothing
                }));
    }

    public void deleteTrainer() {
        PopUpScenario deleteScenario = PopUpScenario.DELETE_TRAINER;
        deleteScenario.setParams(new ArrayList<>(List.of(currentTrainer.name())));
        showPopUp(PopUpScenario.DELETE_TRAINER, result -> {
            if (!result)
                return;
            disposables.add(trainerService
                    .deleteMe()
                    .observeOn(FX_SCHEDULER)
                    .subscribe(trainer -> {
                        PopUpScenario deleteConfirmScenario = PopUpScenario.DELETE_CONFIRMATION_TRAINER;
                        deleteConfirmScenario.setParams(new ArrayList<>(List.of(trainer.name())));
                        showPopUp(deleteConfirmScenario, innerResult -> app.show(lobbyControllerProvider.get()));

                    }, err -> app.show(loginControllerProvider.get())));
        });
    }

    public void showPopUp(PopUpScenario scenario, ModalCallback callback) {
        isPopUpShown.set(true);
        PopUpController popUp = popUpControllerProvider.get();
        popUp.setScenario(scenario);
        popUp.showModal(callback);
    }
}
