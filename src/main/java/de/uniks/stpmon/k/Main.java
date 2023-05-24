package de.uniks.stpmon.k;

import javafx.application.Application;

public class Main {
    public static final String API_DOMAIN = "stpmon.uniks.de";
    public static final String API_URL = "https://" + API_DOMAIN + "/api/v2";
    public static final String WS_URL = "wss://stpmon.uniks.de/ws/v2";

    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}
