package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.chat.ChatController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.service.GroupService;
import de.uniks.stpmon.k.service.UserService;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Singleton
public class FriendListController extends ToastedController {

    private final List<FriendController> friendControllers = new ArrayList<>();
    private final List<FriendController> userControllers = new ArrayList<>();
    private final Subject<String> searchUpdate = PublishSubject.create();

    @FXML
    public TextField searchFriend;
    @FXML
    public Button searchButton;
    @FXML
    public VBox friendList;
    @FXML
    public VBox userSeparator;
    @FXML
    public VBox friendSection;
    @FXML
    public VBox userSection;
    @FXML
    public ScrollPane scrollPane;

    @Inject
    UserService userService;
    @Inject
    GroupService groupService;
    @Inject
    Provider<ChatController> chatControllerProvider;
    @Inject
    Provider<HybridController> hybridControllerProvider;
    @Inject
    Provider<ResourceBundle> resources;

    @Inject
    public FriendListController() {
    }

    private void updateControllers(List<FriendController> controllers, List<User> users, VBox node, boolean newFriends) {
        for (FriendController friendController : controllers) {
            friendController.destroy();
        }
        controllers.clear();
        node.getChildren().clear();
        for (User user : users) {
            FriendController controller = new FriendController(user, newFriends, this, resources);
            controller.init();
            controllers.add(controller);
            Parent parent = controller.render();
            VBox.setMargin(parent, new Insets(3, 0, 3, 0));
            node.getChildren().add(parent);
        }
    }

    @Override
    public void init() {
        subscribe(searchUpdate.flatMap((text) -> userService.filterFriends(text)), (values) -> {
            updateControllers(friendControllers, values, friendSection, false);
            userSeparator.setVisible(!values.isEmpty() && !userControllers.isEmpty());
        });
        subscribe(searchUpdate.flatMap((text) -> userService.searchFriend(text)), (values) -> {
            updateControllers(userControllers, values, userSection, true);
            userSeparator.setVisible(!values.isEmpty() && !friendControllers.isEmpty());
        });
        searchUpdate.onNext("");
    }

    @Override
    public void destroy() {
        super.destroy();

        for (FriendController friendController : userControllers) {
            friendController.destroy();
        }

        for (FriendController friendController : friendControllers) {
            friendController.destroy();
        }
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        searchButton.setOnAction(e -> searchForFriend());

        searchFriend.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                searchForFriend();
            }
        });

        return parent;
    }

    @FXML
    private void searchForFriend() {
        String name = searchFriend.getText();
        searchUpdate.onNext(name);
    }

    public void handleFriend(Boolean newFriend, User user) {
        if (newFriend) {
            disposables.add(userService.addFriend(user)
                    .observeOn(FX_SCHEDULER)
                    .doOnError(this::handleError)
                    .subscribe());
        } else {
            disposables.add(userService
                    .removeFriend(user)
                    .observeOn(FX_SCHEDULER)
                    .doOnError(this::handleError)
                    .subscribe());
        }
    }

    public void openChat(User friend) {
        hybridControllerProvider.get().openChat(friend);
    }

}
