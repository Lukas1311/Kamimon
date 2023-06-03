package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.popup.ModalCallback;
import de.uniks.stpmon.k.controller.popup.PopUpController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.NPCInfo;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.TrainerService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.Locale;
import java.util.ResourceBundle;

import static de.uniks.stpmon.k.controller.sidebar.SidebarTab.CHOOSE_SPRITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerManagementControllerTest extends ApplicationTest {

    @Mock
    RegionService regionService;
    @Mock
    Provider<HybridController> hybridControllerProvider;
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;
    @Mock
    ChooseSpriteController chooseSpriteController;
    @Mock
    Provider<PopUpController> popUpControllerProvider;
    @Mock
    Provider<LobbyController> lobbyControllerProvider;
    @Mock
    TrainerService trainerService;
    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    @Spy
    App app = new App(null);


    @Spy
    @InjectMocks
    TrainerManagementController trainerManagementController;

    NPCInfo npcInfo = new NPCInfo(false);
    Trainer dummytrainer = new Trainer(
            "1", "0", "0", "0", "0", 0, "0", 0, 0, 0, npcInfo);

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        when(trainerService.getMe()).thenReturn(dummytrainer);
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
    void testTryChangeTrainerName() {
        // TODO: region service call
    }

    @Test
    void testDeleteTrainer() {
        final PopUpController popupMock = Mockito.mock(PopUpController.class);
        final LobbyController lobbyMock = Mockito.mock(LobbyController.class);

        when(trainerService.deleteMe()).thenReturn(Observable.just(dummytrainer));
        when(popUpControllerProvider.get()).thenReturn(popupMock);
        doAnswer(invocation -> {
            ModalCallback callback = invocation.getArgument(0);
            callback.onModalResult(true);
            return null;
        }).when(popupMock).showModal(any());
        doNothing().when(app).show(any(LobbyController.class));
        when(lobbyControllerProvider.get()).thenReturn(lobbyMock);

        //action
        clickOn("#deleteTrainerButton");

        verify(popupMock, times(2)).showModal(any());
        verify(trainerService).deleteMe();
        verify(app).show(lobbyMock);
    }

    @Test
    void testOpenTrainerSpriteEditor() {
        final HybridController mock = Mockito.mock(HybridController.class);
        when(hybridControllerProvider.get()).thenReturn(mock);
        doNothing().when(mock).pushTab(CHOOSE_SPRITE);

        clickOn("#trainerSprite");

        verify(mock).pushTab(CHOOSE_SPRITE);
    }

    @Test
    void testSaveChanges() {
        // prep:
        final PopUpController popupMock = Mockito.mock(PopUpController.class);
        final ArgumentCaptor<String> trainerNameCaptor = ArgumentCaptor.forClass(String.class);

        //define mocks
        when(popUpControllerProvider.get()).thenReturn(popupMock);
        when(trainerService.setTrainerName(anyString())).thenReturn(Observable.just(dummytrainer));

        doAnswer(invocation -> {
            ModalCallback callback = invocation.getArgument(0);
            callback.onModalResult(true);
            return null;
        }).when(popupMock).showModal(any());

        //action
        write("\tBob2");
        clickOn("#saveChangesButton");

        //check values
        TextField trainerName = lookup("#trainerNameInput").queryAs(javafx.scene.control.TextField.class);
        assertEquals("Bob2", trainerName.getText());

        //verify mocks
        verify(trainerManagementController).saveChanges();
        verify(popupMock).showModal(any());
        verify(trainerService).setTrainerName(any());
        //TODO: add method for sprite save here
    }
}
