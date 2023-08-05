package de.uniks.stpmon.k.controller.monsters;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.service.MonsterService;
import de.uniks.stpmon.k.service.PresetService;
import io.reactivex.rxjava3.core.Observable;
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
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class TeamControllerTest extends ApplicationTest {

    @Spy
    final App app = new App(null);
    @Spy
    final ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;
    @Mock
    Provider<MonsterBarController> monsterBarControllerProvider;

    @Mock
    PresetService presetService;

    @Mock
    MonsterService monsterService;

    @InjectMocks
    TeamController teamController;


    @Override
    public void start(Stage stage) {
        // show app
        app.start(stage);
        // Set up mock
        when(monsterService.getTeam()).thenReturn(Observable.just(
                List.of(MonsterBuilder.builder().setId("1").setType(1).create()
                )));

        when(presetService.getMonster("1")).thenReturn(
                Observable.just(new MonsterTypeDto(1, "Monster 1", "", List.of(), "")));

        when(resourceBundleProvider.get()).thenReturn(resources);

        MonsterBarController mock = Mockito.mock(MonsterBarController.class);
        when(monsterBarControllerProvider.get()).thenReturn(mock);

        app.show(teamController);
        stage.requestFocus();
    }

    @Test
    public void testShowMonsterList() {
        waitForFxEvents();
        // Verify the size of monsterListVBox
        assertEquals(6, teamController.monsterListVBox.getChildren().size());

        waitForFxEvents();
        // Verify the text of the labels
        Label label = lookup("#monster_label_0").queryAs(Label.class);
        assertTrue(label.getText().endsWith("Monster 1"));

        label = lookup("#monster_label_1").queryAs(Label.class);
        assertEquals("  -", label.getText());

        label = lookup("#monster_label_2").queryAs(Label.class);
        assertEquals("  -", label.getText());

        label = lookup("#monster_label_3").queryAs(Label.class);
        assertEquals("  -", label.getText());

        label = lookup("#monster_label_4").queryAs(Label.class);
        assertEquals("  -", label.getText());

        label = lookup("#monster_label_5").queryAs(Label.class);
        assertEquals("  -", label.getText());
    }


}
