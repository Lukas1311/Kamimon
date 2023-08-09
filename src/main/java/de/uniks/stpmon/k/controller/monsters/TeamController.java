package de.uniks.stpmon.k.controller.monsters;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.MonsterService;
import de.uniks.stpmon.k.service.PresetService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;

public class TeamController extends Controller {

    @FXML
    public VBox monsterListVBox;
    @FXML
    public VBox monsterInformation;
    @FXML
    public ImageView arrowImageView;

    @Inject
    PresetService presetService;
    @Inject
    MonsterService monsterService;
    @Inject
    MonsterInformationController monsterInformationController;
    @Inject
    Provider<MonsterBarController> monsterBarControllerProvider;

    @Inject
    public TeamController() {
    }

    @Override
    public void init() {
        super.init();
        subscribe(monsterService.getTeam(), this::updateListContent);
    }

    @Override
    public Parent render() {
        Parent render = super.render();
        loadImage(arrowImageView, "arrow_up.png");
        loadBgImage(monsterListVBox, getResourcePath() + "TeamBox.png");

        monsterInformation.setVisible(false);
        updateListContent(monsterService.getTeamList());
        return render;
    }

    /**
     * Updates the container text in the monster list.
     */
    public void updateListContent(List<Monster> monsters) {
        for (int slot = 0; slot < 6; slot++) {
            Monster monster = slot >= monsters.size() ? null : monsters.get(slot);
            float currentHP = monster == null ? 0 : monster.currentAttributes().health();
            float maxHP = monster == null ? 0 : monster.attributes().health();
            monsterBarControllerProvider.get().setMonsterStatus(slot, currentHP, maxHP, monster == null);
        }

        if (monsterListVBox == null) {
            return;
        }
        monsterListVBox.getChildren().clear();
        for (int slot = 0; slot < Math.max(monsters.size(), 6); slot++) {
            Label monsterLabel = new Label();
            monsterLabel.getStyleClass().addAll("ingame", "team-monster-entry", "backpack-menu-entry");
            monsterLabel.setId("monster_label_" + slot);
            Monster monster = slot >= monsters.size() ? null : monsters.get(slot);
            // Display monster id if monster exists
            if (monster != null) {
                subscribe(presetService.getMonster(String.valueOf(monster.type())), type -> {
                    monsterLabel.setText("  " + type.name());
                    monsterLabel.setOnMouseClicked(event -> showInformation(monster));
                    monsterLabel.setOnMouseEntered(event ->
                            monsterLabel.setText(monsterLabel.getText().replace("  ", "> ")));
                    monsterLabel.setOnMouseExited(event ->
                            monsterLabel.setText(monsterLabel.getText().replace("> ", "  ")));
                });
            } else {
                monsterLabel.setText("  -");
            }
            monsterListVBox.getChildren().add(monsterLabel);
        }
    }

    public void showInformation(Monster monster) {
        if (monsterInformation.isVisible() && monsterInformation.isVisible()) {
            monsterInformation.setVisible(false);
        } else {
            // Render the monster information
            Parent monsterInformationContent = monsterInformationController.render();
            monsterInformationController.loadMonsterTypeDto(String.valueOf(monster.type()));
            monsterInformationController.loadMonster(monster);
            monsterInformation.getChildren().setAll(monsterInformationContent);
            monsterInformation.setVisible(true);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (monsterListVBox != null) {
            monsterListVBox.getChildren().clear();
        }
        arrowImageView = null;
    }

    @Override
    public String getResourcePath() {
        return "monsters/";
    }

}
