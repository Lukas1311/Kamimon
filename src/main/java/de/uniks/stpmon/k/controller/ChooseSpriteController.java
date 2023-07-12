package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.popup.ModalCallback;
import de.uniks.stpmon.k.controller.popup.PopUpController;
import de.uniks.stpmon.k.controller.popup.PopUpScenario;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.TrainerService;
import de.uniks.stpmon.k.service.world.TextureSetService;
import de.uniks.stpmon.k.utils.Direction;
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

    protected final ObservableList<String> characters = FXCollections.observableArrayList();
    private final BooleanProperty isPopUpShown = new SimpleBooleanProperty(false);

    private boolean isCreation = true;

    protected int currentSpriteIndex;
    protected int previousSpriteIndex;

    @FXML
    public Text chooseTrainer;
    @FXML
    public StackPane spriteContainer;
    @FXML
    public ImageView spriteImage;
    @FXML
    public Button saveSprite;
    @FXML
    public BorderPane chooseTrainerContent;

    @Inject
    PresetService presetService;
    @Inject
    Preferences preferences;
    @Inject
    TrainerService trainerService;

    @Inject
    Provider<CreateTrainerController> createTrainerControllerProvider;
    @Inject
    Provider<PopUpController> popUpControllerProvider;
    @Inject
    Provider<HybridController> hybridControllerProvider;
    @Inject
    TextureSetService textureService;


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
     * Set the creation mode. If true, the CreateTrainerController is shown after saving.
     *
     * @param isCreation true if the CreateTrainerController should be shown after saving
     */
    public void setCreationMode(boolean isCreation) {
        this.isCreation = isCreation;
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
        this.characters.setAll(charactersList);

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
                setSpriteImage(spriteContainer, spriteImage, Direction.BOTTOM, selectedCharacter, textureService),
                this::handleError
        );
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

}
