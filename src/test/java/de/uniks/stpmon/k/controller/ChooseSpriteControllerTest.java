package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.popup.ModalCallback;
import de.uniks.stpmon.k.controller.popup.PopUpController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.TrainerService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import okhttp3.ResponseBody;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChooseSpriteControllerTest extends ApplicationTest {

    @Spy
    App app = new App(null);

    @InjectMocks
    ChooseSpriteController chooseSpriteController;
    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;
    @Mock
    PresetService presetService;
    @Mock
    Preferences preferences;
    @Mock
    Provider<PopUpController> popUpControllerProvider;
    @Mock
    @SuppressWarnings("unused")
    Provider<CreateTrainerController> createTrainerControllerProvider;
    @Mock
    RegionStorage regionStorage;

    @Mock
    Provider<HybridController> hybridControllerProvider;

    @Mock
    TrainerService trainerService;


    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        final Observable<List<String>> characterList = Observable.just(List.of("Sprite1", "Sprite2", "Sprite3"));

        when(resourceBundleProvider.get()).thenReturn(resources);
        when(preferences.getInt(anyString(), anyInt())).thenReturn(0);


        when(presetService.getCharacters()).thenReturn(characterList);
        app.show(chooseSpriteController);
        stage.requestFocus();
    }

    @Test
    public void testGUI() {
        Text chooseTrainer = lookup("#chooseTrainer").query();
        assertNotNull(chooseTrainer);
        assertEquals("Choose your trainer!", chooseTrainer.getText());

        Button saveSprite = lookup("#saveSprite").query();
        assertNotNull(saveSprite);
        assertEquals("Save changes", saveSprite.getText());

        Button spriteLeft = lookup("#spriteLeft").query();
        assertNotNull(spriteLeft);

        Button spriteRight = lookup("#spriteRight").query();
        assertNotNull(spriteRight);
    }

    @Test
    public void testLoadSpriteList() {
        chooseSpriteController.loadSpriteList();
        assertEquals(3, chooseSpriteController.characters.size());
    }

    @Test
    public void testGetSprites() {
        chooseSpriteController.characters.clear();

        List<String> charactersList = Arrays.asList("Sprite1", "Sprite2", "Sprite3");

        when(presetService.getCharacterFile(anyString())).thenReturn(Observable.just(mock(ResponseBody.class)));

        chooseSpriteController.getCharactersList(charactersList);

        assertEquals(3, chooseSpriteController.characters.size());

        // Check the previous sprite character
        chooseSpriteController.currentSpriteIndex = 1;
        chooseSpriteController.toLeft();

        assertEquals(0, chooseSpriteController.currentSpriteIndex);

        // Check the next sprite character
        chooseSpriteController.toRight();

        assertEquals(1, chooseSpriteController.currentSpriteIndex);

    }


    @Test
    public void testSaveSprite() {
        // Mock the necessary methods
        final PopUpController popupMock = Mockito.mock(PopUpController.class);
        final HybridController hybridController = Mockito.mock(HybridController.class);

        chooseSpriteController.setCreationMode(false);
        chooseSpriteController.characters.addAll("Sprite1", "Sprite2", "Sprite3");
        chooseSpriteController.currentSpriteIndex = 0;
        chooseSpriteController.previousSpriteIndex = 1;
        chooseSpriteController.chooseTrainerContent = new BorderPane();

        when(popUpControllerProvider.get()).thenReturn(popupMock);
        doAnswer(invocation -> {
            ModalCallback callback = invocation.getArgument(0);
            callback.onModalResult(true);
            return null;
        }).when(popupMock).showModal(any());

        when(hybridControllerProvider.get()).thenReturn(hybridController);
        doNothing().when(hybridController).popTab();

        when(trainerService.setImage(anyString())).thenReturn(Observable.empty());

        // Test the saveSprite() method
        clickOn("#saveSprite");

        // Verify that the preferences were updated
        assertEquals(0, chooseSpriteController.preferences.getInt("currentSpriteIndex", chooseSpriteController.currentSpriteIndex));
        verify(trainerService).setImage("Sprite1");

    }

}