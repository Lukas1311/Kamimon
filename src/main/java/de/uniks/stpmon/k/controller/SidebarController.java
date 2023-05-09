package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.service.AuthenticationService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
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
    public Button pause;
    @FXML
    public GridPane grid;
    @Inject
    @Singleton
    HybridController hybridController;

    @Inject
    AuthenticationService authService;
    @Inject
    Provider<LoginController> loginControllerProvider;

    @Inject
    public SidebarController() {
    }

    public Parent render() {
        final Parent parent = super.render();
        grid.prefHeightProperty().bind(app.getStage().heightProperty().subtract(35));
        pause.setVisible(false);
        home.setVisible(false);
        logoutButton.setOnAction(e -> logout());

        return parent;
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

    public void setLobby(boolean b) {
        home.setVisible(b);
    }

    public void openChat() {
        hybridController.openSidebar("chat");
    }

    public void openFriends() {

        hybridController.openSidebar("friends");
    }

    public void backtoLobby() {
        hybridController.openSidebar("lobby");
    }

    public void toPause() {
        hybridController.openSidebar("pause");
    }

    public void setPause(boolean b) {
        pause.setVisible(b);
    }


}
