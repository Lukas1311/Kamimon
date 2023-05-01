package de.uniks.stpmon.k.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class SidebarController extends Controller {

    @FXML
    public Button chat;
    @FXML
    public Button friends;
    @FXML
    public Button home;
    @FXML
    public Button logoutButton;
    @FXML
    public VBox vBox;

    public SidebarController() {
    }

    public Parent render() {
        final Parent parent = super.render();

        logoutButton.setOnAction(e -> logout());

        return parent;
    }

    public void openChat() {
    }

    public void openFriends() {
    }

    public void backtoLobby() {
    }

    public void logout() {
        
    }
}
