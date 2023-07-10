package de.uniks.stpmon.k.rest;

import de.uniks.stpmon.k.dto.UpdateOpponentDto;
import de.uniks.stpmon.k.models.Encounter;
import de.uniks.stpmon.k.models.Opponent;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

@SuppressWarnings("unused")
public interface EncounterApiService {

    //------------- Region Encounters -------------------------------
    @GET("regions/{region}/encounters")
    Observable<List<Encounter>> getEncounters(@Path("region") String regionId);

    @GET("regions/{region}/encounters/{id}")
    Observable<Encounter> getEncounter(@Path("region") String regionId,
                                       @Path("id") String encounterId);

    //------------- Encounter Opponents -------------------------------
    @GET("regions/{region}/trainers/{trainer}/opponents")
    Observable<List<Opponent>> getTrainerOpponents(@Path("region") String regionId,
                                                   @Path("trainer") String trainerId);

    @GET("regions/{region}/encounters/{encounter}/opponents")
    Observable<List<Opponent>> getEncounterOpponents(@Path("region") String regionId,
                                                     @Path("encounter") String encounterId);

    @GET("regions/{region}/encounters/{encounter}/opponents/{id}")
    Observable<Opponent> getEncounterOpponent(@Path("region") String regionId,
                                              @Path("encounter") String encounterId,
                                              @Path("id") String opponentId);

    @PATCH("regions/{region}/encounters/{encounter}/opponents/{id}")
        //Make a move or switch monsters
    Observable<Opponent> makeMove(@Path("region") String regionId,
                                  @Path("encounter") String encounterId,
                                  @Path("id") String opponentId,
                                  @Body UpdateOpponentDto opponentDto);

    @DELETE("regions/{region}/encounters/{encounter}/opponents/{id}")
        // Flee from a wild encounter
    Observable<Opponent> fleeEncounter(@Path("region") String regionId,
                                       @Path("encounter") String encounterId,
                                       @Path("id") String opponentId);

}
