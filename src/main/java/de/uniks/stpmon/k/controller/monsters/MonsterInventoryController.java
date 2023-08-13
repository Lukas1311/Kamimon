package de.uniks.stpmon.k.controller.monsters;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.IngameController;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.ItemService;
import de.uniks.stpmon.k.service.MonsterService;
import de.uniks.stpmon.k.service.TrainerService;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import retrofit2.HttpException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Singleton
public class MonsterInventoryController extends Controller {

    public static final int TEAM_SIZE = 6;
    public static final int ROW_COUNT = 4;
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
    @Inject
    ItemService itemService;

    private Monster activeMonster;
    private List<String> monTeamList;
    private int monsterIndexStorage = 0;
    private int rowOffset = 0;
    private String selectedMonster;

    private Parent monParent;

    private boolean isSelectionMode = false;

    @Inject
    public MonsterInventoryController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        parent.setId("monsterInventory");

        monStorage.setOnScroll(event -> {
            int oldOffset = rowOffset;
            if (event.getDeltaY() < 0) {
                rowOffset += 1;
            } else {
                rowOffset -= 1;
            }
            List<Monster> monsters = monsterService.getMonsterList();
            rowOffset = Math.max(0, Math.min(rowOffset, (int) Math.ceil(monsters.size() / 6f) - 4));
            if (oldOffset != rowOffset) {
                showMonsterList(monsters);
            }
        });
        handleDrag(monStorage, this::dragIntoStorageGrid);
        handleDrag(monTeam, this::dragIntoTeamGrid);
        subscribe(monsterService.getTeam().take(1), this::showTeamMonster);
        subscribe(monsterService.getTeam(), monsters -> {
            showTeamMonster(monsters);
            showMonsterList(monsterService.getMonsterList());
        });
        subscribe(monsterService.getMonsters(), this::showMonsterList);
        loadBgImage(monBoxMenuHolder, getResourcePath() + "MonBox_v6.1.png");

        return parent;
    }

    @Override
    public void destroy() {
        super.destroy();
        if (monTeamList != null) {
            // Update team if leave monbox
            // Subscribe has to be in ingame controller to not be destroyed with this controller or not be destroyed at all
            ingameControllerProvider.get().subscribe(trainerService.setTeam(monTeamList));
        }
        monParent = null;
        monTeam = null;
        monStorage = null;
        monBoxMenuHolder = null;
    }

    public void setSelectionMode(boolean isSelectionMode) {
        this.isSelectionMode = isSelectionMode;
    }

    private void showTeamMonster(List<Monster> monsters) {
        monTeamList = new ArrayList<>();
        monTeam.getChildren().clear();

        // Team Monster max 6 slots
        for (int i = 0; i < monsters.size(); i++) {
            Monster monster = monsters.get(i);
            Parent parent = createMonsterItem(monster);
            parent.setId("team_" + i);
            monTeam.add(parent, i, 0);
            monTeamList.add(monster._id());
        }
    }

    private void showMonsterList(List<Monster> monsters) {
        List<Monster> currentMonsters = new ArrayList<>(monsters);
        List<Monster> teamMonsters = monsterService.getTeamList();
        currentMonsters.removeAll(teamMonsters);
        monStorage.getChildren().clear();
        monsterIndexStorage = rowOffset * TEAM_SIZE;

        for (int row = 0; row < ROW_COUNT; row++) {
            for (int column = 0; column < TEAM_SIZE; column++) {
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

        parent.setOnMouseClicked(e -> {
            if (isSelectionMode && itemService != null) {
                subscribe(itemService.useActiveItemIfAvailable(monster._id()), item -> {
                    ingameControllerProvider.get().removeChildren(2);
                    setSelectionMode(false);
                }, error -> {
                    if (error instanceof HttpException) {
                        System.out.println("cannot use the item on this monster");
                    }
                });
            } else {
                triggerMonsterInformation(monster);
            }

        });
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
        targetTeam(parent);
    }

    private void shiftToStorage() {
        monTeamList.remove(selectedMonster);

        monStorage.add(monParent, monsterIndexStorage % TEAM_SIZE, monsterIndexStorage / TEAM_SIZE);
        monsterIndexStorage++;

        trainerService.temporaryApplyTeam(monTeamList);
    }

    private boolean isInvalidDrag(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        // string is used for testing, dragging images does not work in headless mode
        return !dragboard.hasImage() && !dragboard.hasString();
    }

    private void handleDrag(Parent parent, Consumer<? super DragEvent> callback) {
        parent.setOnDragOver(event -> {
            if (event.getGestureSource() != parent && event.getDragboard().hasImage()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
        parent.setOnDragDropped(event -> {
            event.consume();
            if (isInvalidDrag(event)) {
                return;
            }
            callback.accept(event);
        });
    }

    private void targetTeam(Parent monsterItem) {
        handleDrag(monsterItem, (event) -> {
            // Move to storage, if monster under the cursor is in the storage
            if (monsterItem.getId().startsWith("storage_")) {
                shiftToStorage();
            }
            if (monsterItem.getId().startsWith("team_")) {
                boolean alreadyInTeam = monTeam.getChildren().contains(monParent);
                boolean teamIsFull = monTeamList.size() >= TEAM_SIZE;
                if (teamIsFull && !alreadyInTeam) {
                    int monsterIndex = monTeam.getChildren().indexOf(monsterItem);
                    monTeamList.set(monsterIndex, selectedMonster);
                    monTeam.getChildren().remove(monsterIndex);
                } else {
                    // Remove first to avoid duplicates
                    if (alreadyInTeam) {
                        monTeamList.remove(selectedMonster);
                    }
                    int monsterIndex = monTeam.getChildren().indexOf(monsterItem);
                    monStorage.getChildren().remove(monParent);
                    if (!alreadyInTeam) {
                        monsterIndexStorage--;
                    }
                    monTeamList.add(monsterIndex, selectedMonster);
                }
                trainerService.temporaryApplyTeam(monTeamList);
            }
            event.setDropCompleted(true);
        });
    }

    private void dragIntoTeamGrid(DragEvent event) {
        if (!monTeam.getChildren().contains(monParent)
                && monTeamList.size() < TEAM_SIZE) {
            monStorage.getChildren().remove(monParent);

            monTeamList.add(selectedMonster);
            monsterIndexStorage--;
            trainerService.temporaryApplyTeam(monTeamList);
        }
        event.setDropCompleted(true);
    }

    private void dragIntoStorageGrid(DragEvent event) {
        if (!monStorage.getChildren().contains(monParent)) {
            shiftToStorage();
        }
        event.setDropCompleted(true);
    }

    @Override
    public String getResourcePath() {
        return "monsters/";
    }
}
