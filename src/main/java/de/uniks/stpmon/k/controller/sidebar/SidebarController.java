package de.uniks.stpmon.k.controller.sidebar;

import de.uniks.stpmon.k.controller.ChatController;
import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.LoginController;
import de.uniks.stpmon.k.service.AuthenticationService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import static de.uniks.stpmon.k.controller.sidebar.MainWindow.LOBBY;
import static de.uniks.stpmon.k.controller.sidebar.MainWindow.PAUSE;
import static de.uniks.stpmon.k.controller.sidebar.SidebarTab.CHAT_LIST;
import static de.uniks.stpmon.k.controller.sidebar.SidebarTab.FRIEND_LIST;

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
        hybridController.forceTab(CHAT_LIST);
    }

    public void openFriends() {
        hybridController.forceTab(FRIEND_LIST);
    }

    public void backtoLobby() {
        hybridController.openMain(LOBBY);
    }

    public void toPause() {
        hybridController.openMain(PAUSE);
    }

    public void setPause(boolean b) {
        pause.setVisible(b);
    }


}
