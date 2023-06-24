package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.TrainerService;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.MonsterCache;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

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
    public BorderPane monBoxBorderPane;
    @Inject
    Provider<TrainerService> trainerServiceProvider;
    @Inject
    Provider<CacheManager> cacheManagerProvider;
    @Inject
    IResourceService resourceService;


    @Inject
    public MonBoxController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        loadImage(monBoxImage, "monGrid_v4.png");

        Trainer trainer = trainerServiceProvider.get().getMe();
        MonsterCache monsterCache = cacheManagerProvider.get().requestMonsters(trainer._id());
        subscribe(monsterCache.getValues(), monsters -> monsters.forEach(monster -> {
            ImageView monsterImage = new ImageView();
            monsterImage.setFitHeight(67);
            monsterImage.setFitWidth(67);


            subscribe(resourceService.getMonsterImage(String.valueOf(monster.type())), image -> {
                Image monsterImagePNG = ImageUtils.scaledImageFX(image, 4);
                monsterImage.setImage(monsterImagePNG);
            });
            monTeam.add(monsterImage, 0, 0);
        }));


        return parent;
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
