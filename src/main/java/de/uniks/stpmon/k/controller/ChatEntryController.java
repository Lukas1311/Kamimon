package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.service.GroupService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;

public class ChatEntryController  extends Controller {

    @FXML
    public Text chatName;
    @FXML
    public VBox chatEntry;
    public Text chatLastMessage;
    @Inject
    @Singleton
    ChatListController chatListController;

    private final String name;
    @Inject
    public ChatEntryController(String name) {
        this.name = name;
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
            return parent;
        }

}
