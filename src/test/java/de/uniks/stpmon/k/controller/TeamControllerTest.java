package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.service.MonsterService;
import de.uniks.stpmon.k.service.PresetService;
import io.reactivex.rxjava3.core.Observable;
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
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class TeamControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);
    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;
    @Mock
    Provider<MonsterBarController> monsterBarControllerProvider;

    @InjectMocks
    TeamController teamController;

    @Mock
    PresetService presetService;
    @Mock
    MonsterService monsterService;


    @Override
    public void start(Stage stage) throws Exception {
        // show app
        app.start(stage);
        // Set up mock
        when(monsterService.getTeam()).thenReturn(Observable.just(
                List.of(MonsterBuilder.builder().setId("1").setType(1).create(),
                        MonsterBuilder.builder().setId("2").setType(2).create(),
                        MonsterBuilder.builder().setId("3").setType(2).create()
                )));

        when(presetService.getMonster("1")).thenReturn(
                Observable.just(new MonsterTypeDto(1, "Monster 1", "", List.of(), "")));
        when(presetService.getMonster("2")).thenReturn(
                Observable.just(new MonsterTypeDto(1, "Monster 2", "", List.of(), "")));

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

        // Verify the text of the labels
        verifyThat("#monster_label_0", hasText("  Monster 1"));
        verifyThat("#monster_label_1", hasText("  Monster 2"));
        verifyThat("#monster_label_2", hasText("  Monster 2"));
        verifyThat("#monster_label_3", hasText("  -"));
        verifyThat("#monster_label_4", hasText("  -"));
        verifyThat("#monster_label_5", hasText("  -"));
    }
}
