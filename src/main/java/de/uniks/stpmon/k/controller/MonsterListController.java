package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.MonsterCache;
import de.uniks.stpmon.k.service.storage.cache.MonsterTypeCache;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class MonsterListController extends Controller {
    @FXML
    public VBox monsterListVBox;

    MonsterCache monsterCache;
    @Inject
    CacheManager cacheManager;
    MonsterTypeCache monsterTypeCache;
    @Inject
    TrainerStorage trainerStorage;

    @Inject
    public MonsterListController() {
    }

    @Override
    public void init() {
        super.init();

        if (cacheManager == null) {
            return;
        }
        monsterTypeCache = cacheManager.monsterTypeCache();

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

    @Override
    public Parent render() {
        Parent render = super.render();
        // Does not block, because the cache is already initialized
        showMonsterList(monsterCache.getValues().blockingFirst());
        return render;
    }

    /**
     * Set the list of monsters in the monsterListVBox
     */
    public void showMonsterList(List<Monster> monsters) {
        // Not loaded yet
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
                Optional<MonsterTypeDto> type = monsterTypeCache != null
                        ? monsterTypeCache.getValue(String.valueOf(monster.type()))
                        : Optional.empty();
                monsterLabel.setText(type.map(MonsterTypeDto::name).orElse(monster._id()));
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
        if (monsterListVBox != null) {
            monsterListVBox.getChildren().clear();
        }

    }
}
