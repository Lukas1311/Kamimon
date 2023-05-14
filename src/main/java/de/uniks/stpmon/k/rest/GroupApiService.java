package de.uniks.stpmon.k.rest;

import de.uniks.stpmon.k.dto.CreateGroupDto;
import de.uniks.stpmon.k.dto.Group;
import de.uniks.stpmon.k.dto.UpdateGroupDto;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PATCH;
import retrofit2.http.DELETE;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Body;

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

    @GET("groups/{id}")
    Observable<Group> getGroup(
        @Path("id") String id
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
