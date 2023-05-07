package de.uniks.stpmon.k.controller;


import de.uniks.stpmon.k.service.AuthenticationService;
import de.uniks.stpmon.k.service.NetworkAvailability;
import de.uniks.stpmon.k.service.TokenStorage;
import javafx.beans.binding.Bindings;
import de.uniks.stpmon.k.service.UserService;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import retrofit2.HttpException;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends Controller {

    @FXML
    public TextField usernameInput;
    @FXML
    public PasswordField passwordInput;
    @FXML
    public Label errorLabel;
    @FXML
    public Button loginButton;
    @FXML
    public CheckBox rememberMe;
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
    NetworkAvailability netAvailability;
    @Inject
    HybridController hybridController;
    @Inject
    UserService userService;

    private BooleanBinding isInvalid;
    private BooleanBinding passwordTooShort;
    private BooleanBinding usernameTooLong;
    private StringProperty errorText;
    private String password;
    private boolean isEmpty = false;

    @Inject
    public LoginController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        errorLabel.setFont(new Font(10.0));
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
        registerButton.disableProperty().bind(isInvalid);

        // disables all focused input fields, so you can see the input text placeholders
        FX_SCHEDULER.scheduleDirect(parent::requestFocus);
        return parent;
    }

    public void validateLoginAndRegistration() {
        errorText = new SimpleStringProperty("");
        errorLabel.textProperty().bind(errorText);
        if (isInvalid.get()) {
            return;
        }
        if(!netAvailability.isInternetAvailable()) {
            errorText.set("No internet connection");
            return;
        }
    }

    public void login() {
        validateLoginAndRegistration();
        loginWithCredentials(usernameInput.getText(), passwordInput.getText(), rememberMe.isSelected());
    }

    // TODO: is almost the same like register method, i bet we can refactor this to one method
    private void loginWithCredentials(String username, String password, boolean rememberMe){
        disposables.add(authService
                .login(username, password, rememberMe)
                .observeOn(FX_SCHEDULER)
                .subscribe(lr -> {
                    errorText.set("Login successful");
                    errorLabel.setTextFill(Color.GREEN);
                    app.show(hybridController);
                }, error -> {
                    errorText.set(getErrorMessage(error));
                    System.out.println("look here for the error: " + error);
                }));
    }

    public void register() {
        validateLoginAndRegistration();
        disposables.add(userService
                .addUser(usernameInput.getText(), passwordInput.getText())
                .observeOn(FX_SCHEDULER)
                .subscribe(user -> {
                    errorText.set("Registration successful");
                    errorLabel.setTextFill(Color.GREEN);
                    //Login
                    loginWithCredentials(user.name(), passwordInput.getText(), rememberMe.isSelected());
                }, error -> {
                    errorText.set(getErrorMessage(error));
                    System.out.println("look here for the error: " + error);
                }));
    }

    private String getErrorMessage(Throwable error){
        if (!(error instanceof HttpException exception)) {
            return errorText.get();
        }
        return switch (exception.code()) {
            case 400 -> "Validation failed";
            case 401 -> "Invalid username or password";
            case 409 -> "Username was already taken";
            case 429 -> "Rate limit reached";
            default  -> "error";
        };
    }

    @FXML
    public void toggleReleased(MouseEvent mouseEvent) {
        if(isEmpty){
            passwordInput.setText("");
        }else{
            passwordInput.setText(password);
        }
        passwordInput.setPromptText("Password");
    }

    @FXML
    public void togglePressed(MouseEvent mouseEvent) {
        password = passwordInput.getText();
        if(password == null || password.isEmpty()) {
            password = "Password";
            isEmpty = true;
        }else{
            isEmpty = false;
        }
        passwordInput.clear();
        passwordInput.setPromptText(password);
    }

}
