package de.uniks.stpmon.k;

import dagger.BindsInstance;
import dagger.Component;
import de.uniks.stpmon.k.controller.LoginController;

import javax.inject.Singleton;

@Component(modules = {MainModule.class, HttpModule.class, PrefModule.class})
@Singleton
public interface MainComponent {
    // Service not implemented yet
    //LoginService loginService();
    LoginController loginController();

    //controller not implemented yet
    //LobbyController lobbyController();

    @Component.Builder
    interface Builder{
        @BindsInstance
        Builder mainApp(App app);
        MainComponent build();
    }
}
