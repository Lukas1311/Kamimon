package de.uniks.stpmon.k;

import dagger.Component;
import de.uniks.stpmon.k.controller.HybridController;
import de.uniks.stpmon.k.service.UserStorage;

import javax.inject.Singleton;

@Component(modules = {TestModule.class, GroupTestModule.class, MessageTestModule.class})
@Singleton
public interface TestComponent extends MainComponent {

    HybridController hybridController();

    UserStorage userStorage();
    @Component.Builder
    interface Builder extends MainComponent.Builder {

        @Override
        TestComponent build();
    }
}
