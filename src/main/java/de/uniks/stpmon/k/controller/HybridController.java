package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.Group;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.service.GroupService;
import de.uniks.stpmon.k.service.UserService;
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

import java.util.ArrayList;
import java.util.List;
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
    @Inject
    ChatListController chatListController;
    @Inject
    CreateChatController createChatController;
    @Inject
    Provider<ChatController> chatControllerProvider;
    @Inject
    UserService userService;
    @Inject
    GroupService groupService;

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

        // clean up controller
        removeChildren(0);
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        pane.getChildren().add(sidebarController.get().render());

        openSidebar("lobby");
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

        // Check if the controller is a new controller
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
            case "chatList":
                openSecondary(chatListController);
                break;
            case "createChat":
                openSecondary(createChatController);
                break;
            case "friends":
                openSecondary(friendListController);
                break;
            case "pause":
                boolean containsPause = stackPane.getChildren().stream()
                        .anyMatch(node -> node.getId() != null && node.getId().equals("pause"));
                if (containsPause) {
                    openMain(ingameController);
                } else {
                    openMain(pauseController);
                }
                break;
            case "ingame":
                sidebarController.get().setPause(true);
                sidebarController.get().setLobby(true);
                openMain(ingameController);
                break;
            case "lobby":
                sidebarController.get().setPause(false);
                sidebarController.get().setLobby(false);
                openMain(lobbyController);
            default:
                break;
        }
    }

    public void openChat(Group group) {
        ChatController chat = chatControllerProvider.get();
        chat.setGroup(group);
        openSecondary(chat);
    }

    public void openChat(ObservableList<User> friends, User friend) {
        // the user can only open the chat when the other user is a friend
        if (!friends.contains(friend)) {
            return;
        }
        System.out.println("name: " + friend.name() + ", id: " + friend._id());
        ChatController chat = chatControllerProvider.get();
        System.out.println("current chat: " + chat);
        User me = userService.getMe(); // is non api call
        ArrayList<String> privateChatMembers = new ArrayList<>(List.of(friend._id(), me._id()));
        // check if the friend and the user already have a group, if not create one
        disposables.add(
            groupService.getGroupsByMembers(privateChatMembers)
                .observeOn(FX_SCHEDULER)
                .subscribe(groups -> {
                    // just take the first group
                    if (groups.get(0) != null) {
                        chat.setGroup(groups.get(0));
                    } else {
                        System.out.println("firstGroup is null");
                        disposables.add(groupService.createGroup("%s + %s".formatted(friend.name(),me.name()), privateChatMembers)
                            .observeOn(FX_SCHEDULER)
                            .subscribe(group -> chat.setGroup(group))
                        );
                    }
                })
        );
        openSecondary(chat);
    }
}
