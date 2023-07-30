package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.SidebarTab;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class SoundControllerTest extends ApplicationTest {

    @Spy
    final App app = new App(null);
    @Spy
    final ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;
    @Mock
    Provider<HybridController> hybridControllerProvider;
    @InjectMocks
    SoundController soundController;
    @Mock
    Preferences preferences;



    @Override
    public void start(Stage stage) {
        // show app
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        app.show(soundController);
        stage.requestFocus();
    }

    @Test
    public void backButton() {
        final HybridController mock = Mockito.mock(HybridController.class);
        when(hybridControllerProvider.get()).thenReturn(mock);

        clickOn("#backToSettingButton");
        verify(mock).pushTab(SidebarTab.SETTINGS);
    }

    @Test
    public void onMusic() {
        final HybridController mock = Mockito.mock(HybridController.class);
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        when(hybridControllerProvider.get()).thenReturn(mock);

        final Slider musicSlider = lookup("#music").query();
        //check first if value is 0
        assertThat(musicSlider.getValue()).isEqualTo(0);
        //doNothing().when(preferences).put(eq("music"), captor.capture());


        //change value to 100 and go back to Settings
        clickOn(musicSlider);
        //verify(preferences).put(eq("music"), captor.capture());
        assertThat(musicSlider.getValue()).isEqualTo(52.152317880794705);
        sleep(3000);
        clickOn("#backToSettingButton");
        verify(mock).pushTab(SidebarTab.SETTINGS);

        //go back to soundController and look if value is 52.152317880794705

    }
}
