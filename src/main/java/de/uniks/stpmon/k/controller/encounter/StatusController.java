package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.EncounterMember;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.SessionService;
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
    SessionService sessionService;

    public EncounterMember member;

    @Inject
    public StatusController() {
    }

    public void setMember(EncounterMember member) {
        this.member = member;
    }

    @Override
    public Parent render() {
        final Parent parent;
        if (member.isSelf()) {
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
        // Initial state
        updateState(sessionService.getMonster(member));
        // used to get the monster information for the monster of the trainer in the active region
        subscribe(sessionService.listenMonster(member), this::updateState);
    }

    private void updateState(Monster monster) {
        loadMonsterDto(Integer.toString(monster.type()));

        monsterLevel.setText("Lvl. " + monster.level().toString());

        double maxHp = monster.attributes().health();
        double currentHp = monster.currentAttributes().health();
        double hpProgress = currentHp / maxHp;

        hpBar.setProgress(hpProgress);

        if (member.isSelf()) {
            monsterHp.setText(monster.currentAttributes().health() + " / " + monster.attributes().health());

            double maxExp = Math.pow(monster.level(), 3) - Math.pow(monster.level() - 1, 3);
            double currentExp = monster.experience();
            double expProgress = currentExp / maxExp;

            experienceBar.setProgress(expProgress);
        }
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
