package de.uniks.stpmon.k.di;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import de.uniks.stpmon.k.service.EffectContext;

import javax.inject.Singleton;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

@Module
public class MainModule {


    //needed for languages
    @Provides
    ResourceBundle bundle(Preferences preferences) {
        String locale = preferences.get("locale", Locale.ROOT.toLanguageTag());
        if (locale.equals("en")) {
            locale = "";
        }
        return ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.forLanguageTag(locale));
    }

    @Provides
    @Singleton
    ObjectMapper mapper() {
        return new ObjectMapper()
                //needed for error messages
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                //not all properties must be loaded from server
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                //null values are not written into json
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
    }

    @Provides
    @Singleton
    EffectContext effectContext() {
        return new EffectContext();
    }

}
