package de.uniks.stpmon.k.controller.monsters;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.controller.IngameController;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.MonsterService;
import de.uniks.stpmon.k.service.TrainerService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
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

import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class MonsterInventoryControllerTest extends ApplicationTest {
    @Spy
    final App app = new App(null);

    @Mock
    IResourceService resourceService;
    @Mock
    IngameController ingameController;
    @Mock
    Provider<IngameController> ingameControllerProvider;
    @Mock
    TrainerService trainerService;
    @Mock
    MonsterService monsterService;
    @Spy
    @SuppressWarnings("unused")
    EffectContext effectContext = new EffectContext().setSkipLoadImages(true);
    @InjectMocks
    MonsterInventoryController inventoryController;
    final BehaviorSubject<List<Monster>> team = BehaviorSubject.createDefault(List.of());
    final BehaviorSubject<List<Monster>> monsters = BehaviorSubject.createDefault(List.of());

    @Override
    public void start(Stage stage) {
        // show app
        app.start(stage);
        team.onNext(List.of(DummyConstants.MONSTER));
        monsters.onNext(List.of(DummyConstants.MONSTER, MonsterBuilder.builder().setId("second").create()));
        when(monsterService.getMonsters()).thenReturn(monsters);
        when(monsterService.getMonsterList()).thenAnswer((i) -> monsters.getValue());
        when(monsterService.getTeamList()).thenAnswer((i) -> team.getValue());
        when(monsterService.getTeam()).thenReturn(team);
        when(resourceService.getMonsterImage(any()))
                .thenReturn(Observable.just(DummyConstants.EMPTY_IMAGE));
        app.show(inventoryController);
        stage.requestFocus();
    }

    @Test
    void updateTeamOnDestroy() {
        // Mock ingame controller
        when(ingameControllerProvider.get()).thenReturn(ingameController);

        // Destroying the controller should update the team to the server
        inventoryController.destroy();

        // Check if team was updated
        verify(trainerService).setTeam(any());
    }

    @Test
    public void testOpenInfo() {
        // Mock ingame controller
        doNothing().when(ingameController).openMonsterInfo(any());
        when(ingameControllerProvider.get()).thenReturn(ingameController);

        // Click on monster should open info
        clickOn("#team_0");
        waitForFxEvents();

        // Method should be called one time
        verify(ingameController).openMonsterInfo(any());

        // another click should close info
        clickOn("#team_0");
        waitForFxEvents();

        // Close method should be called one time
        verify(ingameController).closeMonsterInfo();

        // Click on monster again should open info again
        clickOn("#team_0");
        waitForFxEvents();

        // Method should be called one time
        verify(ingameController, times(2)).openMonsterInfo(any());

        // Click on another monster should close info and open info for the new monster
        clickOn("#storage_0_0");
        waitForFxEvents();
        // Close method should be called one time
        verify(ingameController, times(3)).openMonsterInfo(any());
        verify(ingameController, times(2)).closeMonsterInfo();
    }

    @Test
    public void test() {
        doAnswer((invocation) -> {
            List<String> teamArg = invocation.getArgument(0);
            team.onNext(teamArg.isEmpty() ? List.of() : List.of(DummyConstants.MONSTER));

            monsters.onNext(List.of(DummyConstants.MONSTER, MonsterBuilder.builder().setId("second").create()));
            return null;
        }).when(trainerService).temporaryApplyTeam(any());

        // Check initial state, one monster in team, one in storage
        verifyThat("#storage_0_0", Node::isVisible);
        verifyThat("#team_0", Node::isVisible);

        // drag and drop monster from team to storage
        moveTo("#team_0");
        drag(MouseButton.PRIMARY);
        dropTo("#monStorage");

        waitForFxEvents();
        // Check if monster was moved into storage
        verifyThat("#storage_0_0", Node::isVisible);
        verifyThat("#storage_0_1", Node::isVisible);

        moveTo("#storage_0_0");
        drag(MouseButton.PRIMARY);
        dropTo("#monTeam");

        // Check if monster was moved into team
        verifyThat("#storage_0_0", Node::isVisible);
        verifyThat("#team_0", Node::isVisible);
    }
}