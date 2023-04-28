package de.uniks.stpmon.k;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class MainModule {
    @Provides
    @Singleton
    ObjectMapper mapper(){
        return new ObjectMapper()
                //needed for error messages
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                //not all properties must be loaded from server
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                //null values are not written into json
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
    }
}
