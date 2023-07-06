package de.uniks.stpmon.k.rest;

import de.uniks.stpmon.k.dto.CreateMessageDto;
import de.uniks.stpmon.k.dto.UpdateMessageDto;
import de.uniks.stpmon.k.models.Message;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

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
