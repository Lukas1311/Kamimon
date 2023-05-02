package de.uniks.stpmon.k.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import javax.inject.Inject;

public class SidebarController extends Controller {

    @FXML
    public Button chat;
    @FXML
    public Button friends;
    @FXML
    public Button home;
    @FXML
    public VBox vBox;

    @Inject
    public SidebarController() {
    }

    public Parent render() {
        final Parent parent = super.render();
        return parent;
    }

    public void openChat() {
    }

    public void openFriends() {
    }

    public void backtoLobby() {
    }
}
