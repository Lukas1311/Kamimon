package de.uniks.stpmon.k.net;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;


@javax.websocket.ClientEndpoint
public class WSEndpoint implements SocketReceiver, SocketSender {

    private final URI endpointURI;
    protected final List<Consumer<String>> messageHandlers = Collections.synchronizedList(new CopyOnWriteArrayList<>());

    Session userSession;

    public WSEndpoint(URI endpointURI) {
        this.endpointURI = endpointURI;
    }

    @Override
    public void addMessageHandler(Consumer<String> msgHandler) {
        this.messageHandlers.add(msgHandler);
    }

    @Override
    public void removeMessageHandler(Consumer<String> msgHandler) {
        this.messageHandlers.remove(msgHandler);
    }

    @Override
    public boolean canClose() {
        return this.messageHandlers.isEmpty();
    }

    @Override
    public boolean isOpen() {
        return this.userSession != null && this.userSession.isOpen();
    }

    @Override
    @SuppressWarnings({"try"})
    public void open() {
        if (isOpen()) {
            return;
        }

        // Needed because intellij does not recognize its own errors
        //noinspection RedundantSuppression
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            //noinspection resource
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeReceiver(SocketReceiver receiver) {
    }

    @OnOpen
    @SuppressWarnings("unused")
    public void onOpen(Session userSession) {
        this.userSession = userSession;
    }

    @OnClose
    @SuppressWarnings("unused")
    public void onClose(Session userSession, CloseReason reason) {
        this.userSession = null;
    }

    @OnMessage
    @SuppressWarnings("unused")
    public void onMessage(String message) {
        for (final Consumer<String> handler : this.messageHandlers) {
            handler.accept(message);
        }
    }

    @OnError
    @SuppressWarnings("unused")
    public void onError(Throwable error) {
        //noinspection CallToPrintStackTrace
        error.printStackTrace();
    }

    @Override
    public WSEndpoint sendMessage(String message, boolean useEndpoint) {
        if (this.userSession == null) {
            return this;
        }
        this.userSession.getAsyncRemote().sendText(message);
        return this;
    }

    @Override
    public void close() {
        if (this.userSession == null) {
            return;
        }

        try {
            this.userSession.close();
        } catch (IOException ignored) {
        }
    }

}
