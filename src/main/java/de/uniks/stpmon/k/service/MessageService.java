package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.Message;

import java.util.ArrayList;

import javax.inject.Inject;

import de.uniks.stpmon.k.dto.CreateMessageDto;
import de.uniks.stpmon.k.dto.UpdateMessageDto;
import de.uniks.stpmon.k.rest.MessageApiService;
import io.reactivex.rxjava3.core.Observable;

public class MessageService {

    public enum MessageNamespace {
        GLOBAL,
        REGIONS,
        GROUPS;

        public String toLower() {
            return name().toLowerCase();
        }
    }

    private final MessageApiService messageApiService;
    // 'namespace' is one of "groups", "regions" or "global" dependant where you open the chat
    // 'parent' is the id of the group, or region, or global world
    
    @Inject
    public MessageService(MessageApiService messageApiService) {
        this.messageApiService = messageApiService;
    }

    // TODO: namespace has to be picked dependant where the user is currently (here either "global" or "groups")
    // groups is taken when the user sends messages in a group or to another user (counts as group)
    /**
     * sends a message to the given id of a namespace
     * @param body is the content of the message
     * @param namespace is one of "global", "regions", "groups"
     * @param parent is the id of the chosen namespace (e.g. id of a group where you want to send a message to)
     * @return the message sent
     */
    public Observable<Message> sendMessage(String body, String namespace, String parent) {
        return messageApiService.sendMessage(
            namespace,
            parent,
            new CreateMessageDto(body)
        );
    }

    /**
     * edit a message with a new message body, should be used when an 'edit flag' is clicked/pressed
     * @param message the current message you are editing
     * @param namespace is one of "global", "regions" or "groups"
     * @param parent is the id of the chosen namespace (e.g. id of a group where you want to send a message to)
     * @param newBody the new contents of your new message body
     * @return the updated new message
     */
    public Observable<Message> editMessage(Message message, String namespace, String parent, String newBody) {
        return messageApiService.editMessage(
            namespace,
            parent,
            message._id(),
            new UpdateMessageDto(newBody)
        );
    }

    /**
     * delete a message
     * @param message the message you want to delete, should be used when a 'delete flag' is clicked/pressed
     * @param namespace is one of "global", "regions", "groups"
     * @param parent is the id of the chosen namespace (e.g. id of a group where you want to send a message to)
     * @return the deleted message
     */
    public Observable<Message> deleteMessage(Message message, String namespace, String parent) {
        return messageApiService.deleteMessage(
            namespace,
            parent,
            message._id()
        );
    }

    // TODO: not sure about the other methods yet
    public Observable<ArrayList<Message>> getAllMessages(String namespace, String parent) {
        return messageApiService.getMessages(
            namespace,
            parent,
            null,
            null,
            100
        );
    }
}
