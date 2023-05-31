package de.uniks.stpmon.k.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


import java.util.ResourceBundle;
import java.util.Locale;

import javax.inject.Provider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.popup.*;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.service.UserService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);

    @Spy
    App app = new App(null);

    @Spy
    @InjectMocks
    UserManagementController userManagementController;

    User dummyUser = new User("0", "Bob", "on", "av", null);


    @Override
    public void start(Stage stage) throws Exception {
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
        verify(mock).showModal(any());
        verify(userService).setPassword(passwordCaptor.capture());
        assertEquals("password", passwordCaptor.getValue());
    }
}
