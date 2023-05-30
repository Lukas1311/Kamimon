package de.uniks.stpmon.k.di;

import dagger.Component;
import de.uniks.stpmon.k.TestModule;
import de.uniks.stpmon.k.UserTestModule;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.service.dummies.MessageApiDummy;
import de.uniks.stpmon.k.service.storage.UserStorage;
import de.uniks.stpmon.k.views.WorldView;

import javax.inject.Singleton;

@Component(modules = {TestModule.class, AuthTestModule.class, GroupTestModule.class,
        MessageTestModule.class, RegionTestModule.class, UserTestModule.class,
        BoundTestModule.class, PresetsTestModule.class})

@Singleton
public interface TestComponent extends MainComponent {

    HybridController hybridController();

    UserStorage userStorage();

    EventListener eventListener();

    MessageApiDummy messageApi();

    WorldView worldView();

    @Component.Builder
    interface Builder extends MainComponent.Builder {

        @Override
        TestComponent build();
    }
}
