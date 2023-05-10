package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.Group;
import de.uniks.stpmon.k.service.GroupService;
import de.uniks.stpmon.k.views.ChatCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ChatListController extends Controller {

    @FXML
    VBox chatList;
    @Inject
    GroupService groupService;
    @Inject
    @Singleton
    Provider<HybridController> hybridControllerProvider;
    @Inject
    Provider<ChatController> chatControllerProvider;

    private final ObservableList<Group> groups = FXCollections.observableArrayList();
    @Inject
    public ChatListController() {
    }

    @Override
    public void init() {
        disposables.add(groupService.getOwnGroups().observeOn(FX_SCHEDULER).subscribe(this.groups::setAll));
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
        chatList.prefWidthProperty().bind(hybridControllerProvider.get().stackPane.widthProperty().multiply(0.3));
        chatList.getChildren().add(groups);
        return parent;
    }

    // a method that is used by the chatEntryController to open a new chat (chatController)
    public void openChat(Group group) {
        ChatController chat = chatControllerProvider.get();
        chat.setGroup(group);
        app.show(chat);
    }
}
