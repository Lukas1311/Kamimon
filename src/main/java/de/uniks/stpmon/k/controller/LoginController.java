package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.service.AuthenticationService;
import de.uniks.stpmon.k.service.TokenStorage;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import retrofit2.HttpException;

import javax.inject.Inject;
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

    @Inject
    AuthenticationService authService;
    @Inject
    TokenStorage tokenStorage;

    private BooleanBinding isInvalid;
    private BooleanBinding passwordTooShort;
    private BooleanBinding usernameTooLong;

    // is needed for dagger
    @Inject
    public LoginController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        errorLabel.setTextFill(Color.RED);
        passwordTooShort = passwordInput.textProperty().length().lessThan(8);
        usernameTooLong = usernameInput.textProperty().length().greaterThan(32);
        errorLabel.textProperty().bind(
            Bindings.when(passwordTooShort.and(passwordInput.textProperty().isNotEmpty()))
                .then("Password too short.")
                .otherwise(Bindings.when(usernameTooLong)
                    .then("Username too long.")
                    .otherwise("")
                )
        );
        isInvalid = usernameInput
            .textProperty()
            .isEmpty()
            .or(passwordTooShort)
            .or(usernameTooLong);

        loginButton.disableProperty().bind(isInvalid);

        return parent;
    }

    public void login() {
        if (isInvalid.get()) {
            return;
        }
        disposables.add(authService
            .login(usernameInput.getText(), passwordInput.getText())
            .observeOn(FX_SCHEDULER)
            .subscribe(lr -> {
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
}
