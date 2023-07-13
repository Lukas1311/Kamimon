package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.controller.action.ActionFieldController;
import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.SessionService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class EncounterOverviewControllerTest extends ApplicationTest {

    @Spy
    final
    App app = new App(null);

    @Mock
    IResourceService resourceService;
    @Mock
    SessionService sessionService;
    @Mock
    Provider<StatusController> statusControllerProvider;
    @Mock
    @SuppressWarnings("unused")
    ActionFieldController actionFieldController;

    @InjectMocks
    EncounterOverviewController encounterOverviewController;
    @Spy
    @SuppressWarnings("unused")
    EffectContext effectContext = new EffectContext().setSkipLoadImages(true);

    final Trainer dummytrainer = TrainerBuilder.builder().setId("1").create();

    @Override
    public void start(Stage stage) {
        app.start(stage);

        // Defines used slots of the encounter
        when(sessionService.getSlots()).thenReturn(List.of(EncounterSlot.PARTY_FIRST, EncounterSlot.PARTY_SECOND,
                EncounterSlot.ENEMY_FIRST, EncounterSlot.ENEMY_SECOND));

        Monster userMonster1 = MonsterBuilder.builder().setId("2").setTrainer(dummytrainer._id()).setType(1).create();
        Monster userMonster2 = MonsterBuilder.builder(userMonster1).setId("3").setType(2).create();
        // Assigns the monsters to the team slots
        when(sessionService.getMonster(EncounterSlot.PARTY_FIRST)).thenReturn(userMonster1);
        when(sessionService.getMonster(EncounterSlot.PARTY_SECOND)).thenReturn(userMonster2);

        // Assigns the monsters to the enemy slots
        when(sessionService.getMonster(EncounterSlot.ENEMY_FIRST))
                .thenReturn(MonsterBuilder.builder().setId("2").setType(1).create());
        when(sessionService.getMonster(EncounterSlot.ENEMY_SECOND))
                .thenReturn(MonsterBuilder.builder().setId("3").setType(2).create());

        when(resourceService.getMonsterImage(any())).thenReturn(Observable.just(DummyConstants.EMPTY_IMAGE));

        when(sessionService.listenOpponent(any())).thenReturn(Observable.just(new Opponent(
                "o_1",
                null,
                null,
                false,
                false,
                null,
                null,
                List.of(),
                0
        )));

        when(statusControllerProvider.get()).thenAnswer(invocation -> {
            VBox statusBox = new VBox();
            statusBox.setStyle("-fx-background-color: black;");
            statusBox.setPrefWidth(50);
            statusBox.setPrefHeight(20);
            StatusController statusController = mock(StatusController.class);
            statusController.fullBox = statusBox;
            when(statusController.render()).thenReturn(statusController.fullBox);
            return statusController;
        });

        app.show(encounterOverviewController);
        stage.requestFocus();
    }

    /*@Test
    void testRender() {
        VBox userMonstersBox = lookup("#userMonsters").queryAs(VBox.class);
        VBox opponentMonstersBox = lookup("#opponentMonsters").queryAs(VBox.class);
        assertNotNull(userMonstersBox);
        assertNotNull(opponentMonstersBox);
        assertNotNull(encounterOverviewController);
        sleep(4000);
    }

     */

}