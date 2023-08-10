package de.uniks.stpmon.k.controller.monsters;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.controller.action.ActionFieldController;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.service.*;
import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import javax.inject.Provider;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@ExtendWith(MockitoExtension.class)
public class MonsterSelectionControllerTest extends ApplicationTest {

    @Spy
    final App app = new App(null);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;
    @Spy
    final ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);

    @Mock
    IResourceService resourceService;
    @Mock
    MonsterService monsterService;
    @Mock
    ActionFieldController actionFieldController;
    @InjectMocks
    MonsterSelectionController monsterSelectionController;

    @Spy
    @SuppressWarnings("unused")
    EffectContext effectContext = new EffectContext().setSkipLoadImages(true);

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        when(resourceService.getMonsterImage(anyString())).thenReturn(Observable.just(DummyConstants.EMPTY_IMAGE));

        int itemTypeId = 1234;

        List<Monster> monsterList = List.of(MonsterBuilder.builder().setId(0).create(),
                MonsterBuilder.builder().setId(1).create(),
                MonsterBuilder.builder().setId(2).create());

        when(monsterService.getTeam()).thenReturn(Observable.just(monsterList));
        doNothing().when(actionFieldController).executeItemMove(anyInt(), anyString());

        monsterSelectionController.setItem(itemTypeId);

        app.show(monsterSelectionController);
        stage.requestFocus();
    }

    @Test
    void testRender() {
        FlowPane monsterSelectionFlow = lookup("#monsterSelectionFlow").queryAs(FlowPane.class);
        assertEquals(3, monsterSelectionFlow.getChildren().size());

        clickOn(monsterSelectionFlow.getChildren().get(1));
        waitForFxEvents();

        verify(actionFieldController).executeItemMove(1234, "1");

        Platform.runLater(() -> monsterSelectionController.destroy());
    }


}

