package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.popup.ModalCallback;
import de.uniks.stpmon.k.controller.popup.PopUpController;
import de.uniks.stpmon.k.controller.popup.PopUpScenario;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.TrainerService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;
import java.util.prefs.Preferences;

public class ChooseSpriteController extends ToastedController {

    public static final double IMAGE_SCALE = 4.0;
    protected final ObservableList<String> characters = FXCollections.observableArrayList();
    private final BooleanProperty isPopUpShown = new SimpleBooleanProperty(false);


    protected int currentSpriteIndex;
    protected int previousSpriteIndex;

    private String SpriteID;

    @FXML
    public Text chooseTrainer;
    @FXML
    public Button spriteLeft;
    @FXML
    public StackPane spriteContainer;
    @FXML
    public ImageView spriteImage;
    @FXML
    public Button spriteRight;
    @FXML
    public Button saveSprite;
    @FXML
    public BorderPane chooseTrainerContent;

    @Inject
    PresetService presetService;
    @Inject
    Preferences preferences;
    @Inject
    RegionStorage regionStorage;
    @Inject
    TrainerService trainerService;
    @Inject
    TrainerStorage trainerStorage;

    @Inject
    Provider<CreateTrainerController> createTrainerControllerProvider;
    @Inject
    Provider<PopUpController> popUpControllerProvider;
    @Inject
    Provider<HybridController> hybridControllerProvider;


    @Inject
    public ChooseSpriteController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        chooseTrainer.setText(translateString("choose_trainer"));
        saveSprite.setText(translateString("saveChanges"));

        // Retrieve the sprite index from the preferences
        currentSpriteIndex = preferences.getInt("currentSpriteIndex", 0);
        previousSpriteIndex = currentSpriteIndex;

        // Load the list of available sprites
        loadSpriteList();


        return parent;
    }

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
        List<String> preMadeCharacters = charactersList.subList(0, Math.min(charactersList.size(), 20));

        this.characters.addAll(preMadeCharacters);

        if (!charactersList.isEmpty()) {
            String selectedCharacter = charactersList.get(currentSpriteIndex);
            // Load the sprite image
            loadSprite(selectedCharacter);
        }
    }

    /**
     * Set up a subscription to fetch the character file for the selected character using the presetService
     */
    public void loadSprite(String selectedCharacter) {
        subscribe(
                presetService.getCharacterFile(selectedCharacter),
                response -> setSpriteImage(spriteContainer, spriteImage, 0, 3, response),
                this::handleError
        );
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
        if (currentSpriteIndex != previousSpriteIndex) {
            // Show a pop-up
            showPopUp(PopUpScenario.SAVE_CHANGES, result -> {
                if (!result) return;
                // Save the currentSpriteIndex to the preferences
                preferences.putInt("currentSpriteIndex", currentSpriteIndex);
                closeAndReturn();
                disposables.add(
                        trainerService.setImage(characters.get(currentSpriteIndex))
                                .observeOn(FX_SCHEDULER)
                                .subscribe(trainer -> {
                                }, this::handleError));
            });
        }
        closeAndReturn();
    }

    /**
     * Display a pop-up window with a specified scenario and a callback function
     */
    public void showPopUp(PopUpScenario scenario, ModalCallback callback) {
        isPopUpShown.set(true);
        PopUpController popUp = popUpControllerProvider.get();
        popUp.setScenario(scenario);
        popUp.showModal(callback);
        isPopUpShown.set(false);
    }

    private void closeAndReturn() {
        if (regionStorage.getRegion() == null) {
            createTrainerControllerProvider.get().setTrainerImage(characters.get(currentSpriteIndex));
            chooseTrainerContent.getChildren().clear();
            chooseTrainerContent.getChildren().setAll(createTrainerControllerProvider.get().render());
        } else {
            chooseTrainerContent.getChildren().clear();
            hybridControllerProvider.get().popTab();
        }
    }

}
