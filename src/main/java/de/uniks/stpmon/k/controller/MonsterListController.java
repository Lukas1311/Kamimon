package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.MonsterCache;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import java.util.List;

public class MonsterListController extends Controller {
    public VBox monsterListVBox;

    @Inject
    MonsterCache monsterCache;
    @Inject
    CacheManager cacheManager;
    @Inject
    TrainerStorage trainerStorage;

    @Inject
    public MonsterListController() {
    }

    @Override
    public void init() {
        super.init();

        if (trainerStorage == null) {
            return;
        }
        Trainer trainer = trainerStorage.getTrainer();
        if (trainer == null) {
            return;
        }
        monsterCache = cacheManager.requestMonsters(trainer._id());
        subscribe(monsterCache.getValues(), this::showMonsterList);
    }

    /**
     * Set the list of monsters in the monsterListVBox
     */
    public void showMonsterList(List<Monster> monsters) {
        monsterListVBox.getChildren().clear();
        for (int slot = 0; slot < Math.max(monsters.size(), 6); slot++) {
            Label monsterLabel = new Label();
            Monster monster = slot >= monsters.size() ? null : monsters.get(slot);
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
