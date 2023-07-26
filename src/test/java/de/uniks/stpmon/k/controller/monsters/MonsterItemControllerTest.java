package de.uniks.stpmon.k.controller.monsters;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.MonsterAttributes;
import de.uniks.stpmon.k.models.MonsterStatus;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.service.IResourceService;
import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
class MonsterItemControllerTest extends ApplicationTest {
    @Spy
    final App app = new App(null);
    @Mock
    IResourceService resourceService;

    MonsterItemController monsterItem;
    Monster currentMonster;


    @Override
    public void start(Stage stage) {
        when(resourceService.getMonsterImage(any())).thenReturn(Observable.just(DummyConstants.EMPTY_IMAGE));
        // show app
        app.start(stage);
        currentMonster = MonsterBuilder.builder()
                .setId("monster_0")
                .setTrainer("trainer_0")
                .setType(1)
                .setAttributes(new MonsterAttributes(10f, 8f, 6f, 4f))
                .setCurrentAttributes(new MonsterAttributes(5f, 4f, 3f, 2f))
                .addStatus(MonsterStatus.CONFUSED, MonsterStatus.BURNED)
                .create();
        monsterItem = new MonsterItemController(currentMonster, resourceService);
        app.show(monsterItem);
        stage.requestFocus();
    }

    @Test
    void testHpColor() {
        // Check initial status
        verifyThat("#healthBar", (ProgressBar bar) -> bar.getProgress() > 0.0f);

        // Update hp to 1
        Platform.runLater(() -> {
            Monster monster = MonsterBuilder.builder(currentMonster)
                    .setCurrentAttributes(new MonsterAttributes(10f, 4f, 3f, 2f))
                    .create();
            monsterItem = new MonsterItemController(monster, resourceService);
            app.show(monsterItem);
        });
        waitForFxEvents();
        // Check if hp bar is full
        verifyThat("#healthBar", (ProgressBar bar) -> bar.getProgress() == 1);

        // Update hp to 0
        Platform.runLater(() -> {
            Monster monster = MonsterBuilder.builder(currentMonster)
                    .setCurrentAttributes(new MonsterAttributes(0f, 4f, 3f, 2f))
                    .create();
            monsterItem = new MonsterItemController(monster, resourceService);
            app.show(monsterItem);
        });
        waitForFxEvents();
        // Check if hp bar is empty
        verifyThat("#healthBar", (ProgressBar bar) -> bar.getProgress() == 0);
    }

    @Test
    void testStatus() {
        // Check initial status
        verifyThat("#effect_burned", Node::isVisible);
        verifyThat("#effect_confused", Node::isVisible);

        //Rerender to add poisoned status
        Platform.runLater(() -> {
            Monster monster = MonsterBuilder.builder(currentMonster)
                    .addStatus(MonsterStatus.POISONED)
                    .create();
            monsterItem = new MonsterItemController(monster, resourceService);
            app.show(monsterItem);
        });
        waitForFxEvents();
        // Check if poisoned status is visible
        verifyThat("#effect_poisoned", Node::isVisible);
    }

}