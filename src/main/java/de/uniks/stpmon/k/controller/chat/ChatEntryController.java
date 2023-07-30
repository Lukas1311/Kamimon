package de.uniks.stpmon.k.controller.chat;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Group;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import static de.uniks.stpmon.k.utils.StringUtils.filterChatName;

public class ChatEntryController extends Controller {

    @FXML
    public Text chatName;
    @FXML
    public VBox chatEntry;

    private final ChatListController chatListController;
    private final Group group;
    private final String name;

    public ChatEntryController(Group group, ChatListController chatListController) {
        this.group = group;
        this.name = group.name();
        this.chatListController = chatListController;
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        parent.setId("entry_" + name);
        chatName.setText(filterChatName(name, chatListController.getCurrentUsername()));
        chatEntry.setId(name);
        parent.setOnMouseClicked(e -> openChat());

        return parent;
    }

    public void openChat() {
        chatListController.openChat(group);
    }

}
