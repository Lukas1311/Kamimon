package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.service.AuthenticationService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;


public class SidebarController extends Controller {

    @FXML
    public Button chatButton;
    @FXML
    public Button friends;
    @FXML
    public Button home;
    @FXML
    public Button logoutButton;
    @FXML
    public VBox vBox;
    @Inject
    @Singleton
    HybridController hybridController;

    @Inject
    AuthenticationService authService;
    @Inject
    Provider<LoginController> loginControllerProvider;
    @Inject
    Provider<ChatController> chatControllerProvider;
    
    @Inject
    public SidebarController() {
    }

    public Parent render() {
        final Parent parent = super.render();

        chatButton.setOnAction(e -> openChat());
        logoutButton.setOnAction(e -> logout());

        return parent;
    }

    public void openChat() {
        app.show(chatControllerProvider.get());
    }

    public void openFriends() {
        hybridController.openSidebar("friends");
    }

    public void backtoLobby() {
    }

    public void logout() {
        disposables.add(authService
            .logout()
            .observeOn(FX_SCHEDULER)
            .subscribe(res -> {
                System.out.println(res);
                app.show(loginControllerProvider.get());
            })
        );
    }
}
