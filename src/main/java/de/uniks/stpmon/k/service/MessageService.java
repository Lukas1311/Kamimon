package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.Message;

import java.util.List;

import javax.inject.Inject;
import javax.xml.stream.events.Namespace;

import de.uniks.stpmon.k.dto.CreateMessageDto;
import de.uniks.stpmon.k.dto.UpdateMessageDto;
import de.uniks.stpmon.k.rest.MessageApiService;
import io.reactivex.rxjava3.core.Observable;

public class MessageService {

    private final MessageApiService messageApiService;
    // 'namespace' is one of "groups", "regions" or "global"
    // 'parent' is some sort of top-level message where all other messages are children of, sort of like a 'forum thread'
    // I guess you could make this dependant on the first message sent in a new chat
    
    @Inject
    public MessageService(MessageApiService messageApiService) {
        this.messageApiService = messageApiService;
    }

    public Observable<Message> sendMessage(String body, String namespace, String parent) {
        // TODO: e.g. for regions this dependant on the regionPicker option the user choses in ChatController
        return messageApiService.sendMessage(
            namespace,
            parent, // TODO: find a fitting string schema
            new CreateMessageDto(body)
        );
    }

    public Observable<Message> editMessage(Message message, String namespace, String parent) {
        String newBody = ""; // TODO: where to get body
        return messageApiService.editMessage(
            namespace,
            parent,
            message._id(),
            new UpdateMessageDto(newBody)
        );
    }

    public Observable<Message> deleteMessage(Message message, String namespace, String parent) {
        return messageApiService.deleteMessage(
            namespace,
            parent,
            message._id()
        );
    }
}
