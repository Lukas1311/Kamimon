package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BackpackMenuEntryControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;
    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);

    @Spy
    BackpackMenuController backpackMenuController;

    @InjectMocks
    BackpackMenuEntryController backpackMenuEntryController;

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);

        backpackMenuController.resources = resourceBundleProvider;
        backpackMenuController.backpackMenuListView = new ListView<>();

        when(backpackMenuController.getId(any())).thenReturn(2);
        backpackMenuEntryController = new BackpackMenuEntryController(backpackMenuController, BackpackMenuOption.MAP);


        app.show(backpackMenuEntryController);
        stage.requestFocus();
    }

    @Test
    void clickOn() {
        final ArgumentCaptor<BackpackMenuOption> captor = ArgumentCaptor.forClass(BackpackMenuOption.class);

        Text text = lookup("#backpackMenuText2").query();
        clickOn(text);
        verify(backpackMenuController).openOption(captor.capture());
        assertEquals(BackpackMenuOption.MAP, captor.getValue());

    }
}