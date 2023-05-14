package de.uniks.stpmon.k;

import dagger.Component;

import javax.inject.Singleton;

@Component(modules = {TestModule.class, GroupTestModule.class, MessageTestModule.class, BoundModule.class})
@Singleton
public interface TestComponent extends MainComponent {
    @Component.Builder
    interface Builder extends MainComponent.Builder {
        TestComponent build();
    }
}
