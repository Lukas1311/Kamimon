package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.sources.IPortalController;
import de.uniks.stpmon.k.service.sources.PortalSource;
import de.uniks.stpmon.k.service.world.WorldLoader;
import javafx.application.Platform;

import javax.inject.Inject;
import javax.inject.Provider;

import static de.uniks.stpmon.k.controller.sidebar.MainWindow.INGAME;

public class PortalController extends ToastedController implements IPortalController {
    @Inject
    LoadingScreenController loadingScreen;
    @Inject
    LoadingRegionController loadingRegionController;
    @Inject
    RegionService regionService;
    @Inject
    PortalSource portalSource;
    @Inject
    WorldLoader worldLoader;

    @Inject
    Provider<HybridController> hybridControllerProvider;

    @Override
    public void init() {
        super.init();
        if (portalSource != null) {
            portalSource.setPortalController(this);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (portalSource != null) {
            portalSource.setPortalController(null);
        }
    }

    public void enterRegion(Region region) {
        subscribe(worldLoader.tryEnterRegion(region), (trainer) -> {
            // ignore because load world is called async by the portal service
        });
    }

    @Override
    public void loadWorld() {
        Platform.runLater(() ->
                loadingScreen.startLoading(() ->
                        loadingScreen.subscribe(worldLoader.getOrLoadWorld(),
                                (area) -> {
                            app.show(hybridControllerProvider.get());
                            hybridControllerProvider.get().openMain(INGAME);
                        })));
    }
}
