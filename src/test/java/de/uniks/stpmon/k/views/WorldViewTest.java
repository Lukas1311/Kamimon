package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.IngameController;
import de.uniks.stpmon.k.controller.MonsterBarController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.di.DaggerTestComponent;
import de.uniks.stpmon.k.di.TestComponent;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.MeshView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class WorldViewTest extends ApplicationTest {

    private final App app = new App(null);
    private final TestComponent component = (TestComponent) DaggerTestComponent.builder().mainApp(app).build();
    @Spy
    @SuppressWarnings("unused")
    public WorldView worldView = component.worldView();
    @Spy
    @SuppressWarnings("unused")
    public HybridController hybridController = component.hybridController();
    @Mock
    @SuppressWarnings("unused")
    public Provider<HybridController> hybridControllerProvider;
    @Mock
    @SuppressWarnings("unused")
    MonsterBarController monsterBarController;

    @InjectMocks
    public IngameController ingameController;

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        //when(hybridControllerProvider.get()).thenReturn(hybridController);
        //doNothing().when(hybridController).closeTab();

        // show app
        app.start(stage);
        app.show(ingameController);
        stage.requestFocus();
    }

    @Test
    public void moveCharacter() {
        SubScene node = lookup("#worldScene").queryAs(SubScene.class);
        Parent root = node.getRoot();
        MeshView character = (MeshView) root.lookup("#character");
        assertEquals(0, character.getTranslateX());

        // test move up
        type(KeyCode.W);
        waitForFxEvents();

        // check if char moved up by 5
        assertEquals(5, character.getTranslateZ());

        // test move down
        type(KeyCode.S);
        waitForFxEvents();

        // check if char moved down by 5
        assertEquals(0, character.getTranslateZ());

        // test move left
        type(KeyCode.A);
        waitForFxEvents();

        // check if char moved left by 5
        assertEquals(-5, character.getTranslateX());

        // test move right
        type(KeyCode.D);
        waitForFxEvents();

        // check if char moved right by 5
        assertEquals(0, character.getTranslateX());
    }
}