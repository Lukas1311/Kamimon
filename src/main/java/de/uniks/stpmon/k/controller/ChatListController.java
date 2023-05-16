package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.Group;
import de.uniks.stpmon.k.service.GroupService;
import de.uniks.stpmon.k.views.ChatCell;
import de.uniks.stpmon.k.ws.EventListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class ChatListController extends Controller {

    @FXML
    public Button newChatButton;
    @FXML
    VBox chatList;
    @Inject
    GroupService groupService;
    @Inject
    @Singleton
    Provider<HybridController> hybridControllerProvider;
    @Inject
    Provider<ChatController> chatControllerProvider;
    @Inject
    EventListener eventListener;

    private final ObservableList<Group> groups = FXCollections.observableArrayList();
    private final Map<String, Group> groupMap = new HashMap<>();

    @Inject
    public ChatListController() {
    }

    @Override
    public void init() {
        disposables.add(groupService.getOwnGroups().observeOn(FX_SCHEDULER).subscribe((list) -> {
            list.forEach(group -> groupMap.put(group._id(), group));
            groups.setAll(groupMap.values());
        }));
        disposables.add(eventListener.listen("groups.*.*", Group.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    final Group group = event.data();
                    switch (event.suffix()) {
                        case "create" -> groupMap.put(group._id(), group);
                        case "update" -> {
                            groupMap.remove(group._id());
                            groupMap.put(group._id(), group);
                        }
                        case "delete" -> groupMap.remove(group._id());
                    }
                    groups.setAll(groupMap.values());
                }));
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        final ListView<Group> groups = new ListView<>(this.groups);
        // pass current chatListController (this) to make use of it in subclasses that cannot use inject
        groups.setCellFactory(param -> new ChatCell(this));
        groups.setOnKeyReleased(event -> {
            if (groups.getSelectionModel().isEmpty()
                    || event.getCode() != KeyCode.ENTER) {
                return;
            }
            openChat(groups.getSelectionModel().getSelectedItem());
        });
        chatList.getChildren().add(groups);
        return parent;
    }

    // a method that is used by the chatEntryController to open a new chat (chatController)
    public void openChat(Group group) {
        hybridControllerProvider.get().openChat(group);
    }

    public void createChat() {
        hybridControllerProvider.get().openSidebar("createChat");
    }
}
