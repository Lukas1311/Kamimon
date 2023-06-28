package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Monster;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

public class EncounterOverviewController extends Controller {


    public ImageView background;
    public VBox userMonsters;
    public VBox opponentMonsters;

    @Inject
    Provider<UserMonsterStatusController> userMonsterStatusController;

    List<Monster> dummyMonsters = new ArrayList<>();

    @Inject
    public EncounterOverviewController() {
    }

    @Override
    public void init() {
        Monster amogus = new Monster(
                "9",
                "testTrainer",
                0,
                1,
                1,
                null,
                null,
                null
        );
        Monster zuendorn = new Monster(
                "109",
                "testTrainer",
                0,
                2,
                2,
                null,
                null,
                null
        );
        Monster angrian = new Monster(
                "10",
                "opponentTrainer",
                0,
                3,
                3,
                null,
                null,
                null
        );
        Monster sanddorm = new Monster(
                "78",
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
                UserMonsterStatusController userMonsterStatusController1 = userMonsterStatusController.get();
                userMonsterStatusController1.setMonster(monster);
                userMonsterStatusController1.loadMonsterDto(String.valueOf(monster.type()));
                userMonsters.getChildren().add(userMonsterStatusController1.render());
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
