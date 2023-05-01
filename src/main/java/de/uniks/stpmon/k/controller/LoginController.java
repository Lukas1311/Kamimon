package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.service.AuthenticationService;
import de.uniks.stpmon.k.service.TokenStorage;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import retrofit2.HttpException;

import javax.inject.Inject;
import javax.swing.*;
import javax.inject.Provider;

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

    @Inject
    AuthenticationService authService;
    @Inject
    TokenStorage tokenStorage;

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
            .or(passwordInput.textProperty().length().lessThan(8))
            .or(usernameInput.textProperty().length().greaterThan(32));
        loginButton.disableProperty().bind(isInvalid);

        return parent;
    }

    public void login() {
        if (isInvalid.get()) {
            return;
        }
        disposables.add(authService.login(usernameInput.getText(), passwordInput.getText()).subscribe(lr -> {
            errorLabel.setText("Login successful");
            errorLabel.setStyle("-fx-text-fill: green; -fx-font-size: 10px;");
            app.show(new DummyController());
        }, error -> {
            String errorText = "error";
            // TODO: refactor to method
            // this can be refactored to an own method in near future
            // cause register and refresh use it too
            if (error instanceof HttpException) {
                HttpException exception = (HttpException) error;

                switch (exception.code()) {
                    case 400:
                        errorText = "Validation failed";
                        break;
                    case 401:
                        errorText = "Invalid username or password";
                        break;
                    case 429:
                        errorText = "Rate limit reached";
                        break;
                    default: break;
                }
            }
            errorLabel.setText(errorText);
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");
            System.out.println("look here for the error: " + error);
        }));
    }

    public void register() {
        // TODO: register function
        app.show(new DummyController());
    }

    public void showPassword() {
        //TODO: The Application must be finished so I can test this funcion!
        SimpleBooleanProperty showPassword = null;
        showPassword.bind(mask.selectedProperty());
    }
}
