package de.uniks.stpmon.k.net;

public interface SocketSender extends ClientEndpoint {

    boolean canClose();

    SocketReceiver sendMessage(String message, boolean useEndpoint);

    void removeReceiver(SocketReceiver receiver);

}
