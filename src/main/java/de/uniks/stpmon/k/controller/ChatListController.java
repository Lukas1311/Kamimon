package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.Group;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.service.GroupService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import javax.inject.Inject;

public class ChatListController extends Controller {

    @Inject
    public VBox chatList;
    @Inject
    GroupService groupService;

    @Inject
    public ChatListController() {
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
        return parent;
    }


}
