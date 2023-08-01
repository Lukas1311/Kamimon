package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.constants.NoneConstants;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.storage.TrainerProvider;
import de.uniks.stpmon.k.service.world.MovementHandler;
import de.uniks.stpmon.k.service.world.WorldService;
import de.uniks.stpmon.k.utils.Direction;
import de.uniks.stpmon.k.utils.ImageUtils;
import de.uniks.stpmon.k.utils.MeshUtils;
import de.uniks.stpmon.k.world.CharacterSet;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Shear;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static de.uniks.stpmon.k.utils.ImageUtils.scaledImageFX;

public abstract class EntityView extends WorldViewable {

    @Inject
    protected WorldService worldService;
    @Inject
    protected EffectContext effectContext;

    // Not injected by dagger, provided by upper class
    private TrainerProvider trainerProvider;
    protected MeshView entityNode;
    protected MeshView shadowNode;
    protected Group entityGroup;
    protected CharacterSet characterSet;
    protected Shear shadowShear;
    private SpriteAnimation moveAnimation;
    private TranslateTransition moveTranslation;
    private Direction moveDirection;
    private Transition idleAnimation;

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
        characterSet = worldService.getCharacter(trainer.image());
    }

    private Node renderShadow() {
        shadowNode = createRectangleScaled(createShadowImage(Direction.from(trainerProvider.getTrainer())),
                -90);

        shadowShear = new Shear(-1, 0, 0, 0);
        shadowNode.getTransforms().add(2, shadowShear);
        shadowNode.getTransforms().add(3,
                new Translate(2 * WorldView.WORLD_UNIT, 0, -0.1 - 0.5 * Math.random()));
        return shadowNode;
    }

    private void applyShadowDirection(Direction direction) {
        if (shadowNode == null) {
            return;
        }
        shadowNode.setMaterial(createMaterial(
                scaledImageFX(createShadowImage(direction),
                        effectContext.getTextureScale())));
    }

    private BufferedImage createShadowImage(Direction direction) {
        return ImageUtils.blackOutImage(characterSet.getPreview(direction), 0.25f);
    }

    private void updateShadowAngel() {
        if (shadowNode == null) {
            return;
        }
        shadowShear.setX(-Math.sin(WorldView.WORLD_ANGLE));
    }

    @Override
    public Node render() {
        MeshView character = createRectangleScaled(
                characterSet.image(),
                CharacterSet.TRAINER_WIDTH, CharacterSet.TRAINER_HEIGHT,
                WorldView.WORLD_ANGLE);
        character.setId("entity");
        entityNode = character;
        Node shadow = renderShadow();
        entityGroup = new Group(entityNode, shadow);
        Trainer trainer = trainerProvider.getTrainer();
        entityGroup.setTranslateX(trainer.x() * WorldView.WORLD_UNIT);
        entityGroup.setTranslateZ((-trainer.y() - WorldView.ENTITY_OFFSET_Y) * WorldView.WORLD_UNIT);
        entityGroup.setTranslateY(-0.35);
        startIdleAnimation(trainer);
        return entityGroup;
    }

    protected void applySprite(float[] data) {
        if (entityNode == null) {
            return;
        }
        TriangleMesh mesh = (TriangleMesh) entityNode.getMesh();
        // Mesh is null if the entity was destroyed
        if (mesh == null) {
            return;
        }
        // Offset to reduce texture bleeding
        float uPadding = (data[2] - data[0]) / 64;
        float vPadding = (data[3] - data[1]) / 256;
        float[] texCoords = {
                data[0] + uPadding, data[1] + vPadding,
                data[2] - uPadding, data[1] + vPadding,
                data[0] + uPadding, data[3] - vPadding,
                data[2] - uPadding, data[3] - vPadding
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
        boolean changedImage = false;
        if (!Objects.equals(characterSet.name(), trainer.image())) {
            characterSet = worldService.getCharacter(trainer.image());
            if (entityNode.getMaterial() instanceof PhongMaterial) {
                Image newTexture = scaledImageFX(characterSet.image(), effectContext.getTextureScale());
                entityNode.setMaterial(createMaterial(newTexture));
            }
            changedImage = true;
        }
        if (Objects.equals(trainer.x(), currentTrainer.x())
                && Objects.equals(trainer.y(), currentTrainer.y())
                && Objects.equals(trainer.direction(), currentTrainer.direction())
                && !changedImage) {
            return;
        }
        if (moveTranslation != null &&
                moveTranslation.getStatus() == Animation.Status.RUNNING) {
            nextTrainer = trainer;
            return;
        }
        nextTrainer = null;
        int diffX = trainer.x() - currentTrainer.x();
        int diffY = trainer.y() - currentTrainer.y();
        Direction dir = Direction.from(trainer);
        if (diffX > 0) {
            dir = Direction.RIGHT;
        } else if (diffX < 0) {
            dir = Direction.LEFT;
        } else if (diffY > 0) {
            dir = Direction.BOTTOM;
        } else if (diffY < 0) {
            dir = Direction.TOP;
        }
        boolean newDirection = !Objects.equals(moveDirection, dir);
        applyMove(trainer, newDirection, dir);
    }

    protected void applyMove(Trainer trainer, boolean newDirection, Direction direction) {
        trainerProvider.setTrainer(trainer);

        // Stop if the entity was destroyed
        if (entityNode == null) {
            return;
        }
        moveDirection = direction;
        moveTranslation = new TranslateTransition();
        moveTranslation.setNode(entityGroup);
        moveTranslation.setDuration(Duration.millis(effectContext.getWalkingSpeed()));
        moveTranslation.setToX(trainer.x() * WorldView.WORLD_UNIT);
        moveTranslation.setToZ(-trainer.y() * WorldView.WORLD_UNIT
                - WorldView.ENTITY_OFFSET_Y * WorldView.WORLD_UNIT);
        moveTranslation.play();
        moveTranslation.setOnFinished((t) -> {
            // Stop if the entity was destroyed
            if (entityNode == null) {
                return;
            }
            if (nextTrainer == null) {
                startIdleAnimation(trainer);
                moveDirection = null;
                return;
            }
            onMoveReceived(nextTrainer);
        });
        if (idleAnimation != null) {
            idleAnimation.stop();
        }
        if (moveAnimation == null
                || newDirection) {
            if (moveAnimation != null) {
                moveAnimation.pause();
            }
            moveAnimation = new SpriteAnimation(characterSet, direction.ordinal(), true);
            moveAnimation.setCycleCount(Animation.INDEFINITE);
            applyShadowDirection(direction);
        }
        moveAnimation.play();
    }

    private void startIdleAnimation(Trainer trainer) {
        if (moveAnimation != null) {
            moveAnimation.pause();
        }
        if (idleAnimation != null) {
            idleAnimation.stop();
        }
        idleAnimation = new SpriteAnimation(characterSet, trainer.direction(), false);
        idleAnimation.setCycleCount(Animation.INDEFINITE);
        idleAnimation.play();
    }

    @Override
    public void init() {
        super.init();
        initTrainer();
        MovementHandler movementHandler = getMovementHandler();
        movementHandler.setInitialTrainer(trainerProvider);
        subscribe(movementHandler.onMovements(), this::onMoveReceived);
    }

    public abstract MovementHandler getMovementHandler();

    @Override
    public void destroy() {
        super.destroy();
        if (moveAnimation != null) {
            moveAnimation.stop();
            moveAnimation = null;
        }
        if (idleAnimation != null) {
            idleAnimation.stop();
            idleAnimation = null;
        }
        if (entityNode != null) {
            MeshUtils.disposeMesh(entityNode);
            entityNode = null;
        }
        entityGroup = null;
    }


    private class SpriteAnimation extends Transition {

        private final float[] data = new float[4];
        private final CharacterSet characterSet;
        private final int direction;
        private final boolean isMoving;
        private int index = -10;

        public SpriteAnimation(CharacterSet characterSet, int direction, boolean isMoving) {
            this.characterSet = characterSet;
            this.direction = direction;
            this.isMoving = isMoving;
            setInterpolator(Interpolator.LINEAR);
            setCycleDuration(Duration.millis(effectContext.getWalkingAnimationSpeed() * (isMoving ? 1 : 2)));

        }

        @Override
        protected void interpolate(double frac) {
            if (entityNode == null) {
                stop();
                return;
            }
            int currentIndex = Math.min((int) (frac * CharacterSet.SPRITES_PER_COLUMN), CharacterSet.SPRITES_PER_COLUMN - 1);
            if (currentIndex == index) {
                return;
            }
            index = currentIndex;
            characterSet.fillSpriteData(data,
                    index,
                    Direction.VALUES[Math.min(Math.max(direction, 0), 3)],
                    isMoving);
            applySprite(data);
        }

    }

}
