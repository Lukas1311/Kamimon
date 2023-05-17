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
        GLOBAL("global"),
        REGIONS("regions"),
        GROUPS("groups");

        private final String namespace;

        MessageNamespace(final String namespace) {
            this.namespace = namespace;
        }

        @Override
        public String toString() {
            return namespace;
        }
    }

    public class InvalidNamespaceException extends IllegalArgumentException {
        public InvalidNamespaceException(String namespace) {
            super("invalid namespace: '" + namespace + "'', must be one of " + MessageNamespace.values());
        }
    }

    private final MessageApiService messageApiService;
    // 'namespace' is one of "groups", "regions" or "global" dependant where you open the chat
    // 'parent' is the id of the group, or region, or global world
    
    @Inject
    public MessageService(MessageApiService messageApiService) {
        this.messageApiService = messageApiService;
    }

    private boolean isValidNamespace(String namespace) {
        try {
            MessageNamespace.valueOf(namespace.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // groups is taken when the user sends messages in a group or to another user (counts as group)
    /**
     * sends a message to the given id of a namespace
     * @param body is the content of the message
     * @param namespace is one of "global", "regions", "groups"
     * @param parent is the id of the chosen namespace (e.g. id of a group where you want to send a message to)
     * @return the message sent
     */
    public Observable<Message> sendMessage(String body, String namespace, String parent) {
        if (isValidNamespace(namespace)) {
            return messageApiService.sendMessage(
                namespace,
                parent,
                new CreateMessageDto(body)
            );
        } else {
            return Observable.error(new InvalidNamespaceException(namespace));
        }
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
        if (isValidNamespace(namespace)) {
            return messageApiService.editMessage(
                namespace,
                parent,
                message._id(),
                new UpdateMessageDto(newBody)
            );
        } else {
            return Observable.error(new InvalidNamespaceException(namespace));
        }
    }

    /**
     * this method takes a message as param and deletes it returning the deleted message afterwards
     * @param message the message you want to delete, should be used when a 'delete flag' is clicked/pressed
     * @param namespace is one of "global", "regions", "groups"
     * @param parent is the id of the chosen namespace (e.g. id of a group where you want to send a message to)
     * @return the deleted message
     */
    public Observable<Message> deleteMessage(Message message, String namespace, String parent) {
        if (isValidNamespace(namespace)) {
            return messageApiService.deleteMessage(
                namespace,
                parent,
                message._id()
            );
        } else {
            return Observable.error(new InvalidNamespaceException(namespace));
        }
    }

    /**
     * this method returns the last 100 or less messages
     * @param namespace is one of "global", "regions", "groups"
     * @param parent is the id of the chosen namespace (e.g. id of a group where you want to send a message to)
     * @return the last 100 or less messages (100 is default value)
     */
    public Observable<ArrayList<Message>> getAllMessages(String namespace, String parent) {
        if (isValidNamespace(namespace)) {
            return messageApiService.getMessages(
                namespace,
                parent,
                null,
                null,
                null
            );
        } else {
            return Observable.error(new InvalidNamespaceException(namespace));
        }
    }

    /**
     * returns equal or less messages by the given limit 
     * @param namespace is one of "global", "regions", "groups"
     * @param parent is the id of the chosen namespace (e.g. id of a group where you want to send a message to)
     * @param limit describes the maximum number of messages that can be received (range 1 - 100)
     * @return all messages within the limit
     */
    public Observable<ArrayList<Message>> getLastMessagesByLimit(String namespace, String parent, int limit) {
        if (isValidNamespace(namespace)) {
            return messageApiService.getMessages(
                namespace,
                parent,
                null,
                null,
                limit
            );
        } else {
            return Observable.error(new InvalidNamespaceException(namespace));
        }
    }
}
