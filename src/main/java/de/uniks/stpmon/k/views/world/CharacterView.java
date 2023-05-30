package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.Main;
import de.uniks.stpmon.k.dto.MoveTrainerDto;
import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.image.Image;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;

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
        Node character = createRectangle(
                new Image(Objects.requireNonNull(Main.class.getResourceAsStream("map/char.png"))),
                angle);
        character.setId("character");
        camera.translateXProperty().addListener((observable, oldValue, newValue) -> {
            character.setTranslateX(character.getTranslateX() - ((double) oldValue - (double) newValue));
            onMove((int) character.getTranslateX(), (int) character.getTranslateY());
        });
        camera.translateZProperty().addListener((observable, oldValue, newValue) -> {
            character.setTranslateZ(character.getTranslateZ() - ((double) oldValue - (double) newValue));
            onMove((int) character.getTranslateX(), (int) character.getTranslateY());
        });
        camera.rotateProperty().addListener((observable, oldValue, newValue) ->
                character.setRotate(character.getRotate() - ((double) oldValue - (double) newValue)));
        return character;
    }

    private void onMove(int x, int y) {
        //TODO: On move
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
                    System.out.printf("moved{trainer=%s,x=%s,y=%s}\n", dto._id(), dto.x(), dto.y());
                });
    }
}
