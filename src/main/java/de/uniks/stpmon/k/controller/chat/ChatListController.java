package de.uniks.stpmon.k.controller.chat;

import de.uniks.stpmon.k.controller.sidebar.TabController;
import de.uniks.stpmon.k.models.Group;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.GroupService;
import de.uniks.stpmon.k.service.UserService;
import de.uniks.stpmon.k.views.ChatCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

import static de.uniks.stpmon.k.controller.sidebar.SidebarTab.CHAT_CREATE;

@Singleton
public class ChatListController extends TabController {

    @FXML
    public Button newChatButton;
    @FXML
    VBox chatList;
    @Inject
    GroupService groupService;
    @Inject
    UserService userService;
    @Inject
    EventListener eventListener;

    private final ObservableList<Group> groups = FXCollections.observableArrayList();
    private final Map<String, Group> groupMap = new HashMap<>();

    @Inject
    public ChatListController() {
    }

    @Override
    public void init() {
        groupMap.clear();
        subscribe(groupService.getOwnGroups(), (list) -> {
            list.forEach(group -> groupMap.put(group._id(), group));
            groups.setAll(groupMap.values());
        }, this::handleError);
        subscribe(eventListener.listen(Socket.WS, "groups.*.*", Group.class), event -> {
            final Group group = event.data();
            switch (event.suffix()) {
                case "created" -> groupMap.put(group._id(), group);
                case "updated" -> {
                    groupMap.remove(group._id());
                    groupMap.put(group._id(), group);
                }
                case "deleted" -> groupMap.remove(group._id());
            }
            groups.setAll(groupMap.values());
        }, this::handleError);
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        final ListView<Group> groups = new ListView<>(this.groups);
        groups.setId("chatListView");
        groups.getStyleClass().add("chat-ov-list");
        groups.setCellFactory(param -> new ChatCell(this));
        groups.setOnKeyReleased(event -> {
            if (groups.getSelectionModel().isEmpty()
                    || event.getCode() != KeyCode.ENTER) {
                return;
            }
            openChat(groups.getSelectionModel().getSelectedItem());
        });
        chatList.getChildren().add(groups);
        VBox.setVgrow(groups, Priority.ALWAYS);
        return parent;
    }

    /**
     * Opens a new chat with the given group.
     *
     * @param group the group to open a chat with
     */
    public void openChat(Group group) {
        openTab(hybrid -> hybrid.openChat(group));
    }

    /**
     * Opens a new created chat screen.
     */
    public void createChat() {
        pushTab(CHAT_CREATE);
    }


    /**
     * returns the username from the current user
     *
     * @return username of current user
     */
    public String getCurrentUsername() {
        return userService.getMe().name();
    }

}
