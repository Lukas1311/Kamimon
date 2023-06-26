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

    List<Monster> dummyMonster = new ArrayList<>();

    @Inject
    public EncounterOverviewController() {
    }

    @Override
    public void init() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        loadImage(background, "encounter/FOREST.png");

        UserMonsterStatusController userMonsterStatusController = new UserMonsterStatusController();
        UserMonsterStatusController userMonsterStatusController2 = new UserMonsterStatusController();
        userMonsters.getChildren().add(userMonsterStatusController.render());
        userMonsters.getChildren().add(userMonsterStatusController2.render());

        return parent;
    }

    @Override
    public String getResourcePath() {
        return "encounter/";
    }
}
