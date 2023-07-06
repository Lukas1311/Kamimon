package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.service.PresetService;
import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ActionFieldChooseOpponentControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);

    @Mock
    PresetService presetService;
    @Mock
    ActionFieldChangeMonsterController actionFieldChangeMonsterController;

    @InjectMocks
    ActionFieldChooseOpponentController actionFieldChooseOpponentController;

    List<Monster> opponentMonsterList = new ArrayList<>();


    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        opponentMonsterList.addAll(List.of(
                MonsterBuilder.builder().setId("2").setType(1).create(),
                MonsterBuilder.builder().setId("3").setType(2).create()
        ));

        app.show(actionFieldChooseOpponentController);

        stage.requestFocus();
    }

    @Test
    void testRender() {
        ActionFieldChangeMonsterController changeMonsterMock = Mockito.mock(ActionFieldChangeMonsterController.class);
        when(actionFieldChangeMonsterController).thenReturn(changeMonsterMock);

        MonsterTypeDto typeDto = new MonsterTypeDto(1, "monster", null, null, null);
        when(presetService.getMonster(any())).thenReturn(Observable.just(typeDto));

        Platform.runLater(() -> actionFieldChooseOpponentController.addActionOption(typeDto.name()));;

        Label mon= lookup("#user_monster_label_0").query();
        assertTrue(mon.getText().endsWith("monster"));

    }
}
