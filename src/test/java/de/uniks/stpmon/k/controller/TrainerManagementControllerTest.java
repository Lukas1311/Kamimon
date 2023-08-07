package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.popup.ModalCallback;
import de.uniks.stpmon.k.controller.popup.PopUpController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.MainWindow;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.TrainerService;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
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
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

import static de.uniks.stpmon.k.controller.sidebar.SidebarTab.CHOOSE_SPRITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerManagementControllerTest extends ApplicationTest {

    @Mock
    Provider<HybridController> hybridControllerProvider;
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;
    @Mock
    Provider<PopUpController> popUpControllerProvider;
    @Mock
    TrainerService trainerService;
    @Mock
    TrainerStorage trainerStorage;
    @Spy
    final ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    @Spy
    final App app = new App(null);

    @Spy
    @InjectMocks
    TrainerManagementController trainerManagementController;
    @Spy
    @SuppressWarnings("unused")
    EffectContext effectContext = new EffectContext().setSkipLoadImages(true);

    final Trainer dummytrainer = TrainerBuilder.builder().setId("1").create();

    @Override
    public void start(Stage stage) {
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        when(trainerService.getMe()).thenReturn(dummytrainer);
        when(trainerStorage.onTrainer()).thenReturn(Observable.just(Optional.of(dummytrainer)));
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
    void testBackToSettingsWithUnsavedChanges() {
        // prep:
        final PopUpController popupMock = Mockito.mock(PopUpController.class);
        final HybridController hybridMock = Mockito.mock(HybridController.class);

        // define mocks:
        when(trainerManagementController.hasUnsavedChanges()).thenReturn(true);
        when(popUpControllerProvider.get()).thenReturn(popupMock);
        doAnswer(invocation -> {
            ModalCallback callback = invocation.getArgument(0);
            callback.onModalResult(true);
            return null;
        }).when(popupMock).showModal(any());
        when(hybridControllerProvider.get()).thenReturn(hybridMock);
        doNothing().when(hybridMock).popTab();

        // action:
        clickOn("#backButton");

        // no values to check

        // check mocks:
        verify(popupMock).showModal(any());
        verify(trainerManagementController).saveSettings();
        verify(hybridMock).popTab();
    }

    @Test
    void testBackToSettingsWithUnsavedChangesClickNo() {
        // prep:
        final PopUpController popupMock = Mockito.mock(PopUpController.class);
        final HybridController hybridMock = Mockito.mock(HybridController.class);

        // define mocks:
        when(trainerManagementController.hasUnsavedChanges()).thenReturn(true);
        when(popUpControllerProvider.get()).thenReturn(popupMock);
        doAnswer(invocation -> {
            ModalCallback callback = invocation.getArgument(0);
            callback.onModalResult(false);
            return null;
        }).when(popupMock).showModal(any());
        when(hybridControllerProvider.get()).thenReturn(hybridMock);
        doNothing().when(hybridMock).popTab();

        // action:
        clickOn("#backButton");

        // no values to check

        // check mocks:
        verify(popupMock).showModal(any());
        verify(trainerManagementController, times(0)).saveSettings();
        verify(hybridMock).popTab();
    }

    @Test
    void testDeleteTrainer() {
        final PopUpController popupMock = Mockito.mock(PopUpController.class);
        final HybridController mock = Mockito.mock(HybridController.class);

        when(trainerService.deleteMe()).thenReturn(Observable.just(dummytrainer));
        when(popUpControllerProvider.get()).thenReturn(popupMock);
        doAnswer(invocation -> {
            ModalCallback callback = invocation.getArgument(0);
            callback.onModalResult(true);
            return null;
        }).when(popupMock).showModal(any());

        when(hybridControllerProvider.get()).thenReturn(mock);
        doNothing().when(mock).openMain(MainWindow.LOBBY);

        //action
        clickOn("#deleteTrainerButton");

        verify(popupMock, times(2)).showModal(any());
        verify(trainerService, times(1)).deleteMe();
        verify(mock, times(1)).openMain(MainWindow.LOBBY);
    }

    @Test
    void testDeleteTrainerAndDiscard() {
        final PopUpController popupMock = Mockito.mock(PopUpController.class);
        final HybridController mock = Mockito.mock(HybridController.class);

        when(popUpControllerProvider.get()).thenReturn(popupMock);
        doAnswer(invocation -> {
            ModalCallback callback = invocation.getArgument(0);
            callback.onModalResult(false);
            return null;
        }).when(popupMock).showModal(any());

        //action
        clickOn("#deleteTrainerButton");

        verify(popupMock, times(1)).showModal(any());
        verify(trainerService, times(0)).deleteMe();
        verify(mock, times(0)).openMain(MainWindow.LOBBY);
    }

    @Test
    void testDeleteTrainerOnError() {
        final PopUpController popupMock = Mockito.mock(PopUpController.class);
        final HybridController mock = Mockito.mock(HybridController.class);

        when(trainerService.deleteMe()).thenReturn(Observable.error(new Exception()));
        when(popUpControllerProvider.get()).thenReturn(popupMock);
        doAnswer(invocation -> {
            ModalCallback callback = invocation.getArgument(0);
            callback.onModalResult(true);
            return null;
        }).when(popupMock).showModal(any());

        when(hybridControllerProvider.get()).thenReturn(mock);
        doNothing().when(mock).openMain(MainWindow.LOBBY);

        //action
        clickOn("#deleteTrainerButton");

        verify(popupMock, times(1)).showModal(any());
        verify(trainerService, times(1)).deleteMe();
        verify(mock, times(1)).openMain(MainWindow.LOBBY);
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
        AtomicBoolean isFirstInvocation = new AtomicBoolean(false);

        doAnswer(invocation -> {
            ModalCallback callback = invocation.getArgument(0);
            callback.onModalResult(isFirstInvocation.getAndSet(true));
            return null;
        }).when(popupMock).showModal(any());

        //action
        write("\tBob2");
        // click save then discard
        clickOn("#saveChangesButton");
        // click save then OK
        clickOn("#saveChangesButton");

        //check values
        TextField trainerName = lookup("#trainerNameInput").queryAs(TextField.class);
        assertEquals("Bob2", trainerName.getText());

        //verify mocks
        verify(trainerManagementController, times(2)).saveChanges();
        verify(trainerManagementController, times(1)).saveSettings();
        verify(popupMock, times(2)).showModal(any());
        verify(trainerService).setTrainerName(trainerNameCaptor.capture());
        assertEquals("Bob2", trainerNameCaptor.getValue());
    }

}
