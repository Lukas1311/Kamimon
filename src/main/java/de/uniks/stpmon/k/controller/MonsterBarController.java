package de.uniks.stpmon.k.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import java.util.Objects;

public class MonsterBarController extends Controller {
    @FXML
    public VBox monsterBar;
    @FXML
    public HBox monsterSlotsHBox;

    protected ImageView[] monsterSlots;

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
     * Show the list of monsters when clicked on monsterBar.
     */
    public void showMonsters() {
    }
}