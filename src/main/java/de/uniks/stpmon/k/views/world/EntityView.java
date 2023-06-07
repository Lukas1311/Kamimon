package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.dto.MoveTrainerDto;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.world.MovementHandler;
import de.uniks.stpmon.k.service.world.MovementScheduler;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

import javax.inject.Inject;

public class EntityView extends WorldViewable {

    @Inject
    protected MovementHandler movementHandler;
    protected Node entityNode;
    protected Trainer trainer;

    @Inject
    public EntityView() {
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    @Override
    public Node render() {
        Node character = createRectangleScaled("map/char.png", WorldView.WORLD_ANGLE);
        character.setId("entity");
        entityNode = character;

        entityNode.setTranslateX(trainer.x() * WorldView.WORLD_UNIT);
        entityNode.setTranslateZ(-trainer.y() * WorldView.WORLD_UNIT -
                WorldView.ENTITY_OFFSET_Y * WorldView.WORLD_UNIT);
        return character;
    }

    protected void onMoveReceived(MoveTrainerDto dto) {
        TranslateTransition transition = new TranslateTransition();
        transition.setNode(entityNode);
        transition.setDuration(Duration.millis(MovementScheduler.MOVEMENT_PERIOD * 2));
        transition.setToX(dto.x() * WorldView.WORLD_UNIT);
        transition.setToZ(-dto.y() * WorldView.WORLD_UNIT
                - WorldView.ENTITY_OFFSET_Y * WorldView.WORLD_UNIT);
        transition.play();
    }

    @Override
    public void init() {
        super.init();
        subscribe(movementHandler.onMovements(trainer), this::onMoveReceived);
    }

}
