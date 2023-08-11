package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.service.SettingsService;
import de.uniks.stpmon.k.world.CharacterSet;
import de.uniks.stpmon.k.world.DayCycle;
import de.uniks.stpmon.k.world.ShadowTransform;
import io.reactivex.rxjava3.core.Observable;
import javafx.animation.Interpolator;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

@Singleton
public class WorldService {
    public static final DayCycle DAY_CYCLE = new DayCycle(8, 20, 3);

    private static final Color[] DAY_COLORS = {
            Color.WHITE,
            Color.LIGHTGOLDENRODYELLOW.interpolate(Color.GOLDENROD, 0.5f),
            Color.LIGHTSLATEGREY,
            Color.LIGHTSLATEGREY.interpolate(Color.MIDNIGHTBLUE, 0.75f)
    };
    public static final float SECONDS_TO_HOURS = 60f * 60f;

    private CharacterSet characterPlaceholder;
    @Inject
    protected TextureSetService textureSetService;
    @Inject
    protected SettingsService settingsService;

    @Inject
    public WorldService() {
    }

    /**
     * Returns a factor that represents the progress of the night. From 0 to 1 and to 0 again.
     *
     * @param time The time of the day.
     * @return 0 at day, 1 at night, 0 at day again. An interpolated value in between.
     */
    public float getNightFactor(LocalTime time) {
        if (!settingsService.getNightEnabled()) {
            return 0;
        }
        if (DAY_CYCLE.isDay(time)) {
            return 0;
        }
        if (DAY_CYCLE.isSunset(time)) {
            LocalTime timeSinceStart = time.minusHours(DAY_CYCLE.sunset());
            float transitionFactor = timeSinceStart.toSecondOfDay() / (SECONDS_TO_HOURS * DAY_CYCLE.transition());
            return (int) (Interpolator.EASE_OUT.interpolate(0f, 1f, transitionFactor) * 100) / 100.0f;
        }
        if (DAY_CYCLE.isSunrise(time)) {
            LocalTime timeSinceStart = time.minusHours(DAY_CYCLE.sunrise());
            float transitionFactor = timeSinceStart.toSecondOfDay() / (SECONDS_TO_HOURS * DAY_CYCLE.transition());
            return (int) (Interpolator.EASE_IN.interpolate(1f, 0f, transitionFactor) * 100) / 100.0f;
        }
        return 1;
    }

    /**
     * Returns a factor that represents the progress of the day. From -1 to 1.
     *
     * @param time The time of the day.
     * @return -1 is in the morning, 0 is at noon, 1 is in the evening.
     */
    public float getDayFactor(LocalTime time) {
        if (!settingsService.getNightEnabled()) {
            return ShadowTransform.DISABLED_FACTOR;
        }
        if (time.getHour() < DAY_CYCLE.dayStart()) {
            return -1;
        }
        if (time.getHour() > DAY_CYCLE.nightStart()) {
            return 1;
        }
        LocalTime scaledTime = time
                .minusHours(DAY_CYCLE.dayStart());
        // Hours between day start and sunset
        int span = DAY_CYCLE.sunset() - DAY_CYCLE.dayStart();
        float halfSpan = span / 2f;
        return ((scaledTime.toSecondOfDay() / SECONDS_TO_HOURS) - halfSpan) / halfSpan;
    }

    /**
     * Returns a transform that should be applied to shadows to represent the time of the day.
     *
     * @param time The time of the day.
     * @return A transformation with scale and shear.
     */
    public ShadowTransform getShadowTransform(LocalTime time) {
        if (!settingsService.getNightEnabled()) {
            return ShadowTransform.DEFAULT_DISABLED;
        }
        float factor = getDayFactor(time);
        float nightFactor = getNightFactor(time);
        if (nightFactor == 1) {
            return ShadowTransform.DEFAULT_DISABLED;
        }
        return new ShadowTransform(1.0f, 1 - Math.abs(factor) / 4f,
                factor * 2, 0.0f,
                roundDown(nightFactor, 0.05f));
    }

    @SuppressWarnings("SameParameterValue")
    private static float roundDown(float x, float a) {
        return (float) (Math.floor(x / a) * a);
    }

    public Color getWorldColor(LocalTime time) {
        float factor = getNightFactor(time);
        if (factor == 1) {
            return DAY_COLORS[DAY_COLORS.length - 1];
        } else if (factor == 0) {
            return DAY_COLORS[0];
        }
        float night = factor * 3 % 3;
        int index = (int) (factor * 3);
        Color color = DAY_COLORS[index];
        Color nextColor = DAY_COLORS[(index + 1) % DAY_COLORS.length];
        return color.interpolate(nextColor, night - index);
    }

    public CharacterSet getCharacter(String name) {
        if (name == null) {
            return getCharacterPlaceholder();
        }
        Optional<CharacterSet> character = textureSetService.getCharacter(name);
        return character.orElseGet(this::getCharacterPlaceholder);
    }

    public boolean isCharacterLoaded(String name) {
        return textureSetService.getCharacter(name)
                .isPresent();
    }

    public Observable<Optional<CharacterSet>> getCharacterLazy(String id) {
        return textureSetService.getCharacterLazy(id);
    }

    public CharacterSet getCharacterPlaceholder() {
        if (characterPlaceholder == null) {
            BufferedImage image;
            try (InputStream stream = CharacterSet.class.getResourceAsStream("char.png")) {
                image = ImageIO.read(Objects.requireNonNull(stream));
            } catch (IOException e) {
                image = new BufferedImage(384, 96, BufferedImage.TYPE_INT_RGB);
            }
            characterPlaceholder = new CharacterSet("placeholder", image);
        }
        return characterPlaceholder;
    }
}
