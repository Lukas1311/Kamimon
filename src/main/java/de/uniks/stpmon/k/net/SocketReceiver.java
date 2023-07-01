package de.uniks.stpmon.k.net;

import java.util.function.Consumer;

public interface SocketReceiver extends ClientEndpoint {

    void addMessageHandler(Consumer<String> msgHandler);

    void removeMessageHandler(Consumer<String> msgHandler);

}
