package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.controller.Viewable;
import de.uniks.stpmon.k.service.storage.CameraStorage;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.WorldRepository;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorldView extends Viewable {

    public static final int WORLD_UNIT = 16;
    public static final int ENTITY_OFFSET_Y = 1;
    public static final int WORLD_ANGLE = -59;

    @Inject
    protected RegionStorage regionStorage;
    @Inject
    protected CharacterView characterView;
    @Inject
    protected WorldRepository storage;
    @Inject
    protected FloorView floorView;
    @Inject
    protected PropView propView;
    @Inject
    protected NPCCollectiveView npcCollectiveView;

    @Inject
    protected CameraStorage cameraStorage;

    @Inject
    public WorldView() {
    }

    private PerspectiveCamera createCamera() {
        PerspectiveCamera camera = new PerspectiveCamera(true);
        // move the far clip to see more of the scene
        camera.setFarClip(10000.0);
        camera.getTransforms()
                .addAll(
                        new Rotate(WORLD_ANGLE, Rotate.X_AXIS),
                        new Translate(0, 0, -480));
        camera.setRotationAxis(Rotate.X_AXIS);
        return camera;
    }

    protected Group render() {
        if (storage.isEmpty()) {
            return new Group();
        }
        Node character = characterView.render();
        Node floor = floorView.render();
        Node props = propView.render();
        Node npc = npcCollectiveView.render();

        // Lights all objects from all sides
        AmbientLight ambient = new AmbientLight();
        ambient.setLightOn(true);

        return new Group(floor, ambient, props, character, npc);
    }

    public SubScene createScene() {
        PerspectiveCamera camera = createCamera();
        if (cameraStorage != null) {
            cameraStorage.setCamera(camera);
        }
        initChildren();
        Group root;
        if (storage.isEmpty()) {
            root = new Group();
        } else {
            root = render();
        }

        // Create sub scene
        SubScene scene = new SubScene(root, 450, 400, true, SceneAntialiasing.DISABLED);
        // Set background color to blackish
        scene.setFill(Color.web("0x13120C"));
        scene.setCamera(camera);
        scene.setId("worldScene");

        return scene;
    }

    private void initChildren() {
        characterView.init();
        floorView.init();
        propView.init();
        npcCollectiveView.init();
    }

    @Override
    public void destroy() {
        super.destroy();

        characterView.destroy();
        floorView.destroy();
        propView.destroy();
        npcCollectiveView.destroy();
    }
}
