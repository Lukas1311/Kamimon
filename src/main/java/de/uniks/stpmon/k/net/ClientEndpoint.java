package de.uniks.stpmon.k.net;

public interface ClientEndpoint {

    boolean isOpen();

    void open();

    void close();

}
