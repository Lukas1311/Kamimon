package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.popup.ModalCallback;
import de.uniks.stpmon.k.controller.popup.PopUpController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.MainWindow;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.TrainerService;
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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateTrainerControllerTest extends ApplicationTest {

    @Mock
    Provider<ResourceBundle> resourceBundleProvider;

    @Spy
    final App app = new App(null);
    @Spy
    final ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);

    @Mock
    @SuppressWarnings("unused")
    RegionService regionService;
    @Mock
    @SuppressWarnings("unused")
    Provider<PopUpController> popUpControllerProvider;
    @Mock
    Provider<HybridController> hybridControllerProvider;
    @Mock
    PresetService presetService;
    @Mock
    @SuppressWarnings("unused")
    TrainerService trainerService;
    @Mock
    @SuppressWarnings("unused")
    Preferences preferences;

    @Spy
    @InjectMocks
    CreateTrainerController createTrainerController;
    @Mock
    WorldLoader worldLoader;
    @Spy
    @SuppressWarnings("unused")
    EffectContext effectContext = new EffectContext().setSkipLoadImages(true);

    final Region dummyRegion = new Region("1", "r", null, null);

    @Override
    public void start(Stage stage) {
        app.start(stage);

        when(resourceBundleProvider.get()).thenReturn(resources);
        final Observable<List<String>> characterList = Observable.just(List.of("Sprite1", "Sprite2", "Sprite3"));
        when(presetService.getCharacters()).thenReturn(characterList);

        createTrainerController.setChosenRegion(dummyRegion);
        createTrainerController.setRandomSeed(12345);

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

        Button spriteLeft = lookup("#spriteLeft").query();
        assertNotNull(spriteLeft);

        Button spriteRight = lookup("#spriteRight").query();
        assertNotNull(spriteRight);

        Button createTrainer = lookup("#createTrainerButton").query();
        assertNotNull(createTrainer);
        assertEquals("Create Trainer", createTrainer.getText());
    }

    @Test
    public void testCreateTrainer() {
        // prep.
        Trainer dummyTrainer = TrainerBuilder.builder().setId(1).setRegion("r").setName("n").setImage("i.png").create();
        final PopUpController popupMock = Mockito.mock(PopUpController.class);

        // define mocks:

        when(regionService.createTrainer(anyString(), anyString(), anyString())).thenReturn(Observable.just(dummyTrainer));
        when(trainerService.setImage(anyString())).thenReturn(Observable.empty());
        when(worldLoader.tryEnterRegion(any())).thenReturn(Observable.empty());
        // when(preferences.getInt(anyString(), anyInt())).thenReturn(0);
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
        verify(regionService).createTrainer("1", "Tom", "Sprite2");
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

    // -------------------------- Choose Sprite -------------------------- //

    @Test
    public void testLoadSpriteList() {
        createTrainerController.loadSpriteList();
        assertEquals(3, createTrainerController.characters.size());
    }

    @Test
    public void testGetSprites() {
        createTrainerController.characters.clear();

        List<String> charactersList = Arrays.asList("Sprite1", "Sprite2", "Sprite3");

        createTrainerController.getCharactersList(charactersList);

        assertEquals(3, createTrainerController.characters.size());

        // Check the previous sprite character
        createTrainerController.currentSpriteIndex = 1;
        createTrainerController.toLeft();

        assertEquals(0, createTrainerController.currentSpriteIndex);

        // Check the next sprite character
        createTrainerController.toRight();

        assertEquals(1, createTrainerController.currentSpriteIndex);
    }

    @Test
    public void testSaveSprite() {
        // Mock the necessary methods
        final PopUpController popupMock = Mockito.mock(PopUpController.class);
        Trainer dummyTrainer = TrainerBuilder.builder().setId(1).setRegion("r").setName("n").setImage("i.png").create();

        clickOn("#createTrainerInput");
        write("Tom\t");


        createTrainerController.characters.addAll("Sprite1", "Sprite2", "Sprite3");
        createTrainerController.currentSpriteIndex = 0;

        when(regionService.createTrainer(anyString(), anyString(), anyString())).thenReturn(Observable.just(dummyTrainer));
        when(worldLoader.tryEnterRegion(any())).thenReturn(Observable.empty());
        when(popUpControllerProvider.get()).thenReturn(popupMock);
        doAnswer(invocation -> {
            ModalCallback callback = invocation.getArgument(0);
            callback.onModalResult(true);
            return null;
        }).when(popupMock).showModal(any());

        when(trainerService.setImage(anyString())).thenReturn(Observable.empty());

        // Test the saveSprite() method
        clickOn("#createTrainerButton");

        // Verify that the preferences were updated
        assertEquals(0, createTrainerController.preferences.getInt("currentSpriteIndex", createTrainerController.currentSpriteIndex));
        verify(trainerService).setImage("Sprite1");
        verify(regionService).createTrainer("1", "Tom", "Sprite1");
        verify(worldLoader).tryEnterRegion(any());
    }


}
