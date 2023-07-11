package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.world.CharacterSet;
import javafx.animation.Interpolator;

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

    private CharacterSet characterPlaceholder;
    @Inject
    protected TextureSetService textureSetService;


    @Inject
    public WorldService() {
    }

    public float getNightFactor(LocalTime time) {
        int hour = time.getHour();
        int minute = time.getMinute();
        if (hour > 7 && hour < 20) {
            return 0;
        }
        if (hour == 20) {
            return (int) (Interpolator.EASE_IN.interpolate(0f, 1f, minute / 60f) * 100) / 100.0f;
        }
        if (hour == 7) {
            return (int) (Interpolator.EASE_OUT.interpolate(1f, 0f, minute / 60f) * 100) / 100.0f;
        }
        return 1;
    }

    public CharacterSet getCharacter(String name) {
        if (name == null) {
            return getCharacterPlaceholder();
        }
        Optional<CharacterSet> character = textureSetService.getCharacter(name);
        return character.orElseGet(this::getCharacterPlaceholder);
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
