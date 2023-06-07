package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.dto.MoveTrainerDto;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.map.TrainerSprite;
import de.uniks.stpmon.k.service.storage.WorldStorage;
import de.uniks.stpmon.k.service.world.CharacterSet;
import de.uniks.stpmon.k.service.world.MovementHandler;
import de.uniks.stpmon.k.service.world.MovementScheduler;
import de.uniks.stpmon.k.utils.Direction;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.util.Duration;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicInteger;

public class EntityView extends WorldViewable {

    @Inject
    protected MovementHandler movementHandler;
    @Inject
    protected WorldStorage worldStorage;
    protected MeshView entityNode;
    protected Trainer trainer;
    protected CharacterSet characterSet;

    @Inject
    public EntityView() {
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
        characterSet = worldStorage.getWorld()
                .characters()
                .get(trainer.image());
    }

    @Override
    public Node render() {
        MeshView character = createRectangleScaled(
                characterSet.image(),
                CharacterSet.TRAINER_WIDTH, CharacterSet.TRAINER_HEIGHT,
                WorldView.WORLD_ANGLE);
        character.setId("entity");
        entityNode = character;

        entityNode.setTranslateX(trainer.x() * WorldView.WORLD_UNIT);
        entityNode.setTranslateZ(-trainer.y() * WorldView.WORLD_UNIT -
                WorldView.ENTITY_OFFSET_Y * WorldView.WORLD_UNIT);
        // Set default sprite
        applySprite(characterSet.getSprite(0, Direction.BOTTOM, false));
        return character;
    }

    protected void applySprite(TrainerSprite sprite) {
        TriangleMesh mesh = (TriangleMesh) entityNode.getMesh();
        float[] texCoords = {
                sprite.minU(), sprite.minV(),
                sprite.maxU(), sprite.minV(),
                sprite.minU(), sprite.maxV(),
                sprite.maxU(), sprite.maxV()
        };
        mesh.getTexCoords().setAll(texCoords);
    }

    AtomicInteger count = new AtomicInteger();

    protected void onMoveReceived(MoveTrainerDto dto) {
        TranslateTransition transition = new TranslateTransition();
        transition.setNode(entityNode);
        transition.setDuration(Duration.millis(MovementScheduler.MOVEMENT_PERIOD));
        transition.setToX(dto.x() * WorldView.WORLD_UNIT);
        transition.setToZ(-dto.y() * WorldView.WORLD_UNIT
                - WorldView.ENTITY_OFFSET_Y * WorldView.WORLD_UNIT);
        transition.play();
        Transition spriteTransition = new Transition() {
            {
                setCycleDuration(Duration.millis(MovementScheduler.MOVEMENT_PERIOD * 2));
            }

            @Override
            protected void interpolate(double frac) {
                applySprite(characterSet.getSprite((int) (frac * CharacterSet.SPRITES_PER_COLUMN), Direction.values()[Math.min(Math.max(dto.direction(), 0), 3)], true));
            }
        };
        spriteTransition.play();
    }

    @Override
    public void init() {
        super.init();
        subscribe(movementHandler.onMovements(trainer), this::onMoveReceived);
    }

}
