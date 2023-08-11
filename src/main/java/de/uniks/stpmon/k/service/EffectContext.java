package de.uniks.stpmon.k.service;

/**
 * Service which controls the rendering of images and animations.
 * It can be used to skip image loading and animations while testing or in other cases.
 */
@SuppressWarnings("unused")
public class EffectContext {

    public static final int MOVEMENT_PERIOD = 150;
    public static final int WALKING_ANIMATION_PERIOD = MOVEMENT_PERIOD * 7;
    private float sprintingFactor = 0.65f;
    private boolean skipLoadImages = false;
    private boolean skipAnimations = false;
    private boolean skipLoading = false;
    private boolean skipAudio = false;
    private int walkingSpeed = MOVEMENT_PERIOD;
    private int walkingAnimationSpeed = WALKING_ANIMATION_PERIOD;
    private double textureScale = 3.0d;
    private int dialogAnimationSpeed = 450;

    private double encounterAnimationSpeed = 1000;

    /**
     * Gets the sprinting factor.
     *
     * @return The dialog animation speed
     */
    public float getSprintingFactor() {
        return sprintingFactor;
    }

    /**
     * Sets the sprinting factor.
     *
     * @param sprintingFactor The sprinting factor
     */
    public void setSprintingFactor(float sprintingFactor) {
        this.sprintingFactor = sprintingFactor;
    }

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

    public EffectContext setSkipLoadAudio(boolean skipAudio) {
        this.skipAudio = skipAudio;
        return this;
    }

    /**
     * Checks if the loading of mp3 audio files should be skipped.
     *
     * @return true if loading should be skipped, false otherwise
     */
    public boolean shouldSkipLoadAudio() {
        return skipAudio;
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
    @SuppressWarnings("unused")
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

    /**
     * Sets the speed of the open dialog animation.
     *
     * @param dialogAnimationSpeed speed of the open dialog animation
     */
    public EffectContext setDialogAnimationSpeed(int dialogAnimationSpeed) {
        this.dialogAnimationSpeed = dialogAnimationSpeed;
        return this;
    }

    /**
     * Returns the speed of the open dialog animation.
     *
     * @return Speed of the open dialog animation.
     */
    public int getDialogAnimationSpeed() {
        return dialogAnimationSpeed;
    }

    /**
     * Sets the speed of the encounter animation.
     *
     * @param encounterAnimationSpeed speed of the encounter animation
     */
    public EffectContext setEncounterAnimationSpeed(double encounterAnimationSpeed) {
        this.encounterAnimationSpeed = encounterAnimationSpeed;
        return this;
    }

    /**
     * Returns the speed of the encounter animation.
     *
     * @return speed of the encounter animation
     */
    public double getEncounterAnimationSpeed() {
        return encounterAnimationSpeed;
    }
}
