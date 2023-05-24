package de.uniks.stpmon.k.rest;


import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface PresetApiService {
    @GET("presets/tilesets/{filename}")
    @Streaming
        //streaming is needed for receiving (larger) files
    Observable<ResponseBody> getTileset(@Path("filename") String filename);

}