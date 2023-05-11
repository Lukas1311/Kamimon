package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import javafx.scene.layout.Pane;
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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HybridControllerTest extends ApplicationTest {

    @Mock
    private Provider<SidebarController> sidebarController;
    @Mock
    private FriendListController friendListController;
    @Mock
    private Provider<LobbyController> lobbyController;
    @Mock
    private PauseController pauseController;
    @Mock
    private IngameController ingameController;
    @Mock
    private ChatListController chatListController;

    private final Pane pane = new Pane();

    @Spy
    App app = new App(null);

    @InjectMocks
    private HybridController hybridController;

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        SidebarController sideMock = Mockito.mock(SidebarController.class);
        when(sidebarController.get()).thenReturn(sideMock);
        doNothing().when(app).show(sideMock);
        hybridController.sidebarController = sidebarController;
        hybridController.pane = pane;
        app.show(hybridController);
        stage.requestFocus();
    }


    @Test
    public void testOpenSidebar() {


    }
}