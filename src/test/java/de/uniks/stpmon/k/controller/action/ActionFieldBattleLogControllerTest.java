package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.App;
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

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ActionFieldBattleLogControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);

    @Mock
    Provider<ActionFieldChangeMonsterController> actionFieldChangeMonsterController;
    @Mock
    Provider<ActionFieldChooseAbilityController> actionFieldChangeAbilityController;
    @Mock
    Provider<ActionFieldChooseOpponentController> actionFieldChooseOpponentController;

    @InjectMocks
    ActionFieldBattleLogController actionFieldBattleLogController;


    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        app.show(actionFieldBattleLogController);

        stage.requestFocus();
    }

    @Test
    void testRender() {
        ActionFieldChangeMonsterController changeMonsterMock = Mockito.mock(ActionFieldChangeMonsterController.class);
        when(actionFieldChangeMonsterController.get()).thenReturn(changeMonsterMock);

        changeMonsterMock.selectedUserMonster = "UserMonsterName";
    }
}
