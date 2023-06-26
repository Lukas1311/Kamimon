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
    MonsterCache monsterCache;


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
        subscribe(monsterCache.getValues(), this::showTeamMonster);
        subscribe(monsterCache.getValues(), this::showMonsterList);
        loadImage(monBoxImage, "monGrid_v4.png");

        return parent;
    }

    private void showTeamMonster(List<Monster> monsters) {
        int i = 0;
        // Team Monster max 6 slots
        for (Monster monster : monsters) {
            ImageView imageView = new ImageView();
            imageView.setFitHeight(67);
            imageView.setFitWidth(67);
            subscribe(resourceService.getMonsterImage(String.valueOf(monster.type())), imageUrl -> {
                // Scale and set the image
                Image image = ImageUtils.scaledImageFX(imageUrl, 4.0);
                imageView.setImage(image);
            });
            monTeam.add(imageView, i, 0);
            monBoxVbox.toFront();
            i++;
        }
    }

    private void showMonsterList(List<Monster> monsters) {
        int columnCount = 6;
        int rowCount = 5;
        int monsterIndex = 0;

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                if (monsterIndex < monsters.size()) {
                    ImageView imageView = new ImageView();
                    imageView.setFitHeight(67);
                    imageView.setFitWidth(67);
                    subscribe(resourceService.getMonsterImage(String.valueOf(monsters.get(monsterIndex).type())), imageUrl -> {
                        // Scale and set the image
                        Image image = ImageUtils.scaledImageFX(imageUrl, 4.0);
                        imageView.setImage(image);
                    });
                    monStorage.add(imageView, column, row);
                    monBoxVbox.toFront();
                    monsterIndex++;
                }
            }
        }

    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
