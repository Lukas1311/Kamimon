package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.constants.NoneConstants;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.map.TrainerSprite;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.WorldStorage;
import de.uniks.stpmon.k.service.world.CharacterSet;
import de.uniks.stpmon.k.service.world.MovementHandler;
import de.uniks.stpmon.k.service.world.MovementScheduler;
import de.uniks.stpmon.k.utils.Direction;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.util.Duration;

import javax.inject.Inject;
import java.util.Objects;

public class EntityView extends WorldViewable {

    @Inject
    protected MovementHandler movementHandler;
    @Inject
    protected WorldStorage worldStorage;
    @Inject
    protected TrainerStorage trainerStorage;
    protected MeshView entityNode;
    protected Trainer trainer;
    protected CharacterSet characterSet;
    private SpriteAnimation moveAnimation;
    private TranslateTransition moveTranslation;

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
        startIdleAnimation(trainer);
        return character;
    }

    protected void applySprite(TrainerSprite sprite) {
        TriangleMesh mesh = (TriangleMesh) entityNode.getMesh();
        // Offset to reduce texture bleeding
        float uPadding = (sprite.maxU() - sprite.minU()) / 64;
        float vPadding = (sprite.maxV() - sprite.minV()) / 64;
        float[] texCoords = {
                sprite.minU() + uPadding, sprite.minV() + vPadding,
                sprite.maxU() - uPadding, sprite.minV() + vPadding,
                sprite.minU() + uPadding, sprite.maxV() - vPadding,
                sprite.maxU() - uPadding, sprite.maxV() - vPadding
        };
        mesh.getTexCoords().setAll(texCoords);
    }

    private Trainer nextTrainer;

    protected void onMoveReceived(Trainer trainer) {
        // Check for invalid trainers
        if (trainer == null
                || trainer == NoneConstants.NONE_TRAINER) {
            return;
        }
        Trainer currentTrainer = trainerStorage.getTrainer();
        if (!currentTrainer.area().equals(trainer.area())) {
            return;
        }
        if (moveTranslation != null &&
                moveTranslation.getStatus() == Animation.Status.RUNNING) {
            nextTrainer = trainer;
            return;
        }
        nextTrainer = null;
        boolean newDirection = !Objects.equals(currentTrainer.direction(), trainer.direction());
        applyMove(trainer, newDirection);
    }

    protected void applyMove(Trainer trainer, boolean newDirection) {
        this.trainer = trainer;
        onMove(trainer);

        moveTranslation = new TranslateTransition();
        moveTranslation.setNode(entityNode);
        moveTranslation.setDuration(Duration.millis(MovementScheduler.MOVEMENT_PERIOD));
        moveTranslation.setToX(trainer.x() * WorldView.WORLD_UNIT);
        moveTranslation.setToZ(-trainer.y() * WorldView.WORLD_UNIT
                - WorldView.ENTITY_OFFSET_Y * WorldView.WORLD_UNIT);
        moveTranslation.play();
        moveTranslation.setOnFinished((t) -> {
            if (nextTrainer == null) {
                startIdleAnimation(trainer);
                return;
            }
            onMoveReceived(nextTrainer);
        });
        if (moveAnimation == null
                || newDirection) {
            moveAnimation = new SpriteAnimation(characterSet, trainer.direction(), true);
            moveAnimation.setCycleCount(Animation.INDEFINITE);
        }
        moveAnimation.play();
    }

    protected void onMove(Trainer trainer) {

    }

    private void startIdleAnimation(Trainer trainer) {
        if (moveAnimation != null) {
            moveAnimation.pause();
        }
        Transition idleAnimation = new SpriteAnimation(characterSet, trainer.direction(), false);
        idleAnimation.setCycleCount(Animation.INDEFINITE);
        idleAnimation.play();
    }

    @Override
    public void init() {
        super.init();
        subscribe(movementHandler.onMovements(trainer), this::onMoveReceived);
    }

    private class SpriteAnimation extends Transition {
        private final CharacterSet characterSet;
        private final int direction;
        private final boolean isMoving;

        public SpriteAnimation(CharacterSet characterSet, int direction, boolean isMoving) {
            this.characterSet = characterSet;
            this.direction = direction;
            this.isMoving = isMoving;
            setCycleDuration(Duration.millis(MovementScheduler.MOVEMENT_PERIOD * 5));

        }

        public int getDirection() {
            return direction;
        }

        @Override
        protected void interpolate(double frac) {
            applySprite(characterSet.getSprite(
                    Math.min((int) (frac * CharacterSet.SPRITES_PER_COLUMN), CharacterSet.SPRITES_PER_COLUMN - 1),
                    Direction.values()[Math.min(Math.max(direction, 0), 3)],
                    isMoving));
        }
    }
}
