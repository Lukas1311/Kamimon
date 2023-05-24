package de.uniks.stpmon.k.rest;

import de.uniks.stpmon.k.dto.Area;
import de.uniks.stpmon.k.dto.CreateTrainerDto;
import de.uniks.stpmon.k.dto.Region;
import de.uniks.stpmon.k.dto.Trainer;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

public interface RegionApiService {

    //---------------- Region Trainers ----------------------------
    @POST("regions/{regionId}/trainers")
    Observable<Trainer> createTrainer(@Path("regionId") String regionId,
                                      @Body CreateTrainerDto trainerDto);

    @GET("regions/{regionId}/trainers")
    Observable<List<Trainer>> getTrainers(@Path("regionId"),
                                          @Query("area") String areaId,
                                          @Query("user") String userId);

    @GET("regions/{regionId}/trainers/{id}")
        //TODO: regionId?
    Observable<Trainer> getTrainer(@Path("id") String trainerId
    );

    @DELETE("regions/{regionId}/trainers/{id}")
    Observable<Trainer> deleteTrainer(@Path("id") String trainerId);

    //------------------- Regions ---------------------------------

    @GET("regions")
    Observable<List<Region>> getRegions();

    @GET("regions/{id}")
    Observable<Region> getRegion(@Path("id") String id);

    //---------------- Region Areas ------------------------------

    @GET("regions/{region}/areas")
    Observable<List<Area>> getRegions(@Path("region") String region);

    @GET("regions/{region}/areas/{id}")
    Observable<Area> getRegion(@Path("region") String region, @Path("id") String id);
}
