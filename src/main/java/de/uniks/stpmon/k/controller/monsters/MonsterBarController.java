package de.uniks.stpmon.k.controller.monsters;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.service.InputHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class MonsterBarController extends Controller {

    @FXML
    public VBox monsterBar;
    @FXML
    public HBox monsterSlotsHBox;
    @FXML
    public VBox monsterList;
    @FXML
    public ImageView arrow;

    protected final ImageView[] monsterSlots = new ImageView[6];

    @Inject
    TeamController teamController;
    @Inject
    InputHandler inputHandler;

    @Inject
    public MonsterBarController() {
    }

    @Override
    public void init() {
        super.init();

        if (teamController == null) {
            return;
        }
        teamController.init();
        onDestroy(teamController::destroy);

        onDestroy(inputHandler.addPressedKeyHandler(event -> {
            if (event.getCode() == KeyCode.N) {
                showMonsters();
            }
        }));
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        loadImage(arrow, getResourcePath() + "monsterbar/monsterbarArrow.png");
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
            loadImage(monsterSlot, getResourcePath() + "monsterbar/freeSlot.png");
            monsterSlotsHBox.getChildren().add(monsterSlot);
            monsterSlot.setId("slot_" + i + "_free");
            monsterSlots[i] = monsterSlot;
        }
    }

    /**
     * Set the monster status for a given slot based on the current and maximum health points of the monster
     *
     * @param slot      The slot index of the monster
     * @param currentHP The current HP of the monster
     * @param maxHP     The maximum HP of the monster
     * @param empty     The slot is empty
     */
    public void setMonsterStatus(int slot, float currentHP, float maxHP, boolean empty) {
        if (slot < 0 || slot >= monsterSlots.length) {
            return;
        }

        ImageView monsterSlot = monsterSlots[slot];
        if (monsterSlot == null) {
            return;
        }

        if (empty) {
            loadImage(monsterSlot, getResourcePath() + "monsterbar/freeSlot.png");
            return;
        }

        if (currentHP <= 0) {
            monsterSlot.setId("slot_" + slot + "_zero");
            loadImage(monsterSlot, getResourcePath() + "monsterbar/healthPointsZero.png");
        } else if (currentHP/maxHP <= 0.33f) {
            loadImage(monsterSlot, getResourcePath() + "monsterbar/healthPointsLow.png");
            monsterSlot.setId("slot_" + slot + "_low");
        } else {
            loadImage(monsterSlot, getResourcePath() + "monsterbar/healthPointsNormal.png");
            monsterSlot.setId("slot_" + slot + "_normal");
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
            Parent monsterListContent = teamController.render();
            monsterList.getChildren().setAll(monsterListContent);
            monsterList.setVisible(true);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        monsterSlotsHBox.getChildren().clear();
        arrow = null;
        Arrays.fill(monsterSlots, null);
    }

    @Override
    public String getResourcePath() {
        return "monsters/";
    }

}
