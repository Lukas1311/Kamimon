package de.uniks.stpmon.k;

import javafx.application.Application;

public class Main {
    public static final String API_DOMAIN = "stpmon.uniks.de";
    public static final String API_URL = "https://" + API_DOMAIN + "/api/v1";
    public static void main(String[] args){
        Application.launch(App.class, args);
    }
}
