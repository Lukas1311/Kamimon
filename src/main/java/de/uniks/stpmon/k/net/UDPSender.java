package de.uniks.stpmon.k.net;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UDPSender extends UDPReceiver implements SocketSender {

    protected final List<SocketReceiver> endpoints = Collections.synchronizedList(new ArrayList<>());

    @Override
    public SocketReceiver sendMessage(String message, boolean useEndpoint) {
        if (useEndpoint) {
            UDPReceiver endpoint = new UDPReceiver();
            endpoint.open();
            try {
                endpoint.socket.setSoTimeout(2000);
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
            endpoint.sendMessage(message);
            endpoint.startReceive();
            endpoints.add(endpoint);
            return endpoint;
        }
        sendMessage(message);
        return this;
    }

    @Override
    public void removeReceiver(SocketReceiver receiver) {
        endpoints.remove(receiver);
    }

    @Override
    public boolean canClose() {
        return endpoints.isEmpty();
    }

    @Override
    public void close() {
        super.close();
        for (SocketReceiver endpoint : endpoints) {
            endpoint.close();
        }
        endpoints.clear();
    }

}
