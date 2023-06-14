package de.uniks.stpmon.k.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.inject.Inject;

public class MonsterBarController extends Controller {
    @FXML
    public VBox monsterBar;
    @FXML
    public HBox monsterSlotsHBox;
    @FXML
    public VBox monsterList;

    protected ImageView[] monsterSlots = new ImageView[6];

    @Inject
    MonsterListController monsterListController;

    @Inject
    public MonsterBarController() {
    }

    @Override
    public void init() {
        super.init();

        if (monsterListController == null) {
            return;
        }
        monsterListController.init();
        onDestroy(monsterListController::destroy);
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
        for (int i = 0; i < monsterSlots.length; i++) {
            ImageView monsterSlot = new ImageView();
            monsterSlot.setFitHeight(30);
            monsterSlot.setFitWidth(30);
            loadImage(monsterSlot, "freeSlot.png");
            monsterSlotsHBox.getChildren().add(monsterSlot);
            monsterSlots[i] = monsterSlot;
        }
    }

    /**
     * Set the monster status for a given slot based on the current and maximum health points of the monster
     *
     * @param slot      The slot index of the monster
     * @param currentHP The current HP of the monster
     * @param maxHP     The maximum HP of the monster
     */
    public void setMonsterStatus(int slot, int currentHP, int maxHP) {
        if (slot < 0 || slot >= monsterSlots.length) {
            return;
        }

        ImageView monsterSlot = monsterSlots[slot];
        if (currentHP <= 0) {
            loadImage(monsterSlot, "healthPointsZero.png");
        } else if (currentHP < maxHP * 0.2) {
            loadImage(monsterSlot, "healthPointsLow.png");
        } else {
            loadImage(monsterSlot, "healthPointsNormal.png");
        }
    }

    /**
     * Show the list of monsters when clicked on monsterBar
     * Hide this list when clicked on monsterBar again
     */
    public void showMonsters() {
        if (monsterList.isVisible()) {
            monsterList.setVisible(false);
        } else {
            // Render the monster list
            Parent monsterListContent = monsterListController.render();
            monsterList.getChildren().setAll(monsterListContent);
            monsterList.setVisible(true);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        monsterSlotsHBox.getChildren().clear();
    }
}
