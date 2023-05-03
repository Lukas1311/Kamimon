package de.uniks.stpmon.k.controller;


import de.uniks.stpmon.k.service.AuthenticationService;
import de.uniks.stpmon.k.service.TokenStorage;
import de.uniks.stpmon.k.service.UserService;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import retrofit2.HttpException;

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
    @FXML
    public RadioButton germanButton;
    @FXML
    public RadioButton englishButton;

    @Inject
    AuthenticationService authService;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    UserService userService;

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

        // disables all focused input fields, so you can see the input text placeholders
        FX_SCHEDULER.scheduleDirect(parent::requestFocus);
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
                    String errorText = getErrorMessage(error);
                    errorLabel.setText(errorText);
                    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");
                    System.out.println("look here for the error: " + error);
                }));
    }

    public void register() {
        if (isInvalid.get()) {
            return;
        }
        disposables.add(userService
                .addUser(usernameInput.getText(), passwordInput.getText())
                .observeOn(FX_SCHEDULER)
                .subscribe(user -> {
                    errorLabel.setText("Registration successful");
                    errorLabel.setStyle("-fx-text-fill: green; -fx-font-size: 10px;");
                    app.show(new DummyController());
                }, error -> {
                    String errorText = getErrorMessage(error);
                    errorLabel.setText(errorText);
                    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");
                    System.out.println("look here for the error: " + error);
                }));

        app.show(new DummyController());
    }

    private String getErrorMessage(Throwable error){
        String errorText = "error";
        // TODO: refactor to method
        // this can be refactored to an own method in near future
        // cause register and refresh use it too
        if (error instanceof HttpException exception) {

            switch (exception.code()) {
                case 400 -> errorText = "Validation failed";
                case 401 -> errorText = "Invalid username or password";
                case 409 -> errorText = "Username was already taken";
                case 429 -> errorText = "Rate limit reached";
                default -> {
                }
            }
        }
        return errorText;
    }
}
