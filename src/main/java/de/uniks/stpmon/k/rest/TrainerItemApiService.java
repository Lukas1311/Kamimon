package de.uniks.stpmon.k.rest;

import de.uniks.stpmon.k.dto.UpdateItemDto;
import de.uniks.stpmon.k.models.Item;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

@SuppressWarnings("unused")
public interface TrainerItemApiService {


    @POST("regions/{regionId}/trainers/{trainerId}/items")
    Observable<Item> updateItem(@Path("regionId") String regionId,
                                @Path("trainerId") String trainerId,
                                @Query("action") String action,
                                @Body UpdateItemDto dto);

    @GET("regions/{regionId}/trainers/{trainerId}/items")
    Observable<List<Item>> getItems(@Path("regionId") String regionId,
                                    @Path("trainerId") String trainerId);

    @GET("regions/{regionId}/trainers/{trainerId}/items")
    Observable<List<Item>> getItems(@Path("regionId") String regionId,
                                    @Path("trainerId") String trainerId,
                                    @Query("types") List<String> types);


    @GET("regions/{regionId}/trainers/{trainerId}/items/{itemId}")
    Observable<Item> getItem(@Path("regionId") String regionId,
                             @Path("trainerId") String trainerId,
                             @Path("itemId") String itemId);


}
