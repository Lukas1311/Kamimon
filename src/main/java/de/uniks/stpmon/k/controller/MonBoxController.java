package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.TrainerService;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.MonsterCache;
import de.uniks.stpmon.k.service.storage.cache.TrainerCache;
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
import java.util.ArrayList;
import java.util.List;

@Singleton
public class MonBoxController extends Controller {


    @FXML
    public GridPane monTeam;
    @FXML
    public GridPane monStorage;
    @FXML
    public AnchorPane monBoxMenuHolder;

    @Inject
    CacheManager cacheManager;
    @Inject
    TrainerStorage trainerStorage;
    @Inject
    IResourceService resourceService;
    @Inject
    Provider<IngameController> ingameControllerProvider;
    @Inject
    TrainerService trainerService;


    private MonsterCache monsterCache;
    private TrainerCache trainerCache;
    private Monster activeMonster;
    private List<String> monTeamList = new ArrayList<>();
    private int monsterIndexStorage = 0;
    private String selectedMonster;

    private Parent monParent;

    @Inject
    public MonBoxController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        Trainer trainer = trainerStorage.getTrainer();
        monsterCache = cacheManager.requestMonsters(trainer._id());
        trainerCache = cacheManager.trainerCache();
        targetGrid2(monStorage);
        targetGrid2(monTeam);
        subscribe(monsterCache.getTeam().getValues().take(1), this::showTeamMonster2);
        subscribe(monsterCache.getTeam().getValues(), monsters -> {
            showTeamMonster2(monsters);
            showMonsterList2(monsterCache.getValues().blockingFirst());
        });
        subscribe(monsterCache.getValues(), this::showMonsterList2);
        loadBgImage(monBoxMenuHolder, "MonBox_v6.png");

        return parent;
    }

    @Override
    public void destroy() {
        super.destroy();
        // Update team if leave monbox
        ingameControllerProvider.get().subscribe(trainerService.setTeam(monTeamList));
    }

    private void showTeamMonster2(List<Monster> monsters) {
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

    private void showMonsterList2(List<Monster> monsters) {
        List<Monster> currentMonsters = new ArrayList<>(monsters);
        List<Monster> teamMonsters = monsterCache.getTeam().getValues().blockingFirst();
        int columnCount = 6;
        int rowCount = 5;
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
        MonItemController mon = new MonItemController(monster, resourceService);
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
        ingameControllerProvider.get().removeChildren(2);
        activeMonster = null;
    }


    private void draggableMonItem(MonItemController monItem, String id) {
        Parent parent = monItem.getParent();
        if (parent == null) {
            return;
        }
        parent.setOnDragDetected(event -> {
            Dragboard dragboard = parent.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putImage(monItem.getMonImage());

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

    private void targetGrid2(GridPane gridPane) {
        gridPane.setOnDragOver(event -> {
            if (event.getGestureSource() != gridPane && event.getDragboard().hasImage()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        gridPane.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasImage()) {
                //ImageView imageView = monImage;
                Parent parent = monParent;

                if (gridPane.equals(monStorage) && !monStorage.getChildren().contains(parent)) {
                    monTeamList.remove(selectedMonster);

                    monStorage.add(parent, monsterIndexStorage, 0);
                    monsterIndexStorage++;

                    Trainer trainer = trainerStorage.getTrainer();
                    Trainer newTrainer = TrainerBuilder.builder(trainer).addTeam(monTeamList).create();
                    trainerCache.updateValue(newTrainer);
                }
                if (gridPane.equals(monTeam)
                        && !monTeam.getChildren().contains(parent)
                        && monTeamList.size() < 6) {
                    monStorage.getChildren().remove(parent);

                    monTeamList.add(selectedMonster);
                    monsterIndexStorage--;
                    Trainer trainer = trainerStorage.getTrainer();
                    Trainer newTrainer = TrainerBuilder.builder(trainer).addTeam(monTeamList).create();
                    trainerCache.updateValue(newTrainer);
                }
                event.setDropCompleted(true);
            }
            event.consume();
        });
    }

}
