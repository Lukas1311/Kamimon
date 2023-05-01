package de.uniks.stpmon.k.rest;

import io.reactivex.rxjava3.core.Observable;
import de.uniks.stpmon.k.dto.Region;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RegionApiService {
    @GET("regions")
    Observable<List<Region>> getRegions();

    @GET("regions/{id}")
    Observable<Region> getRegion(@Path("id") String id);
}
