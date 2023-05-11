package de.uniks.stpmon.k;

import dagger.Component;

import javax.inject.Singleton;

@Component(modules = {TestModule.class, GroupTestModule.class, MessageTestModule.class})
@Singleton
public interface TestComponent {
    @Component.Builder
    interface Builder extends MainComponent.Builder {

    }
}
