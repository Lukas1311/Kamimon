package de.uniks.stpmon.k.controller.sidebar;

import de.uniks.stpmon.k.controller.ChatController;
import de.uniks.stpmon.k.controller.ChatListController;
import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.CreateChatController;
import de.uniks.stpmon.k.controller.FriendListController;
import de.uniks.stpmon.k.controller.IngameController;
import de.uniks.stpmon.k.controller.LobbyController;
import de.uniks.stpmon.k.controller.PauseController;
import de.uniks.stpmon.k.controller.SettingsController;
import de.uniks.stpmon.k.models.Group;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.service.GroupService;
import de.uniks.stpmon.k.service.UserService;
import io.reactivex.rxjava3.core.Observable;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

@Singleton
public class HybridController extends Controller {
    public static final int TAB_WIDTH = -370;
    private final Stack<Controller> tabStack = new Stack<>();
    private final TranslateTransition sidebarTransition = new TranslateTransition(Duration.millis(800));
    private final EventHandler<MouseEvent> consumeMouse = MouseEvent::consume;

    private boolean playAnimations = true;

    private SidebarTab mainTab = SidebarTab.NONE;

    private MainWindow currentWindow = MainWindow.LOBBY;

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
    Provider<ChatController> chatControllerProvider;
    @Inject
    Provider<CreateChatController> createChatControllerProvider;
    @Inject
    UserService userService;
    @Inject
    GroupService groupService;
    @Inject
    SettingsController settingsController;

    @Inject
    public HybridController() {

    }

    public void setPlayAnimations(boolean playAnimations) {
        this.playAnimations = playAnimations;
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
        stackPane.prefWidthProperty().bind(app.getStage().widthProperty().subtract(50));
        openMain(MainWindow.LOBBY);
        return parent;
    }

    @SuppressWarnings("unchecked")
    private <C extends Controller> void pushController(Controller controller, Consumer<C> setup) {
        ObservableList<Node> children = stackPane.getChildren();

        if (setup != null) {
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
                sidebar.setIngame(false);
                sidebar.setSettings(true);
                openMain(lobbyController.get());
            }
            case INGAME -> {
                sidebar.setPause(true);
                sidebar.setIngame(true);
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
            case CHAT_CREATE -> pushController(createChatControllerProvider.get(), setup);
            case FRIEND_LIST -> pushController(friendListController, setup);
            case SETTINGS -> pushController(settingsController, setup);
            case NONE -> {}
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

    private void applyTransition(boolean open, Runnable callback) {
        ObservableList<Node> children = stackPane.getChildren();
        if (children.size() < 2) {
            return;
        }

        if (!playAnimations) {
            if (callback != null) {
                callback.run();
            }
            return;
        }

        // Disable mouse events on the stack pane while the animation is running
        stackPane.addEventFilter(MouseEvent.ANY, consumeMouse);

        Region node = (Region) children.get(open ? 1 : children.size() - 1);

        sidebarTransition.setToX(open ? 0 : TAB_WIDTH);
        sidebarTransition.setFromX(open ? TAB_WIDTH : 0);
        sidebarTransition.setNode(node);
        sidebarTransition.setInterpolator(EASE_OUT_QUINT);
        sidebarTransition.playFromStart();
        sidebarTransition.setOnFinished(event -> {
            stackPane.removeEventFilter(MouseEvent.ANY, consumeMouse);
            if (callback != null) {
                callback.run();
            }
        });

    }

    /**
     * Opens a tab in the sidebar at the first position or closes it if it is already open.
     * This closes all other open tabs.
     */
    public <C extends Controller> void forceTab(SidebarTab tab, Consumer<C> setup) {
        if (mainTab != tab) {
            closeTab();
            mainTab = tab;
            pushTab(tab, setup);

            applyTransition(true, null);
        } else {
            ObservableList<Node> children = stackPane.getChildren();

            mainTab = SidebarTab.NONE;
            // Hide tabs behind the tab that is on top
            for (int i = 1; i < tabStack.size() - 1; i++) {
                children.get(i).setVisible(false);
            }
            applyTransition(false, this::closeTab);
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

    public void createChat(Group group) {
        pushTab(SidebarTab.CHAT_CREATE, (CreateChatController controller) -> controller.setGroup(group));
    }

    private static final Interpolator EASE_OUT_QUINT = new Interpolator() {
        @Override
        protected double curve(double t) {
            return 1 - Math.pow(1 - t, 5);
        }
    };
}
