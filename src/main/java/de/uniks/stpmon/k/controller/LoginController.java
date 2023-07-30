package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.service.AuthenticationService;
import de.uniks.stpmon.k.service.UserService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import retrofit2.HttpException;

import javax.inject.Inject;
import javax.inject.Provider;
import java.net.UnknownHostException;
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
    public ImageView imageViewKamimonLettering;

    @Inject
    AuthenticationService authService;
    @Inject
    Provider<HybridController> hybridControllerProvider;
    @Inject
    UserService userService;
    @Inject
    Preferences preferences;
    @Inject
    Provider<IntroductionController> introductionControllerProvider;
    private final SimpleStringProperty username = new SimpleStringProperty();
    private final SimpleStringProperty password = new SimpleStringProperty();
    private StringProperty errorText;
    private String tempPassword;
    private boolean isEmpty = false;
    private boolean passwordVisible = false;

    @Inject
    public LoginController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        errorLabel.setFont(new Font(10.0));
        errorLabel.setTextFill(Color.RED);
        BooleanBinding passwordTooShort = password.length().lessThan(8);
        BooleanBinding usernameTooLong = username.length().greaterThan(32);

        usernameInput.textProperty().bindBidirectional(username);
        passwordInput.textProperty().bindBidirectional(password);

        boolean germanSelected = Objects.equals(preferences.get("locale", ""), Locale.GERMAN.toLanguageTag());
        germanButton.setSelected(germanSelected);
        englishButton.setSelected(!germanSelected);
        germanButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                setDe();
            }
        });
        englishButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                setEn();
            }
        });

        errorLabel.textProperty().bind(
                Bindings.when(passwordTooShort.and(password.isNotEmpty()))
                        .then(translateString("password.too.short."))
                        .otherwise(Bindings.when(usernameTooLong)
                                .then(translateString("username.too.long."))
                                .otherwise("")
                        )
        );
        BooleanBinding isInvalid = username
                .isEmpty()
                .or(passwordTooShort)
                .or(usernameTooLong);
        loginButton.disableProperty().bind(isInvalid);
        registerButton.disableProperty().bind(isInvalid);

        hidePassword();

        // toggle password
        toggleButton.setOnAction(event -> {
            if (passwordVisible) {
                hidePassword();
            } else {
                showPassword();
            }
            passwordVisible = !passwordVisible;
        });

        // disables all focused input fields, so you can see the input text placeholders
        FX_SCHEDULER.scheduleDirect(parent::requestFocus);

        //Show KAMIMON Logo
        Platform.runLater(() -> {
            loadImage(imageViewKamimonLettering, "kamimonLettering_new.png");
            imageViewKamimonLettering.setPreserveRatio(true);
        });

        return parent;
    }

    public void validateLoginAndRegistration() {
        errorText = new SimpleStringProperty("");
        errorLabel.textProperty().bind(errorText);
    }

    public void login() {
        validateLoginAndRegistration();
        loginWithCredentials(username.get(), password.get(), rememberMe.isSelected(), true);
    }

    private void loginWithCredentials(String username, String password, boolean rememberMe, boolean isRegistered) {
        disposables.add(authService
                .login(username, password, rememberMe)
                .observeOn(FX_SCHEDULER)
                .subscribe(lr -> {
                    errorText.set(translateString("login.successful"));
                    errorLabel.setTextFill(Color.GREEN);
                    if (isRegistered) {
                        app.show(hybridControllerProvider.get());
                    } else {
                        app.show(introductionControllerProvider.get());
                    }
                }, error -> errorText.set(getErrorMessage(error))));
    }

    public void register() {
        validateLoginAndRegistration();
        disposables.add(userService
                .addUser(username.get(), password.get())
                .observeOn(FX_SCHEDULER)
                .subscribe(user -> {
                    errorText.set(translateString("registration.successful"));
                    errorLabel.setTextFill(Color.GREEN);
                    // unbind because else the login successful will take precedence over registration successful
                    errorLabel.textProperty().unbind();
                    //Login
                    loginWithCredentials(user.name(), password.get(), rememberMe.isSelected(), false);
                }, error -> errorText.set(getErrorMessage(error))));
    }

    private String getErrorMessage(Throwable error) {
        try {
            throw error;
        } catch (UnknownHostException unknownHostException) {
            return translateString("no.internet.connection");
        } catch (HttpException httpException) {
            return handleHttpException(httpException);
        } catch (Throwable throwable) {
            return "undefined error";
        }
    }

    private String handleHttpException(HttpException exception) {
        return switch (exception.code()) {
            case 400 -> translateString("validation.failed");
            case 401 -> translateString("invalid.username.or.password");
            case 409 -> translateString("username.already.in.use");
            case 429 -> translateString("rate.limit.reached");
            default -> translateString("error");
        };
    }

    private void hidePassword() {
        toggleButton.getStyleClass().clear();
        toggleButton.getStyleClass().addAll("login-password-button", "login-password-button-visible");
        if (isEmpty) {
            password.set("");
        } else {
            password.set(tempPassword);
        }
        passwordInput.setPromptText(translateString("password"));
        passwordInput.setDisable(false);
    }

    private void showPassword() {
        toggleButton.getStyleClass().clear();
        toggleButton.getStyleClass().addAll("login-password-button", "login-password-button-invisible");
        tempPassword = password.get();
        if (tempPassword == null || tempPassword.isEmpty()) {
            tempPassword = translateString("password");
            isEmpty = true;
        } else {
            isEmpty = false;
        }
        password.set(""); // clears the bound input field
        passwordInput.setPromptText(tempPassword);
        passwordInput.setDisable(true);
    }

    @FXML
    public void setDe() {
        setLanguage(Locale.GERMAN);
    }

    @FXML
    public void setEn() {
        setLanguage(Locale.ENGLISH);
    }

    private void setLanguage(Locale locale) {
        preferences.put("locale", locale.toLanguageTag());
        app.show(this); //reloaded
    }

}