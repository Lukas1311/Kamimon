package de.uniks.stpmon.k.rest;

import de.uniks.stpmon.k.dto.CreateGroupDto;
import de.uniks.stpmon.k.dto.UpdateGroupDto;
import de.uniks.stpmon.k.models.Group;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.ArrayList;


public interface GroupApiService {

    @POST("groups")
    Observable<Group> createGroup(
            @Body CreateGroupDto group
    );

    @GET("groups")
    Observable<ArrayList<Group>> getGroups();

    @GET("groups")
    Observable<ArrayList<Group>> getGroups(
            @Query("members") String members
    );

    @PATCH("groups/{id}")
    Observable<Group> editGroup(
            @Path("id") String id,
            @Body UpdateGroupDto group
    );

    @DELETE("groups/{id}")
    Observable<Group> deleteGroup(
            @Path("id") String id
    );

}
