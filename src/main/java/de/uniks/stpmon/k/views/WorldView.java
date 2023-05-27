package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.Viewable;
import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.TileMapService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.utils.MeshUtils;
import de.uniks.stpmon.k.utils.TileMap;
import javafx.event.EventHandler;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;

import static de.uniks.stpmon.k.utils.ImageUtils.scaledImageFX;

@Singleton
public class WorldView extends Viewable {

    public static final int MOVMENT_UNIT = 8;
    public static double IMAGE_SCALE = 2.0;

    @Inject
    protected TileMapService tileMapService;
    @Inject
    protected RegionService regionService;
    @Inject
    protected RegionStorage regionStorage;

    @Inject
    public WorldView() {
    }

    private Material createMaterial(Image image) {
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(image);
        return material;
    }

    private MeshView createFloorScaled(String path) {
        Image scaledimage = scaledImageFX(path, IMAGE_SCALE);

        return createFloor(scaledimage,
                (int) (scaledimage.getWidth() / IMAGE_SCALE),
                (int) (scaledimage.getHeight() / IMAGE_SCALE));
    }

    private MeshView createFloor(Image image, int width, int height) {
        MeshView floor = MeshUtils.createPlane(width, height);
        floor.setDrawMode(DrawMode.FILL);
        floor.setCullFace(CullFace.BACK);
        floor.setMaterial(createMaterial(image));

        return floor;
    }

    private MeshView createEntityScaled(String path, int angle) {
        Image scaledimage = scaledImageFX(path, IMAGE_SCALE);
        return createEntity(scaledimage,
                (int) (scaledimage.getWidth() / IMAGE_SCALE),
                (int) (scaledimage.getHeight() / IMAGE_SCALE), angle);
    }

    private MeshView createEntity(Image image, int width, int height, int angle) {
        MeshView entity = MeshUtils.createRectangle(width, height);
        entity.setDrawMode(DrawMode.FILL);
        entity.setCullFace(CullFace.BACK);
        entity.setTranslateY(-16);
        entity.setTranslateX(8);
        entity.setTranslateZ(-8);
        entity.setRotationAxis(Rotate.X_AXIS);
        entity.setRotate(angle);
        entity.setMaterial(createMaterial(image));
        return entity;
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

        MeshView character = createEntityScaled("map/char.png", angle);
        character.setId("character");

        camera.translateXProperty().addListener((observable, oldValue, newValue) ->
                character.setTranslateX(character.getTranslateX() - ((double) oldValue - (double) newValue)));
        camera.translateZProperty().addListener((observable, oldValue, newValue) ->
                character.setTranslateZ(character.getTranslateZ() - ((double) oldValue - (double) newValue)));
        camera.rotateProperty().addListener((observable, oldValue, newValue) ->
                character.setRotate(character.getRotate() - ((double) oldValue - (double) newValue)));

        MeshView floor = createFloorScaled("map/natchester.png");
        floor.setId("floor");
        floor.setTranslateX(1280.0 / 2);
        floor.setTranslateZ(-1280.0 / 2);

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
        Area area = regionStorage.getArea();
        if (area == null || area.map() == null) {
            return;
        }
        TileMap tileMap = tileMapService.createMap(area);
        BufferedImage image = tileMap.renderMap();
    }

    private static EventHandler<KeyEvent> keyPressed(PerspectiveCamera camera) {
        return (event) -> {
            System.out.println("Key pressed: " + event.getCode());
            switch (event.getCode()) {
                case W -> camera.setTranslateZ(camera.getTranslateZ() + MOVMENT_UNIT);
                case S -> camera.setTranslateZ(camera.getTranslateZ() - MOVMENT_UNIT);
                case A -> camera.setTranslateX(camera.getTranslateX() - MOVMENT_UNIT);
                case D -> camera.setTranslateX(camera.getTranslateX() + MOVMENT_UNIT);
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
