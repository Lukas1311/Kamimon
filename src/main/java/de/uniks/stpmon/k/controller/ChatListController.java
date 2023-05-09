package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.Group;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.service.GroupService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import javax.inject.Inject;

public class ChatListController extends Controller {

    @Inject
    public VBox chatList;
    @Inject
    GroupService groupService;

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
        final ListView<Group> friends = new ListView<>(this.groups);
        chatList.getChildren().add(friends);
        return parent;
    }


}
