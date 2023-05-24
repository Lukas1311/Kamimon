package de.uniks.stpmon.k;

import dagger.Component;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.service.UserStorage;
import de.uniks.stpmon.k.ws.EventListener;

import javax.inject.Singleton;

@Component(modules = {TestModule.class, AuthTestModule.class, GroupTestModule.class, MessageTestModule.class,
        RegionTestModule.class, UserTestModule.class, BoundTestModule.class
})
@Singleton
public interface TestComponent extends MainComponent {

    HybridController hybridController();

    UserStorage userStorage();

    EventListener eventListener();

    MessageApiDummy messageApi();

    @Component.Builder
    interface Builder extends MainComponent.Builder {

        @Override
        TestComponent build();
    }
}
