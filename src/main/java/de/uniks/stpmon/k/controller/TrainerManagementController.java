package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.popup.ModalCallback;
import de.uniks.stpmon.k.controller.popup.PopUpController;
import de.uniks.stpmon.k.controller.popup.PopUpScenario;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.MainWindow;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.TrainerService;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

import static de.uniks.stpmon.k.controller.sidebar.SidebarTab.CHOOSE_SPRITE;

public class TrainerManagementController extends ToastedController {

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
    @FXML
    public StackPane spriteContainer;

    @FXML
    public Text trainerNameText;

    @Inject
    Provider<HybridController> hybridControllerProvider;
    @Inject
    Provider<PopUpController> popUpControllerProvider;
    @Inject
    TrainerService trainerService;
    @Inject
    TrainerStorage trainerStorage;
    @Inject
    TextureSetService textureService;

    private Trainer currentTrainer;
    private final BooleanProperty isPopUpShown = new SimpleBooleanProperty(false);
    private final SimpleStringProperty trainerName = new SimpleStringProperty();
    private BooleanBinding trainerNameInvalid;
    private Boolean changesSaved = false;
    private BooleanBinding changesMade;

    private final BooleanProperty disableEdit = new SimpleBooleanProperty(false);

    @Inject
    public TrainerManagementController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        currentTrainer = trainerService.getMe();
        trainerManagementScreen.prefHeightProperty().bind(app.getStage().heightProperty().subtract(35));

        subscribe(trainerStorage.onTrainer(), trainer -> {
            if (trainer.isPresent()) {
                trainerNameInput.setPromptText(trainer.get().name());
                currentTrainer = trainer.get();
                subscribe(
                        setSpriteImage(spriteContainer, trainerSprite, Direction.BOTTOM, trainer.get().image(), textureService),
                        this::handleError
                );
            }
        });

        BooleanBinding trainerNameTooLong = trainerName.length().greaterThan(32);
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

        deleteTrainerButton.disableProperty().bind(disableEdit);
        saveChangesButton.disableProperty().bind(disableEdit);

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

    public Boolean hasUnsavedChanges() {
        return changesMade.get() && !changesSaved;
    }

    public void openTrainerSpriteEditor() {
        hybridControllerProvider.get().pushTab(CHOOSE_SPRITE);
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
    }

    private void saveTrainerName(String newTrainerName) {
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
                        showPopUp(deleteConfirmScenario, innerResult ->
                                hybridControllerProvider.get().openMain(MainWindow.LOBBY));

                    }, err -> hybridControllerProvider.get().openMain(MainWindow.LOBBY)));
        });
    }

    public void showPopUp(PopUpScenario scenario, ModalCallback callback) {
        isPopUpShown.set(true);
        PopUpController popUp = popUpControllerProvider.get();
        popUp.setScenario(scenario);
        popUp.showModal(callback);
    }

}
