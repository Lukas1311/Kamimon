package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.controller.Viewable;
import de.uniks.stpmon.k.models.Region;
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

    public static final int MOVEMENT_UNIT = 8;

    @Inject
    protected RegionStorage regionStorage;
    @Inject
    protected CharacterView characterView;
    @Inject
    protected FloorView floorView;
    @Inject
    protected PropView propView;

    @Inject
    public WorldView() {
    }

    private PerspectiveCamera createCamera(int angle) {
        PerspectiveCamera camera = new PerspectiveCamera(true);
        // move the far clip to see more of the scene
        camera.setFarClip(10000.0);
        camera.getTransforms()
                .addAll(
                        new Rotate(angle, Rotate.X_AXIS),
                        new Translate(0, 0, -480));
        camera.setRotationAxis(Rotate.X_AXIS);
        return camera;
    }

    public SubScene renderScene() {
        int angle = -59;
        PerspectiveCamera camera = createCamera(angle);
        int x = 0;
        int y = 0;
        Region region = regionStorage.getRegion();
        if (region != null) {
            x += region.spawn().x() * 16;
            y += (region.spawn().y() + 1) * 16;
        }
        Node character = characterView.render(angle, camera);

        Node floor = floorView.render(angle, camera);
        camera.setTranslateX(x);
        camera.setTranslateZ(-y);

        Node props = propView.render(angle, camera);


        // Lights all objects from all sides
        AmbientLight ambient = new AmbientLight();
        ambient.setLightOn(true);

        Group root = new Group(floor, ambient, character, props);

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
        propView.init();
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
                case W -> camera.setTranslateZ(camera.getTranslateZ() + MOVEMENT_UNIT);
                case S -> camera.setTranslateZ(camera.getTranslateZ() - MOVEMENT_UNIT);
                case A -> camera.setTranslateX(camera.getTranslateX() - MOVEMENT_UNIT);
                case D -> camera.setTranslateX(camera.getTranslateX() + MOVEMENT_UNIT);
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
