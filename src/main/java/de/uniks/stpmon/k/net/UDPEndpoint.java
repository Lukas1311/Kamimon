package de.uniks.stpmon.k.net;

import de.uniks.stpmon.k.Main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.function.Consumer;

public class UDPEndpoint extends ClientEndpoint {
    private byte[] buf = new byte[508];
    private DatagramSocket receiveSocket;
    private DatagramSocket sendSocket;
    private Thread thread;

    @Override
    public void open() {
        if (isOpen()) {
            return;
        }
        try {
            receiveSocket = new DatagramSocket(Main.UDP_PORT, InetAddress.getByName(Main.UDP_URL));
            sendSocket = new DatagramSocket(Main.UDP_PORT, InetAddress.getByName(Main.UDP_URL));
            startReceive();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessage(String message) {
        if (this.receiveSocket == null) {
            return;
        }
        try {
            buf = message.getBytes();

            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            sendSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startReceive() {
        thread = new Thread(() -> {
            while (!receiveSocket.isClosed()) {
                try {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    receiveSocket.receive(packet);
                    String received = new String(packet.getData(), 0, packet.getLength());
                    for (Consumer<String> handler : messageHandlers) {
                        handler.accept(received);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @Override
    public boolean isOpen() {
        return receiveSocket != null && receiveSocket.isConnected() && receiveSocket.isConnected();
    }

    @Override
    public void close() {
        if (receiveSocket == null) {
            return;
        }
        receiveSocket.close();
        sendSocket.close();
        thread.interrupt();
    }
}
