package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.world.CharacterSet;
import de.uniks.stpmon.k.world.WorldSet;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Singleton
public class WorldService {
    private CharacterSet characterPlaceholder;

    @Inject
    public WorldService() {
    }

    public CharacterSet getCharacter(WorldSet worldSet, String name) {
        if (name == null) {
            return getCharacterPlaceholder();
        }
        if (worldSet == null) {
            return getCharacterPlaceholder();
        }
        Map<String, CharacterSet> characterSet = worldSet.characters();
        if (characterSet == null || !characterSet.containsKey(name)) {
            return getCharacterPlaceholder();
        }
        return characterSet.get(name);
    }

    public CharacterSet getCharacterPlaceholder() {
        if (characterPlaceholder == null) {
            BufferedImage image;
            try {
                image = ImageIO.read(Objects.requireNonNull(CharacterSet.class.getResourceAsStream("char.png")));
            } catch (IOException e) {
                image = new BufferedImage(384, 96, BufferedImage.TYPE_INT_RGB);
            }
            characterPlaceholder = new CharacterSet("placeholder", image);
        }
        return characterPlaceholder;
    }
}
