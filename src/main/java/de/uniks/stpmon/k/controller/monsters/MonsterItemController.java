package de.uniks.stpmon.k.controller.monsters;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.MonsterStatus;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class MonsterItemController extends Controller {

    @FXML
    public StackPane backgroundStackPane;
    @FXML
    public ProgressBar healthBar;
    @FXML
    public ImageView monsterImageView;
    @FXML
    public VBox monsterEffectRight;
    @FXML
    public VBox monsterEffectLeft;

    private final IResourceService resourceService;
    private final Monster monster;
    private Parent parent;
    private float currHp = 0.0f;
    private float maxHp = 1.0f;

    public MonsterItemController(Monster monster, IResourceService resourceService) {
        this.monster = monster;
        this.resourceService = resourceService;

        if (monster != null) {
            currHp = monster.currentAttributes().health();
            maxHp = monster.attributes().health();
        }
    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        this.parent = parent;

        healthBar.progressProperty().addListener((obs, oldProgress, newProgress) -> {
            healthBar.getStyleClass().clear();
            healthBar.getStyleClass().add("hp-progressbar");

            if (newProgress.floatValue() >= 0.66f) {
                healthBar.getStyleClass().add("hp-progressbar-green");
            } else if (newProgress.floatValue() >= 0.33f) {
                healthBar.getStyleClass().add("hp-progressbar-orange");
            } else {
                healthBar.getStyleClass().add("hp-progressbar-red");
            }
        });

        healthBar.setProgress(currHp / maxHp);

        if (monster != null && resourceService != null) {
            monsterEffectRight.getChildren().clear();
            monsterEffectLeft.getChildren().clear();
            List<MonsterStatus> statusList = monster.status();
            for (int i = 0; i < statusList.size(); i++) {
                MonsterStatus status = statusList.get(i);
                MonsterStatusController statusController = new MonsterStatusController(status);
                Node node = statusController.render();
                statusController.shrink();
                if (i % 2 == 0) {
                    monsterEffectRight.getChildren().add(node);
                } else {
                    monsterEffectLeft.getChildren().add(node);
                }
            }

            subscribe(resourceService.getMonsterImage(String.valueOf(monster.type())), imageUrl -> {
                // Scale and set the image for the Clipboard
                Image image = ImageUtils.scaledImageFX(imageUrl, 0.5);
                monsterImageView.setImage(image);
                monsterImageView.setPreserveRatio(true);
            });
        }

        return parent;
    }

    public Image getMonImage() {
        return monsterImageView.getImage();
    }

    public Parent getParent() {
        return parent;
    }

    @Override
    public String getResourcePath() {
        return "monsters/";
    }

}
