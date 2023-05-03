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
    HybridController hybridController;
    @Inject
    UserService userService;

    private BooleanBinding isInvalid;


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
        registerButton.disableProperty().bind(isInvalid);

        // disables all focused input fields, so you can see the input text placeholders
        FX_SCHEDULER.scheduleDirect(parent::requestFocus);
        return parent;
    }

    public void login() {
        if (isInvalid.get()) {
            return;
        }
        loginWithCredentials(usernameInput.getText(), passwordInput.getText());
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
                    //Login
                    loginWithCredentials(user.name(), passwordInput.getText());
                }, error -> {
                    String errorText = getErrorMessage(error);
                    errorLabel.setText(errorText);
                    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");
                    System.out.println("look here for the error: " + error);
                }));
    }

    private void loginWithCredentials(String username, String password){
        disposables.add(authService
                .login(username, password)
                .observeOn(FX_SCHEDULER)
                .subscribe(lr -> {
                    errorLabel.setText("Login successful");
                    errorLabel.setStyle("-fx-text-fill: green; -fx-font-size: 10px;");
                    app.show(hybridController);
                }, error -> {
                    String errorText = getErrorMessage(error);
                    errorLabel.setText(errorText);
                    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");
                    System.out.println("look here for the error: " + error);
                }));

    }

    private String getErrorMessage(Throwable error){
        String errorText = "error";
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
