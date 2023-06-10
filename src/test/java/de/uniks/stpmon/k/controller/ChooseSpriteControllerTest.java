package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.popup.PopUpController;
import de.uniks.stpmon.k.service.PresetService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    Provider<CreateTrainerController> createTrainerControllerProvider;
    @Mock
    Provider<PopUpController> popUpControllerProvider;


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

    /*
    @Test
    public void testSaveSprite() {
        // Create mock
        final BorderPane chooseTrainerMock = Mockito.mock(BorderPane.class);
        final CreateTrainerController createTrainerMock = Mockito.mock(CreateTrainerController.class);
        final PopUpController popUpControllerMock = Mockito.mock(PopUpController.class);

        // Set up
        chooseSpriteController.chooseTrainerContent = chooseTrainerMock;
        chooseSpriteController.createTrainerControllerProvider = () -> createTrainerMock;
        chooseSpriteController.popUpControllerProvider = () -> popUpControllerMock;

        chooseSpriteController.previousSpriteIndex = 0;
        chooseSpriteController.currentSpriteIndex = 0;

        AnchorPane anchorPane = new AnchorPane();
        when(createTrainerMock.render()).thenReturn(anchorPane);
        when(chooseTrainerMock.getChildren()).thenReturn(FXCollections.observableArrayList());

        clickOn("#saveSprite");

        verify(preferences).putInt("currentSpriteIndex", 0);
        verify(chooseTrainerMock, times(2)).getChildren();
        verify(popUpControllerMock, never()).setScenario(any(PopUpScenario.class));
        verify(popUpControllerMock, never()).showModal(any(ModalCallback.class));
    }

     */

}