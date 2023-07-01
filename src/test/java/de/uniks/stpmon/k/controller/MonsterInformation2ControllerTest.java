package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.MonsterAttributes;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.PresetService;
import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.awt.image.BufferedImage;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class MonsterInformation2ControllerTest extends ApplicationTest {

    @Spy
    App app = new App(null);

    @Mock
    PresetService presetService;
    @Mock
    IResourceService resourceService;

    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;

    @InjectMocks
    MonsterInformation2Controller monsterInformation2Controller;
    @Spy
    @SuppressWarnings("unused")
    EffectContext effectContext = new EffectContext().setSkipLoadImages(true);

    @Override
    public void start(Stage stage) throws Exception {
        // show app
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        app.show(monsterInformation2Controller);
        stage.requestFocus();
    }

    @Test
    public void testInfoMonsterTypeDto() {
        List<String> types = Arrays.asList("fire", "water");
        MonsterTypeDto monsterTypeDto = new MonsterTypeDto(1, "monster", "image", types, "description");
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_4BYTE_ABGR);

        when(presetService.getMonster(anyString())).thenReturn(Observable.just(monsterTypeDto));
        when(resourceService.getMonsterImage(anyString())).thenReturn(Observable.just(image));

        monsterInformation2Controller.loadMonsterTypeDto(monsterTypeDto.id().toString());
        waitForFxEvents();

        assertEquals("monster", monsterInformation2Controller.monsterNameLabel.getText());
        assertEquals(7, monsterInformation2Controller.overviewGrid.getChildren().size());
    }

    @Test
    public void testInfoMonster() {
        SortedMap<String, Integer> abilities = new TreeMap<>();
        abilities.put("1", 15);
        abilities.put("2", 10);
        AbilityDto abilityDto = new AbilityDto(1, "abilityName", "abilityDescription", "fire", 20, 1, 25);
        MonsterAttributes attributes = new MonsterAttributes(10, 8, 6, 4);
        MonsterAttributes currentAttributes = new MonsterAttributes(5, 4, 3, 2);
        Monster monster = MonsterBuilder.builder().setId("id")
                .setType(1)
                .setLevel(1)
                .setExperience(10)
                .setAbilities(abilities)
                .setAttributes(attributes)
                .setCurrentAttributes(currentAttributes)
                .create();

        when(presetService.getAbility(anyString())).thenReturn(Observable.just(abilityDto));




        Platform.runLater(() -> {
            monsterInformation2Controller.loadMonster(monster);



        });
        waitForFxEvents();
        assertEquals("Lvl. 1", monsterInformation2Controller.monsterLevelLabel.getText());
        assertEquals("HP: 5/10", monsterInformation2Controller.monsterHpLabel.getText());
        assertEquals("XP: 10", monsterInformation2Controller.monsterXpLabel.getText());
        assertEquals("10", monsterInformation2Controller.hpValueLabel.getText());
        assertEquals("8", monsterInformation2Controller.atkValueLabel.getText());
        assertEquals("6", monsterInformation2Controller.defValueLabel.getText());
        assertEquals("4", monsterInformation2Controller.speValueLabel.getText());

        FxAssert.verifyThat("#typeLabel_1", hasText("FIRE"));
        FxAssert.verifyThat("#nameLabel_1", hasText("abilityName"));
        FxAssert.verifyThat("#powLabel_1", hasText("25"));
        FxAssert.verifyThat("#accLabel_1", hasText("100"));
        FxAssert.verifyThat("#useLabel_1", hasText("??/20"));
    }

    @Test
    public void testAbilityDescription() {

        SortedMap<String, Integer> abilities = new TreeMap<>();
        abilities.put("1", 15);
        abilities.put("2", 10);
        AbilityDto abilityDto = new AbilityDto(1, "abilityName", "AbilityDescription", "fire", 20, 1, 25);
        MonsterAttributes attributes = new MonsterAttributes(10, 8, 6, 4);
        MonsterAttributes currentAttributes = new MonsterAttributes(5, 4, 3, 2);
        Monster monster = MonsterBuilder.builder().setId("id")
                .setType(1)
                .setLevel(1)
                .setAbilities(abilities)
                .setAttributes(attributes)
                .setCurrentAttributes(currentAttributes)
                .create();

        when(presetService.getAbility(anyString())).thenReturn(Observable.just(abilityDto));

        Platform.runLater(() -> monsterInformation2Controller.loadMonster(monster));

        waitForFxEvents();

        clickOn("#nameLabel_1");
        waitForFxEvents();
        assertTrue(monsterInformation2Controller.descriptionLabel.isVisible());
        assertFalse(monsterInformation2Controller.infoGrid.isVisible());
        assertEquals("AbilityDescription", monsterInformation2Controller.descriptionLabel.getText());
    }

}
