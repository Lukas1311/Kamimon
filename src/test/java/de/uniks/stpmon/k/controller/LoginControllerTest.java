package de.uniks.stpmon.k.controller;

import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
// import static org.mockito.Mockito.verify;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.doNothing;
// import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.dto.LoginResult;
import de.uniks.stpmon.k.service.AuthenticationService;
import de.uniks.stpmon.k.service.NetworkAvailability;
import de.uniks.stpmon.k.service.TokenStorage;
import de.uniks.stpmon.k.service.UserService;

import io.reactivex.rxjava3.core.Observable;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javax.inject.Provider;



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
    App app = new App(null);

    @InjectMocks
    LoginController loginController;
    

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        app.show(loginController);
    }


    @Test
    void testLogin() {
        when(authService.login(any(), any(), eq(false))).thenReturn(Observable.just(
            new LoginResult(null, null, null, null, null, "a", "r")
        ));

        final HybridController mock = Mockito.mock(HybridController.class);
        when(hybridControllerProvider.get()).thenReturn(mock);
        doNothing().when(app).show(mock);

        write("\tstring\t");
        write("stringst\t\t\t");
        // clickOn("#loginButton");
        //clickOn("#loginButton")
        press(KeyCode.ENTER);
        release(KeyCode.ENTER);



        verify(app).show(mock);
    }

    @Test
    void testRegister() {

    }
}
