package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.dto.MoveTrainerDto;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.storage.CameraStorage;
import de.uniks.stpmon.k.service.world.MovementHandler;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CharacterView extends WorldViewable {

    @Inject
    protected CameraStorage cameraStorage;
    @Inject
    protected MovementHandler movementHandler;

    @Inject
    public CharacterView() {
    }

    @Override
    public Node render() {
        Node character = createRectangleScaled("map/char.png", WorldView.WORLD_ANGLE);
        character.setId("character");
        PerspectiveCamera camera = cameraStorage.getCamera();
        character.setTranslateX(camera.getTranslateX() + WorldView.ENTITY_OFFSET_X * WorldView.WORLD_UNIT);
        character.setTranslateZ(camera.getTranslateZ() - WorldView.ENTITY_OFFSET_Y * WorldView.WORLD_UNIT);
        camera.translateXProperty().addListener((observable, oldValue, newValue) -> {
            character.setTranslateX(character.getTranslateX() - ((double) oldValue - (double) newValue));
        });
        camera.translateZProperty().addListener((observable, oldValue, newValue) -> {
            character.setTranslateZ(character.getTranslateZ() - ((double) oldValue - (double) newValue));
        });
        return character;
    }

    private void onMoveReceived(MoveTrainerDto dto) {
        PerspectiveCamera camera = cameraStorage.getCamera();
        camera.setTranslateX(dto.x() * WorldView.WORLD_UNIT);
        camera.setTranslateZ(-dto.y() * WorldView.WORLD_UNIT);
    }

    private void onInitial(Trainer trainer) {
        PerspectiveCamera camera = cameraStorage.getCamera();
        camera.setTranslateX(trainer.x() * WorldView.WORLD_UNIT);
        camera.setTranslateZ(-trainer.y() * WorldView.WORLD_UNIT);
    }

    @Override
    public void init() {
        super.init();
        subscribe(movementHandler.addMoveListener(), this::onMoveReceived);
        onDestroy(movementHandler.addKeyHandler());
        movementHandler.initialPosition(this::onInitial);
    }
}
