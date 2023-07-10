package de.uniks.stpmon.k.di;

import dagger.BindsInstance;
import dagger.Component;
import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.LoadingScreenController;
import de.uniks.stpmon.k.controller.LoginController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.service.AuthenticationService;
import de.uniks.stpmon.k.service.ILifecycleService;
import de.uniks.stpmon.k.service.InputHandler;

import javax.inject.Singleton;
import java.util.Set;

@Component(modules = {MainModule.class, HttpModule.class, PrefModule.class, BoundModule.class})
@Singleton
public interface MainComponent {

    AuthenticationService authenticationService();

    LoginController loginController();

    @SuppressWarnings("EmptyMethod")
    HybridController hybridController();

    LoadingScreenController loadingScreenController();

    InputHandler inputHandler();

    Set<ILifecycleService> lifecycleServices();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder mainApp(App app);

        @SuppressWarnings("EmptyMethod")
        MainComponent build();

    }

}
