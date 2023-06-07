package de.uniks.stpmon.k;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.storage.WorldStorage;
import de.uniks.stpmon.k.service.world.WorldSet;

import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static org.mockito.Mockito.mock;

@Module
public class TestModule {

    @Provides
    static Preferences prefs() {
        return mock(Preferences.class);
    }

    @Provides
    static ResourceBundle resources() {
        return ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    }

    @Provides
    @Singleton
    static EffectContext effectContext() {
        return new EffectContext()
                .setSkipLoading(true)
                .setSkipLoadImages(true)
                .setSkipAnimations(true);
    }

    @Provides
    @Singleton
    static WorldStorage worldStorage() {
        //TODO: remove if we find a way to mock the tilemap for testing
        BufferedImage images = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        //--------------------
        WorldSet world = new WorldSet(images, images, new ArrayList<>(), Collections.emptyMap());
        return new WorldStorage() {
            @Override
            public void setWorld(WorldSet world) {
            }

            @Override
            public WorldSet getWorld() {
                return world;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        };
    }

    @Provides
    @Singleton
    static EventListener eventListener() {
        return mock(EventListener.class);
    }

    @Provides
    static ObjectMapper mapper() {
        return mock(ObjectMapper.class);
    }

}
