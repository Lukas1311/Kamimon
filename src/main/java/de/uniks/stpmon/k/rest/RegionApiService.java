package de.uniks.stpmon.k.rest;

import de.uniks.stpmon.k.dto.Area;
import io.reactivex.rxjava3.core.Observable;
import de.uniks.stpmon.k.dto.Region;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface RegionApiService {
    @GET("regions")
    Observable<List<Region>> getRegions();

    @GET("regions/{id}")
    Observable<Region> getRegion(@Path("id") String id);

    @GET("regions/{region}/areas")
    Observable<List<Area>> getRegions(@Path("region") String region);

    @GET("regions/{region}/areas/{id}")
    Observable<Area> getRegion(@Path("region") String region, @Path("id") String id);

}
