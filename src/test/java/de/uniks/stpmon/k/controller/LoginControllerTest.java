package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.LoginResult;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.service.AuthenticationService;
import de.uniks.stpmon.k.service.NetworkAvailability;
import de.uniks.stpmon.k.service.UserService;
import de.uniks.stpmon.k.service.storage.TokenStorage;

import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import javax.inject.Provider;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;


@ExtendWith(MockitoExtension.class)
public class LoginControllerTest extends ApplicationTest {
    @Mock
    AuthenticationService authService;
    @Mock
    TokenStorage tokenStorage;
    @Mock
    NetworkAvailability netAvailability;
    @Mock
    Provider<HybridController> hybridControllerProvider;
    @Mock
    UserService userService;
    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;

    @Spy
    App app = new App(null);

    @Spy
    @InjectMocks
    LoginController loginController;
    @Mock
    Preferences preferences;


    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        when(preferences.get(anyString(), anyString())).thenReturn("en");
        app.show(loginController);
        stage.requestFocus();
    }


    @Test
    void testLogin() {
        when(authService.login(any(), any(), eq(false))).thenReturn(Observable.just(
                new LoginResult(null, null, null, null, null, "a", "r")
        ));

        final HybridController mock = Mockito.mock(HybridController.class);
        when(hybridControllerProvider.get()).thenReturn(mock);
        doNothing().when(app).show(mock);

        // write username and password
        write("\tstring\t");
        write("stringst");
        // tab 3 times to go on the login button -> is faster than click on button (no mouse movement)
        write("\t\t\t");
        Button selectedButton = lookup("#loginButton").queryButton();
        assertThat(selectedButton).isFocused();
        press(KeyCode.ENTER).release(KeyCode.ENTER);

        Label label = lookup("#errorLabel").queryAs(Label.class);
        verifyThat(label, LabeledMatchers.hasText("Login successful"));
        verify(app).show(mock);
    }

    @Test
    void testRegister() {
        User bob = new User(null, "Bob", null, null, null);
        when(userService.addUser(any(), any())).thenReturn(Observable.just(
                bob
        ));

        // tab into username input field
        write("\t");
        // type username and tab into password field
        write("Bob\t");
        // type password
        write("password");
        // tab 3 times to go on the login button -> is faster than click on button (no mouse movement)
        write("\t\t\t\t");
        // Retrieve the currently selected button
        Button selectedButton = lookup("#registerButton").queryAs(Button.class);
        assertThat(selectedButton).isFocused();
        press(KeyCode.ENTER).release(KeyCode.ENTER);

        Label label = lookup("#errorLabel").queryAs(Label.class);
        verifyThat(label, LabeledMatchers.hasText("Registration successful"));
        // app stays in login controller after registration call, only afterwards it will login into hybridController
        verify(app).show(loginController);
    }

    @Test
    void testPasswordTooShort() {
        Label label = lookup("#errorLabel").queryAs(Label.class);

        // tab into password input field
        write("\t\t");
        // type password that is too short (< 8 chars)
        write("string");
        verifyThat(label, LabeledMatchers.hasText("Password too short."));
    }

    @Test
    void testUsernameTooLong() {
        waitForFxEvents();
        Label label = lookup("#errorLabel").queryAs(Label.class);

        // tab into username input field
        write("\t");
        // type username that is too long (> 32 chars)
        write("stringstringstringstringstringstring"); // 36 chars
        verifyThat(label, LabeledMatchers.hasText("Username too long."));
    }

    @Test
    void testShowPassword() {
        Button pwdToggleButton = lookup("#toggleButton").queryButton();
        PasswordField pwdField = lookup("#passwordInput").queryAs(PasswordField.class);
        // tab into password field
        // tab to the toggle button password field is empty
        write("\t\t\t");
        assertThat(pwdToggleButton).isFocused();
        press(KeyCode.ENTER);
        assertThat(pwdField.getPromptText()).isEqualTo("Password");
        release(KeyCode.ENTER);
        // tab back to password field
        press(KeyCode.SHIFT).press(KeyCode.TAB).release(KeyCode.TAB).release(KeyCode.SHIFT);
        write("stringst");
        // click show password button and verify the show password
        write("\t");
        press(KeyCode.ENTER);
        waitForFxEvents(); // not really necessary i guess
        // get password input field to verify the contents

        // check if prompt text matches the password that was written into password field before
        assertThat(pwdField.getPromptText()).isEqualTo("stringst");
        release(KeyCode.ENTER);
    }

    @Test
    void testChoseLanguage() {
        // // prep:
        // TextField usernameField = lookup("#usernameInput").queryAs(TextField.class);
        // RadioButton enButton = lookup("#englishButton").queryAs(RadioButton.class);
        // RadioButton deButton = lookup("#germanButton").queryAs(RadioButton.class);
        // assertTrue(enButton.isSelected());
        // assertThat(usernameField.getPromptText()).isEqualTo("Username");
        // // define mocks:
        // when(resourceBundleProvider.get()).thenReturn(resources);
        // when(preferences.get(anyString(), anyString())).thenReturn("de");

        // doAnswer(invocation -> {
        //     System.out.println("es gibt kuchen!");
        //     preferences.put("locale", "de");
        //     resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.forLanguageTag("de"));
        //     resourceBundleProvider.get();
        //     return null;
        // }).when(loginController).setDe();

        // // action: chose the DE button
        // write("\t".repeat(5));
        // press(KeyCode.LEFT).release(KeyCode.LEFT);
        // assertTrue(deButton.isSelected());
        // press(KeyCode.ENTER).release(KeyCode.ENTER);

        // // check values:
        // assertThat(usernameField.getPromptText()).isEqualTo("Benutzername");

        // // verify mock:
        // verify(loginController).setDe();
    }
    
    @Test
    void testGetErrorMessage() {
        // prep:
        Map<String, Integer> errorMap = new HashMap<>();
        errorMap.put("Validation failed", 400);
        errorMap.put("Invalid username or password", 401);
        errorMap.put("Username was already taken", 409);
        errorMap.put("Rate limit reached", 429);

        for (Map.Entry<String, Integer> entry : errorMap.entrySet()) {
            String expectedErrorMsg = entry.getKey();
            int statusCode = entry.getValue();

            Response<Object> response = Response.error(statusCode, ResponseBody.create(null, "test"));

            // define mocks:
            when(authService.login(any(), any(), anyBoolean())).thenReturn(Observable.error(new HttpException(response)));

            // action:
            Platform.runLater(() -> {
                loginController.login();
            });
            waitForFxEvents();

            // check error label text
            Label label = lookup("#errorLabel").queryAs(Label.class);
            verifyThat(label, LabeledMatchers.hasText(expectedErrorMsg));
        };

        // test has to be verified 4 times because we check 4 error codes
        verify(authService, times(4)).login(any(), any(), anyBoolean());
    }
}
