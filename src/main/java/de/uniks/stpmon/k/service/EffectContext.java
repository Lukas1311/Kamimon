package de.uniks.stpmon.k.service;

/**
 * Service which controls the rendering of images and animations.
 * It can be used to skip image loading and animations while testing or in other cases.
 */
public class EffectContext {
    private boolean skipLoadImages = false;
    private boolean skipAnimations = false;
    private boolean skipLoading = false;


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
}
