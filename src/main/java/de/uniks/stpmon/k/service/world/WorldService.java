package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.world.CharacterSet;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
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

    public LocalTime getCurrentTime() {
        return Instant.now().atZone(ZoneId.systemDefault()).toLocalTime();
    }

    public float getNightFactor(LocalTime time) {
        int hour = time.getHour();
        int factor = ((hour - 20) + 24) % 24 - 6;
        return (int) (Math.max(0, -((1f) / (36f)) * factor * factor + 1) * 100) / 100.0f;
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
