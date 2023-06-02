package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.service.RegionService;

import javax.inject.Inject;
import javax.inject.Provider;

import static de.uniks.stpmon.k.controller.sidebar.MainWindow.INGAME;

public class PortalController extends ToastedController {
    @Inject
    LoadingScreenController loadingScreen;
    @Inject
    RegionService regionService;

    @Inject
    Provider<HybridController> hybridControllerProvider;

    public void enterRegion(Region region) {
        loadingScreen.startLoading(() ->
                subscribe(regionService.enterRegion(region),
                        (area) -> {
                            app.show(hybridControllerProvider.get());
                            hybridControllerProvider.get().openMain(INGAME);
                        }));
    }
}
