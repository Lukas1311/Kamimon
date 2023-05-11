package de.uniks.stpmon.k;

import dagger.Component;
import de.uniks.stpmon.k.controller.GroupTestModule;

import javax.inject.Singleton;

@Component(modules = {TestModule.class, GroupTestModule.class})
@Singleton
public interface TestComponent {
    @Component.Builder
    interface Builder extends MainComponent.Builder {

    }
}
