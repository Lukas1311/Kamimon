package de.uniks.stpmon.k.di;

import dagger.Component;
import de.uniks.stpmon.k.TestModule;
import de.uniks.stpmon.k.UserTestModule;
import de.uniks.stpmon.k.controller.WorldController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.dummies.MessageApiDummy;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.UserStorage;
import de.uniks.stpmon.k.service.storage.WorldRepository;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;

import javax.inject.Singleton;

@Component(modules = {TestModule.class, AuthTestModule.class, GroupTestModule.class,
        MessageTestModule.class, RegionTestModule.class, UserTestModule.class,
        BoundTestModule.class, PresetsTestModule.class, EncounterTestModule.class})

@Singleton
public interface TestComponent extends MainComponent {

    HybridController hybridController();

    UserStorage userStorage();

    WorldRepository worldStorage();

    EventListener eventListener();

    MessageApiDummy messageApi();

    WorldController worldController();

    TrainerStorage trainerStorage();

    RegionStorage regionStorage();

    RegionService regionService();

    CacheManager cacheManager();

    EventDummy eventDummy();

    @Component.Builder
    interface Builder extends MainComponent.Builder {

        @Override
        TestComponent build();

    }

}
