package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.SidebarTab;
import de.uniks.stpmon.k.models.NPCInfo;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.TrainerService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.UserStorage;
import io.reactivex.rxjava3.core.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SettingsControllerTest extends ApplicationTest {

    @Mock
    TrainerStorage trainerStorage;
    @Spy
    App app = new App(null);
    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;
    @Mock
    Provider<HybridController> hybridControllerProvider;
    @Mock
    UserStorage userStorage;
    @Mock
    @SuppressWarnings("unused")
    TrainerService trainerService;
    @InjectMocks
    SettingsController settingsController;

    @Mock
    RegionStorage regionStorage;
    @Mock
    PresetService presetService;

    @Override

    public void start(Stage stage) throws Exception {
        // set trainer
        NPCInfo npcInfo = new NPCInfo(false);
        Trainer trainer = new Trainer(
                "1", "RegionA", "TestUser", "Bob", "0", 0, "0", 0, 0, 0, npcInfo);
        lenient().when(trainerStorage.getTrainer()).thenReturn(trainer);
        when(trainerStorage.onTrainer()).thenReturn(Observable.just(trainer));
        when(regionStorage.getRegion()).thenReturn(DummyConstants.REGION);
        // set user
        when(userStorage.getUser()).thenReturn(new User("1", "TestUser", "1", "1", new ArrayList<>()));
        when(trainerStorage.getTrainerLoaded()).thenReturn(new SimpleBooleanProperty(true));
        when(presetService.getCharacterFile(anyString())).thenReturn(Observable.empty());
        // show app
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        app.show(settingsController);
        stage.requestFocus();


    }

    @Test
    public void checkTrainer() {

        assertEquals("TestUser", trainerStorage.getTrainer().user());
        assertEquals("RegionA", trainerStorage.getTrainer().region());
        assertEquals("Bob", trainerStorage.getTrainer().name());
    }

    @Test
    public void backButton() {
        final HybridController mock = Mockito.mock(HybridController.class);
        when(hybridControllerProvider.get()).thenReturn(mock);
        doNothing().when(mock).forceTab(SidebarTab.SETTINGS);

        clickOn("#backButton");
        verify(mock).forceTab(SidebarTab.SETTINGS);
    }

    @Test
    public void editUser() {
        final HybridController mock = Mockito.mock(HybridController.class);
        when(hybridControllerProvider.get()).thenReturn(mock);
        doNothing().when(mock).pushTab(SidebarTab.USER_MANAGEMENT);

        clickOn("#editUserButton");
        verify(mock).pushTab(SidebarTab.USER_MANAGEMENT);
    }

    @Test
    public void editTrainer() {
        final HybridController mock = Mockito.mock(HybridController.class);
        when(hybridControllerProvider.get()).thenReturn(mock);
        doNothing().when(mock).pushTab(SidebarTab.TRAINER_MANAGEMENT);

        clickOn("#editTrainerButton");
        verify(mock).pushTab(SidebarTab.TRAINER_MANAGEMENT);
    }
}
