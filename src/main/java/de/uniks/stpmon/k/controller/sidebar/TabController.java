package de.uniks.stpmon.k.controller.sidebar;

import de.uniks.stpmon.k.controller.ToastedController;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.function.Consumer;

public class TabController extends ToastedController {

    @Inject
    Provider<HybridController> hybridControllerProvider;

    @Inject
    public TabController() {
    }

    /**
     * Pop the current tab from the tab stack
     */
    protected void popTab() {
        hybridControllerProvider.get().popTab();
    }

    /**
     * Push a new tab to the tab stack
     */
    @SuppressWarnings("SameParameterValue")
    protected void pushTab(SidebarTab tab) {
        hybridControllerProvider.get().pushTab(tab);
    }

    /**
     * Open a new tab on the tab stack.
     */
    protected void openTab(Consumer<HybridController> callback) {
        callback.accept(hybridControllerProvider.get());
    }

}
