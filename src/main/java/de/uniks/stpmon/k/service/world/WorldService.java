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
            Color.MIDNIGHTBLUE.brighter().brighter(),
            Color.LIGHTSLATEGREY,
            Color.LIGHTYELLOW,
            Color.WHITE,
            Color.LIGHTGOLDENRODYELLOW,
            Color.LIGHTSLATEGREY,
            Color.MIDNIGHTBLUE.brighter().brighter()
    };

    private CharacterSet characterPlaceholder;
    @Inject
    protected TextureSetService textureSetService;
    @Inject
    protected SettingsService settingsService;

    @Inject
    public WorldService() {
    }

    public float getNightFactor(LocalTime time) {
        if (!settingsService.getNightEnabled()) {
            return 0;
        }
        float factor = 1.0f;
        int seconds = (int) (time.toSecondOfDay() * factor);
        LocalTime scaledTime = LocalTime.ofSecondOfDay(seconds);
        int hour = scaledTime.getHour();
        if (hour >= (DAY_CYCLE.dayStart())
                && hour < (DAY_CYCLE.nightStart() - DAY_CYCLE.transition())) {
            return 0;
        }
        if (hour < DAY_CYCLE.nightStart() && hour >= DAY_CYCLE.nightStart() - DAY_CYCLE.transition()) {
            LocalTime dayStart = scaledTime.minusHours(DAY_CYCLE.nightStart() - DAY_CYCLE.transition());
            float transitionFactor = dayStart.toSecondOfDay() / (60f * 60f * DAY_CYCLE.transition());
            return (int) (Interpolator.EASE_OUT.interpolate(0f, 1f, transitionFactor) * 100) / 100.0f;
        }
        if (hour >= DAY_CYCLE.dayStart() - DAY_CYCLE.transition() && hour < DAY_CYCLE.dayStart()) {
            LocalTime dayStart = scaledTime.minusHours(DAY_CYCLE.dayStart() - DAY_CYCLE.transition());
            float transitionFactor = dayStart.toSecondOfDay() / (60f * 60f * DAY_CYCLE.transition());
            return (int) (Interpolator.EASE_IN.interpolate(1f, 0f, transitionFactor) * 100) / 100.0f;
        }
        return 1;
    }

    public float getDayFactor(LocalTime time) {
        if (!settingsService.getNightEnabled()) {
            return 0;
        }
        return 1;
    }

    public ShadowTransform getShadowTransform(LocalTime time) {
        float factor = getDayFactor(time);
        return ShadowTransform.EMPTY;
    }

    public Color getWorldColor(LocalTime time) {
        return DAY_COLORS[3];
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
