package de.uniks.stpmon.k.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;

import javax.inject.Inject;
import java.util.Objects;

public class MonsterBarController extends Controller {
    @FXML
    public VBox monsterBar;
    @FXML
    public HBox monsterSlotsHBox;

    protected ImageView[] monsterSlots;
    protected Popup monsterListPopup;

    @Inject
    MonsterListController monsterListController;

    @Inject
    public MonsterBarController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        createMonsterSlots();
        return parent;
    }

    /**
     * Create six slots to accommodate monsters and initialize them with the image "freeSlot.png"
     */
    public void createMonsterSlots() {
        monsterSlotsHBox.getChildren().clear();
        monsterSlots = new ImageView[6];
        for (int i = 0; i < 6; i++) {
            ImageView monsterSlot = new ImageView();
            monsterSlot.setFitHeight(30);
            monsterSlot.setFitWidth(30);
            monsterSlot.setImage(loadImage("freeSlot.png"));
            monsterSlotsHBox.getChildren().add(monsterSlot);
            monsterSlots[i] = monsterSlot;
        }
    }

    /**
     * Set the monster status for a given slot based on the current and maximum health points of the monster
     * @param slot The slot index of the monster
     * @param currentHP The current HP of the monster
     * @param maxHP The maximum HP of the monster
     */
    public void setMonsterStatus(int slot, int currentHP, int maxHP) {
        if (slot < 0 || slot >= monsterSlots.length) {
            return;
        }

        ImageView monsterSlot = monsterSlots[slot];
        if(currentHP <= 0) {
            monsterSlot.setImage(loadImage("healthPointsZero.png"));
        } else if (currentHP < maxHP * 0.2) {
            monsterSlot.setImage(loadImage("healthPointsLow.png"));
        } else {
            monsterSlot.setImage(loadImage("healthPointsNormal.png"));
        }
    }

    /**
     * Load an image using its file path
     */
    public Image loadImage(String image) {
        return new Image(Objects.requireNonNull(MonsterBarController.class.getResource(image)).toString());
    }

    /**
     * Show the list of monsters when clicked on monsterBar
     * Hide this list when clicked on monsterBar again
     */
    public void showMonsters() {
        if (monsterListPopup == null) {
            Parent monsterList = monsterListController.render();
            // Create a new popup and add the monster list to its content
            monsterListPopup = new Popup();
            // Set the autoHide property of the popup to false, so it does not automatically hide
            monsterListPopup.setAutoHide(false);
            monsterListPopup.getScene().setRoot(monsterList);

            Window window = monsterBar.getScene().getWindow();
            // Add listeners whenever the x or y property changes
            window.xProperty().addListener((observable, oldValue, newValue) -> updatePosition());
            window.yProperty().addListener((observable, oldValue, newValue) -> updatePosition());

            monsterListPopup.setOnShown(event -> updatePosition());
        }

        // If the monster list is already showing, hide it when clicked on the monster bar
        if (monsterListPopup.isShowing()) {
            monsterListPopup.hide();
        } else {
            Platform.runLater(() -> {
                updatePosition();
                monsterListPopup.show(monsterBar.getScene().getWindow());
            });
        }

        monsterBar.getScene().setOnMousePressed(event -> {
            // Check if the click event is outside the bounds of the monsterBar
            if (!monsterBar.getBoundsInParent().contains(event.getX(), event.getY())) {
                monsterListPopup.hide();
            }
        });
    }

    /**
     * Update the position of the monsterListPopup
     */
    private void updatePosition() {
        // Convert the local coordinates of the monsterBar to screen coordinates
        if (monsterBar != null && monsterListPopup != null) {
            double x = monsterBar.localToScreen(monsterBar.getBoundsInLocal()).getMinX() + 30;
            double y = monsterBar.localToScreen(monsterBar.getBoundsInLocal()).getMaxY();
            monsterListPopup.setX(x);
            monsterListPopup.setY(y);
        }
    }

    @Override
    public void destroy() {
        monsterSlotsHBox.getChildren().clear();
    }
}
