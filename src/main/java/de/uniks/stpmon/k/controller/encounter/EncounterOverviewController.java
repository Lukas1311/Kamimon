package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Monster;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class EncounterOverviewController extends Controller {


    public ImageView background;
    public VBox userMonsters;
    public VBox opponentMonsters;

    List<Monster> dummyMonsters = new ArrayList<>();

    @Inject
    public EncounterOverviewController() {
    }

    @Override
    public void init() {
        Monster amogus = new Monster(
                "Amogus",
                "testTrainer",
                0,
                1,
                1,
                null,
                null,
                null
        );
        Monster zuendorn = new Monster(
                "Zuendorn",
                "testTrainer",
                0,
                2,
                2,
                null,
                null,
                null
        );
        Monster angrian = new Monster(
                "Angrian",
                "opponentTrainer",
                0,
                3,
                3,
                null,
                null,
                null
        );
        Monster sanddorm = new Monster(
                "Sanddorm",
                "opponentTrainer",
                0,
                4,
                4,
                null,
                null,
                null
        );
        dummyMonsters.add(amogus);
        dummyMonsters.add(zuendorn);
        dummyMonsters.add(angrian);
        dummyMonsters.add(sanddorm);
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        loadImage(background, "encounter/FOREST.png");

        renderMonsters();

        return parent;
    }

    private void renderMonsters() {
        for (Monster monster : dummyMonsters) {
            if (monster.trainer().equals("testTrainer")) {
                UserMonsterStatusController userMonsterStatusController = new UserMonsterStatusController(monster);
                userMonsterStatusController.init();
                userMonsters.getChildren().add(userMonsterStatusController.render());
            } else {
                OpponentMonsterStatusController opponentMonsterStatusController = new OpponentMonsterStatusController(monster);
                opponentMonsterStatusController.init();
                opponentMonsters.getChildren().add(opponentMonsterStatusController.render());
            }
        }
    }

    @Override
    public String getResourcePath() {
        return "encounter/";
    }
}
