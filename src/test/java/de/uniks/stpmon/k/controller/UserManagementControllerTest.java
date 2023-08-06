package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.popup.ModalCallback;
import de.uniks.stpmon.k.controller.popup.PopUpController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.service.UserService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import retrofit2.HttpException;
import retrofit2.Response;

import javax.inject.Provider;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class UserManagementControllerTest extends ApplicationTest {

    @Mock
    UserService userService;
    @Mock
    Provider<HybridController> hybridControllerProvider;
    @Mock
    Provider<LoginController> loginControllerProvider;
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;
    @Mock
    Provider<PopUpController> popUpControllerProvider;
    @Spy
    final ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);

    @Spy
    final App app = new App(null);

    @Spy
    @InjectMocks
    UserManagementController userManagementController;

    final User dummyUser = new User("0", "Bob", "on", "av", null);


    @Override
    public void start(Stage stage) {
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        when(userService.getMe()).thenReturn(dummyUser);
        app.show(userManagementController);
        stage.requestFocus();
    }


    @Test
    void testBackToSettings() {
        // define mocks:
        final HybridController mock = Mockito.mock(HybridController.class);
        when(hybridControllerProvider.get()).thenReturn(mock);
        doNothing().when(mock).popTab();

        // action:
        clickOn("#backButton");

        // no values to check

        // check mocks:
        verify(mock).popTab();
    }

    @Test
    void testBackToSettingsWithUnsavedChanges() {
        // prep:
        final PopUpController popupMock = Mockito.mock(PopUpController.class);
        final HybridController hybridMock = Mockito.mock(HybridController.class);

        // define mocks:
        when(userManagementController.hasUnsavedChanges()).thenReturn(true);
        when(popUpControllerProvider.get()).thenReturn(popupMock);
        doAnswer(invocation -> {
            ModalCallback callback = invocation.getArgument(0);
            callback.onModalResult(true);
            return null;
        }).when(popupMock).showModal(any());
        when(hybridControllerProvider.get()).thenReturn(hybridMock);
        doNothing().when(hybridMock).popTab();

        // action:
        clickOn("#backButton");

        // no values to check

        // check mocks:
        verify(popupMock).showModal(any());
        verify(hybridMock).popTab();
    }

    @Test
    void testDeleteUser() {
        // prep:
        final PopUpController popupMock = Mockito.mock(PopUpController.class);
        final LoginController loginMock = Mockito.mock(LoginController.class);

        // define mocks:
        when(userService.deleteMe()).thenReturn(Observable.just(dummyUser));
        when(popUpControllerProvider.get()).thenReturn(popupMock);
        doAnswer(invocation -> {
            ModalCallback callback = invocation.getArgument(0);
            callback.onModalResult(true);
            return null;
        }).when(popupMock).showModal(any());
        doNothing().when(app).show(any(LoginController.class));
        when(loginControllerProvider.get()).thenReturn(loginMock);

        // action:
        clickOn("#deleteUserButton");

        // check possible values:

        // verify mocks:
        verify(popupMock, times(2)).showModal(any());
        verify(userService).deleteMe();
        verify(app).show(loginMock);
    }

    @Test
    void testSaveChangesUsername() {
        // prep:
        final PopUpController mock = Mockito.mock(PopUpController.class);
        final ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
        // User dummyUser = new User("1", "Bob", null, null, null);

        // define mocks:
        when(userService.setUsername(anyString())).thenReturn(Observable.just(dummyUser));
        when(popUpControllerProvider.get()).thenReturn(mock);
        doAnswer(invocation -> {
            ModalCallback callback = invocation.getArgument(0);
            callback.onModalResult(true);
            return null;
        }).when(mock).showModal(any());

        // action:
        write("\tBob");
        clickOn("#saveChangesButton");

        // check values:
        TextField usernameText = lookup("#usernameInput").queryAs(TextField.class);
        assertEquals("Bob", usernameText.getText());

        // check mocks:
        verify(userManagementController).saveChanges();
        verify(mock).showModal(any());
        verify(userService).setUsername(usernameCaptor.capture());
        assertEquals("Bob", usernameCaptor.getValue());
    }

    @Test
    void testSaveChangesPassword() {
        // prep:
        final PopUpController mock = Mockito.mock(PopUpController.class);
        final ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        // User dummyUser = new User("1", "Bob", null, null, null);

        // define mocks:
        when(userService.setPassword(any())).thenReturn(Observable.just(dummyUser));
        when(popUpControllerProvider.get()).thenReturn(mock);
        doAnswer(invocation -> {
            ModalCallback callback = invocation.getArgument(0);
            callback.onModalResult(true);
            return null;
        }).when(mock).showModal(any());

        // action:
        write("\t\tpassword");
        clickOn("#saveChangesButton");

        // check values:
        TextField passwordText = lookup("#passwordInput").queryAs(TextField.class);
        assertEquals("password", passwordText.getText());

        // check mocks:
        verify(userManagementController).saveChanges();
        verify(mock).showModal(any());
        verify(userService).setPassword(passwordCaptor.capture());
        assertEquals("password", passwordCaptor.getValue());
    }

    @Test
    void testSaveChangesUsernameError() {
        // prep:
        final PopUpController mock = Mockito.mock(PopUpController.class);

        Response<Object> response409 = Response.error(409, ResponseBody.create(null, "test"));
        Response<Object> response999 = Response.error(999, ResponseBody.create(null, "test"));
        Map<String, Throwable> errorMap = new HashMap<>();
        errorMap.put("error", new HttpException(response999));
        errorMap.put("Username is already in use", new HttpException(response409));

        for (Map.Entry<String, Throwable> entry : errorMap.entrySet()) {
            String expectedErrorMsg = entry.getKey();
            Throwable error = entry.getValue();

            // define mocks:
            when(popUpControllerProvider.get()).thenReturn(mock);
            when(userService.setUsername(anyString())).thenReturn(Observable.error(error));
            doAnswer(invocation -> {
                ModalCallback callback = invocation.getArgument(0);
                callback.onModalResult(true);
                return null;
            }).when(mock).showModal(any());

            // action:
            write("\tBob");
            clickOn("#saveChangesButton");
            waitForFxEvents();

            // check values:
            Label errorLabel = lookup("#usernameInfo").queryAs(Label.class);
            assertEquals(expectedErrorMsg, errorLabel.getText());
        }

        // verify mocks:
        verify(userManagementController, times(2)).saveChanges();
        verify(mock, times(2)).showModal(any());
        verify(userService, times(2)).setUsername(any());
    }

    @Test
    void testSaveChangesPasswordError() {
        // prep:
        final PopUpController mock = Mockito.mock(PopUpController.class);

        // define mocks:
        when(popUpControllerProvider.get()).thenReturn(mock);
        when(userService.setPassword(anyString())).thenReturn(Observable.error(new Exception()));
        doAnswer(invocation -> {
            ModalCallback callback = invocation.getArgument(0);
            callback.onModalResult(true);
            return null;
        }).when(mock).showModal(any());

        // action:
        write("\t\tpassword");
        clickOn("#saveChangesButton");

        // check values:
        Label errorLabel = lookup("#passwordInfo").queryAs(Label.class);
        assertEquals("Error", errorLabel.getText());

        // verify mocks:
        verify(userManagementController).saveChanges();
        verify(mock).showModal(any());
        verify(userService).setPassword(any());
    }

}
