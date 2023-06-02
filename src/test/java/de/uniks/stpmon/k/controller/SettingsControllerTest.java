package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.models.NPCInfo;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.service.TrainerService;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.UserStorage;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
    UserStorage userStorage;
    @Mock
    @SuppressWarnings("unused")
    TrainerService trainerService;

    @InjectMocks
    SettingsController settingsController;

    @Override
    public void start(Stage stage) throws Exception {
        // set trainer
        NPCInfo npcInfo = new NPCInfo(false);
        Trainer trainer = new Trainer(
                "1", "RegionA", "TestUser", "Bob", "0", 0, "0", 0, 0, 0, npcInfo);
        when(trainerStorage.getTrainer()).thenReturn(trainer);

        // set user
        when(userStorage.getUser()).thenReturn(new User("1", "TestUser", "1", "1", new ArrayList<>()));

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
}
