package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.CreateMessageDto;
import de.uniks.stpmon.k.dto.Message;
import de.uniks.stpmon.k.dto.UpdateMessageDto;
import de.uniks.stpmon.k.rest.MessageApiService;
import de.uniks.stpmon.k.service.MessageService.InvalidNamespaceException;
import de.uniks.stpmon.k.service.MessageService.MessageNamespace;

import io.reactivex.rxjava3.core.Observable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    MessageApiService msgApiService;

    @InjectMocks
    MessageService msgService;

    @Test
    void testSendMessage() {
        // define mocks:
        Mockito.when(msgApiService
            .sendMessage(any(), any(), any())).thenReturn(Observable.just(
                new Message("11","22","id1","id2","bdy")
            )
        );

        // action:
        final Message msg = msgService.sendMessage("hi", MessageNamespace.GROUPS.toString(), "p")
            .blockingFirst();

        // check values:
        assertEquals("11",msg.createdAt());
        assertEquals("22",msg.updatedAt());
        assertEquals("id1", msg._id());
        assertEquals("id2", msg.sender());
        assertEquals("bdy", msg.body());

        // check mocks:
        verify(msgApiService).sendMessage("groups", "p", new CreateMessageDto("hi"));
    }

    @Test
    void testDeleteMessage() {
        // define mocks:
        Mockito.when(msgApiService
            .deleteMessage(any(), any(), any())).thenReturn(Observable.just(
                new Message("11","22","id1","id2","bdy")
            )
        );

        // action:
        final Message msg = msgService.deleteMessage(
            new Message("123", "321", "id", "s", "b"), MessageNamespace.GLOBAL.toString(), "p")
                .blockingFirst();

        // check values:
        assertEquals("11",msg.createdAt());
        assertEquals("22",msg.updatedAt());
        assertEquals("id1", msg._id());
        assertEquals("id2", msg.sender());
        assertEquals("bdy", msg.body());

        // check mocks:
        verify(msgApiService).deleteMessage("global", "p", "id");
    }

    @Test
    void testEditMessage() {
        // define mocks:
        Mockito.when(msgApiService
            .editMessage(any(), any(), any(), any())).thenReturn(Observable.just(
                new Message("11","22","id1","id2","bdy")
            )
        );

        // action:
        final Message msg = msgService.editMessage(
            new Message("123", "321", "id", "s", "old bdy"),
            MessageNamespace.REGIONS.toString(), "p", "new bdy"
        ).blockingFirst();

        // check values:
        assertEquals("11",msg.createdAt());
        assertEquals("22",msg.updatedAt());
        assertEquals("id1", msg._id());
        assertEquals("id2", msg.sender());
        assertEquals("bdy", msg.body());

        // check mocks:
        verify(msgApiService).editMessage("regions", "p", "id", new UpdateMessageDto("new bdy"));
    }

    @Test
    void testGetAllMessages() {
        // define mocks:
        Mockito.when(msgApiService
            .getMessages(any(), any(), any(), any(), any())).thenReturn(Observable.just(new ArrayList<>(List.of(
                new Message("11","22","id1","id2","bdy")
            ))
        ));

        // action:
        final ArrayList<Message> msgs = msgService.getAllMessages(MessageNamespace.GROUPS.toString(), "p").blockingFirst();

        // check values of the last message in returned messages:
        assertEquals("11",msgs.get(msgs.size() - 1).createdAt());
        assertEquals("22",msgs.get(msgs.size() - 1).updatedAt());
        assertEquals("id1", msgs.get(msgs.size() - 1)._id());
        assertEquals("id2", msgs.get(msgs.size() - 1).sender());
        assertEquals("bdy", msgs.get(msgs.size() - 1).body());

        // check mocks:
        verify(msgApiService).getMessages("groups", "p", null, null, null);
    }

    @Test
    void TestGetLastMessagesByLimit() {
        // preparation:
        Message msgTemplate = new Message("0","0","i","s","b");
        ArrayList<Message> tenMessages = new ArrayList<>(Collections.nCopies(9, msgTemplate));
        tenMessages.add(new Message("11","22","id1","id2","bdy"));

        // define mocks:
        Mockito.when(msgApiService
            .getMessages(any(), any(), any(), any(), any())).thenReturn(Observable.just(tenMessages));

        // action:
        final ArrayList<Message> msgs = msgService.getLastMessagesByLimit(MessageNamespace.GROUPS.toString(), "p",10).blockingFirst();

        // check values of the last message in returned messages:
        assertEquals("11",msgs.get(msgs.size() - 1).createdAt());
        assertEquals("22",msgs.get(msgs.size() - 1).updatedAt());
        assertEquals("id1", msgs.get(msgs.size() - 1)._id());
        assertEquals("id2", msgs.get(msgs.size() - 1).sender());
        assertEquals("bdy", msgs.get(msgs.size() - 1).body());
        assertEquals(10, msgs.size());

        // check mocks:
        verify(msgApiService).getMessages("groups", "p", null, null, 10);
    }

    @Test
    void TestMessageActionsWithInvalidNamespace() {   
        // preparation:
        String invalidNamespace = "invalid";
        Message dummyMsg = new Message("a", "b", "c", "d", "e");
        // action:
        final Observable<Message> result1 = msgService.sendMessage("hi", invalidNamespace, "p");
        final Observable<Message> result2 = msgService.deleteMessage(dummyMsg, invalidNamespace, "p");
        final Observable<Message> result3 = msgService.editMessage(dummyMsg, invalidNamespace, "p", "hi");
        final Observable<ArrayList<Message>> result4 = msgService.getAllMessages(invalidNamespace, "p");
        final Observable<ArrayList<Message>> result5 = msgService.getLastMessagesByLimit(invalidNamespace, "p", 5);

        // check the observables (must be exception)
        result1.test().assertError(InvalidNamespaceException.class);
        result2.test().assertError(InvalidNamespaceException.class);
        result3.test().assertError(InvalidNamespaceException.class);
        result4.test().assertError(InvalidNamespaceException.class);
        result5.test().assertError(InvalidNamespaceException.class);
    }
}
