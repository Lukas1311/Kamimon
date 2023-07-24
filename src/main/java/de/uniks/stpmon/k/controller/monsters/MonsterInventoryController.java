package de.uniks.stpmon.k.controller.monsters;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.IngameController;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.MonsterService;
import de.uniks.stpmon.k.service.TrainerService;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class MonsterInventoryController extends Controller {


    @FXML
    public GridPane monTeam;
    @FXML
    public GridPane monStorage;
    @FXML
    public AnchorPane monBoxMenuHolder;

    @Inject
    IResourceService resourceService;
    @Inject
    Provider<IngameController> ingameControllerProvider;
    @Inject
    TrainerService trainerService;
    @Inject
    MonsterService monsterService;

    private Monster activeMonster;
    private List<String> monTeamList = new ArrayList<>();
    private int monsterIndexStorage = 0;
    private String selectedMonster;

    private Parent monParent;

    @Inject
    public MonsterInventoryController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        targetGrid(monStorage);
        targetGrid(monTeam);
        subscribe(monsterService.getTeam().take(1), this::showTeamMonster);
        subscribe(monsterService.getTeam(), monsters -> {
            showTeamMonster(monsters);
            showMonsterList(monsterService.getMonsters().blockingFirst());
        });
        subscribe(monsterService.getMonsters(), this::showMonsterList);
        loadBgImage(monBoxMenuHolder, "MonBox_v6.png");

        return parent;
    }

    @Override
    public void destroy() {
        super.destroy();
        // Update team if leave monbox
        // Subscribe has to be in ingame controller to not be destroyed with this controller or not be destroyed at all
        ingameControllerProvider.get().subscribe(trainerService.setTeam(monTeamList));
    }

    private void showTeamMonster(List<Monster> monsters) {
        int monsterIndexTeam = 0;
        monTeamList = new ArrayList<>();
        monTeam.getChildren().clear();

        // Team Monster max 6 slots
        for (int i = 0; i < monsters.size(); i++) {
            Monster monster = monsters.get(i);
            Parent parent = createMonsterItem(monster);
            parent.setId("team_" + i);
            monTeam.add(parent, monsterIndexTeam, 0);
            monTeamList.add(monster._id());
            monsterIndexTeam++;
        }
    }

    private void showMonsterList(List<Monster> monsters) {
        List<Monster> currentMonsters = new ArrayList<>(monsters);
        List<Monster> teamMonsters = monsterService.getTeam().blockingFirst();
        int columnCount = 6;
        int rowCount = 4;
        currentMonsters.removeAll(teamMonsters);
        monStorage.getChildren().clear();
        monsterIndexStorage = 0;

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                if (monsterIndexStorage < currentMonsters.size()) {
                    Monster monster = currentMonsters.get(monsterIndexStorage);
                    Node node = createMonsterItem(monster);
                    node.setId("storage_" + row + "_" + column);
                    monStorage.add(node, column, row);
                    monsterIndexStorage++;
                }
            }
        }
    }

    private Parent createMonsterItem(Monster monster) {
        MonsterItemController mon = new MonsterItemController(monster, resourceService);
        Parent parent = mon.render();
        draggableMonItem(mon, monster._id());
        parent.setOnMouseClicked(e -> triggerMonsterInformation(monster));
        return parent;
    }

    public void triggerMonsterInformation(Monster monster) {
        if (activeMonster == null) {
            openMonsterInformation(monster);
        } else {
            if (activeMonster == monster) {
                closeMonsterInformation();
            } else {
                closeMonsterInformation();
                openMonsterInformation(monster);
            }
        }
    }

    private void openMonsterInformation(Monster monster) {
        activeMonster = monster;
        ingameControllerProvider.get().openMonsterInfo(monster);
    }

    private void closeMonsterInformation() {
        ingameControllerProvider.get().closeMonsterInfo();
        activeMonster = null;
    }

    private void draggableMonItem(MonsterItemController monItem, String id) {
        Parent parent = monItem.getParent();
        if (parent == null) {
            return;
        }
        parent.setOnDragDetected(event -> {
            Dragboard dragboard = parent.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            // string is used for testing, dragging images does not work in headless mode
            if (GraphicsEnvironment.isHeadless()) {
                content.putString("test_string");
            } else {
                content.putImage(monItem.getMonImage());
            }

            dragboard.setContent(content);
            monParent = parent;
            selectedMonster = id;
            event.consume();
        });

        parent.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                monParent = null;
            }
            event.consume();
        });
    }

    private void targetGrid(GridPane gridPane) {
        gridPane.setOnDragOver(event -> {
            if (event.getGestureSource() != gridPane && event.getDragboard().hasImage()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        gridPane.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            // string is used for testing, dragging images does not work in headless mode
            if (dragboard.hasImage() || dragboard.hasString()) {
                Parent parent = monParent;

                if (gridPane.equals(monStorage) && !monStorage.getChildren().contains(parent)) {
                    monTeamList.remove(selectedMonster);

                    monStorage.add(parent, monsterIndexStorage % 6, monsterIndexStorage / 6);
                    monsterIndexStorage++;

                    trainerService.temporaryApplyTeam(monTeamList);
                }
                if (gridPane.equals(monTeam)
                        && !monTeam.getChildren().contains(parent)
                        && monTeamList.size() < 6) {
                    monStorage.getChildren().remove(parent);

                    monTeamList.add(selectedMonster);
                    monsterIndexStorage--;
                    trainerService.temporaryApplyTeam(monTeamList);
                }
                event.setDropCompleted(true);
            }
            event.consume();
        });
    }

    @Override
    public String getResourcePath() {
        return "monsters/";
    }

}