package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.MonsterCache;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class MonBoxController extends Controller {

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
    MonsterInformationController monsterInformationController;
    @Inject
    Provider<IngameController> ingameControllerProvider;
    private MonsterCache monsterCache;
    private Monster activeMonster;


    @Inject
    public MonBoxController() {
    }

    @Override
    public void init() {
        super.init();

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        Trainer trainer = trainerStorage.getTrainer();
        monsterCache = cacheManager.requestMonsters(trainer._id());
        subscribe(monsterCache.getTeam().getValues(), this::showTeamMonster);
        subscribe(monsterCache.getValues(), this::showMonsterList);
        loadImage(monBoxImage, "monGrid_v4.png");

        return parent;
    }

    private void showTeamMonster(List<Monster> monsters) {
        int monsterIndex = 0;
        // Team Monster max 6 slots
        for (Monster monster : monsters) {
            ImageView imageView = new ImageView();
            imageView.setFitHeight(67);
            imageView.setFitWidth(67);
            subscribe(resourceService.getMonsterImage(String.valueOf(monster.type())), imageUrl -> {
                // Scale and set the image
                Image image = ImageUtils.scaledImageFX(imageUrl, 2.0);
                imageView.setImage(image);
                imageView.setOnMouseClicked(e -> triggerMonsterInformation(monster)
                );
            });
            monTeam.add(imageView, monsterIndex, 0);
            monBoxVbox.toFront();
            monsterIndex++;
        }
    }

    private void showMonsterList(List<Monster> monsters) {
        List<Monster> teamMonsters = monsterCache.getTeam().getValues().blockingFirst();
        int columnCount = 6;
        int rowCount = 5;
        int monsterIndex = 0;
        monsters.removeAll(teamMonsters);

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                if (monsterIndex < monsters.size()) {
                    ImageView imageView = new ImageView();
                    imageView.setFitHeight(67);
                    imageView.setFitWidth(67);
                    int finalMonsterIndex = monsterIndex;
                    subscribe(resourceService.getMonsterImage(String.valueOf(monsters.get(monsterIndex).type())), imageUrl -> {
                        // Scale and set the image
                        Image image = ImageUtils.scaledImageFX(imageUrl, 2.0);
                        imageView.setImage(image);
                        imageView.setOnMouseClicked(e -> triggerMonsterInformation(monsters.get(finalMonsterIndex))
                        );
                    });
                    monStorage.add(imageView, column, row);
                    monBoxVbox.toFront();
                    monsterIndex++;
                }
            }
        }

    }
    private void openMonsterInformation(Monster monster) {
        activeMonster = monster;
        ingameControllerProvider.get().openMonsterInfo(monster);
    }

    public void triggerMonsterInformation(Monster monster) {
        if (activeMonster == null) {
            openMonsterInformation(monster);
        } else {
            if(activeMonster == monster) {
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

    @Override
    public void destroy() {
        super.destroy();
    }
}
