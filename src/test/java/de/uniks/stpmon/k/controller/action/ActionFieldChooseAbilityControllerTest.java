package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.service.PresetService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class ActionFieldChooseAbilityControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);

    @Mock
    PresetService presetService;
    @Mock
    ActionFieldChangeMonsterController actionFieldChangeMonsterController;

    @InjectMocks
    ActionFieldChooseAbilityController actionFieldChooseAbilityController;


    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        app.show(actionFieldChooseAbilityController);

        stage.requestFocus();
    }

    @Test
    void testRender() {
        AbilityDto abilityDto = new AbilityDto(1, "abilityName", null, "fire", null, null, null);

        when(presetService.getAbility(anyString())).thenReturn(Observable.just(abilityDto));

        actionFieldChooseAbilityController.setAction(abilityDto.name());
        waitForFxEvents();

        Text text = lookup("#ability_0").query();
        assertTrue(text.getText().endsWith("abilityName"));
    }

}
