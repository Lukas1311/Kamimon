package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.service.InputHandler;
import de.uniks.stpmon.k.service.storage.CameraStorage;
import de.uniks.stpmon.k.service.storage.TrainerProvider;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.world.MovementDispatcher;
import de.uniks.stpmon.k.utils.Direction;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.input.KeyEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CharacterView extends EntityView {

    @Inject
    protected MovementDispatcher dispatcher;
    @Inject
    protected CameraStorage cameraStorage;
    @Inject
    protected TrainerStorage trainerStorage;
    @Inject
    protected InputHandler inputHandler;

    @Inject
    public CharacterView() {
    }

    @Override
    protected TrainerProvider getProvider() {
        return trainerStorage;
    }

    @Override
    public Node render() {
        Node character = super.render();
        character.setId("character");
        PerspectiveCamera camera = cameraStorage.getCamera();
        camera.setTranslateX(character.getTranslateX());
        camera.setTranslateZ(character.getTranslateZ());

        listen(character.translateXProperty(), (observable, oldValue, newValue) ->
                camera.setTranslateX(camera.getTranslateX() - ((double) oldValue - (double) newValue)));
        listen(character.translateZProperty(), (observable, oldValue, newValue) ->
                camera.setTranslateZ(camera.getTranslateZ() - ((double) oldValue - (double) newValue)));
        return character;
    }

    private void keyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case W, UP -> dispatcher.moveDirection(Direction.TOP);
            case S, DOWN -> dispatcher.moveDirection(Direction.BOTTOM);
            case A, LEFT -> dispatcher.moveDirection(Direction.LEFT);
            case D, RIGHT -> dispatcher.moveDirection(Direction.RIGHT);
            default -> {
            }
        }
    }

    @Override
    public void init() {
        super.init();
        onDestroy(inputHandler.addPressedKeyHandler(this::keyPressed));
    }

    @Override
    public MovementDispatcher getMovementHandler() {
        return dispatcher;
    }
}
