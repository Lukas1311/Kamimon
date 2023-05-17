package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.dto.Group;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.service.GroupService;
import de.uniks.stpmon.k.service.UserService;
import de.uniks.stpmon.k.service.UserStorage;
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
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.HashSet;

public class CreateChatController extends Controller {

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
    Provider<HybridController> hybridControllerProvider;
    @Inject
    GroupService groupService;
    @Inject
    UserService userService;
    @Inject
    UserStorage userStorage;

    private final ObservableList<User> members = FXCollections.observableArrayList();
    public final HashSet<String> groupMembers = new HashSet<>();
    private Group group;

    private BooleanBinding isInvalid;
    private BooleanBinding groupNameTooLong;
    public SimpleBooleanProperty notEnoughGroupMembers = new SimpleBooleanProperty(true);

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
            notEnoughGroupMembers.setValue(true);
            members.setAll(userMap.values());
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
                                .otherwise(Bindings.when(notEnoughGroupMembers)
                                        .then(resources.getString("not.enough.group.members"))
                                        .otherwise(""))
                        )
        );
        isInvalid = groupNameField
                .textProperty()
                .isEmpty()
                .or(notEnoughGroupMembers)
                .or(groupNameTooLong);
        createGroupButton.disableProperty().bind(isInvalid);

        final ListView<User> groupMembers = new ListView<>(this.members);
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
        hybridControllerProvider.get().popTab();
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
                .subscribe(group -> {
                    hybridControllerProvider.get().popTab();
                    hybridControllerProvider.get().openChat(group);
                }));
    }

    public void handleGroup(User item) {
        if (!groupMembers.contains(item._id())) {
            groupMembers.add(item._id());
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
