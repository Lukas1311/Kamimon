package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.encounter.EncounterOverviewController;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.service.MonsterService;
import de.uniks.stpmon.k.service.PresetService;
import io.reactivex.rxjava3.core.Observable;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActionFieldSelectMonControllerTest extends ApplicationTest {

    @Spy
    final App app = new App(null);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;
    @Spy
    final ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);

    @InjectMocks
    ActionFieldSelectMonController selectMonController;

    @Mock
    Provider<EncounterOverviewController> encOverviewProvider;
    @Mock
    MonsterService monService;
    @Mock
    PresetService presetService;
    @Mock
    Provider<ActionFieldController> actionFieldControllerProvider;

    @Override
    public void start(Stage stage) {
        app.start(stage);

        when(resourceBundleProvider.get()).thenReturn(resources);

        //mock monService
        Monster mon0 = MonsterBuilder.builder()
                .setId("0")
                .setType(0)
                .create();
        Monster mon1 = MonsterBuilder.builder()
                .setId("1")
                .setType(1)
                .create();
        List<Monster> team = new ArrayList<>();
        team.add(mon0);
        team.add(mon1);
        when(monService.getTeam()).thenReturn(Observable.just(team));

        //mock presetService
        MonsterTypeDto monTypeDto0 = new MonsterTypeDto(0, "Mon0", null, null, "");
        MonsterTypeDto monTypeDto1 = new MonsterTypeDto(1, "Mon1", null, null, "");
        when(presetService.getMonster(0)).thenReturn(Observable.just(monTypeDto0));
        when(presetService.getMonster(1)).thenReturn(Observable.just(monTypeDto1));

        app.show(selectMonController);
        stage.requestFocus();
    }

    @Test
    void clickOnMon() {
        ActionFieldController actionFieldController = mock(ActionFieldController.class);
        when(actionFieldControllerProvider.get()).thenReturn(actionFieldController);
        when(actionFieldController.isMonInfoOpen()).thenReturn(true);
        EncounterOverviewController encounterOverviewController = mock(EncounterOverviewController.class);
        when(encOverviewProvider.get()).thenReturn(encounterOverviewController);

        //action
        clickOn("#select_mon_Mon1");
        clickOn("#backOption");
    }
}