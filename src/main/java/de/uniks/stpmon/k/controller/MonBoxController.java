package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Trainer;
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

    public final static int IMAGESIZE = 67;

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
    private List<String> monStorageList = new ArrayList<>();
    private int monsterIndexTeam = 0;
    private int monsterIndexStorage = 0;


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
        subscribe(monsterCache.getTeam().getValues(), this::showTeamMonster);
        subscribe(monsterCache.getValues(), this::showMonsterList);
        loadImage(monBoxImage, "monGrid_v4.png");

        return parent;
    }

    private void showTeamMonster(List<Monster> monsters) {
        monsterIndexTeam = 0;
        monTeamList = new ArrayList<>();

        // Team Monster max 6 slots
        for (Monster monster : monsters) {
            ImageView imageView = createMonsterImageView(monster);
            monTeam.add(imageView, monsterIndexTeam, 0);
            monBoxVbox.toFront();
            monTeamList.add(monster._id());
            monsterIndexTeam++;
        }
    }

    private void showMonsterList(List<Monster> monsters) {
        List<Monster> teamMonsters = monsterCache.getTeam().getValues().blockingFirst();
        int columnCount = 6;
        int rowCount = 5;
        monsters.removeAll(teamMonsters);
        monsterIndexStorage = 0;
        monStorageList = new ArrayList<>();

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                if (monsterIndexStorage < monsters.size()) {
                    Monster monster = monsters.get(monsterIndexStorage);
                    ImageView imageView = createMonsterImageView(monster);
                    monStorage.add(imageView, column, row);
                    monBoxVbox.toFront();
                    monStorageList.add(monster._id());
                    monsterIndexStorage++;
                }
            }
        }
    }

    private ImageView createMonsterImageView(Monster monster) {
        ImageView imageView = new ImageView();
        imageView.setFitHeight(IMAGESIZE);
        imageView.setFitWidth(IMAGESIZE);

        subscribe(resourceService.getMonsterImage(String.valueOf(monster.type())), imageUrl -> {
            // Scale and set the image for the Clipboard
            Image image = ImageUtils.scaledImageFX(imageUrl, 0.5);
            imageView.setImage(image);
            draggableimage(imageView);
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


    private ImageView draggableimage(ImageView imageView) {
        {
            imageView.setOnDragDetected(event -> {
                Dragboard dragboard = imageView.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putImage(imageView.getImage());
                dragboard.setContent(content);
                monImage = imageView;
                event.consume();
            });

            imageView.setOnDragDone(event -> {
                if (event.getTransferMode() == TransferMode.MOVE) {

                    monImage = null;
                }
                event.consume();
            });

            return imageView;
        }

    }

    private GridPane targetGrid(GridPane gridPane) {
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

                if (gridPane.equals(monStorage) && !monStorage.getChildren().contains(imageView) && monTeamList.size() > 1) {
                    int tmp_index = monsterIndexTeam - 1;
                    String mon = monTeamList.get(tmp_index);
                    monTeamList.remove(mon);
                    monTeam.getChildren().remove(imageView);

                    monStorageList.add(mon);
                    monStorage.add(imageView, monsterIndexStorage, 0);
                    monsterIndexStorage++;
                }
                if (gridPane.equals(monTeam) && !monTeam.getChildren().contains(imageView)) {
                    if (monTeamList.size() < 6) {
                        int tmp_index = monsterIndexStorage - 1;
                        String mon = monStorageList.get(tmp_index);
                        monStorageList.remove(mon);
                        monStorage.getChildren().remove(imageView);

                        monTeamList.add(mon);
                        monTeam.add(imageView, monsterIndexTeam, 0);
                        monsterIndexTeam++;
                    }
                }
                event.setDropCompleted(true);
            }
            event.consume();
        });

        return gridPane;
    }


}
