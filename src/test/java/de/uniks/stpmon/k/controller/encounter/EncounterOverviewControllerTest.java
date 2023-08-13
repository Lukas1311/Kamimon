package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.action.ActionFieldController;
import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.service.AnimationService;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.SessionService;
import de.uniks.stpmon.k.service.SoundService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
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
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class EncounterOverviewControllerTest extends ApplicationTest {

    @Spy
    final
    App app = new App(null);

    @Mock
    SessionService sessionService;
    @Mock
    @SuppressWarnings("unused")
    SoundService soundService;
    @Mock
    Provider<StatusController> statusControllerProvider;
    @Mock
    @SuppressWarnings("unused")
    ActionFieldController actionFieldController;
    @SuppressWarnings("unused")
    @Mock
    AnimationService animationService;

    @InjectMocks
    EncounterOverviewController encounterOverviewController;
    @Spy
    @SuppressWarnings("unused")
    EffectContext effectContext = new EffectContext()
            .setSkipLoadImages(true)
            .setSkipLoading(true);
    @Mock
    RegionStorage regionStorage;

    @Override
    public void start(Stage stage) {
        app.start(stage);

        Region region = new Region("0", "region", null, null);
        when(regionStorage.getRegion()).thenReturn(region);

        // Defines used slots of the encounter
        when(sessionService.getSlots()).thenReturn(List.of(EncounterSlot.PARTY_FIRST, EncounterSlot.PARTY_SECOND,
                EncounterSlot.ENEMY_FIRST, EncounterSlot.ENEMY_SECOND));

        when(sessionService.listenMonster(any())).thenReturn(Observable.empty());
        when(sessionService.listenOpponentDeletion(any())).thenReturn(Observable.empty());

        lenient().when(sessionService.listenOpponent(any())).thenReturn(Observable.just(new Opponent(
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

    @Test
    void testRender() {
        VBox userMonstersBox = lookup("#userMonsters").queryAs(VBox.class);
        VBox opponentMonstersBox = lookup("#opponentMonsters").queryAs(VBox.class);
        assertNotNull(userMonstersBox);
        assertNotNull(opponentMonstersBox);
        assertNotNull(encounterOverviewController);
    }
}
