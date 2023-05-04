package de.uniks.stpmon.k.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class HybridController extends Controller {
    @FXML
    public HBox hBox;
    @FXML
    public Pane pane;
    @FXML
    public StackPane stackPane;
    @Inject
    Provider<SidebarController> sidebarController;
    @Inject
    FriendListController friendListController;
    @Inject
    Provider<LobbyController> lobbyController;
    @Inject
    PauseController pauseController;

    @Inject
    public HybridController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        pane.getChildren().add(sidebarController.get().render());
        stackPane.getChildren().add(lobbyController.get().render());
        return parent;
    }

    public void openSidebar(String string) {
        switch (string) {
            case "friends":
                if (stackPane.getChildren().size() > 1) {
                    stackPane.getChildren().remove(1);
                } else {
                    stackPane.getChildren().add(friendListController.render());
                }
                break;
            case "pause":
                boolean containsPause = stackPane.getChildren().stream()
                        .anyMatch(node -> node.getId() != null && node.getId().equals("pause"));
                if (containsPause) {
                    stackPane.getChildren().removeAll(stackPane.getChildren());
                    stackPane.getChildren().add(new IngameController().render());
                } else {
                    stackPane.getChildren().removeAll(stackPane.getChildren());
                    stackPane.getChildren().add(pauseController.render());
                }
                break;
            case "ingame":
                sidebarController.get().setPause(true);
                sidebarController.get().setLobby(true);
                stackPane.getChildren().removeAll(stackPane.getChildren());
                stackPane.getChildren().add(new IngameController().render());
                break;
            case "lobby":
                sidebarController.get().setPause(false);
                sidebarController.get().setLobby(false);
                stackPane.getChildren().removeAll(stackPane.getChildren());
                stackPane.getChildren().add(lobbyController.get().render());
            default:
                break;
        }

    }
}
