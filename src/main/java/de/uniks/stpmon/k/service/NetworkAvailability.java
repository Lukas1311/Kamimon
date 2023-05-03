package de.uniks.stpmon.k.service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NetworkAvailability {

    @Inject
    public NetworkAvailability() {
    }

    public boolean isInternetAvailable() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                System.out.println(ni);
                if (!ni.isUp() || ni.isLoopback() | ni.isVirtual()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (!address.isLinkLocalAddress() && !address.isLoopbackAddress() && !address.isMulticastAddress()) {
                        System.out.println(address);
                        return true;
                    }
                }
            }
            return false;
        } catch (SocketException e) {
            return false;
        }
    }
}
