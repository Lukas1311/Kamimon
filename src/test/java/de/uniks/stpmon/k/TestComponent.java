package de.uniks.stpmon.k;

import dagger.Component;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.service.UserStorage;
import de.uniks.stpmon.k.ws.EventListener;

import javax.inject.Singleton;

@Component(modules = {TestModule.class, GroupTestModule.class, MessageTestModule.class, BoundModule.class})
@Singleton
public interface TestComponent extends MainComponent {

    HybridController hybridController();

    UserStorage userStorage();

    EventListener eventListener();
    @Component.Builder
    interface Builder extends MainComponent.Builder {

        @Override
        TestComponent build();
    }
}
