package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.Group;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.service.GroupService;
import de.uniks.stpmon.k.service.UserService;
import de.uniks.stpmon.k.service.UserStorage;
import de.uniks.stpmon.k.views.GroupMemberCell;
import io.reactivex.rxjava3.functions.Consumer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.*;

public class CreateChatController extends Controller {


    public Button returnButton;
    public Button leaveGroupButton;
    public VBox groupMemberList;
    public TextField groupNameField;
    public Button createGroupButton;
    public Label errorLabel;

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

    private Group group;

    private BooleanBinding isInvalid;
    private BooleanBinding groupNameTooLong;

    @Inject
    public CreateChatController() {
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Override
    public void init() {
        groupMembers.add(userStorage.getUser()._id());
        Map<String, User> userMap = new LinkedHashMap<>();
        Consumer<List<User>> updateUsers = (users) -> {
            for (User user : users) {
                userMap.put(user._id(), user);
            }
            // Remove the user itself from the list
            userMap.remove(userService.getMe()._id());
            friends.setAll(userMap.values());
        };
        groupMembers.add(userStorage.getUser()._id());
        if (group != null) {
            groupMembers.addAll(group.members());
            disposables.add(userService.getUsers(group.members()).observeOn(FX_SCHEDULER).subscribe(updateUsers));
        }
        disposables.add(userService.getFriends().observeOn(FX_SCHEDULER).subscribe(updateUsers));
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        errorLabel.setFont(new Font(10));
        errorLabel.setTextFill(Color.RED);
        groupNameTooLong = groupNameField.textProperty().length().greaterThan(32);

        errorLabel.textProperty().bind(
                Bindings.when(groupNameTooLong.and(groupNameField.textProperty().isNotEmpty()))
                        .then(resources.getString("group.name.too.long"))
                        .otherwise(Bindings.when(groupNameField.textProperty().isEmpty())
                                .then(resources.getString("group.name.is.empty"))
                                .otherwise("")
                        )
        );
        isInvalid = groupNameField
                .textProperty()
                .isEmpty()
                .or(groupNameTooLong);
        createGroupButton.disableProperty().bind(isInvalid);

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

        if (group != null) {
            leaveGroupButton.setVisible(true);
            createGroupButton.setText(resources.getString("create.group.button.update"));
            createGroupButton.setOnAction(e -> updateGroup());
        }

        return parent;
    }

    private void updateGroup() {
        if (groupNameField.getText().isEmpty()) {
            return;
        }
        disposables.add(groupService.updateGroup(group, groupNameField.getText(), groupMembers)
                .observeOn(FX_SCHEDULER)
                .subscribe(group1 -> hybridControllerProvider.get().openChat(group1)));
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
        disposables.add(groupService.createGroup(groupNameField.getText(), groupMemberNames)
                .observeOn(FX_SCHEDULER)
                .subscribe(hybridControllerProvider.get()::openChat));
    }

    public void handleGroup(User item) {
        if (!groupMembers.contains(item._id())) {
            groupMembers.add(item._id());
        } else {
            groupMembers.remove(item._id());
        }
    }

    public boolean isSelected(String id) {
        return groupMembers.contains(id);
    }
}
