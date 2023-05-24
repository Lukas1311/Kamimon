package de.uniks.stpmon.k.rest;

import de.uniks.stpmon.k.dto.*;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

public interface RegionApiService {

    //---------------- Region Trainers ----------------------------
    @POST("regions/{regionId}/trainers")
    Observable<Trainer> createTrainer(@Path("regionId") String regionId,
                                      @Body CreateTrainerDto trainerDto);

    @GET("regions/{regionId}/trainers")
    Observable<List<Trainer>> getTrainers(@Path("regionId") String regionId,
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
    Observable<List<Area>> getAreas(@Path("region") String region);

    @GET("regions/{region}/areas/{id}")
    Observable<Area> getArea(@Path("region") String region, @Path("id") String id);

    //------------- Trainer Monsters -------------------------------
    @GET("regions/{regionId}/trainers/{trainerId}/monsters")
    Observable<List<Monster>> getMonsters(@Path("trainerId") String trainerId);

    @GET("regions/{regionId}/trainers/{trainerId}/monsters/{id}")
    Observable<Monster> getMonster(@Path("id") String monsterId);
}
