package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.constants.NoneConstants;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.storage.TrainerProvider;
import de.uniks.stpmon.k.service.storage.WorldRepository;
import de.uniks.stpmon.k.service.world.MovementHandler;
import de.uniks.stpmon.k.service.world.WorldService;
import de.uniks.stpmon.k.utils.Direction;
import de.uniks.stpmon.k.world.CharacterSet;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.util.Duration;

import javax.inject.Inject;
import java.util.Objects;

import static de.uniks.stpmon.k.utils.ImageUtils.scaledImageFX;

public abstract class EntityView extends WorldViewable {

    @Inject
    protected MovementHandler movementHandler;
    @Inject
    protected WorldRepository worldRepository;
    @Inject
    protected WorldService worldService;
    @Inject
    protected EffectContext effectContext;

    // Not injected by dagger, provided by upper class
    private TrainerProvider trainerProvider;
    protected MeshView entityNode;
    protected CharacterSet characterSet;
    private SpriteAnimation moveAnimation;
    private TranslateTransition moveTranslation;
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

    protected void applySprite(float[] data) {
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
        boolean newDirection = !Objects.equals(currentTrainer.direction(), trainer.direction());
        applyMove(trainer, newDirection);
    }

    protected void applyMove(Trainer trainer, boolean newDirection) {
        trainerProvider.setTrainer(trainer);

        moveTranslation = new TranslateTransition();
        moveTranslation.setNode(entityNode);
        moveTranslation.setDuration(Duration.millis(effectContext.getWalkingSpeed()));
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
        idleAnimation = new SpriteAnimation(characterSet, trainer.direction(), false);
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


    @Override
    public void destroy() {
        super.destroy();
        if (moveAnimation != null) {
            moveAnimation.stop();
        }
        if (idleAnimation != null) {
            idleAnimation.stop();
        }
        if (entityNode != null) {
            entityNode.setMesh(null);
            if (entityNode.getMaterial() instanceof PhongMaterial phongMaterial) {
                phongMaterial.setDiffuseMap(null);
            }
            entityNode.setMaterial(null);
        }
    }


    private class SpriteAnimation extends Transition {

        private final float[] data = new float[4];
        private final CharacterSet characterSet;
        private final int direction;
        private final boolean isMoving;

        public SpriteAnimation(CharacterSet characterSet, int direction, boolean isMoving) {
            this.characterSet = characterSet;
            this.direction = direction;
            this.isMoving = isMoving;
            setInterpolator(Interpolator.LINEAR);
            setCycleDuration(Duration.millis(effectContext.getWalkingAnimationSpeed() * (isMoving ? 1 : 2)));

        }

        @Override
        protected void interpolate(double frac) {
            characterSet.fillSpriteData(data,
                    Math.min((int) (frac * CharacterSet.SPRITES_PER_COLUMN), CharacterSet.SPRITES_PER_COLUMN - 1),
                    Direction.values()[Math.min(Math.max(direction, 0), 3)],
                    isMoving);
            applySprite(data);
        }

    }

}
