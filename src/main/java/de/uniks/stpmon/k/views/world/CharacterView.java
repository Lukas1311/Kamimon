package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.service.InputHandler;
import de.uniks.stpmon.k.service.storage.CameraStorage;
import de.uniks.stpmon.k.service.storage.TrainerProvider;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.world.MovementDispatcher;
import de.uniks.stpmon.k.utils.Direction;
import javafx.application.Platform;
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
            case W -> dispatcher.pushKey(Direction.TOP);
            case S -> dispatcher.pushKey(Direction.BOTTOM);
            case A -> dispatcher.pushKey(Direction.LEFT);
            case D -> dispatcher.pushKey(Direction.RIGHT);
            case SHIFT -> dispatcher.setSprinting(true);
            case CONTROL -> dispatcher.setSneaking(true);
            case UP -> dispatcher.lookDirection(Direction.TOP);
            case DOWN -> dispatcher.lookDirection(Direction.BOTTOM);
            case LEFT -> dispatcher.lookDirection(Direction.LEFT);
            case RIGHT -> dispatcher.lookDirection(Direction.RIGHT);
            default -> {
                return;
            }
        }
        // Focus the main window, to prevent the sidebar button from consuming the arrow key events
        if (!entityNode.isFocused()) {
            Platform.runLater(() -> entityNode.requestFocus());
        }
        event.consume();
    }

    private void keyRelease(KeyEvent event) {
        switch (event.getCode()) {
            case W -> dispatcher.releaseKey(Direction.TOP);
            case S -> dispatcher.releaseKey(Direction.BOTTOM);
            case A -> dispatcher.releaseKey(Direction.LEFT);
            case D -> dispatcher.releaseKey(Direction.RIGHT);
            case CONTROL -> dispatcher.setSneaking(false);
            case SHIFT -> dispatcher.setSprinting(false);
            default -> {
                return;
            }
        }
        event.consume();
    }

    @Override
    public void init() {
        super.init();
        onDestroy(inputHandler.addPressedKeyHandler(this::keyPressed));
        onDestroy(inputHandler.addReleasedKeyHandler(this::keyRelease));
        onDestroy(dispatcher.init());
    }

    @Override
    protected boolean isSprinting() {
        return dispatcher != null && dispatcher.isSprinting();
    }

    @Override
    protected boolean isSneaking() {
        return dispatcher != null && dispatcher.isSneaking();
    }

    @Override
    public MovementDispatcher getMovementHandler() {
        return dispatcher;
    }
}
