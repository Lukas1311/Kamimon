package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.MonsterService;
import de.uniks.stpmon.k.service.PresetService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;

public class MonsterListController extends Controller {
    @FXML
    public VBox monsterListVBox;
    @FXML
    public VBox monsterInformation;

    @Inject
    PresetService presetService;
    @Inject
    MonsterService monsterService;
    @Inject
    MonsterInformationController monsterInformationController;
    @Inject
    Provider<MonsterBarController> monsterBarControllerProvider;

    @Inject
    public MonsterListController() {
    }

    @Override
    public void init() {
        super.init();

        subscribe(monsterService.getTeam(), this::updateListContent);

        updateStatus();
    }

    @Override
    public Parent render() {
        Parent render = super.render();
        monsterInformation.setVisible(false);
        // Does not block, because the cache is already initialized
        updateListContent(monsterService.getTeam().blockingFirst());
        return render;
    }

    /**
     * Updates the container text in the monster list.
     */
    public void updateListContent(List<Monster> monsters) {
        updateStatus();

        if (monsterListVBox == null) {
            return;
        }
        monsterListVBox.getChildren().clear();
        for (int slot = 0; slot < Math.max(monsters.size(), 6); slot++) {
            Label monsterLabel = new Label();
            monsterLabel.setId("monster_label_" + slot);
            Monster monster = slot >= monsters.size() ? null : monsters.get(slot);
            // Display monster id if monster exists
            if (monster != null) {
                subscribe(presetService.getMonster(String.valueOf(monster.type())), type -> {
                    monsterLabel.setText(type.name());
                    monsterLabel.setOnMouseClicked(event -> showInformation(monster, monsterLabel));
                });
            } else {
                // Display "<free>" if no monster exists
                monsterLabel.setText("<" + translateString("free") + ">");
            }
            monsterListVBox.getChildren().add(monsterLabel);
        }
    }

    public void showInformation(Monster monster, Label monsterLabel) {
        if (monsterInformation.isVisible() && monsterInformation.isVisible()) {
            monsterInformation.setVisible(false);
            subscribe(presetService.getMonster(String.valueOf(monster.type())),
                    type -> monsterLabel.setText(type.name()));
        } else {
            // Render the monster information
            Parent monsterInformationContent = monsterInformationController.render();
            monsterInformationController.loadMonsterTypeDto(String.valueOf(monster.type()));
            monsterInformationController.loadMonster(monster);
            monsterInformation.getChildren().setAll(monsterInformationContent);
            monsterInformation.setVisible(true);
            monsterLabel.setText("> " + monsterLabel.getText());
        }
    }

    /**
     * Updates the monster status in the monster list.
     */
    public void updateStatus() {
        List<Monster> monsters = monsterService.getTeam().blockingFirst();
        for (int slot = 0; slot < monsters.size(); slot++) {
            Monster monster = monsters.get(slot);
            int currentHP = monster.currentAttributes().health();
            int maxHP = monster.attributes().health();
            monsterBarControllerProvider.get().setMonsterStatus(slot, currentHP, maxHP);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (monsterListVBox != null) {
            monsterListVBox.getChildren().clear();
        }
    }
}
