package de.uniks.stpmon.k.controller.monsters;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.action.ActionFieldController;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.MonsterService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class MonsterSelectionController extends Controller {

    @FXML
    public AnchorPane monsterSelectionAnchorPane;
    @FXML
    public FlowPane monsterSelectionFlow;

    @Inject
    ActionFieldController actionFieldController;
    @Inject
    IResourceService resourceService;
    @Inject
    MonsterService monsterService;

    private int itemType;

    @Inject
    public MonsterSelectionController() {

    }

    @Override
    public Parent render(){
        final Parent parent = super.render();
        loadBgImage(monsterSelectionAnchorPane, "inventory/InventoryBox_v1.1.png");
        subscribe(monsterService.getTeam(), this::showTeamMonster);
        return parent;
    }

    private void showTeamMonster(List<Monster> monsterList) {
        monsterSelectionFlow.getChildren().clear();
        for (Monster monster : monsterList) {
            monsterSelectionFlow.getChildren().add(createMonsterItem(monster));
        }
    }

    public void useItem(String monsterId){
        if(itemType == 0 || actionFieldController == null) {
            return;
        }
        actionFieldController.executeItemMove(itemType, monsterId);
    }

    private Parent createMonsterItem(Monster monster) {
        MonsterItemController mon = new MonsterItemController(monster, resourceService);
        Parent parent = mon.render();

        parent.setOnMouseClicked(e -> useItem(monster._id()));
        return parent;
    }

    public void setItem(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public String getResourcePath() {
        return "monsters/";
    }

    @Override
    public void destroy(){
        if(monsterSelectionFlow != null) {
            monsterSelectionFlow.getChildren().clear();
        }
    }

}
