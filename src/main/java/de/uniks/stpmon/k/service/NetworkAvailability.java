package de.uniks.stpmon.k.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.uniks.stpmon.k.Main;

@Singleton
public class NetworkAvailability {

    @Inject
    public NetworkAvailability() {
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName(Main.API_DOMAIN);
            return address.isReachable(10000);
        // UnknownHostException is IOException
        } catch (IOException e) {
            return false;
        }
    }
}
