package de.uniks.stpmon.k.service.dummies;

import de.uniks.stpmon.k.dto.CreateMessageDto;
import de.uniks.stpmon.k.dto.UpdateMessageDto;
import de.uniks.stpmon.k.models.Event;
import de.uniks.stpmon.k.models.Message;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.rest.MessageApiService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.mockito.Mockito;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class MessageApiDummy implements MessageApiService {

    //this ArrayList contains all messages
    //each entry is an array ob size = 3: [<namespace>, <parent>, <message>]
    final ArrayList<Object[]> messageEntities = new ArrayList<>();
    final String senderId = "0";
    final Subject<Event<Message>> events = PublishSubject.create();
    @Inject
    EventListener eventListener;
    String groupId = "0";

    @Inject
    public MessageApiDummy() {
    }

    /***
     * Initializes the mocking of the event listener for the events of the given group.
     *
     * @param groupId ID of the group to mock the events for.
     */
    public void mockEvents(String groupId) {
        this.groupId = groupId;

        Mockito.when(eventListener.listen(Socket.WS, "groups.%s.messages.*.*".formatted(groupId), Message.class))
                .thenReturn(events);
    }

    /**
     * Adds messagesEntities to the ArrayList
     *
     * @param namespace: global, regions or groups
     * @param parent:    id of receiver
     * @param createdAt: date of sending
     * @param updatedAt: date of last edit
     * @param body:      content of message
     */
    private Message addMessageEntity(String namespace, String parent,
                                     String createdAt, String updatedAt,
                                     String body) {
        Message message = new Message(
                createdAt,
                updatedAt,
                String.valueOf(messageEntities.size()),
                senderId,
                body
        );
        Object[] entity = {namespace, parent, message};
        messageEntities.add(entity);
        return message;
    }

    /**
     * Sends a message with senderId = 0
     *
     * @param namespace: global, regions, groups
     * @param parent:    id of receiving instance
     * @param msg:       dto with body
     * @return the Message that was sent
     */
    @Override
    public Observable<Message> sendMessage(String namespace, String parent, CreateMessageDto msg) {
        Message message = addMessageEntity(namespace,
                parent,
                "2023-01-01T00:00:00.000Z",
                "2023-01-01T00:00:00.000Z",
                msg.body());
        events.onNext(new Event<>("groups.%s.messages.%s.created".formatted(groupId, message._id()), message));
        return Observable.just(message);
    }

    /**
     * Filter messages
     * Note: if you don't want to add messages manually, you can call initDummyMessages()
     *
     * @param namespace:
     * @param parent:
     * @param dateTimeAfter:  can be null
     * @param dateTimeBefore: can be null
     * @param limit:          if null, limit = 100
     * @return filtered Messages
     */
    @Override
    public Observable<ArrayList<Message>> getMessages(String namespace, String parent,
                                                      String dateTimeAfter, String dateTimeBefore,
                                                      Integer limit) {
        List<Object[]> filteredList = messageEntities
                .stream()
                .filter(entity -> entity[0].equals(namespace) && entity[1].equals(parent))
                .toList();


        if (dateTimeAfter != null) {
            final Timestamp after = Timestamp.valueOf(dateTimeAfter);
            filteredList = filteredList
                    .stream()
                    .filter(e -> {
                        Message m = (Message) e[2];
                        return Timestamp.valueOf(m.createdAt()).after(after);
                    })
                    .toList();
        }
        if (dateTimeBefore != null) {
            final Timestamp before = Timestamp.valueOf(dateTimeBefore);
            filteredList = filteredList
                    .stream()
                    .filter(e -> {
                        Message m = (Message) e[2];
                        return Timestamp.valueOf(m.createdAt()).before(before);
                    })
                    .toList();
        }
        if (limit == null) {
            if (filteredList.size() > 100) {
                filteredList = filteredList.subList(filteredList.size() - 100, filteredList.size());
            }
        }

        ArrayList<Message> messages = new ArrayList<>();
        for (Object[] o : filteredList) {
            messages.add((Message) o[2]);
        }
        return Observable.just(messages);
    }

    private Observable<Message> getMessage(String namespace, String parent, String id) {
        Optional<Object[]> e = messageEntities
                .stream()
                .filter(entity -> entity[0].equals(namespace)
                        && entity[1].equals(parent)
                        && ((Message) entity[2])._id().equals(id))
                .findFirst();

        //returns the found message or (if no message is found) an error
        return e.map(objects -> Observable.just((Message) objects[2])).orElseGet(()
                -> Observable.error(new Throwable("404 Not found")));
    }

    @Override
    public Observable<Message> editMessage(String namespace, String parent, String id, UpdateMessageDto msg) {
        Optional<Object[]> e = messageEntities
                .stream()
                .filter(entity -> entity[0].equals(namespace)
                        && entity[1].equals(parent)
                        && ((Message) entity[2])._id().equals(id))
                .findFirst();

        if (e.isPresent()) {
            messageEntities.remove(e.get());
            Message message = getMessage(namespace, parent, id).blockingFirst();
            Instant timestamp = Instant.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            String formattedTimestamp = formatter.format(timestamp) + "Z";
            Message editedMessage = addMessageEntity(namespace, parent, message.createdAt(), formattedTimestamp, msg.body());
            events.onNext(new Event<>("groups.%s.messages.%s.updated".formatted(groupId, message._id()), editedMessage));
            return Observable.just(editedMessage);
        }
        return Observable.error(new Throwable("404 Not found"));

    }

    @Override
    public Observable<Message> deleteMessage(String namespace, String parent, String id) {
        Optional<Object[]> e = messageEntities
                .stream()
                .filter(entity -> entity[0].equals(namespace)
                        && entity[1].equals(parent)
                        && ((Message) entity[2])._id().equals(id))
                .findFirst();
        if (e.isPresent()) {
            messageEntities.remove(e.get());
            Message message = (Message) e.get()[2];
            events.onNext(new Event<>("groups.%s.messages.%s.updated".formatted(groupId, message._id()), message));
            return Observable.just(message);
        }
        return Observable.error(new Throwable("404 Not found"));

    }

}
