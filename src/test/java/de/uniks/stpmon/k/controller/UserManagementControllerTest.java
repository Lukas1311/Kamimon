package de.uniks.stpmon.k.controller;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ResourceBundle;
import java.util.Locale;

import javax.inject.Provider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.service.UserService;
import io.reactivex.rxjava3.core.Observable;
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
    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);

    @Spy
    App app = new App(null);

    @InjectMocks
    UserManagementController userManagementController;

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        app.show(userManagementController);
        stage.requestFocus();
    }


    @Test
    void testBackToSettings() {
        // define mocks:
        final HybridController mock = Mockito.mock(HybridController.class);
        when(hybridControllerProvider.get()).thenReturn(mock);
        doNothing().when(mock).forceTab(any());

        // action:
        clickOn("#backButton");

        // no values to check

        // check mocks:
        verify(mock).forceTab(any());
    }

    @Test
    void testDeleteUser() {
        final LoginController mock = Mockito.mock(LoginController.class);
        User userMock = new User("0", "a", "o", "a", null);
        when(userService.deleteMe()).thenReturn(Observable.just(userMock));

        clickOn("#deleteUserButton");

        verify(userService).deleteMe();
    }

    @Test
    void testSaveChanges() {

    }
}
