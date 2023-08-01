package de.uniks.stpmon.k.controller.monsters;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.di.DaggerTestComponent;
import de.uniks.stpmon.k.di.TestComponent;
import de.uniks.stpmon.k.service.InputHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MonsterBarControllerTest extends ApplicationTest {

    @Mock
    private ImageView monsterSlot;

    @Mock
    TeamController teamController;

    @Spy
    @SuppressWarnings("unused")
    InputHandler inputHandler;

    @Spy
    private final App app = new App(null);

    private final TestComponent component = (TestComponent) DaggerTestComponent.builder().mainApp(app).build();

    @InjectMocks
    MonsterBarController monsterBarController;

    @Override
    public void start(Stage stage) {
        // show app
        app.setMainComponent(component);
        app.start(stage);
        app.show(monsterBarController);
        stage.requestFocus();
    }

    @AfterEach
    void afterEach() {
        // Remove used input handler
        app.removeInputHandler(component);
    }

    @Test
    public void testSetMonsterStatus_ZeroHP() throws InterruptedException {
        // Load the image for zero HP
        Image zeroHP = new Image(Objects.requireNonNull(
            getClass().getResource("/de/uniks/stpmon/k/controller/monsters/monsterbar/healthPointsZero.png")
        ).toString());
        
        // Set the mock
        when(monsterSlot.getImage()).thenReturn(zeroHP);

        monsterBarController.setMonsterStatus(0, 0, 100, false);
        Thread.sleep(1000);

        // Check if the image is the same when HP is 0%
        assertEquals(zeroHP, monsterSlot.getImage());
    }

    @Test
    public void testSetMonsterStatus_NormalHP() throws InterruptedException {
        // Load the image for normal HP
        Image normalHP = new Image(Objects.requireNonNull(
            getClass().getResource("/de/uniks/stpmon/k/controller/monsters/monsterbar/healthPointsNormal.png")
        ).toString());

        // Set the mock
        when(monsterSlot.getImage()).thenReturn(normalHP);

        monsterBarController.setMonsterStatus(1, 50, 100, false);
        Thread.sleep(1000);

        // Check if the image is the same when HP is higher than 20%
        assertEquals(normalHP, monsterSlot.getImage());
    }

    @Test
    public void testSetMonsterStatus_LowHP() throws InterruptedException {
        // Load the image for low HP
        Image lowHP = new Image(Objects.requireNonNull(
            getClass().getResource("/de/uniks/stpmon/k/controller/monsters/monsterbar/healthPointsLow.png")
        ).toString());

        // Set the mock
        when(monsterSlot.getImage()).thenReturn(lowHP);

        monsterBarController.setMonsterStatus(2, 10, 100, false);
        Thread.sleep(1000);

        // Check if the image is the same when HP is lower than 20%
        assertEquals(lowHP, monsterSlot.getImage());
    }

    @Test
    public void testShowMonsters() {
        VBox monsterList = new VBox();
        when(teamController.render()).thenReturn(monsterList);

        // Click on monster bar to show the popup
        clickOn("#monsterBar");

        // Check if the monster list is showing
        assertTrue(monsterList.isVisible());
    }

}
