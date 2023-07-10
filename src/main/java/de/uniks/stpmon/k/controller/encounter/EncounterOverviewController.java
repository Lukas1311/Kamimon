package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.IngameController;
import de.uniks.stpmon.k.controller.action.ActionFieldController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.MainWindow;
import de.uniks.stpmon.k.dto.AbilityMove;
import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.InputHandler;
import de.uniks.stpmon.k.service.SessionService;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.HashMap;

public class EncounterOverviewController extends Controller {

    public static final double IMAGE_SCALE = 6.0;

    private final HashMap<EncounterSlot, Transition> attackAnimations = new HashMap<>(4);

    private final HashMap<EncounterSlot, ImageView> monsterImages = new HashMap<>(4);


    @FXML
    public StackPane fullBox;
    @FXML
    public ImageView background;
    @FXML
    public VBox userMonsters;
    @FXML
    public VBox opponentMonsters;
    @FXML
    public ImageView userMonster0;
    @FXML
    public ImageView userMonster1;
    @FXML
    public ImageView opponentMonster0;
    @FXML
    public ImageView opponentMonster1;
    @FXML
    public VBox actionFieldBox;


    @Inject
    IResourceService resourceService;
    @Inject
    Provider<StatusController> statusControllerProvider;
    @Inject
    Provider<HybridController> hybridControllerProvider;
    @Inject
    SessionService sessionService;
    @Inject
    ActionFieldController actionFieldController;

    @Inject
    InputHandler inputHandler;

    @Inject
    public EncounterOverviewController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        // relations between Encounter slots and image views
        monsterImages.put(EncounterSlot.PARTY_FIRST, userMonster0);
        monsterImages.put(EncounterSlot.PARTY_SECOND, userMonster1);
        monsterImages.put(EncounterSlot.ENEMY_FIRST, opponentMonster0);
        monsterImages.put(EncounterSlot.ENEMY_SECOND, opponentMonster1);


        loadImage(background, "encounter/FOREST.png");
        background.fitHeightProperty().bind(fullBox.heightProperty());
        background.fitWidthProperty().bind(fullBox.widthProperty());

        Parent actionField = this.actionFieldController.render();
        if (actionField != null) {
            actionFieldBox.getChildren().add(actionField);
        }

        //click on the first mon of opponent to get out of the encounter
        //Note: the encounter is still active after this
        opponentMonster0.setOnMouseClicked(e -> {
            IngameController.disableEncounter = true;
            HybridController controller = hybridControllerProvider.get();
            app.show(controller);
            controller.openMain(MainWindow.INGAME);
        });

        //add a translation transition to all monster images
        for ( EncounterSlot slot : monsterImages.keySet()) {
            ImageView view = monsterImages.get(slot);


            TranslateTransition translation = new TranslateTransition(Duration.millis(250), view);
            if(slot.enemy()) {
                translation.setByY(30);
                translation.setByX(-60);
            } else {
                translation.setByY(-30);
                translation.setByX(60);
            }
            translation.setAutoReverse(true);
            translation.setCycleCount(2);

            attackAnimations.put(slot, translation);
        }

        onDestroy(inputHandler.addPressedKeyFilter(event -> {
            switch (event.getCode()) {
                    case G -> {
                        // Block movement and backpack, if map overview is shown
                        renderAttack(EncounterSlot.PARTY_FIRST);
                        event.consume();
                    }

                    case H -> {
                        // Block movement and backpack, if map overview is shown
                        renderAttack(EncounterSlot.ENEMY_FIRST);
                        event.consume();
                    }

                    default -> {
                    }

                }
        }));

        subscribeFight();

        renderMonsterLists();
        animateMonsterEntrance();

        return parent;
    }

    @Override
    public void init(){




    }


    private void subscribeFight(){
        for (EncounterSlot slot : sessionService.getSlots()) {
            subscribe(sessionService.listenOpponent(slot), next -> {
                //using result to print text to action field

                //using IMove to animate attack
                if(next.move() instanceof AbilityMove){
                    renderAttack(slot);
                }
            });
        }
    }


    private void renderAttack(EncounterSlot slot){
        attackAnimations.get(slot).play();
    }


    private void renderMonsterLists() {
        for (EncounterSlot slot : sessionService.getSlots()) {
            if (slot.enemy()) {
                if (slot.partyIndex() == 0) {
                    renderMonsters(opponentMonsters, opponentMonster0, slot);
                } else {
                    renderMonsters(opponentMonsters, opponentMonster1, slot);
                }
            } else {
                if (slot.partyIndex() == 0) {
                    renderMonsters(userMonsters, userMonster0, slot);
                } else {
                    renderMonsters(userMonsters, userMonster1, slot);
                }
            }
        }
    }

    private void renderMonsters(VBox monstersContainer, ImageView monsterImageView, EncounterSlot slot) {
        Monster monster = sessionService.getMonster(slot);
        StatusController statusController = statusControllerProvider.get();
        statusController.setSlot(slot);
        monstersContainer.getChildren().add(statusController.render());

        if (slot.partyIndex() == 0) {
            loadMonsterImage(String.valueOf(monster.type()), monsterImageView, slot.enemy());
            if (!slot.enemy()) {
                VBox.setMargin(statusController.fullBox, new Insets(-18, 0, 0, 0));
            } else {
                VBox.setMargin(statusController.fullBox, new Insets(0, 125, 0, 0));
            }
        } else if (slot.partyIndex() == 1) {
            loadMonsterImage(String.valueOf(monster.type()), monsterImageView, slot.enemy());
            if (!slot.enemy()) {
                VBox.setMargin(statusController.fullBox, new Insets(-5, 0, 0, 125));
            }
        }
    }

    public void loadMonsterImage(String monsterId, ImageView monsterImage, boolean attacker) {
        subscribe(resourceService.getMonsterImage(monsterId), imageUrl -> {
            // Scale and set the image
            Image image = ImageUtils.scaledImageFX(imageUrl, IMAGE_SCALE);
            if (attacker) {
                monsterImage.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            } else {
                monsterImage.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            }
            monsterImage.setImage(image);
        });
    }

    private void animateMonsterEntrance() {
        ObservableList<Node> teamMonsters = userMonsters.getChildren();
        if (teamMonsters.size() > 1) {
            teamMonsters.get(1).setOpacity(0);
            userMonster1.setOpacity(0);
        }
        ObservableList<Node> attackerMonsters = opponentMonsters.getChildren();
        if (attackerMonsters.size() > 1) {
            attackerMonsters.get(1).setOpacity(0);
            opponentMonster1.setOpacity(0);
        }
        actionFieldBox.setOpacity(0);

        //the first monster of the user and opponent always gets rendered
        ParallelTransition userFullTransition1 =
                createMonsterTransition(userMonster0, teamMonsters.get(0), false);
        ParallelTransition opponentFullTransition1 =
                createMonsterTransition(opponentMonster0, attackerMonsters.get(0), true);

        ParallelTransition parallel1 = new ParallelTransition(userFullTransition1, opponentFullTransition1);

        parallel1.setOnFinished(e -> {
            if (teamMonsters.size() > 1) {
                teamMonsters.get(1).setOpacity(1);
                userMonster1.setOpacity(1);
            }
            if (attackerMonsters.size() > 1) {
                attackerMonsters.get(1).setOpacity(1);
                opponentMonster1.setOpacity(1);
            }
        });

        ParallelTransition userFullTransition2 = null;
        if (teamMonsters.size() > 1) {
            userFullTransition2 = createMonsterTransition(userMonster1, teamMonsters.get(1), false);
        }

        ParallelTransition opponentFullTransition2 = null;
        if (attackerMonsters.size() > 1) {
            opponentFullTransition2 = createMonsterTransition(opponentMonster1, attackerMonsters.get(1), true);
        }

        ParallelTransition parallel2 = new ParallelTransition();
        if (userFullTransition2 != null) {
            parallel2.getChildren().add(userFullTransition2);
        }
        if (opponentFullTransition2 != null) {
            parallel2.getChildren().add(opponentFullTransition2);
        }
        SequentialTransition sequence = new SequentialTransition(parallel1, parallel2);
        sequence.setOnFinished(e -> actionFieldBox.setOpacity(1));

        TranslateTransition actionFieldTransition = new TranslateTransition(
                Duration.millis(effectContext.getEncounterAnimationSpeed()), actionFieldBox);
        actionFieldTransition.setFromX(600);
        actionFieldTransition.setToX(0);
        SequentialTransition fullSequence = new SequentialTransition(sequence, actionFieldTransition);
        fullSequence.play();
    }

    private ParallelTransition createMonsterTransition(Node image, Node status, boolean attacker) {
        //the first monster of the user and opponent always gets rendered
        return new ParallelTransition(createNodeTransition(status, attacker), createNodeTransition(image, attacker));
    }


    private TranslateTransition createNodeTransition(Node node, boolean fromRight) {
        TranslateTransition transition = new TranslateTransition(
                Duration.millis(effectContext.getEncounterAnimationSpeed()), node);
        transition.setFromX(fromRight ? 600 : -600);
        transition.setToX(0);
        return transition;
    }

    @Override
    public String getResourcePath() {
        return "encounter/";
    }
}
