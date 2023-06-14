package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.utils.ImageUtils;
import de.uniks.stpmon.k.utils.ResponseUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import javax.inject.Inject;
import java.util.List;

public class MonsterInformationController extends ToastedController {
    @FXML
    public ImageView monsterImage;
    @FXML
    public Label speedLabel;
    @FXML
    public Label currentHPLabel;
    @FXML
    public Label maxHPLabel;
    @FXML
    public Label levelLabel;
    @FXML
    public Label experienceLabel;
    @FXML
    public Text monsterNameText;
    @FXML
    public Text descriptionText;
    @FXML
    public TextFlow descriptionTextFlow;
    @FXML
    public HBox typeListHBox;
    @FXML
    public HBox abilitiesHBox;

    @Inject
    PresetService presetService;

    @Inject
    public MonsterInformationController() {
    }

    @Override
    public Parent render() {
        return super.render();
    }

    public void loadMonsterTypeDto(String id) {
        final double SCALE = 4.0;

        disposables.add(presetService.getMonster(id)
                .observeOn(FX_SCHEDULER)
                .subscribe(monsterTypeDto -> {
                    monsterNameText.setText(monsterTypeDto.name());

                    List<String> types = monsterTypeDto.type();
                    updateTypeList(types);

                    descriptionText.setText(monsterTypeDto.description());
                    descriptionTextFlow.getChildren().clear();
                    descriptionTextFlow.getChildren().add(descriptionText);
                }));

        disposables.add(ResponseUtils.readImage(presetService.getMonsterImage(id))
                .observeOn(FX_SCHEDULER)
                .subscribe(imageUrl -> {
                    Image image = ImageUtils.scaledImageFX(imageUrl, SCALE);

                    monsterImage.setImage(image);
                }));
    }

    private void updateTypeList(List<String> types) {
        typeListHBox.getChildren().clear();
        for (String type : types) {
            Label typeLabel = new Label(type);
            typeLabel.getStyleClass().add("monster");
            typeLabel.getStyleClass().add("monster-type");
            typeListHBox.getChildren().add(typeLabel);
        }
    }

    public void loadMonster(Monster monster) {
        speedLabel.setText(String.valueOf(monster.currentAttributes().speed()));

        maxHPLabel.setText(String.valueOf(monster.attributes().health()));
        currentHPLabel.setText(String.valueOf(monster.currentAttributes().health()));

        levelLabel.setText(String.valueOf(monster.level()));

        experienceLabel.setText(String.valueOf(monster.experience()));
    }
}
