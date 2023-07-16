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
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class MonBoxController extends Controller {

    public final static int IMAGE_SIZE = 67;

    @FXML
    public StackPane monBoxStackPane;
    @FXML
    public GridPane monTeam;
    @FXML
    public GridPane monStorage;
    @FXML
    public ImageView monBoxImage;
    @FXML
    public VBox monBoxVbox;

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
    private ImageView monImage;
    private List<String> monTeamList = new ArrayList<>();
    private int monsterIndexStorage = 0;
    private String selectedMonster;

    @Inject
    public MonBoxController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        Trainer trainer = trainerStorage.getTrainer();
        monsterCache = cacheManager.requestMonsters(trainer._id());
        trainerCache = cacheManager.trainerCache();
        targetGrid(monStorage);
        targetGrid(monTeam);
        subscribe(monsterCache.getTeam().getValues().take(1), this::showTeamMonster);
        subscribe(monsterCache.getTeam().getValues(), monsters -> {
            showTeamMonster(monsters);
            showMonsterList(monsterCache.getValues().blockingFirst());
        });
        subscribe(monsterCache.getValues(), this::showMonsterList);
        loadImage(monBoxImage, "monGrid_v4.png");

        return parent;
    }

    @Override
    public void destroy() {
        super.destroy();
        // Update team if leave monbox
        ingameControllerProvider.get().subscribe(trainerService.setTeam(monTeamList));
    }

    private void showTeamMonster(List<Monster> monsters) {
        int monsterIndexTeam = 0;
        monTeamList = new ArrayList<>();
        monTeam.getChildren().clear();

        // Team Monster max 6 slots
        for (int i = 0; i < monsters.size(); i++) {
            Monster monster = monsters.get(i);
            ImageView imageView = createMonsterImageView(monster, monster._id());
            imageView.setId("team_" + i);
            monTeam.add(imageView, monsterIndexTeam, 0);
            monBoxVbox.toFront();
            monTeamList.add(monster._id());
            monsterIndexTeam++;
        }
    }

    private void showMonsterList(List<Monster> monsters) {
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
                    ImageView imageView = createMonsterImageView(monster, monster._id());
                    imageView.setId("storage_" + row + "_" + column);
                    monStorage.add(imageView, column, row);
                    monBoxVbox.toFront();
                    monsterIndexStorage++;
                }
            }
        }
    }

    private ImageView createMonsterImageView(Monster monster, String id) {
        ImageView imageView = new ImageView();
        imageView.setFitHeight(IMAGE_SIZE);
        imageView.setFitWidth(IMAGE_SIZE);

        subscribe(resourceService.getMonsterImage(String.valueOf(monster.type())), imageUrl -> {
            // Scale and set the image for the Clipboard
            Image image = ImageUtils.scaledImageFX(imageUrl, 1.2);
            imageView.setImage(image);
            draggableImage(imageView, id);
        });

        imageView.setOnMouseClicked(e -> triggerMonsterInformation(monster));
        return imageView;
    }

    private void openMonsterInformation(Monster monster) {
        activeMonster = monster;
        ingameControllerProvider.get().openMonsterInfo(monster);
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

    private void closeMonsterInformation() {
        ingameControllerProvider.get().removeChildren(2);
        activeMonster = null;
    }


    private void draggableImage(ImageView imageView, String id) {
        imageView.setOnDragDetected(event -> {
            Dragboard dragboard = imageView.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putImage(imageView.getImage());
            dragboard.setContent(content);
            monImage = imageView;
            selectedMonster = id;
            event.consume();
        });

        imageView.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                monImage = null;
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
            if (dragboard.hasImage()) {
                ImageView imageView = monImage;
                if (gridPane.equals(monStorage) && !monStorage.getChildren().contains(imageView)) {
                    monTeamList.remove(selectedMonster);

                    monStorage.add(imageView, monsterIndexStorage, 0);
                    monsterIndexStorage++;

                    Trainer trainer = trainerStorage.getTrainer();
                    Trainer newTrainer = TrainerBuilder.builder(trainer).addTeam(monTeamList).create();
                    trainerCache.updateValue(newTrainer);
                }
                if (gridPane.equals(monTeam)
                        && !monTeam.getChildren().contains(imageView)
                        && monTeamList.size() < 6) {
                    monStorage.getChildren().remove(imageView);

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
