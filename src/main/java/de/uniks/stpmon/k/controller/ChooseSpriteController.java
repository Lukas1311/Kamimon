package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.popup.ModalCallback;
import de.uniks.stpmon.k.controller.popup.PopUpController;
import de.uniks.stpmon.k.controller.popup.PopUpScenario;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.views.ConfigHelper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import okhttp3.ResponseBody;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Provider;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class ChooseSpriteController extends ToastedController {
    private final ObservableList<String> characters = FXCollections.observableArrayList();
    private BooleanProperty isPopUpShown = new SimpleBooleanProperty(false);

    private int currentSpriteIndex;
    private int previousSpriteIndex;

    @FXML
    public Text chooseTrainer;
    @FXML
    public Button spriteLeft;
    @FXML
    public ImageView spriteImage;
    @FXML
    public Button spriteRight;
    @FXML
    public Button saveSprite;
    @FXML
    public VBox chooseTrainerContent;

    @Inject
    PresetService presetService;

    @Inject
    Provider<CreateTrainerController> createTrainerControllerProvider;
    @Inject
    Provider<PopUpController> popUpControllerProvider;

    @Inject
    public ChooseSpriteController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        chooseTrainer.setText(translateString("choose_trainer"));
        saveSprite.setText(translateString("saveChanges"));

        // Retrieve the sprite index from the configuration file
        currentSpriteIndex = ConfigHelper.getSpriteIndex();
        previousSpriteIndex = ConfigHelper.getSpriteIndex();
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
                .subscribe(this::getCharactersList, this::handleError));
    }

    /**
     * Update the characters list with the names of the available characters
     * If there are characters, load the sprite image for the currently selected character based on the currentSpriteIndex
     */
    public void getCharactersList(List<String> charactersList) {
        this.characters.addAll(charactersList);

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
        disposables.add(presetService.getCharacterFile(selectedCharacter)
                .observeOn(FX_SCHEDULER)
                .subscribe(this::setSpriteImage, this::handleError));
    }

    /**
     * Processes the ResponseBody containing the image data for the sprite
     */
    public void setSpriteImage(ResponseBody responseBody) {
        if (responseBody != null) {
            try (responseBody) {
                // Read the image data from the response body and create a BufferedImage
                ByteArrayInputStream inputStream = new ByteArrayInputStream(responseBody.bytes());
                BufferedImage bufferedImage = ImageIO.read(inputStream);
                int spriteWidth = 20;
                int spriteHeight = 35;
                int spriteX = 48;
                int spriteY = 0;
                // extract the sprite from the original image
                BufferedImage image = bufferedImage.getSubimage(spriteX, spriteY, spriteWidth, spriteHeight);
                // Convert the BufferedImage to JavaFX Image
                Image fxImage = SwingFXUtils.toFXImage(image, null);
                // Set the image
                spriteImage.setImage(fxImage);
            } catch (IOException e) {
                handleError(e);
            }
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
        // TODO: Test implementieren
        /*
        String selectedCharacter = characters.get(currentSpriteIndex);
        String previousCharacter = characters.get(previousSpriteIndex);
        if (!selectedCharacter.equals(previousCharacter)) {
            // Show a pop-up
            showPopUp(PopUpScenario.SAVE_CHANGES, result -> {
                if (!result) return;
                // Save the currentSpriteIndex to the configuration file
                ConfigHelper.saveSpriteIndex(currentSpriteIndex);
                // Render the createTrainerController
                chooseTrainerContent.getChildren().clear();
                chooseTrainerContent.getChildren().setAll(createTrainerControllerProvider.get().render());
            });
        } else {
            // Save the previousSpriteIndex to the configuration file
            ConfigHelper.saveSpriteIndex(previousSpriteIndex);
            // Render the createTrainerController
            chooseTrainerContent.getChildren().clear();
            chooseTrainerContent.getChildren().setAll(createTrainerControllerProvider.get().render());
        }

         */
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
