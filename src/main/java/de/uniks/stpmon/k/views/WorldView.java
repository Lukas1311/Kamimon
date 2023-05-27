package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.Main;
import de.uniks.stpmon.k.controller.Viewable;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.TileMapService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.utils.MeshUtils;
import io.reactivex.rxjava3.schedulers.Schedulers;
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
import java.util.Objects;

@Singleton
public class WorldView extends Viewable {

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

    private MeshView createFloor(Image image) {
        double height = image.getHeight();
        double width = image.getWidth();
        MeshView floor = MeshUtils.createPlane((int) width * 2, (int) height * 2);
        floor.setDrawMode(DrawMode.FILL);
        floor.setScaleX(0.5);
        floor.setScaleY(0.5);
        floor.setScaleZ(0.5);
        floor.setCullFace(CullFace.BACK);
        floor.setMaterial(createMaterial(image));

        return floor;
    }

    private MeshView createEntity(Image image, int angle) {
        MeshView entity = MeshUtils.createRectangle(32, 64);
        entity.setDrawMode(DrawMode.FILL);
        entity.setScaleX(0.5);
        entity.setScaleY(0.5);
        entity.setScaleZ(0.5);
        entity.setCullFace(null);
        entity.setTranslateY(-14);
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

        MeshView character = createEntity(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("map/char.png"))), angle);

        camera.translateXProperty().addListener((observable, oldValue, newValue) ->
                character.setTranslateX(character.getTranslateX() - ((double) oldValue - (double) newValue)));
        camera.translateZProperty().addListener((observable, oldValue, newValue) ->
                character.setTranslateZ(character.getTranslateZ() - ((double) oldValue - (double) newValue)));
        camera.rotateProperty().addListener((observable, oldValue, newValue) ->
                character.setRotate(character.getRotate() - ((double) oldValue - (double) newValue)));

        MeshView floor = createFloor(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("map/natchester.png"))));

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

        return scene;
    }

    @Override
    public void init() {
        if (regionStorage.isEmpty()) {
            return;
        }
        subscribe(regionService.getRegion("645e32c6866ace359554a7ec")
                        .flatMap((region) -> regionService.getAreas(region._id()))
                        .observeOn(Schedulers.io()).map((areas) ->
                                tileMapService.createMap(areas.get(0)).renderMap()),
                (image) -> {
                    // imageView.setImage(SwingFXUtils.toFXImage(image, null));
                },
                (error) -> {
                    System.out.println("Error loading image");
                    error.printStackTrace();
                });
    }

    @Override
    public void destroy() {

    }

    private static EventHandler<KeyEvent> keyPressed(PerspectiveCamera camera) {
        return (event) -> {
            System.out.println("Key pressed: " + event.getCode());
            switch (event.getCode()) {
                case W -> camera.setTranslateZ(camera.getTranslateZ() + 5);
                case S -> camera.setTranslateZ(camera.getTranslateZ() - 5);
                case A -> camera.setTranslateX(camera.getTranslateX() - 5);
                case D -> camera.setTranslateX(camera.getTranslateX() + 5);
                case Q -> camera.setRotate(camera.getRotate() - 2.5);
                case E -> camera.setRotate(camera.getRotate() + 2.5);
                case UP -> camera.setRotate(camera.getRotate() - 10);
                case DOWN -> camera.setRotate(camera.getRotate() + 10);
                case LEFT -> camera.setRotate(camera.getRotate() - 10);
                case RIGHT -> camera.setRotate(camera.getRotate() + 10);
            }
            System.out.println("X: " + camera.getTranslateX());
            System.out.println("Y: " + camera.getTranslateY());
            System.out.println("Z: " + camera.getTranslateZ());
        };
    }
}
