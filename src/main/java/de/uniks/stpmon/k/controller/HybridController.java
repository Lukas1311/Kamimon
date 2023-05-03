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
        if ("friends".equals(string)) {
            if(stackPane.getChildren().size() > 1) {
                stackPane.getChildren().remove(1);
            }
            else {
                stackPane.getChildren().add(friendListController.render());
            }
        }
    }

    public void openStackpane(String string) {
        if ("ingame".equals(string)) {
            stackPane.getChildren().remove(0);
            stackPane.getChildren().add(new IngameController().render());
        }
    }
}
