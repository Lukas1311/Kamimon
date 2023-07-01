package de.uniks.stpmon.k.service.sources;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PortalSource {

    private IPortalController portalController;
    private boolean teleporting;

    @Inject
    public PortalSource() {
    }

    public void setPortalController(IPortalController portalController) {
        this.portalController = portalController;
    }

    public IPortalController getPortalController() {
        return portalController;
    }

    public boolean isTeleporting() {
        return teleporting;
    }

    public void setTeleporting(boolean teleporting) {
        this.teleporting = teleporting;
    }

}
