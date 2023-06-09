package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.views.world.WorldView;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorldController extends Controller {

    @Inject
    protected WorldView worldView;

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
    }

    @Override
    public Parent render() {
        BorderPane parent = (BorderPane) super.render();
        SubScene scene = worldView.createScene();
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
