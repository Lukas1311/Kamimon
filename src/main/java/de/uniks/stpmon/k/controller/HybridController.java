package de.uniks.stpmon.k.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Stack;

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
    IngameController ingameController;

    private final Stack<Controller> tabStack = new Stack<>();

    @Inject
    public HybridController() {
    }

    @Override
    public void init() {
        sidebarController.get().init();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        pane.getChildren().add(sidebarController.get().render());
        stackPane.getChildren().add(lobbyController.get().render());
        return parent;
    }

    private void openMain(Controller controller) {
        ObservableList<Node> children = stackPane.getChildren();
        removeChildren(0);

        controller.init();
        tabStack.push(controller);
        children.add(controller.render());
    }


    private void openMain(Provider<? extends Controller> controller) {
        openMain(controller.get());
    }

    private void openSecondary(Controller controller) {
        ObservableList<Node> children = stackPane.getChildren();
        Controller last = removeChildren(1);

        if (last != controller) {
            controller.init();
            tabStack.push(controller);
            children.add(controller.render());
        }
    }

    private void openSecondary(Provider<? extends Controller> controller) {
        openSecondary(controller.get());
    }

    private Controller removeChildren(int startIndex) {
        Controller lastController = null;
        ObservableList<Node> children = stackPane.getChildren();
        for (int i = tabStack.size() - 1; i >= startIndex; i--) {
            lastController = tabStack.pop();
            lastController.destroy();
            children.remove(i);
        }
        return lastController;
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
