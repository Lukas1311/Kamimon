package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.WorldLoader;
import de.uniks.stpmon.k.service.sources.IPortalController;
import de.uniks.stpmon.k.service.sources.PortalSource;
import javafx.application.Platform;

import javax.inject.Inject;
import javax.inject.Provider;

import static de.uniks.stpmon.k.controller.sidebar.MainWindow.INGAME;

public class PortalController extends ToastedController implements IPortalController {
    @Inject
    LoadingScreenController loadingScreen;
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
        portalSource.setPortalController(this);
    }

    @Override
    public void destroy() {
        super.destroy();
        portalSource.setPortalController(null);
    }

    public void enterRegion(Region region) {
        subscribe(worldLoader.enterRegion(region), (trainer) -> {
            // ignore because load world is called async by the portal service
        });
    }

    @Override
    public void loadWorld() {
        Platform.runLater(() -> loadingScreen.startLoading(() ->
                loadingScreen.subscribe(worldLoader.getOrLoadWorld(),
                        (area) -> {
                            app.show(hybridControllerProvider.get());
                            hybridControllerProvider.get().openMain(INGAME);
                        })));
    }
}
