package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.LoginResult;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.service.AuthenticationService;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.UserService;
import de.uniks.stpmon.k.service.storage.TokenStorage;
import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import retrofit2.HttpException;
import retrofit2.Response;

import javax.inject.Provider;
import java.net.UnknownHostException;
import java.util.*;
import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest extends ApplicationTest {

    @Mock
    AuthenticationService authService;
    @Mock
    @SuppressWarnings("unused")
    TokenStorage tokenStorage;
    @Mock
    Provider<HybridController> hybridControllerProvider;
    @Mock
    Provider<IntroductionController> introductionControllerProvider;
    @Mock
    UserService userService;
    @Spy
    final ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;
    @Spy
    @SuppressWarnings("unused")
    EffectContext effectContext = new EffectContext().setSkipLoadImages(true);

    @Spy
    final App app = new App(null);

    @Spy
    @InjectMocks
    LoginController loginController;
    @Mock
    Preferences preferences;


    @Override
    public void start(Stage stage) {
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        when(preferences.get(anyString(), anyString())).thenReturn("en");
        app.show(loginController);
        stage.requestFocus();
    }


    @Test
    void testLogin() {
        // define mocks:
        when(authService.login(any(), any(), eq(false))).thenReturn(Observable.just(
                new LoginResult(null, null, null, null, null, "a", "r")
        ));
        final HybridController mock = Mockito.mock(HybridController.class);
        when(hybridControllerProvider.get()).thenReturn(mock);
        doNothing().when(app).show(mock);

        // action:
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

        // verify mocks:
        verify(authService).login(any(), any(), anyBoolean());
        verify(app).show(mock);
    }

    @Test
    void testRegister() {
        // prep:
        User bob = new User("1", "bob", "o", "a", new ArrayList<>(List.of("f")));
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        // define mocks:
        final IntroductionController introMock = Mockito.mock(IntroductionController.class);
        when(introductionControllerProvider.get()).thenReturn(introMock);
        when(userService.addUser(any(), any())).thenReturn(Observable.just(bob));
        when(authService.login(any(), any(), anyBoolean())).thenReturn(Observable.just(new LoginResult(null, null, null, null, null, null, null)));
        doNothing().when(app).show(introMock);

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
        waitForFxEvents();

        Label label = lookup("#errorLabel").queryAs(Label.class);
        verifyThat(label, LabeledMatchers.hasText("Registration successful"));

        verify(userService).addUser(captor.capture(), any());
        assertEquals("Bob", captor.getValue());
        verify(authService).login(captor.capture(), any(), anyBoolean());
        assertEquals("bob", captor.getValue());
        verify(introductionControllerProvider).get();
        verify(app).show(introMock);
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
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        waitForFxEvents();
        assertThat(pwdField.getPromptText()).isEqualTo("Password");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        waitForFxEvents();
        // tab back to password field
        press(KeyCode.SHIFT).type(KeyCode.TAB).release(KeyCode.SHIFT);
        write("stringst");
        // click show password button and verify the show password
        write("\t");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        waitForFxEvents(); // not really necessary I guess
        // get password input field to verify the contents

        // check if prompt text matches the password that was written into password field before
        assertThat(pwdField.getPromptText()).isEqualTo("stringst");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        waitForFxEvents();
    }

    @Test
    void testChoseLanguage() {
        // prep:
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        TextField usernameField = lookup("#usernameInput").queryAs(TextField.class);
        RadioButton enButton = lookup("#englishButton").queryAs(RadioButton.class);
        RadioButton deButton = lookup("#germanButton").queryAs(RadioButton.class);
        assertTrue(enButton.isSelected());
        // check that language is english first
        assertThat(usernameField.getPromptText()).isEqualTo("Username");
        // define mocks:
        doNothing().when(preferences).put(eq("locale"), captor.capture());
        doNothing().when(app).show(loginController);

        // action: chose the DE button
        write("\t".repeat(5));
        press(KeyCode.LEFT).release(KeyCode.LEFT);
        assertTrue(deButton.isSelected());
        press(KeyCode.ENTER).release(KeyCode.ENTER);

        // verify mock:
        verify(preferences).put("locale", captor.getValue());
        assertEquals("de", captor.getValue());

        // action: chose the EN button
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        assertTrue(enButton.isSelected());

        press(KeyCode.ENTER).release(KeyCode.ENTER);

        // verify mock:
        verify(preferences).put("locale", captor.getValue());
        assertEquals("en", captor.getValue());

        verify(loginController).setDe();
        verify(loginController).setEn();
    }

    @Test
    void testLoginWithoutInternetConnection() {
        // define mocks:
        when(authService.login(any(), any(), anyBoolean())).thenReturn(Observable.error(new UnknownHostException()));

        // action:
        Platform.runLater(() -> loginController.login());
        waitForFxEvents();

        // check error label text
        Label label = lookup("#errorLabel").queryAs(Label.class);
        verifyThat(label, LabeledMatchers.hasText("No internet connection"));

        // test has to be verified 5 times because we check 5 error codes
        verify(authService, times(1)).login(any(), any(), anyBoolean());
    }

    @Test
    void testGetErrorMessage() {
        // prep:
        Map<String, Integer> errorMap = new HashMap<>();
        errorMap.put("Validation failed", 400);
        errorMap.put("Invalid username or password", 401);
        errorMap.put("Username is already in use", 409);
        errorMap.put("Rate limit reached", 429);
        errorMap.put("Error", 999); // for default case

        for (Map.Entry<String, Integer> entry : errorMap.entrySet()) {
            String expectedErrorMsg = entry.getKey();
            int statusCode = entry.getValue();

            Response<Object> response = Response.error(statusCode, ResponseBody.create(null, "test"));

            // define mocks:
            when(authService.login(any(), any(), anyBoolean())).thenReturn(Observable.error(new HttpException(response)));

            // action:
            Platform.runLater(() -> loginController.login());
            waitForFxEvents();

            // check error label text
            Label label = lookup("#errorLabel").queryAs(Label.class);
            verifyThat(label, LabeledMatchers.hasText(expectedErrorMsg));
        }

        // test has to be verified 5 times because we check 5 error codes
        verify(authService, times(5)).login(any(), any(), anyBoolean());
    }

}
