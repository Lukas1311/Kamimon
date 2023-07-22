package de.uniks.stpmon.k.controller.monsters;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.controller.IngameController;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.MonsterService;
import de.uniks.stpmon.k.service.TrainerService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
    BehaviorSubject<List<Monster>> team = BehaviorSubject.createDefault(List.of());
    BehaviorSubject<List<Monster>> monsters = BehaviorSubject.createDefault(List.of());

    @Override
    public void start(Stage stage) {
        // show app
        app.start(stage);
        team.onNext(List.of(DummyConstants.MONSTER));
        when(monsterService.getMonsters()).thenReturn(monsters);
        when(monsterService.getTeam()).thenReturn(team);
        when(resourceService.getMonsterImage(any()))
                .thenReturn(Observable.just(DummyConstants.EMPTY_IMAGE));
        // when(ingameControllerProvider.get()).thenReturn(ingameController);
        app.show(inventoryController);
        stage.requestFocus();
    }

    @Test
    public void test() {
        moveTo("#team_0");
        drag(MouseButton.PRIMARY);
        dropTo("#monStorage");
    }
}