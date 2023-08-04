package de.uniks.stpmon.k.world;

/**
 * @param scaleX     Scale factor for x-axis
 * @param scaleY     Scale factor for y-axis
 * @param shearX     Shear factor for x-axis
 * @param shearY     Shear factor for y-axis
 * @param timeFactor Current factor which represents the time of day.
 */
public record ShadowTransform(
        float scaleX,
        float scaleY,
        float shearX,
        float shearY,
        float timeFactor) {
    public static final ShadowTransform DEFAULT_ENABLED = new ShadowTransform(1.0f, 1.0f, 0.0f, 0.0f, 0.0f);
    public static final ShadowTransform DEFAULT_DISABLED = new ShadowTransform(0.0f, 0.0f, 0.0f, 0.0f, -2f);
    public static final int DISABLED_FACTOR = -2;

    public boolean isDisabled() {
        return timeFactor() > 1.0f || timeFactor() < -1.0f;
    }
}
