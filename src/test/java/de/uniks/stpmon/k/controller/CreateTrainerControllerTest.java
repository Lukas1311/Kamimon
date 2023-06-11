package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.popup.ModalCallback;
import de.uniks.stpmon.k.controller.popup.PopUpController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.MainWindow;
import de.uniks.stpmon.k.models.NPCInfo;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.world.WorldLoader;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateTrainerControllerTest extends ApplicationTest {
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;

    @Spy
    App app = new App(null);
    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);

    @Mock
    RegionService regionService;
    @Mock
    Provider<PopUpController> popUpControllerProvider;
    @Mock
    @SuppressWarnings("unused")
    Provider<IngameController> ingameControllerProvider;
    @Mock
    Provider<HybridController> hybridControllerProvider;
    @Mock
    @SuppressWarnings("unused")
    RegionStorage regionStorage;
    @Mock
    WorldLoader worldLoader;

    @Spy
    @InjectMocks
    CreateTrainerController createTrainerController;

    Region dummyRegion = new Region("1", "r", null, null);

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);

        createTrainerController.setChosenRegion(dummyRegion);

        app.show(createTrainerController);
        stage.requestFocus();
    }

    /**
     * Verify the functionality of a graphical user interface (GUI) related to creating trainers and sprites.
     */
    @Test
    public void testGUI() {
        clickOn("#createTrainerInput");
        write("Tom\t");

        Button createSprite = lookup("#createSpriteButton").query();
        assertNotNull(createSprite);
        assertEquals("Create Sprite", createSprite.getText());

        Button createTrainer = lookup("#createTrainerButton").query();
        assertNotNull(createTrainer);
        assertEquals("Create Trainer", createTrainer.getText());
    }

    @Test
    public void testCreateTrainer() {
        // prep.
        NPCInfo npcInfo = new NPCInfo(false);
        Trainer dummyTrainer = new Trainer("1", "r", "0", "n", "i.png", 0, "0", 0, 0, 0, npcInfo);
        final PopUpController popupMock = Mockito.mock(PopUpController.class);

        // define mocks:
        when(regionService.createTrainer(anyString(), anyString(), anyString())).thenReturn(Observable.just(dummyTrainer));
        when(worldLoader.tryEnterRegion(any())).thenReturn(Observable.empty());
        when(popUpControllerProvider.get()).thenReturn(popupMock);
        doAnswer(invocation -> {
            ModalCallback callback = invocation.getArgument(0);
            callback.onModalResult(true);
            return null;
        }).when(popupMock).showModal(any());

        // action:
        write("\tTom");
        clickOn("#createTrainerButton");

        // verify mocks:
        verify(createTrainerController).createTrainer();
        verify(popupMock).showModal(any());
        verify(regionService).createTrainer("1", "Tom", "Premade_Character_01.png");
        verify(worldLoader).tryEnterRegion(any());
    }

    @Test
    public void testCreateTrainerInvalid() {

        HybridController hybridMock = Mockito.mock(HybridController.class);
        PopUpController popupMock = Mockito.mock(PopUpController.class);

        write("\t");
        write("Nom".repeat(11));

        Button createTrainerButton = lookup("#createTrainerButton").queryButton();
        assertTrue(createTrainerButton.isDisabled());
        Label trainerNameInfo = lookup("#trainerNameInfo").queryAs(Label.class);
        assertEquals("Trainer name too long.", trainerNameInfo.getText());

        clickOn(createTrainerButton);
        verifyNoInteractions(popupMock, hybridMock);
    }

    @Test
    public void createSprite() {

    }

    @Test
    void testCloseWindow() {
        // define mocks:
        HybridController hybridMock = Mockito.mock(HybridController.class);
        when(hybridControllerProvider.get()).thenReturn(hybridMock);

        // action:
        clickOn("#closeButton");

        // values to check:

        // verify mocks:
        verify(createTrainerController).closeWindow();
        verify(hybridMock).openMain(MainWindow.LOBBY);
    }
}
