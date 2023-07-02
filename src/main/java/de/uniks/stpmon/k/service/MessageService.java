package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.CreateMessageDto;
import de.uniks.stpmon.k.dto.UpdateMessageDto;
import de.uniks.stpmon.k.models.Message;
import de.uniks.stpmon.k.rest.MessageApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.ArrayList;

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

    private final MessageApiService messageApiService;

    @Inject
    public MessageService(MessageApiService messageApiService) {
        this.messageApiService = messageApiService;
    }

    // groups is taken when the user sends messages in a group or to another user (counts as group)

    /**
     * sends a message to the given id of a namespace
     *
     * @param body      is the content of the message
     * @param namespace is one of "global", "regions", "groups"
     * @param parent    is the id of the chosen namespace (e.g. id of a group where you want to send a message to)
     * @return the message sent
     */
    public Observable<Message> sendMessage(String body, MessageNamespace namespace, String parent) {
        return messageApiService.sendMessage(
                namespace.toString(),
                parent,
                new CreateMessageDto(body)
        );
    }

    /**
     * edit a message with a new message body, should be used when an 'edit flag' is clicked/pressed
     *
     * @param message   the current message you are editing
     * @param namespace is one of "global", "regions" or "groups"
     * @param parent    is the id of the chosen namespace (e.g. id of a group where you want to send a message to)
     * @param newBody   the new contents of your new message body
     * @return the updated new message
     */
    public Observable<Message> editMessage(Message message, MessageNamespace namespace, String parent, String newBody) {
        return messageApiService.editMessage(
                namespace.toString(),
                parent,
                message._id(),
                new UpdateMessageDto(newBody)
        );
    }

    /**
     * this method takes a message as param and deletes it returning the deleted message afterwards
     *
     * @param message   the message you want to delete, should be used when a 'delete flag' is clicked/pressed
     * @param namespace is one of "global", "regions", "groups"
     * @param parent    is the id of the chosen namespace (e.g. id of a group where you want to send a message to)
     * @return the deleted message
     */
    public Observable<Message> deleteMessage(Message message, MessageNamespace namespace, String parent) {
        return messageApiService.deleteMessage(
                namespace.toString(),
                parent,
                message._id()
        );
    }

    /**
     * this method returns the last 100 or fewer messages
     *
     * @param namespace is one of "global", "regions", "groups"
     * @param parent    is the id of the chosen namespace (e.g. id of a group where you want to send a message to)
     * @return the last 100 or fewer messages (100 is default value)
     */
    public Observable<ArrayList<Message>> getAllMessages(MessageNamespace namespace, String parent) {
        return messageApiService.getMessages(
                namespace.toString(),
                parent,
                null,
                null,
                null
        );
    }

    /**
     * returns equal or fewer messages by the given limit
     *
     * @param namespace is one of "global", "regions", "groups"
     * @param parent    is the id of the chosen namespace (e.g. id of a group where you want to send a message to)
     * @param limit     describes the maximum number of messages that can be received (range 1 - 100)
     * @return all messages within the limit
     */
    public Observable<ArrayList<Message>> getLastMessagesByLimit(MessageNamespace namespace, String parent, int limit) {
        return messageApiService.getMessages(
                namespace.toString(),
                parent,
                null,
                null,
                limit
        );
    }

}
