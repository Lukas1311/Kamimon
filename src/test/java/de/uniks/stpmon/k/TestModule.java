package de.uniks.stpmon.k;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.service.EffectContext;

import javax.inject.Singleton;
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
                .setSkipAnimations(true)
                .setWalkingSpeed(1)
                .setTextureScale(1.0d);
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
