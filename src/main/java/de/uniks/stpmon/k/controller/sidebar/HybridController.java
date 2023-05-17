package de.uniks.stpmon.k.controller.sidebar;

import de.uniks.stpmon.k.controller.*;
import de.uniks.stpmon.k.dto.Group;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.service.GroupService;
import de.uniks.stpmon.k.service.UserService;
import io.reactivex.rxjava3.core.Observable;
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
import java.util.function.Consumer;

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
    private SidebarTab mainTab = SidebarTab.NONE;
    private MainWindow currentWindow = MainWindow.LOBBY;

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

        openMain(MainWindow.LOBBY);
        return parent;
    }

    private <C extends Controller> void pushController(Controller controller, Consumer<C> setup) {
        ObservableList<Node> children = stackPane.getChildren();

        if (setup != null) {
            // noinspection unchecked
            setup.accept((C) controller);
        }
        controller.init();
        tabStack.push(controller);
        children.add(controller.render());
    }

    private void removeChildren(int endIndex) {
        ObservableList<Node> children = stackPane.getChildren();
        for (int i = tabStack.size() - 1; i >= endIndex; i--) {
            Controller controller = tabStack.pop();
            controller.destroy();
            children.remove(i);
        }
    }

    private void openMain(Controller controller) {
        removeChildren(0);

        pushController(controller, null);
    }

    public void openMain(MainWindow window) {
        SidebarController sidebar = sidebarController.get();
        MainWindow newWindow = window;
        switch (window) {
            case LOBBY -> {
                sidebar.setPause(false);
                sidebar.setLobby(false);
                openMain(lobbyController.get());
            }
            case INGAME -> {
                sidebar.setPause(true);
                sidebar.setLobby(true);
                openMain(ingameController);
            }
            case PAUSE -> {
                boolean pause = currentWindow == MainWindow.PAUSE;
                openMain(pause ? ingameController : pauseController);
                newWindow = pause ? MainWindow.INGAME : MainWindow.PAUSE;
            }
        }
        this.currentWindow = newWindow;
    }

    /**
     * Adds a new tab to the sidebar. This does not close any other tabs but instead puts the new tab on top of the
     * stack.
     */
    public <C extends Controller> void pushTab(SidebarTab tab, Consumer<C> setup) {
        if (mainTab == SidebarTab.NONE) {
            return;
        }

        switch (tab) {
            case CHAT -> pushController(chatControllerProvider.get(), setup);
            case CHAT_LIST -> pushController(chatListController, setup);
            case CHAT_CREATE -> pushController(createChatController, setup);
            case FRIEND_LIST -> pushController(friendListController, setup);
        }
    }

    /**
     * Adds a new tab to the sidebar. This does not close any other tabs but instead puts the new tab on top of the
     * stack.
     */
    public void pushTab(SidebarTab tab) {
        pushTab(tab, null);
    }

    /**
     * Closes the tab that is currently open on top of the stack.
     * This does not close the main window.
     */
    public void popTab() {
        removeChildren(Math.max(tabStack.size() - 1, 1));
    }

    /**
     * Opens a tab in the sidebar at the first position or closes it if it is already open.
     * This closes all other open tabs.
     */
    public <C extends Controller> void forceTab(SidebarTab tab, Consumer<C> setup) {
        closeTab();
        if (mainTab != tab) {
            mainTab = tab;
            pushTab(tab, setup);
        } else {
            mainTab = SidebarTab.NONE;
        }
    }

    /**
     * Opens a tab in the sidebar at the first position or closes it if it is already open.
     * This closes all other open tabs.
     */
    public void forceTab(SidebarTab tab) {
        forceTab(tab, null);
    }

    /**
     * Closes all tabs in the sidebar.
     * This does not close the main window.
     */
    public void closeTab() {
        // remove all tabs, only leave the main window
        removeChildren(1);
    }

    public void openChat(Group group) {
        pushTab(SidebarTab.CHAT, (ChatController c) -> c.setGroup(group));
    }

    /**
     * Check if the friend and the user already have a group, if not create one
     */
    private Observable<Group> getOrCreateGroup(User friend) {
        // the user can only open the chat when the other user is a friend
        User me = userService.getMe(); // is non api call
        ArrayList<String> privateChatMembers = new ArrayList<>(List.of(friend._id(), me._id()));
        return groupService.getGroupsByMembers(privateChatMembers)
                .flatMap(groups -> {
                    if (!groups.isEmpty() && groups.get(0) != null) {
                        return Observable.just(groups.get(0));
                    }
                    return groupService.createGroup("%s + %s".formatted(friend.name(), me.name()), privateChatMembers);
                });
    }

    public void openChat(User friend) {
        disposables.add(getOrCreateGroup(friend)
                .observeOn(FX_SCHEDULER)
                .subscribe(group ->
                        pushTab(SidebarTab.CHAT, (ChatController controller) -> controller.setGroup(group)))
        );
    }
}