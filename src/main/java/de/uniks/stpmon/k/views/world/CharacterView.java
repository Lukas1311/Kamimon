package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.dto.MoveTrainerDto;
import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CharacterView extends WorldController {

    @Inject
    protected EventListener listener;
    @Inject
    protected RegionStorage regionStorage;

    @Inject
    public CharacterView() {
    }

    @Override
    public Node render(int angle, PerspectiveCamera camera) {
        Node character = createRectangleScaled("map/char.png", angle);
        character.setId("character");
        camera.translateXProperty().addListener((observable, oldValue, newValue) -> {
            character.setTranslateX(character.getTranslateX() - ((double) oldValue - (double) newValue));
        });
        camera.translateZProperty().addListener((observable, oldValue, newValue) -> {
            character.setTranslateZ(character.getTranslateZ() - ((double) oldValue - (double) newValue));
        });
        return character;
    }

    public void onMove(int x, int y) {
    }

    public void onMoveReceived(MoveTrainerDto dto) {
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
        subscribe(listener.listen(Socket.UDP,
                        "areas.*.trainers.*.moved",
                        MoveTrainerDto.class),
                (event) -> {
                    MoveTrainerDto dto = event.data();
                    onMoveReceived(dto);
                });
    }
}
