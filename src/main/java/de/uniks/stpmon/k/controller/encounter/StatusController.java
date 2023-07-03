package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.TrainerService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.cache.ICache;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;

public class StatusController extends Controller {
    @FXML
    public VBox fullBox;
    @FXML
    public ImageView monsterStatusView;
    @FXML
    public ProgressBar hpBar;
    @FXML
    public Text monsterName;
    @FXML
    public Text monsterLevel;
    @FXML
    public Text monsterHp;
    @FXML
    public ProgressBar experienceBar;

    @Inject
    PresetService presetService;
    @Inject
    RegionService regionService;
    @Inject
    RegionStorage regionStorage;
    @Inject
    TrainerService trainerService;

    public Monster monster;
    private ICache<Monster, String> monsterCache;

    @Inject
    public StatusController() {
    }

    public void setMonster(Monster monster) {
        this.monster = monster;
    }

    public void setMonsterCache(ICache<Monster, String> monsterCache) {
        this.monsterCache = monsterCache;
    }

    @Override
    public Parent render() {
        final Parent parent;
        if (monster.trainer().equals(trainerService.getMe()._id())) {
            parent = load("UserMonsterStatus");
            loadImage(monsterStatusView, "encounter/userMonsterStatus.png");
            loadMonsterInformation();
        } else {
            parent = load("OpponentMonsterStatus");
            loadImage(monsterStatusView, "encounter/opponentMonsterStatus.png");
            loadMonsterInformation();
        }
        return parent;
    }

    public void loadMonsterInformation() {
        // used to get the monster information for the monster of the trainer in the active region
        disposables.add(monsterCache.listenValue(monster._id())
                .observeOn(FX_SCHEDULER)
                .subscribe(optMonster -> {
                    if (optMonster.isEmpty()) {
                        return;
                    }
                    Monster monster1 = optMonster.get();
                    monsterHp.setText(monster1.currentAttributes().health() + " / " + monster1.attributes().health());
                    monsterLevel.setText("Lvl. " + monster1.level().toString());

                    double maxHp = monster1.attributes().health();
                    double currentHp = monster1.currentAttributes().health();
                    double hpProgress = currentHp / maxHp;

                    hpBar.setProgress(hpProgress);

                    if (trainerService.getMe().team().contains(monster1._id())) {
                        double maxExp = Math.pow(monster1.level(), 3) - Math.pow(monster1.level() - 1, 3);
                        double currentExp = monster1.experience();
                        double expProgress = currentExp / maxExp;

                        experienceBar.setProgress(expProgress);
                    }
                }));
    }


    public void loadMonsterDto(String id) {
        disposables.add(presetService.getMonster(id)
                .observeOn(FX_SCHEDULER)
                .subscribe(monsterTypeDto -> monsterName.setText(monsterTypeDto.name())));
    }

    @Override
    public String getResourcePath() {
        return "encounter/";
    }
}
