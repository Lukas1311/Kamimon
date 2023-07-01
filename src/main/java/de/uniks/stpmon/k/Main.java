package de.uniks.stpmon.k;

import javafx.application.Application;

public class Main {

    public static final String API_DOMAIN = "stpmon.uniks.de";
    public static final String API_URL = "https://" + API_DOMAIN + "/api/v3";
    public static final String WS_URL = "wss://stpmon.uniks.de/ws/v3";
    public static final String UDP_URL = "stpmon.uniks.de";
    public static final int UDP_PORT = 30013;

    public static void main(String[] args) {
        Application.launch(App.class, args);
    }

}
