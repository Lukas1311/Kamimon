package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import javax.inject.Inject;
import java.util.List;

public class MonsterInformationController extends ToastedController {
    @FXML
    public ImageView monsterImage;
    @FXML
    public Label healthLabel;
    @FXML
    public Label attackLabel;
    @FXML
    public Label defenseLabel;
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
    public VBox abilitiesVBox;

    @Inject
    PresetService presetService;
    @Inject
    IResourceService resourceService;


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

                    // Retrieves the list of types and updates the type list UI
                    List<String> types = monsterTypeDto.type();
                    updateTypeList(types);

                    descriptionText.setText(monsterTypeDto.description());
                    descriptionTextFlow.getChildren().clear();
                    descriptionTextFlow.getChildren().add(descriptionText);
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
        typeListHBox.getChildren().clear();
        for (String type : types) {
            Label typeLabel = new Label(type);
            // Add CSS
            typeLabel.getStyleClass().add("monster-type");
            typeListHBox.getChildren().add(typeLabel);
        }
    }

    public void loadMonster(Monster monster) {
        int currentHealth = (int)(((double)monster.currentAttributes().health() / monster.attributes().health()) * 100);
        int currentAttack= (int)(((double)monster.currentAttributes().attack() / monster.attributes().attack()) * 100);
        int currentDefense = (int)(((double)monster.currentAttributes().defense() / monster.attributes().defense()) * 100);
        int currentSpeed = (int)(((double)monster.currentAttributes().speed() / monster.attributes().speed()) * 100);

        // Set all labels
        healthLabel.setText(String.valueOf(currentHealth));
        attackLabel.setText(String.valueOf(currentAttack));
        defenseLabel.setText(String.valueOf(currentDefense));
        speedLabel.setText(String.valueOf(currentSpeed));
        maxHPLabel.setText(String.valueOf(monster.attributes().health()));
        currentHPLabel.setText(String.valueOf(monster.currentAttributes().health()));
        levelLabel.setText(String.valueOf(monster.level()));
        experienceLabel.setText(String.valueOf(monster.experience()));

        abilitiesVBox.getChildren().clear();

        // Iterate over the abilities of the monster
        for (String key : monster.abilities().keySet()) {
            if (monster.abilities().containsKey(key)) {
                HBox abilityBox = createAbilityBox(key);
                abilitiesVBox.getChildren().add(abilityBox);
            }
        }
    }

    private HBox createAbilityBox(String id) {
        // Create the main container
        HBox abilityBox = new HBox();

        // Create the container for the ability name and type
        HBox nameAndTypeBox = new HBox();
        Label abilityTypeLabel = new Label();
        Text abilityNameText = new Text();

        // Create the container for displaying the current and maximum uses of the ability
        HBox amountUsesBox = new HBox();
        Label currentUsesLabel = new Label();
        Label slash = new Label("/");
        Label maxUsesLabel = new Label();

        abilityBox.getChildren().addAll(nameAndTypeBox, amountUsesBox);
        nameAndTypeBox.getChildren().addAll(abilityTypeLabel, abilityNameText);
        amountUsesBox.getChildren().addAll(currentUsesLabel, slash , maxUsesLabel);

        // Apply CSS styles
        abilityBox.getStyleClass().add("ability");
        nameAndTypeBox.getStyleClass().add("ability-name-type-box");
        abilityTypeLabel.getStyleClass().add("ability-type");
        amountUsesBox.getStyleClass().add("ability-uses");


        // Subscribe to the ability information from the preset service
        disposables.add(presetService.getAbility(id)
                .observeOn(FX_SCHEDULER)
                .subscribe(ability -> {
                        abilityTypeLabel.setText(ability.type());
                        abilityNameText.setText(ability.name());
                    maxUsesLabel.setText(String.valueOf(ability.maxUses()));
                    abilityBox.setId("abilityBox" + ability.id());
                    abilityBox.setOnMouseClicked(event -> descriptionText.setText(ability.description()));
                }));

        return abilityBox;
    }

}
