package de.uniks.stpmon.k.controller.monsters;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.MonsterStatus;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map.Entry;

@Singleton
public class MonsterInformationController extends Controller {

    @FXML
    public ImageView monsterImage;
    @FXML
    public GridPane overviewGrid;
    @FXML
    public Label monsterNameLabel;
    @FXML
    public Label monsterLevelUpgradeLabel;
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

    private String monsterDescription;
    private Monster monster;

    @Inject
    public MonsterInformationController() {
    }

    public void loadMonsterTypeDto(String id) {
        final double SCALE = 4.0;

        disposables.add(presetService.getMonster(id)
                .observeOn(FX_SCHEDULER)
                .subscribe(monsterTypeDto -> {
                    monsterDescription = monsterTypeDto.description();
                    monsterNameLabel.setText(monsterTypeDto.name());
                    // Retrieves the list of types and updates the type list UI
                    List<String> types = monsterTypeDto.type();
                    updateTypeList(types);
                    loadStati();
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
        for (int i = 0; i < 5; i++) {
            // start second row, because name is on first row
            removeNodeByRowColumnIndex(i + 1, 1, overviewGrid);
            if (i < types.size()) {
                overviewGrid.add(typeLabel(types.get(i)), 1, i + 1);
            }
        }
    }

    private String attToString(Float value) {
        return Integer.toString((int) Math.ceil(value));
    }

    public void loadMonster(Monster monster) {
        this.monster = monster;
        // Set all labels
        monsterLevelLabel.setText("Lvl. " + monster.level());
        monsterHpLabel.setText("HP: "
                + attToString(monster.currentAttributes().health())
                + "/" + attToString(monster.attributes().health()));
        monsterXpLabel.setText("XP: " + monster.experience()
                + "/" + (int) (Math.pow(monster.level(), 3) - Math.pow(monster.level() - 1, 3)));

        hpValueLabel.setText(attToString(monster.attributes().health()));
        atkValueLabel.setText(attToString(monster.attributes().attack()));
        defValueLabel.setText(attToString(monster.attributes().defense()));
        speValueLabel.setText(attToString(monster.attributes().speed()));

        // Iterate over the abilities of the monster
        cleanupAttackGrid();
        int i = 1;
        for (Entry<String, Integer> entry : monster.abilities().entrySet()) {
            if (monster.abilities().containsKey(entry.getKey())) {
                fillAbilityTable(entry, i);
                i++;
            }
        }
        loadStati();
    }

    private void loadStati() {
        //check if one or two stati are set
        if (monster != null) {
            //load status effect
            List<MonsterStatus> monsterStatuses = monster.status();
            int row = getFirstFreeRowForStatus();
            int elementInRow = 0;
            for (int i = row; i < 5; i++) {
                removeNodeByRowColumnIndex(i, 1, overviewGrid);
            }
            FlowPane flow = new FlowPane();
            flow.setVgap(2);
            flow.setHgap(2);
            flow.setPrefWrapLength(65);
            for (MonsterStatus status : monsterStatuses) {
                MonsterStatusController statusController = new MonsterStatusController(status);
                Parent statusParent = statusController.render();
                GridPane.setHalignment(statusParent, HPos.LEFT);
                flow.getChildren().add(statusParent);
                elementInRow++;
                if (elementInRow == 3) {
                    elementInRow = 0;
                    row++;
                }
            }
            overviewGrid.add(flow, 1, row);
            GridPane.setMargin(flow, new Insets(8, 0, 0, 12));
        }
    }

    /**
     * Iterates over overviewGrid to check if the monster has one or two stati set
     *
     * @return index of the first free row
     */
    private int getFirstFreeRowForStatus() {
        int row = 2;
        int col = 1;
        for (Node node : overviewGrid.getChildren()) {
            if (GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) == row &&
                    GridPane.getColumnIndex(node) != null && GridPane.getColumnIndex(node) == col) {
                return 3;
            }
        }
        return 2;
    }

    public void removeNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        gridPane.getChildren().removeIf(node -> {
            if (row != 0 && (GridPane.getRowIndex(node) == null || GridPane.getRowIndex(node) == 0)) {
                return false;
            }
            if (column != 0 && (GridPane.getColumnIndex(node) == null || GridPane.getColumnIndex(node) == 0)) {
                return false;
            }
            return GridPane.getRowIndex(node) != null
                    && GridPane.getColumnIndex(node) != null
                    && GridPane.getColumnIndex(node) == column
                    && GridPane.getRowIndex(node) == row;
        });
    }

    private Label typeLabel(String monsterType) {
        Label label = new Label();
        label.setId(monsterType.toUpperCase() + "_label");
        label.setText(monsterType.toUpperCase());
        label.getStyleClass().clear();
        label.getStyleClass().addAll("monster-type-general", "monster-type-" + monsterType);
        return label;
    }

    private void fillAbilityTable(Entry<String, Integer> abilityEntry, int rowIndex) {
        String abilityId = abilityEntry.getKey();
        int currentUses = abilityEntry.getValue();
        subscribe(presetService.getAbility(abilityId),
                ability -> fillAbilityRow(ability, currentUses, rowIndex)
        );
    }

    private void fillAbilityRow(AbilityDto ability, int currentUses, int rowIndex) {
        Label typeLabel = typeLabel(ability.type());
        typeLabel.setId("typeLabel_" + rowIndex);
        Label nameLabel = createAbilityLabel(ability, rowIndex);

        Label powLabel = new Label(ability.power().toString());
        powLabel.setId("powLabel_" + rowIndex);

        Label accLabel = new Label(String.valueOf((int) (ability.accuracy().doubleValue() * 100.0)));
        accLabel.setId("accLabel_" + rowIndex);

        Label useLabel = new Label(currentUses + "/" + ability.maxUses());
        useLabel.setId("useLabel_" + rowIndex);

        for (int i = 0; i < 5; i++) {
            removeNodeByRowColumnIndex(rowIndex, i, attackGrid);
        }

        attackGrid.add(typeLabel, 0, rowIndex);
        attackGrid.add(nameLabel, 1, rowIndex);
        attackGrid.add(powLabel, 2, rowIndex);
        attackGrid.add(accLabel, 3, rowIndex);
        attackGrid.add(useLabel, 4, rowIndex);
    }

    private Label createAbilityLabel(AbilityDto ability, int rowIndex) {
        Label nameLabel = new Label(ability.name());
        nameLabel.setId("nameLabel_" + rowIndex);

        nameLabel.setOnMouseClicked(event -> {
            if (!descriptionLabel.isVisible()
                    || !descriptionLabel.getText().contains(ability.name() + ":\n" + ability.description())) {
                descriptionLabel.setVisible(true);
                descriptionLabel.setText(ability.name() + ":\n" + ability.description());
                infoGrid.setVisible(false);
            } else {
                descriptionLabel.setVisible(false);
                descriptionLabel.setText("");
                infoGrid.setVisible(true);
            }
        });
        return nameLabel;
    }

    private void cleanupAttackGrid() {
        // iterate over rows
        for (int i = 1; i < 5; i++) {
            // interate over columns
            for (int j = 0; j < 5; j++) {
                removeNodeByRowColumnIndex(i, j, attackGrid);
                Label label = new Label("-");
                attackGrid.add(label, j, i);
            }
        }
    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        loadBgImage(mainPane, getResourcePath() + "MonInfo_v2.3.png");
        descriptionLabel.setVisible(false);
        descriptionLabel.setWrapText(true);

        monsterImage.setOnMouseClicked(event -> {
            if (!descriptionLabel.isVisible() || !descriptionLabel.getText().equals(monsterDescription)) {
                descriptionLabel.setVisible(true);
                descriptionLabel.setText(monsterDescription);
                infoGrid.setVisible(false);
            } else {
                descriptionLabel.setVisible(false);
                descriptionLabel.setText("");
                infoGrid.setVisible(true);
            }
        });

        return parent;
    }

    @Override
    public void destroy() {
        super.destroy();
        monsterImage = null;
    }

    public void loadLevelUp(Monster oldMon, Monster newMon) {
        loadMonsterTypeDto(String.valueOf(newMon.type()));
        loadMonster(newMon);
        loadLevelUpDifference(oldMon);
    }

    private void loadLevelUpDifference(Monster oldMon) {
        monsterLevelUpgradeLabel.setStyle("-fx-text-fill: blue");
        monsterLevelUpgradeLabel.setText("  < " + oldMon.level());


        setLevelUpText(hpUpdateLabel, Math.round(oldMon.attributes().health()));


        setLevelUpText(atkUpdateLabel, Math.round(oldMon.attributes().attack()));

        setLevelUpText(defUpdateLabel, Math.round(oldMon.attributes().defense()));

        setLevelUpText(speUpdateLabel, Math.round(oldMon.attributes().speed()));
    }

    private void setLevelUpText(Label label, int diff) {
        label.setStyle("-fx-text-fill: blue");
        label.setText("< " + diff);
    }

    @Override
    public String getResourcePath() {
        return "monsters/";
    }
}
