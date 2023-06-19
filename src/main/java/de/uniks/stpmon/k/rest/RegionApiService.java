package de.uniks.stpmon.k.rest;

import de.uniks.stpmon.k.dto.CreateTrainerDto;
import de.uniks.stpmon.k.dto.UpdateOpponentDto;
import de.uniks.stpmon.k.dto.UpdateTrainerDto;
import de.uniks.stpmon.k.models.*;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface RegionApiService {

    //---------------- Region Trainers ----------------------------
    @POST("regions/{region}/trainers")
    Observable<Trainer> createTrainer(@Path("region") String regionId,
                                      @Body CreateTrainerDto trainerDto);

    @GET("regions/{region}/trainers")
    Observable<List<Trainer>> getTrainers(@Path("region") String regionId,
                                          @Query("area") String areaId);

    @GET("regions/{region}/trainers")
    Observable<List<Trainer>> getTrainers(@Path("region") String regionId);

    @GET("regions/{region}/trainers")
    Observable<List<Trainer>> getMainTrainers(@Path("region") String regionId,
                                              @Query("user") String userId);

    @GET("regions/{region}/trainers/{id}")
    Observable<Trainer> getTrainer(@Path("region") String regionId,
                                   @Path("id") String trainerId);

    @PATCH("regions/{region}/trainers/{id}")
    Observable<Trainer> updateTrainer(@Path("region") String regionId,
                                      @Path("id") String trainerId,
                                      @Body UpdateTrainerDto trainerDto);


    @DELETE("regions/{region}/trainers/{id}")
    Observable<Trainer> deleteTrainer(@Path("region") String regionId,
                                      @Path("id") String trainerId);

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
    @GET("regions/{region}/trainers/{trainer}/monsters")
    Observable<List<Monster>> getMonsters(@Path("region") String region, @Path("trainer") String trainerId);

    @GET("regions/{region}/trainers/{trainer}/monsters/{id}")
    Observable<Monster> getMonster(@Path("region") String region, @Path("id") String monsterId);

    //------------- Region Encounters -------------------------------
    @GET("regions/{region}/encounters")
    Observable<List<Encounter>> getEncounters();

    @GET("regions/{region}/encounters/{id}")
    Observable<Encounter> getEncounter(@Path("region") String region,
                                       @Path("id") String id);

    //------------- Encounter Opponents -------------------------------
    @GET("regions/{region}/trainers/{trainer}/opponents")
    Observable<List<Opponent>> getTrainerOpponents(@Path("region") String region,
                                                  @Path("trainer") String trainerId);

    @GET("regions/{region}/encounters/{encounter}/opponents")
    Observable<List<Opponent>> getEncounterOpponents(@Path("region") String region,
                                                      @Path("encounter") String encounterId);

    @GET("regions/{region}/encounters/{encounter}/opponents/{id}")
    Observable<Opponent> getEncounterOpponent(@Path("region") String region,
                                               @Path("encounter") String encounterId,
                                               @Path("id") String id);

    @PATCH("regions/{region}/encounters/{encounter}/opponents/{id}") //Make a move or switch monsters
    Observable<Opponent> makeMove(@Path("region") String region,
                                   @Path("encounter") String encounterId,
                                   @Path("id") String id,
                                   @Body UpdateOpponentDto opponentDto);

    @DELETE("regions/{region}/encounters/{encounter}/opponents/{id}") // Flee from a wild encounter
    Observable<Opponent> fleeEncounter(@Path("region") String region,
                                        @Path("encounter") String encounterId,
                                        @Path("id") String id);
}
