package de.uniks.stpmon.k.net;

import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.util.function.Consumer;


@javax.websocket.ClientEndpoint
public class WSEndpoint extends ClientEndpoint {
    private final URI endpointURI;

    Session userSession;

    public WSEndpoint(URI endpointURI) {
        this.endpointURI = endpointURI;
    }

    @Override
    public boolean isOpen() {
        return this.userSession != null && this.userSession.isOpen();
    }

    @Override
    public void open() {
        if (isOpen()) {
            return;
        }
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen(Session userSession) {
        this.userSession = userSession;
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        this.userSession = null;
    }

    @OnMessage
    public void onMessage(String message) {
        for (final Consumer<String> handler : this.messageHandlers) {
            handler.accept(message);
        }
    }

    @OnError
    public void onError(Throwable error) {
        error.printStackTrace();
    }

    @Override
    public void sendMessage(String message) {
        if (this.userSession == null) {
            return;
        }
        this.userSession.getAsyncRemote().sendText(message);
    }

    @Override
    public void close() {
        if (this.userSession == null) {
            return;
        }

        try {
            this.userSession.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
