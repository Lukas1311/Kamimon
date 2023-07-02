package de.uniks.stpmon.k.di;

import dagger.Module;
import dagger.Provides;
import de.uniks.stpmon.k.Main;

import javax.inject.Singleton;
import java.util.prefs.Preferences;

@Module
public class PrefModule {

    @Provides
    @Singleton
    Preferences prefs() {
        return Preferences.userNodeForPackage(Main.class);
    }

}
