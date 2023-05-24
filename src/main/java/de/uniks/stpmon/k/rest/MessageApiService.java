package de.uniks.stpmon.k.rest;

import de.uniks.stpmon.k.dto.CreateMessageDto;
import de.uniks.stpmon.k.dto.UpdateMessageDto;
import de.uniks.stpmon.k.models.Message;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.ArrayList;


public interface MessageApiService {
    @POST("{namespace}/{parent}/messages")
    Observable<Message> sendMessage(
            @Path("namespace") String namespace,
            @Path("parent") String parent,
            @Body CreateMessageDto msg
    );

    // Lists the last (limit) messages sent before (createdBefore).
    @GET("{namespace}/{parent}/messages")
    Observable<ArrayList<Message>> getMessages(
            @Path("namespace") String namespace,
            @Path("parent") String parent,
            @Query("createdAfter") String dateTimeAfter,
            @Query("createdBefore") String dateTimeBefore,
            @Query("limit") Integer limit
    );

    @GET("{namespace}/{parent}/messages/{id}")
    Observable<Message> getMessage(
            @Path("namespace") String namespace,
            @Path("parent") String parent,
            @Path("id") String id
    );

    @PATCH("{namespace}/{parent}/messages/{id}")
    Observable<Message> editMessage(
            @Path("namespace") String namespace,
            @Path("parent") String parent,
            @Path("id") String id,
            @Body UpdateMessageDto msg
    );

    @DELETE("{namespace}/{parent}/messages/{id}")
    Observable<Message> deleteMessage(
            @Path("namespace") String namespace,
            @Path("parent") String parent,
            @Path("id") String id
    );
}
