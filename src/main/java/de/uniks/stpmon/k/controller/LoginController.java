package de.uniks.stpmon.k.controller;


import de.uniks.stpmon.k.service.AuthenticationService;
import de.uniks.stpmon.k.service.NetworkAvailability;
import de.uniks.stpmon.k.service.TokenStorage;
import javafx.beans.binding.Bindings;
import de.uniks.stpmon.k.service.UserService;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
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
import javax.inject.Provider;
import java.util.Locale;
import java.util.prefs.Preferences;

public class LoginController extends Controller {

    @FXML
    public TextField usernameInput;
    @FXML
    public PasswordField passwordInput;
    @FXML
    public Button toggleButton;
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
    Provider<HybridController> hybridControllerProvider;
    @Inject
    UserService userService;
    @Inject
    Preferences preferences;

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
                .then(resources.getString("password too short."))
                .otherwise(Bindings.when(usernameTooLong)
                    .then(resources.getString("username too long."))
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

        // shows Password on holding mouse button or holding enter
        toggleButton.armedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                showPassword();
            }else{
                hidePassword();
            }
        });

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
            errorText.set(resources.getString("no internet connection"));
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
                    errorText.set(resources.getString("login successful"));
                    errorLabel.setTextFill(Color.GREEN);
                    app.show(hybridControllerProvider.get());
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
                    errorText.set(resources.getString("registration successful"));
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
            case 400 -> resources.getString("validation failed");
            case 401 -> resources.getString("invalid username or password");
            case 409 -> resources.getString("username was already taken");
            case 429 -> resources.getString("rate limit reached");
            default  -> resources.getString("error");
        };
    }

    private void hidePassword() {
        if(isEmpty){
            passwordInput.setText("");
        }else{
            passwordInput.setText(password);
        }
        passwordInput.setPromptText(resources.getString("password"));
    }

    private void showPassword() {
        password = passwordInput.getText();
        if(password == null || password.isEmpty()) {
            password = resources.getString("password");
            isEmpty = true;
        }else{
            isEmpty = false;
        }
        passwordInput.clear();
        passwordInput.setPromptText(password);
    }

    public void setDe() {
        setLanguage(Locale.GERMAN);
    }

    public void setEn() {
        setLanguage(Locale.ENGLISH);
    }

    private void setLanguage(Locale locale) {
        preferences.put("locale", locale.toLanguageTag());
    }
}
