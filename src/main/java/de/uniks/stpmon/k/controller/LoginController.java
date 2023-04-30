package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.Controller;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.inject.Inject;

public class LoginController extends Controller{

    @FXML
    public TextField usernameInput;
    @FXML
    public PasswordField passwordInput;
    @FXML
    public Label errorLabel;
    @FXML
    public Button loginButton;
    @FXML
    public Button registerButton;

    // TODO: loginService
    // @Inject
    // LoginService loginService;
    // TODO: tokenStorage
    // @Inject
    // TokenStorage tokenStorage;

    private BooleanBinding isInvalid;

    // is needed for dagger
    @Inject
    public LoginController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        isInvalid = usernameInput
            .textProperty()
            .isEmpty()
            .or(passwordInput.textProperty().length().lessThan(8));
        loginButton.disableProperty().bind(isInvalid);

        return parent;
    }

    public void login() {
        if (isInvalid.get()) {
            return;
        }
        loginService.login(usernameInput.getText(), passwordInput.getText()).subscribe(lr -> {
            System.out.println(lr);
            System.out.println(tokenStorage.getToken());
        }, error -> {
            System.out.println(error);
        });
    }

    public void register() {
        // TODO: register function
    }
}
