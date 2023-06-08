package de.uniks.stpmon.k.di;

import dagger.BindsInstance;
import dagger.Component;
import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.LoadingScreenController;
import de.uniks.stpmon.k.controller.LoginController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.service.AuthenticationService;
import de.uniks.stpmon.k.service.InputHandler;
import de.uniks.stpmon.k.service.storage.IFriendCache;

import javax.inject.Singleton;

@Component(modules = {MainModule.class, HttpModule.class, PrefModule.class, BoundModule.class})
@Singleton
public interface MainComponent {

    AuthenticationService authenticationService();

    LoginController loginController();

    HybridController hybridController();

    LoadingScreenController loadingScreenController();

    IFriendCache friendCache();

    InputHandler inputHandler();

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder mainApp(App app);

        MainComponent build();
    }
}
