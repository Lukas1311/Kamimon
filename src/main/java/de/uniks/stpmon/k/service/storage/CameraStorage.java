package de.uniks.stpmon.k.service.storage;

import javafx.scene.PerspectiveCamera;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CameraStorage {

    private PerspectiveCamera camera;

    @Inject
    public CameraStorage() {
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }

    public void setCamera(PerspectiveCamera camera) {
        this.camera = camera;
    }

}
