package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.MonsterService;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import javax.inject.Inject;

public class MonsterListController extends ToastController {
    public VBox monsterListVBox;

    @Inject
    MonsterService monsterService;

    @Inject
    public MonsterListController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        showMonsterList();
        return parent;
    }

    /**
     * Set the list of monsters in the monsterListVBox
     */
    public void showMonsterList() {
        monsterListVBox.getChildren().clear();
        for (int slot = 0; slot < 6; slot++) {
            Label monsterLabel = new Label();
            String monsterId = Integer.toString(slot + 1);
            Monster monster = monsterService.getMonster(monsterId);
            // Display monster id if monster exists
            if (monster != null) {
                monsterLabel.setText(monster._id());
            } else {
                // Display "<free>" if no monster exists
                monsterLabel.setText("<" + translateString("free") + ">");
            }
            monsterListVBox.getChildren().add(monsterLabel);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        monsterListVBox.getChildren().clear();
    }
}
