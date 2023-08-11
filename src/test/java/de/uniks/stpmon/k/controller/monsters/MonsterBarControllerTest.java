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
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

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

    final Image imageMock = Mockito.mock(Image.class);

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
    public void testSetMonsterStatus_ZeroHP() {
        // Set the mock
        when(monsterSlot.getImage()).thenReturn(imageMock);

        monsterBarController.setMonsterStatus(0, 0, 100, false);

        // Check if the image is the same when HP is 0%
        assertEquals(imageMock, monsterSlot.getImage());
    }

    @Test
    public void testSetMonsterStatus_NormalHP() {
        // Set the mock
        when(monsterSlot.getImage()).thenReturn(imageMock);

        monsterBarController.setMonsterStatus(1, 50, 100, false);

        // Check if the image is the same when HP is higher than 20%
        assertEquals(imageMock, monsterSlot.getImage());
    }

    @Test
    public void testSetMonsterStatus_LowHP() {
        // Set the mock
        when(monsterSlot.getImage()).thenReturn(imageMock);

        monsterBarController.setMonsterStatus(2, 10, 100, false);

        // Check if the image is the same when HP is lower than 20%
        assertEquals(imageMock, monsterSlot.getImage());
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
