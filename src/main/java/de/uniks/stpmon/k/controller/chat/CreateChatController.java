package de.uniks.stpmon.k.controller.chat;

import de.uniks.stpmon.k.controller.sidebar.TabController;
import de.uniks.stpmon.k.models.Group;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.service.GroupService;
import de.uniks.stpmon.k.service.UserService;
import de.uniks.stpmon.k.service.storage.UserStorage;
import de.uniks.stpmon.k.views.GroupMemberCell;
import io.reactivex.rxjava3.functions.Consumer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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
import java.util.*;

public class CreateChatController extends TabController {

    @FXML
    public Button returnButton;
    @FXML
    public Button leaveGroupButton;
    @FXML
    public VBox groupMemberList;
    @FXML
    public TextField groupNameField;
    @FXML
    public Button createGroupButton;
    @FXML
    public Label errorLabel;

    @Inject
    GroupService groupService;
    @Inject
    UserService userService;
    @Inject
    UserStorage userStorage;

    private final ObservableList<User> members = FXCollections.observableArrayList();
    private final HashSet<String> groupMembers = new HashSet<>();
    private final SimpleBooleanProperty notEnoughGroupMembers = new SimpleBooleanProperty(true);
    private Group group;

    @Inject
    public CreateChatController() {
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Override
    public void init() {
        groupMembers.clear();
        groupMembers.add(userStorage.getUser()._id());
        Map<String, User> userMap = new LinkedHashMap<>();
        Consumer<List<User>> updateUsers = (users) -> {
            for (User user : users) {
                userMap.put(user._id(), user);
            }
            // Remove the user itself from the list
            userMap.remove(userService.getMe()._id());
            notEnoughGroupMembers.setValue(groupMembers.isEmpty());

            members.setAll(userMap.values());
        };
        groupMembers.add(userStorage.getUser()._id());
        if (group != null) {
            groupMembers.addAll(group.members());
            subscribe(userService.getUsers(group.members()), updateUsers);
        }
        subscribe(userService.getFriends(), updateUsers);
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        errorLabel.setFont(new Font(10));
        errorLabel.setTextFill(Color.RED);
        BooleanBinding groupNameTooLong = groupNameField.textProperty().length().greaterThan(32);

        errorLabel.textProperty().bind(
                Bindings.when(groupNameTooLong.and(groupNameField.textProperty().isNotEmpty()))
                        .then(translateString("group.name.too.long"))
                        .otherwise(Bindings.when(groupNameField.textProperty().isEmpty())
                                .then(translateString("group.name.is.empty"))
                                .otherwise(Bindings.when(notEnoughGroupMembers)
                                        .then(translateString("not.enough.group.members"))
                                        .otherwise(""))
                        )
        );
        BooleanBinding isInvalid = groupNameField
                .textProperty()
                .isEmpty()
                .or(notEnoughGroupMembers)
                .or(groupNameTooLong);
        createGroupButton.disableProperty().bind(isInvalid);

        final ListView<User> groupMembers = new ListView<>(this.members);
        groupMembers.getStyleClass().add("edit-chat-list");
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
            createGroupButton.setText(translateString("create.group.button.update"));
            createGroupButton.setOnAction(e -> updateGroup());
            groupNameField.setText(group.name());
        }

        return parent;
    }

    private void updateGroup() {
        if (groupNameField.getText().isEmpty()) {
            return;
        }
        subscribe(groupService.updateGroup(group, groupNameField.getText(), groupMembers),
                group1 -> {
                    popTab();
                    popTab();
                    openTab(hybrid -> hybrid.openChat(group1));
                });
    }

    public void returnToChatList() {
        popTab();
    }

    public void leaveGroup() {
        if (group == null) {
            return;
        }
        // Remove the user itself from the list, even if the group will be deleted
        groupMembers.remove(userStorage.getUser()._id());
        subscribe(groupService.deleteOrUpdateGroup(group, group.name(), new ArrayList<>(groupMembers)), group -> {
            popTab();
            popTab();
        }, this::handleError);
    }

    public void createGroup() {
        if (groupNameField.getText().isEmpty()) {
            return;
        }
        final ArrayList<String> groupMemberNames = new ArrayList<>(groupMembers);
        subscribe(groupService.createGroup(groupNameField.getText(), groupMemberNames), group -> {
            popTab();
            openTab(hybrid -> hybrid.openChat(group));
        }, this::handleError);
    }

    public void handleGroup(User item) {
        if (groupMembers.add(item._id())) {
            notEnoughGroupMembers.setValue(false);
        } else {
            groupMembers.remove(item._id());
            if (groupMembers.size() == 1) {
                notEnoughGroupMembers.setValue(true);
            }
        }
    }

    public boolean isSelected(String id) {
        return groupMembers.contains(id);
    }

}