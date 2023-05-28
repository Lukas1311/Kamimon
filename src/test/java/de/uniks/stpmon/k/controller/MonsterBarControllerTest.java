package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MonsterBarControllerTest extends ApplicationTest {
    @Mock
    private ImageView monsterSlot;

    @Mock
    MonsterListController monsterListController;

    @Spy
    App app = new App(null);

    @InjectMocks
    MonsterBarController monsterBarController;

    @Override
    public void start(Stage stage) throws Exception {
        // show app
        app.start(stage);
        app.show(monsterBarController);
        stage.requestFocus();
    }

    @Test
    public void testSetMonsterStatus_ZeroHP() throws InterruptedException {
        // Load the image for zero HP
        Image zeroHP = new Image(Objects.requireNonNull(getClass().getResource("/de/uniks/stpmon/k/controller/healthPointsZero.png")).toString());
        // Set the mock
        when(monsterSlot.getImage()).thenReturn(zeroHP);

        monsterBarController.setMonsterStatus(0, 0, 100);
        Thread.sleep(1000);

        // Check if the image is the same when HP is 0%
        assertEquals(zeroHP, monsterSlot.getImage());
    }

    @Test
    public void testSetMonsterStatus_NormalHP() throws InterruptedException {
        // Load the image for normal HP
        Image normalHP = new Image(Objects.requireNonNull(getClass().getResource("/de/uniks/stpmon/k/controller/healthPointsNormal.png")).toString());
        // Set the mock
        when(monsterSlot.getImage()).thenReturn(normalHP);

        monsterBarController.setMonsterStatus(1, 50, 100);
        Thread.sleep(1000);

        // Check if the image is the same when HP is higher than 20%
        assertEquals(normalHP, monsterSlot.getImage());
    }

    @Test
    public void testSetMonsterStatus_LowHP() throws InterruptedException {
        // Load the image for low HP
        Image lowHP = new Image(Objects.requireNonNull(getClass().getResource("/de/uniks/stpmon/k/controller/healthPointsLow.png")).toString());
        // Set the mock
        when(monsterSlot.getImage()).thenReturn(lowHP);

        monsterBarController.setMonsterStatus(2, 10, 100);
        Thread.sleep(1000);

        // Check if the image is the same when HP is lower than 20%
        assertEquals(lowHP, monsterSlot.getImage());
    }

    @Test
    public void testShowMonsters() {
        Pane monsterList = new Pane();
        when(monsterListController.render()).thenReturn(monsterList);

        Platform.runLater(() -> {
            // Show the popup
            monsterBarController.showMonsters();

            // Check if the monster list is showing
            assertTrue(monsterBarController.monsterListPopup.isShowing());

            // Hide the popup
            monsterBarController.showMonsters();

            // Check if the monster list is hidden
            assertFalse(monsterBarController.monsterListPopup.isShowing());

        });
    }
}
