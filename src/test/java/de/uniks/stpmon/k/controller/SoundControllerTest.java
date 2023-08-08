package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.SidebarTab;
import de.uniks.stpmon.k.service.SettingsService;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;
import static org.testfx.api.FxAssert.verifyThat;
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
    SettingsService settingsService;
    @Mock
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
        final Slider musicSlider = lookup("#music").query();
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

    /*
    @Test
    void testChoseLanguage() {
        // prep:
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        RadioButton enButton = lookup("#englishButton").queryAs(RadioButton.class);
        RadioButton deButton = lookup("#germanButton").queryAs(RadioButton.class);
        assertTrue(enButton.isSelected());
        // check that language is english first
        assertThat(deButton.getText()).isEqualTo("German");
        // define mocks:
        doNothing().when(preferences).put(eq("locale"), captor.capture());
        doNothing().when(app).show(soundController);

        // action: chose the DE button
        write("\t".repeat(5));
        press(KeyCode.LEFT).release(KeyCode.LEFT);
        //assertTrue(deButton.isSelected());
        press(KeyCode.ENTER).release(KeyCode.ENTER);

        // verify mock:
        verify(preferences).put("locale", captor.getValue());
        assertEquals("de", captor.getValue());

        // action: chose the EN button
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        assertTrue(enButton.isSelected());

        press(KeyCode.ENTER).release(KeyCode.ENTER);

        // verify mock:
        verify(preferences).put("locale", captor.getValue());
        assertEquals("en", captor.getValue());

        verify(soundController).setDe();
        verify(soundController).setEn();
    }

     */

}
