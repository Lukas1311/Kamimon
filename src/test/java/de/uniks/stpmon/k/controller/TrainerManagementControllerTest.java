package de.uniks.stpmon.k.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.inject.Provider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.service.RegionService;
import javafx.stage.Stage;

@ExtendWith(MockitoExtension.class)
public class TrainerManagementControllerTest extends ApplicationTest {

    @Mock
    RegionService regionService;
    @Mock
    Provider<HybridController> hybridControllerProvider;
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;
    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);

    @Spy
    App app = new App(null);

    @InjectMocks
    TrainerManagementController trainerManagementController;

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        app.show(trainerManagementController);
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
    void testChangeTrainerName() {
        // TODO: region service call
    }

    @Test
    void testDeleteTrainer() {
        // TODO: region service call
    }

    @Test
    void testOpenTrainerSpriteEditor() {

    }

    @Test
    void testSaveChanges() {

    }
}
