package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.controller.Viewable;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import javafx.event.EventHandler;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorldView extends Viewable {

    @Inject
    protected RegionStorage regionStorage;
    @Inject
    protected CharacterView characterView;
    @Inject
    protected FloorView floorView;

    @Inject
    public WorldView() {
    }

    private PerspectiveCamera createCamera(int angle) {
        PerspectiveCamera camera = new PerspectiveCamera(true);
        // move the far clip to see more of the scene
        camera.setFarClip(10000.0);
        camera.getTransforms()
                .addAll(new Rotate(angle, Rotate.X_AXIS),
                        new Translate(0, 0, -620));
        camera.setRotationAxis(Rotate.X_AXIS);
        return camera;
    }

    public SubScene renderScene() {
        int angle = -45;
        PerspectiveCamera camera = createCamera(angle);

        Node character = characterView.render(angle, camera);

        Node floor = floorView.render(angle, camera);

        // Lights all objects from all sides
        AmbientLight ambient = new AmbientLight();
        ambient.setLightOn(true);

        Group root = new Group(floor, ambient, character);

        app.getStage()
                .getScene()
                .setOnKeyPressed(keyPressed(camera));

        // Create sub scene
        SubScene scene = new SubScene(root, 450, 400, true, SceneAntialiasing.DISABLED);
        // Set background color to blackish
        scene.setFill(Color.web("0x13120C"));
        scene.setCamera(camera);
        scene.setId("worldScene");

        return scene;
    }

    @Override
    public void init() {
        if (regionStorage.isEmpty()) {
            return;
        }
        characterView.init();
        floorView.init();
    }

    @Override
    public void destroy() {
        super.destroy();

        characterView.destroy();
        floorView.destroy();
    }

    private static EventHandler<KeyEvent> keyPressed(PerspectiveCamera camera) {
        return (event) -> {
            System.out.println("Key pressed: " + event.getCode());
            switch (event.getCode()) {
                case W -> camera.setTranslateZ(camera.getTranslateZ() + 5);
                case S -> camera.setTranslateZ(camera.getTranslateZ() - 5);
                case A -> camera.setTranslateX(camera.getTranslateX() - 5);
                case D -> camera.setTranslateX(camera.getTranslateX() + 5);
            }
            rotate(event, camera);
        };
    }

    private static void rotate(KeyEvent event, PerspectiveCamera camera) {
        switch (event.getCode()) {
            case Q -> camera.setRotate(camera.getRotate() - 2.5);
            case E -> camera.setRotate(camera.getRotate() + 2.5);
        }
    }
}