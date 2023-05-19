package de.uniks.stpmon.k.controller;


import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.service.AuthenticationService;
import de.uniks.stpmon.k.service.NetworkAvailability;
import de.uniks.stpmon.k.service.TokenStorage;
import de.uniks.stpmon.k.service.UserService;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import retrofit2.HttpException;

import javax.inject.Inject;
import javax.inject.Provider;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
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
    @FXML
    public VBox loginScreen;
    @FXML
    public ToggleGroup lang;
    @FXML
    public ImageView kamimonLetteringImageView;

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

    @Inject
    IntroductionController introductionController;

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
        loadImage("kamimonLettering.png");
        //initImageAsync(kamimonLetteringImageView, ;

        errorLabel.setFont(new Font(10.0));
        errorLabel.setTextFill(Color.RED);
        passwordTooShort = passwordInput.textProperty().length().lessThan(8);
        usernameTooLong = usernameInput.textProperty().length().greaterThan(32);

        boolean germanSelected = Objects.equals(preferences.get("locale", ""), Locale.GERMAN.toLanguageTag());
        germanButton.setSelected(germanSelected);
        englishButton.setSelected(!germanSelected);
        errorLabel.textProperty().bind(
            Bindings.when(passwordTooShort.and(passwordInput.textProperty().isNotEmpty()))
                .then(translateString("password.too.short."))
                .otherwise(Bindings.when(usernameTooLong)
                    .then(translateString("username.too.long."))
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
            errorText.set(translateString("no.internet.connection"));
            return;
        }
    }

    public void login() {
        validateLoginAndRegistration();
        loginWithCredentials(usernameInput.getText(), passwordInput.getText(), rememberMe.isSelected(), true);
    }

    private void loginWithCredentials(String username, String password, boolean rememberMe, boolean isRegistered){
        disposables.add(authService
                .login(username, password, rememberMe)
                .observeOn(FX_SCHEDULER)
                .subscribe(lr -> {
                    errorText.set(translateString("login.successful"));
                    errorLabel.setTextFill(Color.GREEN);
                    if(isRegistered) {
                        app.show(hybridControllerProvider.get());
                    }else {
                        app.show(introductionController);
                    }
                }, error -> {
                    errorText.set(getErrorMessage(error));

                }));
    }

    public void register() {
        validateLoginAndRegistration();
        disposables.add(userService
                .addUser(usernameInput.getText(), passwordInput.getText())
                .observeOn(FX_SCHEDULER)
                .subscribe(user -> {
                    errorText.set(translateString("registration.successful"));
                    errorLabel.setTextFill(Color.GREEN);
                    //Login
                    loginWithCredentials(user.name(), passwordInput.getText(), rememberMe.isSelected(), false);
                }, error -> {
                    errorText.set(getErrorMessage(error));
                }));
    }

    private String getErrorMessage(Throwable error){
        if (!(error instanceof HttpException exception)) {
            return errorText.get();
        }
        return switch (exception.code()) {
            case 400 -> translateString("validation.failed");
            case 401 -> translateString("invalid.username.or.password");
            case 409 -> translateString("username.was.already.taken");
            case 429 -> translateString("rate.limit.reached");
            default  -> translateString("error");
        };
    }

    private void hidePassword() {
        if(isEmpty){
            passwordInput.setText("");
        }else{
            passwordInput.setText(password);
        }
        passwordInput.setPromptText(translateString("password"));
    }

    private void showPassword() {
        password = passwordInput.getText();
        if(password == null || password.isEmpty()) {
            password = translateString("password");
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
        app.show(this); //reloaded
    }


    private Image loadImage(String image) {
        return new Image(Objects.requireNonNull(LoadingScreenController.class.getResource(image)).toString());
    }

}
