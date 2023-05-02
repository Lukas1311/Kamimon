package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.service.AuthenticationService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;

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

    @Inject
    AuthenticationService authService;
    @Inject
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
        disposables.add(authService
            .logout()
            .observeOn(FX_SCHEDULER)
            .subscribe(res -> {
                System.out.println(res);
                app.show(new LoginController());
            })
        );
    }
}
