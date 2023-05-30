package de.uniks.stpmon.k.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public abstract class ClientEndpoint {
    protected final List<Consumer<String>> messageHandlers = Collections.synchronizedList(new ArrayList<>());

    public void addMessageHandler(Consumer<String> msgHandler) {
        this.messageHandlers.add(msgHandler);
    }

    public void removeMessageHandler(Consumer<String> msgHandler) {
        this.messageHandlers.remove(msgHandler);
    }

    public boolean hasMessageHandlers() {
        return !this.messageHandlers.isEmpty();
    }

    public abstract boolean isOpen();

    public abstract void open();

    public abstract void sendMessage(String message);

    public abstract void close();

}
