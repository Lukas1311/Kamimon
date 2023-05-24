package de.uniks.stpmon.k.controller;

import javax.inject.Inject;
import javax.inject.Provider;

import de.uniks.stpmon.k.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class UserManagementController extends Controller {
    @FXML
    public TextField usernameInput;
    @FXML
    public TextField passwordInput;
    @FXML
    public Button deleteUserButton;
    @FXML
    public Button saveChangesButton;
    @FXML
    public Button backButton;

    @Inject
    UserService userService;
    @Inject
    Provider<LoginController> loginControllerProvider;

    @Inject
    public UserManagementController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        // TODO: all ui functionality here

        return parent;
    }

    public void deleteUser() {
        disposables.add(userService
            .deleteMe()
            .observeOn(FX_SCHEDULER)
            .subscribe(usr -> {
                // TODO: user deleted pop up
                app.show(loginControllerProvider.get());
            })
        );
    }

    public void saveChanges() {
        // TODO: add saved changes pop up here
    }
}
