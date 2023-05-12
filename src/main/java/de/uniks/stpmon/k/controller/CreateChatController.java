package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.service.GroupService;
import de.uniks.stpmon.k.service.UserService;
import de.uniks.stpmon.k.service.UserStorage;
import de.uniks.stpmon.k.views.GroupMemberCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.HashSet;

public class CreateChatController extends Controller {


    public Button returnButton;
    public Button leaveGroupButton;
    public VBox groupMemberList;
    public TextField groupNameField;
    public Button createGroupButton;

    @Inject
    Provider<HybridController> hybridControllerProvider;

    @Inject
    GroupService groupService;

    @Inject
    UserService userService;

    @Inject
    UserStorage userStorage;

    private final ObservableList<User> friends = FXCollections.observableArrayList();

    public final HashSet<String> groupMembers = new HashSet<>();

    @Inject
    public CreateChatController() {
    }

    @Override
    public void init() {
        groupMembers.add(userStorage.getUser()._id());
        disposables.add(userService.getFriends().observeOn(FX_SCHEDULER).subscribe(this.friends::setAll));
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        final ListView<User> groupMembers = new ListView<>(this.friends);
        groupMemberList.getChildren().add(groupMembers);
        VBox.setVgrow(groupMembers, Priority.ALWAYS);
        groupMembers.setCellFactory(e -> new GroupMemberCell(this));


        returnButton.setOnAction(e -> returnToChatList());

        groupNameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                createGroup();
            }
        });
        createGroupButton.setOnAction(e -> createGroup());

        return parent;
    }

    public void returnToChatList() {
        hybridControllerProvider.get().openSidebar("chatList");
    }

    public void leaveGroup() {
        //TODO
    }


    public void createGroup() {
        if (groupNameField.getText().isEmpty()) {
            return;
        }
        final ArrayList<String> groupMemberNames = new ArrayList<>(groupMembers);
        disposables.add(groupService.createGroup(groupNameField.getText(), groupMemberNames).observeOn(FX_SCHEDULER).subscribe(group -> {
            hybridControllerProvider.get().openChat(group);
        }, error -> {
            System.out.println("look here for the error: " + error);
        }));
    }

    public void handleGroup(User item) {
        if (!groupMembers.contains(item._id())) {
            groupMembers.add(item._id());
        } else {
            groupMembers.remove(item._id());
        }
    }
}
