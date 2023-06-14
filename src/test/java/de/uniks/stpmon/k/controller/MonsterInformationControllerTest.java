package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.MonsterAttributes;
import de.uniks.stpmon.k.service.PresetService;
import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MonsterInformationControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);

    @Mock
    PresetService presetService;

    @InjectMocks
    MonsterInformationController monsterInformationController;

    @Override
    public void start(Stage stage) throws Exception {
        // show app
        app.start(stage);
        app.show(monsterInformationController);
        stage.requestFocus();
    }

    @Test
    public void testInfoMonsterTypeDto() {
        List<String> types = Arrays.asList("type1", "type2");
        MonsterTypeDto monsterTypeDto = new MonsterTypeDto(1, "monster", "image", types, "description");

        when(presetService.getMonster(anyString())).thenReturn(Observable.just(monsterTypeDto));

        monsterInformationController.loadMonsterTypeDto(monsterTypeDto.id().toString());

        assertEquals("monster", monsterInformationController.monsterNameText.getText());
        assertEquals(2, monsterInformationController.typeListHBox.getChildren().size());
        assertEquals("description", monsterInformationController.descriptionText.getText());
    }

    @Test
    public void testInfoMonster() {
        MonsterAttributes attributes = new MonsterAttributes(10,8,6,4);
        MonsterAttributes currentAttributes = new MonsterAttributes(5,4,3,2);
        Monster monster = new Monster("id", null, 1, 1, 0, null, attributes, currentAttributes);

        Platform.runLater(() -> {
            monsterInformationController.loadMonster(monster);

            assertEquals("2", monsterInformationController.speedLabel.getText());
            assertEquals("1", monsterInformationController.levelLabel.getText());
            assertEquals("5", monsterInformationController.currentHPLabel.getText());
            assertEquals("10", monsterInformationController.maxHPLabel.getText());
            assertEquals("1", monsterInformationController.levelLabel.getText());
            assertEquals("0", monsterInformationController.experienceLabel.getText());
        });
    }

}
