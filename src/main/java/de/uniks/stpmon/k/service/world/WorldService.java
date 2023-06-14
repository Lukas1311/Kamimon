package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.CharacterSetCache;
import de.uniks.stpmon.k.world.CharacterSet;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Singleton
public class WorldService {
    private CharacterSet characterPlaceholder;
    @Inject
    protected CacheManager cacheManager;


    @Inject
    public WorldService() {
    }

    public CharacterSet getCharacter(String name) {
        if (name == null) {
            return getCharacterPlaceholder();
        }
        CharacterSetCache characterSetCache = cacheManager.characterSetCache();
        Optional<CharacterSet> character = characterSetCache.getValue(name);
        return character.orElseGet(this::getCharacterPlaceholder);
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
