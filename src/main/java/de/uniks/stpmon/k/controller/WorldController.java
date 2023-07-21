package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.views.world.WorldView;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.layout.BorderPane;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorldController extends Controller {

    @Inject
    protected WorldView worldView;
    private SubScene scene;
    private BorderPane parent;

    @Inject
    public WorldController() {
    }

    @Override
    public void init() {
        super.init();
        worldView.init();
    }

    @Override
    public void destroy() {
        super.destroy();
        worldView.destroy();
        if (scene != null) {
            scene.widthProperty().unbind();
            scene.heightProperty().unbind();
            Group group = (Group) scene.getRoot();
            group.getChildren().clear();
            // Clear the scene
            scene.setRoot(new Group());
            scene.setCamera(null);
        }
        scene = null;
        if (parent != null) {
            parent.getChildren().clear();
            parent = null;
        }
    }

    @Override
    public Parent render() {
        parent = (BorderPane) super.render();
        scene = worldView.createScene();
        if (scene != null) {
            parent.getChildren().add(0, scene);

            // Scale the scene to the parent
            scene.widthProperty()
                    .bind(parent.widthProperty());
            scene.heightProperty()
                    .bind(parent.heightProperty());
        }
        return parent;
    }


}
