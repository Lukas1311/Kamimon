package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.popup.ModalCallback;
import de.uniks.stpmon.k.controller.popup.PopUpController;
import de.uniks.stpmon.k.controller.popup.PopUpScenario;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.MainWindow;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.TrainerService;
import de.uniks.stpmon.k.service.world.TextureSetService;
import de.uniks.stpmon.k.utils.Direction;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.List;
import java.util.prefs.Preferences;

@Singleton
public class CreateTrainerController extends PortalController {

    protected final ObservableList<String> characters = FXCollections.observableArrayList();
    private final BooleanProperty isPopUpShown = new SimpleBooleanProperty(false);

    protected int currentSpriteIndex;
    protected int previousSpriteIndex;

    @FXML
    public TextField createTrainerInput;
    @FXML
    public Label trainerNameInfo;
    @FXML
    public Button closeButton;
    @FXML
    public ImageView trainerSprite;
    @FXML
    public Button createTrainerButton;
    @FXML
    public AnchorPane createTrainerContent;
    @FXML
    public StackPane spriteContainer;
    @FXML
    public ImageView spriteBackground;
    @FXML
    public Button spriteLeft;
    @FXML
    public Button spriteRight;

    @Inject
    RegionService regionService;
    @Inject
    PresetService presetService;
    @Inject
    Preferences preferences;
    @Inject
    TrainerService trainerService;
    @Inject
    Provider<PopUpController> popUpControllerProvider;
    @Inject
    Provider<HybridController> hybridControllerProvider;
    @Inject
    TextureSetService textureService;

    private Region chosenRegion;
    private String chosenSprite = "Premade_Character_01.png";
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
        createTrainerButton.disableProperty().bind(isPopUpShown.or(trainerNameInvalid));

        createTrainerButton.setOnAction(click -> {
            saveSprite();
            createTrainer();
        });
        closeButton.setOnAction(click -> closeWindow());

        // Retrieve the sprite index from the preferences
        currentSpriteIndex = preferences.getInt("currentSpriteIndex", 0);
        previousSpriteIndex = currentSpriteIndex;

        // Load the list of available sprites
        loadSpriteList();

        return parent;
    }

    public void loadSprite(String selectedCharacter) {
        subscribe(
                setSpriteImage(spriteContainer, trainerSprite, Direction.BOTTOM, selectedCharacter, textureService, 120, 120),
                this::handleError
        );
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

    // -------------------------- Choose Sprite -------------------------- //


    /**
     * Load the list of available sprites from the preset service
     */
    public void loadSpriteList() {
        disposables.add(presetService.getCharacters()
                .observeOn(FX_SCHEDULER)
                .subscribe(response -> {
                            getCharactersList(response);
                            currentSpriteIndex %= characters.size();
                            previousSpriteIndex %= characters.size();
                        }
                        , this::handleError));
    }

    /**
     * Update the characters list with the names of the available characters
     * If there are characters, load the sprite image for the currently selected character based on the currentSpriteIndex
     */
    public void getCharactersList(List<String> charactersList) {
        this.characters.setAll(charactersList);

        if (!charactersList.isEmpty()) {
            String selectedCharacter = charactersList.get(currentSpriteIndex);
            // Load the sprite image
            loadSprite(selectedCharacter);
        }
    }

    /**
     * Navigate to the previous sprite character
     */
    public void toLeft() {
        if (!characters.isEmpty()) {
            currentSpriteIndex--;
            if (currentSpriteIndex < 0) {
                currentSpriteIndex = characters.size() - 1;
            }
            String selectedCharacter = characters.get(currentSpriteIndex);
            // Load the sprite image
            loadSprite(selectedCharacter);
        }

    }

    /**
     * Navigate to the next sprite character
     */
    public void toRight() {
        if (!characters.isEmpty()) {
            currentSpriteIndex++;
            if (currentSpriteIndex >= characters.size()) {
                currentSpriteIndex = 0;
            }
            String selectedCharacter = characters.get(currentSpriteIndex);
            // Load the sprite image
            loadSprite(selectedCharacter);
        }
    }

    /**
     * Save the selected sprite character
     * Check if the selected character is different from the previous character and shows a pop-up for confirmation
     */
    public void saveSprite() {
        // Save the currentSpriteIndex to the preferences
        setTrainerImage(characters.get(currentSpriteIndex));
        preferences.putInt("currentSpriteIndex", currentSpriteIndex);
        disposables.add(
                trainerService.setImage(characters.get(currentSpriteIndex))
                        .observeOn(FX_SCHEDULER)
                        .subscribe(trainer -> {
                        }, this::handleError));
    }

}
