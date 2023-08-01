package de.uniks.stpmon.k.controller.sidebar;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.LoginController;
import de.uniks.stpmon.k.service.AuthenticationService;
import de.uniks.stpmon.k.service.InputHandler;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
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
    public Button logoutButton;
    @FXML
    public Button pause;
    @FXML
    public GridPane grid;
    @FXML
    public Button settings;
    @Inject
    @Singleton
    HybridController hybridController;
    @Inject
    AuthenticationService authService;
    @Inject
    Provider<LoginController> loginControllerProvider;
    @Inject
    TrainerStorage trainerStorage;
    @Inject
    RegionStorage regionStorage;
    @Inject
    InputHandler inputHandler;
    boolean ingame = false;

    @Inject
    public SidebarController() {
    }

    @Override
    public void init() {
        super.init();
        onDestroy(inputHandler.addPressedKeyHandler(event -> {
            switch (event.getCode()) {
                case C -> {
                    openChat();
                    event.consume();
                }
                case F -> {
                    openFriends();
                    event.consume();
                }
                case P -> {
                    toPause();
                    event.consume();
                }
                case NUMBER_SIGN, O -> {
                    openSettings();
                    event.consume();
                }
                case ESCAPE -> {
                    if (event.isShiftDown()) {
                        if (ingame) {
                            trainerStorage.setTrainer(null);
                            backtoLobby();
                        } else {
                            logout();
                        }
                    }
                    event.consume();
                }
                default -> {
                }
            }
        }));
    }

    public Parent render() {
        final Parent parent = super.render();
        grid.prefHeightProperty().bind(app.getStage().heightProperty().subtract(35));
        pause.setVisible(false);
        settings.setVisible(false);
        logoutButton.setOnAction(event -> {
            if (ingame) {
                trainerStorage.setTrainer(null);
                regionStorage.setRegion(null);
                backtoLobby();
            } else {
                logout();
            }
        });
        return parent;
    }

    public void setIngame(boolean ingame) {
        this.ingame = ingame;
    }

    public void logout() {
        disposables.add(authService
                .logout()
                .observeOn(FX_SCHEDULER)
                .subscribe(res -> app.show(loginControllerProvider.get()))
        );
    }

    public void backtoLobby() {
        hybridController.openMain(LOBBY);
    }

    public void openChat() {
        hybridController.forceTab(CHAT_LIST);
    }

    public void openFriends() {
        hybridController.forceTab(FRIEND_LIST);
    }

    public void toPause() {
        hybridController.openMain(PAUSE);
    }

    public void setPause(boolean b) {
        pause.setVisible(b);
    }

    public void setSettings(boolean b) {
        settings.setVisible(b);
    }

    public void updatePauseButton(boolean isPause) {
        pause.setTooltip(new Tooltip(translateString(isPause ? ("resume.game") : ("pause.game"))));
    }

    public void updateLogoutButton(boolean leaveRegion) {
        logoutButton.setTooltip(new Tooltip(translateString(leaveRegion ? ("leaveRegion") : ("logout"))));
    }

    public void openSettings() {
        hybridController.forceTab(SidebarTab.SETTINGS);
    }

}
