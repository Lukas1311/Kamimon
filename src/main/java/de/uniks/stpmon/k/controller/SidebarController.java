package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.service.AuthenticationService;
import de.uniks.stpmon.k.dto.Group;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

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
    Provider<ChatController> chatControlleProvider;

    @Inject
    public SidebarController() {
    }

    public Parent render() {
        final Parent parent = super.render();
        grid.prefHeightProperty().bind(app.getStage().heightProperty().subtract(35));
        pause.setVisible(false);
        home.setVisible(false);
        logoutButton.setOnAction(e -> logout());
        chat.setOnAction(e -> openChat());
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
        // TODO: replace this with chat list view controller
        ChatController chat = chatControlleProvider.get();
        Group dummyGroup = new Group(null, null, "6457a3ce4d233ed4626d20c0", "test", null);
        chat.setGroup(dummyGroup);
        app.show(chat);
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
