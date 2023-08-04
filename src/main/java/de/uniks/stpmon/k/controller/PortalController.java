package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.service.sources.IPortalController;
import de.uniks.stpmon.k.service.sources.PortalSource;
import de.uniks.stpmon.k.service.world.WorldLoader;
import javafx.application.Platform;

import javax.inject.Inject;
import javax.inject.Provider;

import static de.uniks.stpmon.k.controller.sidebar.MainWindow.INGAME;

public class PortalController extends ToastedController implements IPortalController {

    @Inject
    LoadingRegionController loadingRegionController;
    @Inject
    PortalSource portalSource;
    @Inject
    WorldLoader worldLoader;

    @Inject
    Provider<HybridController> hybridControllerProvider;

    private boolean mainPortal = false;

    @Override
    public void init() {
        super.init();
        if (portalSource != null && portalSource.getPortalController() == null) {
            portalSource.setPortalController(this);
            mainPortal = true;
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (portalSource != null && mainPortal) {
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
                loadingRegionController.startLoading(() -> loadingRegionController.subscribe(worldLoader.loadWorld(),
                        () -> {
                            app.show(hybridControllerProvider.get());
                            hybridControllerProvider.get().openMain(INGAME);
                        })));
    }

}
