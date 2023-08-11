package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.SidebarTab;
import de.uniks.stpmon.k.service.SettingsService;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
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
import java.util.prefs.Preferences;

import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;

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
    SettingsService settingsService;
    @Mock
    @SuppressWarnings("unused")
    Preferences preferences;

    @Override
    public void start(Stage stage) {
        // show app
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        when(settingsService.getNightEnabled()).thenReturn(true);
        when(settingsService.getSoundValue()).thenReturn(100f);

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
        final Slider musicSlider = lookup("#musicSlider").query();
        //check first if value is 100
        assertThat(musicSlider.getValue()).isEqualTo(100);

        // set a new value on the slider
        clickOn(musicSlider);

        // Check if value is changed
        assertThat(musicSlider.getValue()).isNotEqualTo(100);
        // Check if value is saved
        verify(settingsService).setSoundValue(anyFloat());
    }

    @Test
    public void onNightEnabled() {
        // Check if initially is checked
        verifyThat("#nightMode", CheckBox::isSelected);

        // Disable night mode
        clickOn("#nightMode");

        // Check if was unchecked
        verifyThat("#nightMode", not(CheckBox::isSelected));
        // check if value is saved
        verify(settingsService).setNightEnabled(false);

        // Enable again
        clickOn("#nightMode");

        // Check if was checked
        verifyThat("#nightMode", CheckBox::isSelected);
        // check if value is saved
        verify(settingsService).setNightEnabled(true);
    }

}
