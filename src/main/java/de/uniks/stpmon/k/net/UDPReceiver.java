package de.uniks.stpmon.k.net;

import de.uniks.stpmon.k.Main;

import java.io.IOException;
import java.net.*;
import java.util.function.Consumer;

public class UDPReceiver implements SocketReceiver {

    protected DatagramSocket socket;
    private Thread thread;
    private Consumer<String> handler;
    private String subscribeMessage;

    @Override
    public void addMessageHandler(Consumer<String> msgHandler) {
        this.handler = msgHandler;
    }

    @Override
    public void removeMessageHandler(Consumer<String> msgHandler) {
        this.handler = null;
        close();
    }

    @Override
    public boolean isOpen() {
        return socket != null && !socket.isClosed();
    }

    public void sendMessage(String message) {
        if (subscribeMessage == null) {
            subscribeMessage = message;
        }
        try {
            byte[] bytes = message.getBytes();
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(Main.UDP_URL), Main.UDP_PORT);
            socket.connect(InetAddress.getByName(Main.UDP_URL), Main.UDP_PORT);
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void open() {
        if (isOpen()) {
            return;
        }
        try {
            socket = new DatagramSocket();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void startReceive() {
        final byte[] buf = new byte[508];
        thread = new Thread(() -> {
            boolean active = true;
            while (active && !socket.isClosed()) {
                try {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    if (handler == null) {
                        return;
                    }
                    String received = new String(packet.getData());
                    handler.accept(received);
                } catch (SocketTimeoutException ex) {
                    // resend subscribe message
                    sendMessage(subscribeMessage);
                } catch (SocketException e) {
                    // Close thread if socket is closed
                    active = false;
                } catch (IOException ignored) {
                }
            }
        });
        thread.start();
    }

    @Override
    public void close() {
        if (thread != null) {
            thread.interrupt();
        }
        if (socket == null) {
            return;
        }
        socket.close();
    }

}
