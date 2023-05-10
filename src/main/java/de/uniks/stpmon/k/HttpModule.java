package de.uniks.stpmon.k;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import de.uniks.stpmon.k.rest.*;
import de.uniks.stpmon.k.service.TokenStorage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.inject.Singleton;

@Module
public class HttpModule {
    @Provides
    @Singleton
    static OkHttpClient client(TokenStorage tokenStorage){
        return new OkHttpClient.Builder().addInterceptor(chain -> {
            final String token = tokenStorage.getToken();
            if(token == null){
                return chain.proceed(chain.request());
            }
            // TODO: remove at the end: token for debugging swagger api
            System.out.println(token);
            final Request newRequest = chain
                    .request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(newRequest);
        }).build();
    }

    @Provides
    @Singleton
    Retrofit retrofit(OkHttpClient client, ObjectMapper mapper){
        return new Retrofit.Builder()
                .baseUrl(Main.API_URL.endsWith("/") ? Main.API_URL : Main.API_URL + "/")
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper)) //connection dagger & retrofit
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    AuthenticationApiService authApi(Retrofit retrofit) {
        return retrofit.create(AuthenticationApiService.class);
    }

    @Provides
    @Singleton
    UserApiService userApi(Retrofit retrofit){
        return retrofit.create(UserApiService.class);
    }

    @Provides
    @Singleton
    GroupApiService groupApi(Retrofit retrofit){
        return retrofit.create(GroupApiService.class);
    }
    @Provides
    @Singleton
    RegionApiService regionApi(Retrofit retrofit) {
        return retrofit.create(RegionApiService.class);
    }

    @Provides
    @Singleton
    MessageApiService messageApi(Retrofit retrofit) {
        return retrofit.create(MessageApiService.class);
    }
}
