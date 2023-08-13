package de.uniks.stpmon.k.views.world;

import com.sun.prism.impl.Disposer;
import de.uniks.stpmon.k.controller.Viewable;
import de.uniks.stpmon.k.service.SettingsService;
import de.uniks.stpmon.k.service.storage.CameraStorage;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.WorldRepository;
import de.uniks.stpmon.k.service.world.ClockService;
import de.uniks.stpmon.k.service.world.WorldService;
import de.uniks.stpmon.k.world.ShadowTransform;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalTime;

@Singleton
public class WorldView extends Viewable {

    public static final int WORLD_UNIT = 16;
    public static final int ENTITY_OFFSET_Y = 1;
    public static final int WORLD_ANGLE = -49;

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
    protected ClockService clockService;
    @Inject
    protected WorldService worldService;
    @Inject
    protected SettingsService settingsService;
    private ShadowTransform lastShadowTransform = ShadowTransform.DEFAULT_ENABLED;
    private AmbientLight ambient;

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

        // Disable shadows for indoor worlds
        if (storage.isIndoor()) {
            propView.updateShadow(ShadowTransform.DEFAULT_DISABLED);
            characterView.updateShadow(ShadowTransform.DEFAULT_DISABLED);
            npcCollectiveView.updateShadow(ShadowTransform.DEFAULT_DISABLED);
        }

        // Lights all objects from all sides
        ambient = new AmbientLight();
        ambient.setColor(Color.WHITE);

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
        if (storage.isIndoor()) {
            return;
        }
        subscribe(clockService.onTime(), (time) -> {
            ShadowTransform transform = worldService.getShadowTransform(time);
            if (transform.equals(lastShadowTransform)) {
                return;
            }
            updateShadows(transform);
            if (ambient != null) {
                ambient.setColor(worldService.getWorldColor(time));
            }
        });
        subscribe(settingsService.onNightModeEnabled(), (enabled) -> {
            if (ambient == null) {
                return;
            }
            if (!enabled) {
                ambient.setColor(Color.WHITE);
                updateShadows(ShadowTransform.DEFAULT_DISABLED);
                return;
            }
            LocalTime time = clockService.onTime().blockingFirst();
            updateShadows(worldService.getShadowTransform(time));
            ambient.setColor(worldService.getWorldColor(time));
        });
    }

    private void updateShadows(ShadowTransform transform) {
        if (transform.equals(lastShadowTransform)) {
            return;
        }
        lastShadowTransform = transform;
        propView.updateShadow(transform);
        characterView.updateShadow(transform);
        npcCollectiveView.updateShadow(transform);
    }

    @Override
    public void destroy() {
        super.destroy();

        ambient = null;
        lastShadowTransform = ShadowTransform.DEFAULT_ENABLED;
        characterView.destroy();
        floorView.destroy();
        propView.destroy();
        npcCollectiveView.destroy();
        cameraStorage.setCamera(null);
        Disposer.cleanUp();
        System.gc();
    }

}
