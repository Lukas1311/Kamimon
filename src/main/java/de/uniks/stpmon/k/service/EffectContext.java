package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.views.world.EntityView;

/**
 * Service which controls the rendering of images and animations.
 * It can be used to skip image loading and animations while testing or in other cases.
 */
public class EffectContext {

    private boolean skipLoadImages = false;
    private boolean skipAnimations = false;
    private boolean skipLoading = false;
    private int walkingSpeed = EntityView.MOVEMENT_PERIOD;
    private int walkingAnimationSpeed = EntityView.WALKING_ANIMATION_PERIOD;
    private double textureScale = 4.0d;


    /**
     * Tells the controllers to skip loading images.
     * This is usually used to skip image loading when the view is tested.
     *
     * @param skipLoadImages true if images should not be loaded, false otherwise
     * @return The current instance for chaining method calls
     */
    public EffectContext setSkipLoadImages(boolean skipLoadImages) {
        this.skipLoadImages = skipLoadImages;
        return this;
    }

    /**
     * Checks if images should be skipped.
     *
     * @return true if images should not be loaded, false otherwise
     */
    public boolean shouldSkipLoadImages() {
        return skipLoadImages;
    }


    /**
     * Tells the controllers to skip animations.
     *
     * @param skipAnimations true if animations should be skipped, false otherwise
     * @return The current instance for chaining method calls
     */
    public EffectContext setSkipAnimations(boolean skipAnimations) {
        this.skipAnimations = skipAnimations;
        return this;
    }

    /**
     * Checks if animations should be skipped.
     *
     * @return true if animations should be skipped, false otherwise
     */
    public boolean shouldSkipAnimations() {
        return skipAnimations;
    }

    /**
     * Tells the loading screen not to be shown and instead call the onLoadingFinished callback immediately.
     *
     * @param skipLoading true if loading should be skipped, false otherwise
     * @return The current instance for chaining method calls
     */
    public EffectContext setSkipLoading(boolean skipLoading) {
        this.skipLoading = skipLoading;
        return this;
    }

    /**
     * Checks if the loading screen should be skipped.
     *
     * @return true if loading should be skipped, false otherwise
     */
    public boolean shouldSkipLoading() {
        return skipLoading;
    }

    /**
     * Sets the walking speed of the player.
     * This is the time in milliseconds the player needs to walk one tile.
     *
     * @param walkingSpeed The walking speed of the player in milliseconds per tile
     */
    public EffectContext setWalkingSpeed(int walkingSpeed) {
        this.walkingSpeed = walkingSpeed;
        return this;
    }

    /**
     * Gets the walking speed of the player.
     *
     * @return The walking speed of the player in milliseconds per tile.
     */
    public int getWalkingSpeed() {
        return walkingSpeed;
    }

    /**
     * Sets the walking animation speed of the player.
     *
     * @param walkingAnimationSpeed The walking animation speed of the player in milliseconds per 6 frames
     */
    public EffectContext setWalkingAnimationSpeed(int walkingAnimationSpeed) {
        this.walkingAnimationSpeed = walkingAnimationSpeed;
        return this;
    }

    /**
     * Gets the walking animation speed of the player.
     *
     * @return The walking animation speed of the player in milliseconds per 6 frames
     */
    public int getWalkingAnimationSpeed() {
        return walkingAnimationSpeed;
    }

    /**
     * Sets the texture scale of the texture used in the world.
     *
     * @param textureScale Texture scale
     */
    public EffectContext setTextureScale(double textureScale) {
        this.textureScale = textureScale;
        return this;
    }

    /**
     * Gets the texture scale of the texture used in the world.
     *
     * @return Texture scale
     */
    public double getTextureScale() {
        return textureScale;
    }
}
