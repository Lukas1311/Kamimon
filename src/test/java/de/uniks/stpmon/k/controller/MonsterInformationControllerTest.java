package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.MonsterAttributes;
import de.uniks.stpmon.k.service.IResourceService;
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

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class MonsterInformationControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);

    @Mock
    PresetService presetService;
    @Mock
    IResourceService resourceService;

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
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_4BYTE_ABGR);

        when(presetService.getMonster(anyString())).thenReturn(Observable.just(monsterTypeDto));
        when(resourceService.getMonsterImage(anyString())).thenReturn(Observable.just(image));

        monsterInformationController.loadMonsterTypeDto(monsterTypeDto.id().toString());
        waitForFxEvents();

        assertEquals("monster", monsterInformationController.monsterNameText.getText());
        assertEquals(2, monsterInformationController.typeListHBox.getChildren().size());
        assertEquals("description", monsterInformationController.descriptionText.getText());
    }

    @Test
    public void testInfoMonster() {
        SortedMap<String, Integer> abilities = new TreeMap<>();
        abilities.put("1", 15);
        abilities.put("2", 10);
        AbilityDto abilityDto = new AbilityDto(1,"abilityName", null, "abilityType", 20, null, null);
        MonsterAttributes attributes = new MonsterAttributes(10,8,6,4);
        MonsterAttributes currentAttributes = new MonsterAttributes(5,4,3,2);
        Monster monster = new Monster("id", null, 1, 1, 0, abilities, attributes, currentAttributes);

        when(presetService.getAbility(anyString())).thenReturn(Observable.just(abilityDto));

        Platform.runLater(() -> {
            monsterInformationController.loadMonster(monster);

            assertEquals("50", monsterInformationController.healthLabel.getText());
            assertEquals("50", monsterInformationController.attackLabel.getText());
            assertEquals("50", monsterInformationController.defenseLabel.getText());
            assertEquals("50", monsterInformationController.speedLabel.getText());
            assertEquals("1", monsterInformationController.levelLabel.getText());
            assertEquals("5", monsterInformationController.currentHPLabel.getText());
            assertEquals("10", monsterInformationController.maxHPLabel.getText());
            assertEquals("1", monsterInformationController.levelLabel.getText());
            assertEquals("0", monsterInformationController.experienceLabel.getText());
        });
    }

    @Test
    public void testAbilityDescrition() {
        SortedMap<String, Integer> abilities = new TreeMap<>();
        abilities.put("1", 15);
        abilities.put("2", 10);
        AbilityDto abilityDto = new AbilityDto(1, "abilityName", "AbilityDescription", "abilityType", 20, null, null);
        MonsterAttributes attributes = new MonsterAttributes(10, 8, 6, 4);
        MonsterAttributes currentAttributes = new MonsterAttributes(5, 4, 3, 2);
        Monster monster = new Monster("id", null, 1, 1, 0, abilities, attributes, currentAttributes);

        when(presetService.getAbility(anyString())).thenReturn(Observable.just(abilityDto));

        Platform.runLater(() -> monsterInformationController.loadMonster(monster));

        waitForFxEvents();

        clickOn("#abilityBox1");
        waitForFxEvents();
        assertEquals("AbilityDescription", monsterInformationController.descriptionText.getText());

    }

}
