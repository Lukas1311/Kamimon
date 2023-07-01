package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class MonsterInformation2Controller extends Controller {

    @FXML
    public ImageView monsterImage;
    @FXML
    public GridPane overviewGrid;
    @FXML
    public Label monsterNameLabel;
    @FXML
    public Label mosterLevelUpgradeLabel;
    @FXML
    public Label monsterLevelLabel;
    @FXML
    public Label monsterHpLabel;
    @FXML
    public Label monsterXpLabel;
    @FXML
    public GridPane attackGrid;
    @FXML
    public Label hpValueLabel;
    @FXML
    public Label atkValueLabel;
    @FXML
    public Label defValueLabel;
    @FXML
    public Label speValueLabel;
    @FXML
    public Label hpUpdateLabel;
    @FXML
    public Label atkUpdateLabel;
    @FXML
    public Label defUpdateLabel;
    @FXML
    public Label speUpdateLabel;
    @FXML
    public AnchorPane mainPane;
    @FXML
    public Label descriptionLabel;
    @FXML
    public GridPane infoGrid;

    @Inject
    PresetService presetService;
    @Inject
    IResourceService resourceService;

    @Inject
    public MonsterInformation2Controller() {
    }

    public void loadMonsterTypeDto(String id) {
        final double SCALE = 4.0;

        disposables.add(presetService.getMonster(id)
                .observeOn(FX_SCHEDULER)
                .subscribe(monsterTypeDto -> {
                    monsterNameLabel.setText(monsterTypeDto.name());
                    // Retrieves the list of types and updates the type list UI
                    List<String> types = monsterTypeDto.type();
                    updateTypeList(types);
                }));

        disposables.add(resourceService.getMonsterImage(id)
                .observeOn(FX_SCHEDULER)
                .subscribe(imageUrl -> {
                    // Scale and set the image
                    Image image = ImageUtils.scaledImageFX(imageUrl, SCALE);
                    monsterImage.setImage(image);
                }));
    }

    private void updateTypeList(List<String> types) {
        // Update the UI with a list of types
        for (int i = 0; i < 4; i++) {
            removeNodeByRowColumnIndex(i, 1, overviewGrid);
            if (i < types.size()) {
                overviewGrid.add(typeLabel(null, types.get(i)), 1, i);
            }
        }
    }

    public void loadMonster(Monster monster) {
        // Set all labels
        monsterLevelLabel.setText("Lvl. " + monster.level());
        monsterHpLabel.setText("HP: "
                + monster.currentAttributes().health()
                + "/" + monster.attributes().health());
        monsterXpLabel.setText("XP: " + monster.experience()); //TODO what is the maximum experience

        //TODO where should the current values (atk, def, speed, ..) shown?
        hpValueLabel.setText(monster.attributes().health().toString());
        atkValueLabel.setText(monster.attributes().attack().toString());
        defValueLabel.setText(monster.attributes().defense().toString());
        speValueLabel.setText(monster.attributes().speed().toString());

        // Iterate over the abilities of the monster
        cleanupAttackGrid();
        int i = 1;
        for (String key : monster.abilities().keySet()) {
            if (monster.abilities().containsKey(key)) {
                fillAbilityTable(key, i);
                i++;
            }
        }
    }

    public void removeNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        gridPane.getChildren().removeIf(node -> {
            if(row != 0 && (GridPane.getRowIndex(node) == null || GridPane.getRowIndex(node) == 0)) {
                return false;
            }
            if(column != 0 && (GridPane.getColumnIndex(node) == null || GridPane.getColumnIndex(node) == 0)) {
                return false;
            }
            return GridPane.getRowIndex(node) != null
                    && GridPane.getColumnIndex(node) != null
                    && GridPane.getColumnIndex(node) == column
                    && GridPane.getRowIndex(node) == row;
        });
    }


    private Label typeLabel(Label label, String monsterType) {
        MonsterType type = MonsterType.valueOf(monsterType.toUpperCase());
        if (label == null) {
            label = new Label();
        }
        label.setText(type.getTypeName().toUpperCase());
        label.getStyleClass().clear();
        label.getStyleClass().addAll("monster-type-general", type.getStyleClass());
        return label;
    }

    private void fillAbilityTable(String abilityId, int rowIndex) {
        disposables.add(presetService.getAbility(abilityId)
                .observeOn(FX_SCHEDULER)
                .subscribe(ability -> {
                    fillAbilityRow(ability, rowIndex);
                }));
    }

    private void fillAbilityRow(AbilityDto ability, int rowIndex) {
        Label typeLabel = typeLabel(null, ability.type());
        Label nameLabel = new Label(ability.name());
        nameLabel.setOnMouseClicked(event -> {

            if(descriptionLabel.isVisible() && descriptionLabel.getText().equals(ability.description())) {
                descriptionLabel.setVisible(false);
                descriptionLabel.setText("");
                infoGrid.setVisible(true);
            } else {
                descriptionLabel.setVisible(true);
                descriptionLabel.setText(ability.description());
                infoGrid.setVisible(false);
            }
        });
        //TODO add style here
        Label powLabel = new Label(ability.power().toString());
        Label accLabel = new Label(String.valueOf((int) (ability.accuracy().doubleValue() * 100.0)));
        Label useLabel = new Label("??" + "/" + ability.maxUses());

        for (int i = 0; i < 5; i++) {
            removeNodeByRowColumnIndex(rowIndex, i, attackGrid);
        }

        attackGrid.add(typeLabel, 0, rowIndex);
        attackGrid.add(nameLabel, 1, rowIndex);
        attackGrid.add(powLabel, 2, rowIndex);
        attackGrid.add(accLabel, 3, rowIndex);
        attackGrid.add(useLabel, 4, rowIndex);
    }


    private void cleanupAttackGrid() {
        //iterate over rows
        for (int i = 1; i < 5; i++) {
            //interate over columns
            for (int j = 0; j < 5; j++) {
                removeNodeByRowColumnIndex(i, j, attackGrid);
                Label label = new Label("-");
                //TODO Add style classes

            }
        }
    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        mainPane.setBackground(new Background(loadBgImage("MonInfoView_v2.3-final.png")));
        descriptionLabel.setVisible(false);
        descriptionLabel.setWrapText(true);
        return parent;
    }

}
