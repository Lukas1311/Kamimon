package de.uniks.stpmon.k;

import dagger.Module;
import dagger.Provides;
import de.uniks.stpmon.k.service.TileMapService;
import de.uniks.stpmon.k.ws.EventListener;

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
    static EventListener eventListener() {
        return mock(EventListener.class);
    }

    @Provides
    @Singleton
    static TileMapService tileMapService() {
        return mock(TileMapService.class);
    }

}
