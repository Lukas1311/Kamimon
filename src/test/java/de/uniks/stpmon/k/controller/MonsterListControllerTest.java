package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.MonsterCache;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Label;
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
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class MonsterListControllerTest extends ApplicationTest {
    @Mock
    MonsterCache monsterCache;

    @Spy
    App app = new App(null);
    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;

    @InjectMocks
    MonsterListController monsterListController;
    @Mock
    TrainerStorage trainerStorage;
    @Mock
    CacheManager cacheManager;


    @Override
    public void start(Stage stage) throws Exception {
        // show app
        app.start(stage);
        when(trainerStorage.getTrainer()).thenReturn(DummyConstants.TRAINER);
        when(cacheManager.requestMonsters(any())).thenReturn(monsterCache);
        // Set up mock
        when(monsterCache.getValues()).thenReturn(Observable.just(
                List.of(new Monster("1", null, null, null, null, null, null, null),
                        new Monster("2", null, null, null, null, null, null, null),
                        new Monster("3", null, null, null, null, null, null, null))));

        when(resourceBundleProvider.get()).thenReturn(resources);
        app.show(monsterListController);
        stage.requestFocus();
    }

    @Test
    public void testShowMonsterList() throws InterruptedException {
        waitForFxEvents();

        // Verify the size of monsterListVBox
        assertEquals(6, monsterListController.monsterListVBox.getChildren().size());

        // Verify the text of the labels
        assertEquals("1", ((Label) monsterListController.monsterListVBox.getChildren().get(0)).getText());
        assertEquals("2", ((Label) monsterListController.monsterListVBox.getChildren().get(1)).getText());
        assertEquals("3", ((Label) monsterListController.monsterListVBox.getChildren().get(2)).getText());
        assertEquals("<free>", ((Label) monsterListController.monsterListVBox.getChildren().get(3)).getText());
        assertEquals("<free>", ((Label) monsterListController.monsterListVBox.getChildren().get(4)).getText());
        assertEquals("<free>", ((Label) monsterListController.monsterListVBox.getChildren().get(5)).getText());
    }
}
