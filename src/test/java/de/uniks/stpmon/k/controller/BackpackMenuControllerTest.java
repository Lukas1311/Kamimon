package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BackpackMenuControllerTest extends ApplicationTest {

    @Spy
    App app = new App(null);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;
    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);

    @Mock
    BackpackController backpackController;
    @Spy
    Provider<MonsterBarController> monsterBarControllerProvider;

    @InjectMocks
    BackpackMenuController backpackMenuController;

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        app.show(backpackMenuController);
        stage.requestFocus();
    }

    @Test
    void hoverOver() {
        Label label = lookup("#backpackMenuSelectedLabel_0").query();
        moveTo(label);
        assertEquals(">", label.getText());
        Label label2 = lookup("#backpackMenuSelectedLabel_1").query();
        moveTo(label2);
        assertEquals("", label.getText());
    }

    @Test
    void clickOnMonsters() {
        doNothing().when(backpackController).closeBackPackMenu();
        MonsterBarController monsterBarController = Mockito.mock(MonsterBarController.class);
        when(monsterBarControllerProvider.get()).thenReturn(monsterBarController);
        doNothing().when(monsterBarController).showMonsters();

        Label label = lookup("#backpackMenuLabel_1").query();
        clickOn(label);
        verify(monsterBarController).showMonsters();

    }

}