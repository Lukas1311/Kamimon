package de.uniks.stpmon.k.controller;


import de.uniks.stpmon.k.service.AuthenticationService;
import de.uniks.stpmon.k.service.NetworkAvailability;
import de.uniks.stpmon.k.service.TokenStorage;
import de.uniks.stpmon.k.service.UserService;
import de.uniks.stpmon.k.views.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import retrofit2.HttpException;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends Controller implements Initializable {

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
    private Label shownPassword;

    @FXML
    private ToggleButton toggleButton;

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
    @FXML
    private Button actionButton = new Button("View");

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

        //showPassword();

        return parent;
    }

    private void showPassword() {
        // Erstellen des ImageView-Objekts für das Passwort Masken Icon
        Image passwordMaskImage = new Image("password-mask.png");
        ImageView passwordMaskImageView = new ImageView(passwordMaskImage);

        // Erstellen eines HBox-Containers, um das Passwortfeld und das ImageView-Objekt zu platzieren
        HBox passwordBox = new HBox();
        passwordBox.getChildren().addAll(passwordInput, passwordMaskImageView);
        passwordBox.setSpacing(10);

        // Erstellen eines VBox-Containers, um das HBox-Objekt zu platzieren
        VBox vbox = new VBox();
        vbox.getChildren().add(passwordBox);
        vbox.setPadding(new Insets(10));
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
        loginWithCredentials(usernameInput.getText(), passwordInput.getText());
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
                    loginWithCredentials(user.name(), passwordInput.getText());
                }, error -> {
                    errorText.set(getErrorMessage(error));
                    System.out.println("look here for the error: " + error);
                }));
    }

    private void loginWithCredentials(String username, String password){
        disposables.add(authService
                .login(username, password)
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
    void toggleButton(ActionEvent event) {
        if(toggleButton.isSelected()) {
            shownPassword.setVisible(true);
            shownPassword.textProperty().bind(Bindings.concat(passwordInput.getText()));
            toggleButton.setText("Hide");
        }else {
            shownPassword.setVisible(false);
            toggleButton.setText("Show");
        }
    }

    @FXML
    void passwordFieldKeyTyped(KeyEvent event) {
        shownPassword.textProperty().bind(Bindings.concat(passwordInput.getText()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        shownPassword.setVisible(false);
    }
}
