package de.uniks.stpmon.k.controller;

import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import static org.testfx.assertions.api.Assertions.assertThat;
import static org.testfx.api.FxAssert.verifyThat;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.dto.LoginResult;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.service.AuthenticationService;
import de.uniks.stpmon.k.service.NetworkAvailability;
import de.uniks.stpmon.k.service.TokenStorage;
import de.uniks.stpmon.k.service.UserService;

import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javax.inject.Provider;
import io.reactivex.rxjava3.core.Observable;



@ExtendWith(MockitoExtension.class)
public class IngameControllerTest extends ApplicationTest {

    @Spy
    App app = new App(null);

    @InjectMocks
    IngameController ingameController;

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        app.show(ingameController);
        stage.requestFocus();
    }
}
