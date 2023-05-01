package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.Controller;
// TODO: implement TokenStorage
// import de.uniks.stpmon.k.service.TokenStorage;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;

import javax.inject.Inject;
import javax.swing.*;

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
    @FXML
    public RadioButton germanButton;
    @FXML
    public RadioButton englishButton;
    @FXML
    public CheckBox mask;

    // TODO: loginService
    // @Inject
    // LoginService loginService;
    // TODO: implement TokenStorage
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
        // TODO: login function implementation
        // if (isInvalid.get()) {
        //     return;
        // }
        // disposables.add(loginService.login(usernameInput.getText(), passwordInput.getText()).subscribe(lr -> {
        //     System.out.println(lr);
        //     System.out.println(tokenStorage.getToken());
        // }, error -> {
        //     System.out.println(error);
        // }));
    }

    public void register() {
        // TODO: register function
    }

    public void showPassword() {
        //TODO: The Application must be finished so I can test this funcion!
        SimpleBooleanProperty showPassword = null;
        showPassword.bind(mask.selectedProperty());
    }
}
