package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.Group;
import de.uniks.stpmon.k.service.GroupService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

public class ChatEntryController  extends Controller {

    @FXML
    public Text chatName;
    @FXML
    public VBox chatEntry;
    @Inject
    ChatController chatController;

    private final ChatListController chatListController;
    private final Group group;
    private final String name;

    @Inject
    public ChatEntryController(Group group, ChatListController chatListController) {
        this.group = group;
        this.name = group.name();
        this.chatListController = chatListController;
    }

    @Override
        public void init() {
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        chatName.setText(name);
        parent.setOnMouseClicked(e -> openChat());

        return parent;
    }

    public void openChat() {
        chatListController.openChat(group);
    }

}