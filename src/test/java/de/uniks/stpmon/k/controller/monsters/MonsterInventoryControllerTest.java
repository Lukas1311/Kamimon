package de.uniks.stpmon.k.controller.monsters;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.IngameController;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.MonsterService;
import de.uniks.stpmon.k.service.TrainerService;
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

    @Override
    public void start(Stage stage) {
        // show app
        app.start(stage);
        //when(resourceBundleProvider.get()).thenReturn(resources);
        when(monsterService.getMonsters()).thenReturn(Observable.empty());
        when(monsterService.getTeam()).thenReturn(Observable.empty());
        app.show(inventoryController);
        stage.requestFocus();
    }

    @Test
    public void test() {

    }
}