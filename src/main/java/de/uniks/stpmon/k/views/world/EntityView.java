package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.constants.NoneConstants;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.map.TrainerSprite;
import de.uniks.stpmon.k.service.storage.TrainerProvider;
import de.uniks.stpmon.k.service.storage.WorldStorage;
import de.uniks.stpmon.k.service.world.CharacterSet;
import de.uniks.stpmon.k.service.world.MovementHandler;
import de.uniks.stpmon.k.service.world.WorldSet;
import de.uniks.stpmon.k.utils.Direction;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public abstract class EntityView extends WorldViewable {

    public static final int MOVEMENT_PERIOD = 200;

    @Inject
    protected MovementHandler movementHandler;
    @Inject
    protected WorldStorage worldStorage;
    // Not injected by dagger, provided by upper class
    private TrainerProvider trainerProvider;
    protected MeshView entityNode;
    protected CharacterSet characterSet;
    private SpriteAnimation moveAnimation;
    private TranslateTransition moveTranslation;

    protected TrainerProvider getProvider() {
        return trainerProvider;
    }

    protected void initTrainer() {
        if (trainerProvider == null) {
            trainerProvider = getProvider();
        }
        Trainer trainer = trainerProvider.getTrainer();
        if (trainer == null) {
            throw new IllegalStateException("Trainer cannot be null");
        }
        WorldSet worldSet = worldStorage.getWorld();
        if (worldSet != null) {
            characterSet = worldSet
                    .characters()
                    .get(trainer.image());
        } else {
            characterSet = getPlaceholder();
        }
    }

    private CharacterSet getPlaceholder() {
        BufferedImage image;
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("char.png")));
        } catch (IOException e) {
            image = new BufferedImage(384, 96, BufferedImage.TYPE_INT_RGB);
        }
        return new CharacterSet("placeholder", image);
    }

    @Override
    public Node render() {
        MeshView character = createRectangleScaled(
                characterSet.image(),
                CharacterSet.TRAINER_WIDTH, CharacterSet.TRAINER_HEIGHT,
                WorldView.WORLD_ANGLE);
        character.setId("entity");
        entityNode = character;
        Trainer trainer = trainerProvider.getTrainer();
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
        float vPadding = (sprite.maxV() - sprite.minV()) / 256;
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
        Trainer currentTrainer = trainerProvider.getTrainer();
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
        trainerProvider.setTrainer(trainer);

        moveTranslation = new TranslateTransition();
        moveTranslation.setNode(entityNode);
        moveTranslation.setDuration(Duration.millis(MOVEMENT_PERIOD));
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
        initTrainer();
        movementHandler.setInitialTrainer(trainerProvider);
        subscribe(movementHandler.onMovements(), this::onMoveReceived);
    }


    private class SpriteAnimation extends Transition {
        private final CharacterSet characterSet;
        private final int direction;
        private final boolean isMoving;

        public SpriteAnimation(CharacterSet characterSet, int direction, boolean isMoving) {
            this.characterSet = characterSet;
            this.direction = direction;
            this.isMoving = isMoving;
            setCycleDuration(Duration.millis(MOVEMENT_PERIOD * 5));

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
