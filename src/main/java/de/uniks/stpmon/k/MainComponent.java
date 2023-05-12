package de.uniks.stpmon.k;

import dagger.BindsInstance;
import dagger.Component;
import de.uniks.stpmon.k.controller.HybridController;
import de.uniks.stpmon.k.controller.LoadingScreenController;
import de.uniks.stpmon.k.controller.LoginController;
import de.uniks.stpmon.k.service.AuthenticationService;
import de.uniks.stpmon.k.service.NetworkAvailability;

import javax.inject.Singleton;

@Component(modules = {MainModule.class, HttpModule.class, PrefModule.class})
@Singleton
public interface MainComponent {
    NetworkAvailability networkAvailability();
    AuthenticationService authenticationService();
    LoginController loginController();
    HybridController hybridController();
    LoadingScreenController loadingScreenController();

    @Component.Builder
    interface Builder{
        @BindsInstance
        Builder mainApp(App app);
        MainComponent build();
    }
}
