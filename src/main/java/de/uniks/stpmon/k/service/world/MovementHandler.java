package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.dto.MoveTrainerDto;
import de.uniks.stpmon.k.models.Event;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.utils.Direction;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.Objects;

public class MovementHandler {

    private MoveTrainerDto lastMovement;
    private boolean movementBlocked = false;

    @Inject
    protected EventListener listener;
    @Inject
    protected WorldLoader worldLoader;
    @Inject
    protected TrainerStorage trainerStorage;

    @Inject
    public MovementHandler() {

    }

    public Observable<Trainer> onMovements(Trainer trainer) {
        return listener.listen(Socket.UDP, String.format("areas.%s.trainers.%s.moved",
                        trainer.area(),
                        trainer._id()), MoveTrainerDto.class)
                .map(Event::data)
                .flatMap(this::onMoveReceived);
    }

    private Observable<Trainer> onMoveReceived(MoveTrainerDto dto) {
        Trainer trainer = trainerStorage.getTrainer();
        Trainer newTrainer = new Trainer(trainer._id(),
                trainer.region(),
                trainer.user(),
                trainer.name(),
                trainer.image(),
                trainer.coins(),
                dto.area(),
                dto.x(),
                dto.y(),
                dto.direction(),
                trainer.npc());
        // Check if area changed
        if (!Objects.equals(newTrainer.area(), trainer.area())) {
            movementBlocked = true;
            return worldLoader.tryEnterArea(trainer)
                    .doOnComplete(() -> movementBlocked = false);
        }
        return Observable.just(newTrainer);
    }

    private MoveTrainerDto createMoveDto(int diffX, int diffY, int dir) {
        Trainer trainer = trainerStorage.getTrainer();
        return new MoveTrainerDto(trainer._id(), trainer.area(),
                trainer.x() + diffX, trainer.y() + diffY,
                dir);
    }

    public void move(MoveTrainerDto dto) {
        if (movementBlocked) {
            return;
        }
        lastMovement = dto;
        listener.send(Socket.UDP, String.format("areas.%s.trainers.%s.moved",
                dto.area(),
                dto._id()), dto);
    }


    public void moveDirection(Direction direction) {
        int diffX = 0;
        int diffY = 0;
        switch (direction) {
            case TOP -> diffY -= 1;
            case BOTTOM -> diffY += 1;
            case LEFT -> diffX -= 1;
            case RIGHT -> diffX += 1;
            default -> {

            }
        }
        if (diffX == 0 && diffY == 0) {
            return;
        }
        int dir;
        if (diffX > 0) {
            dir = 0;
        } else if (diffX < 0) {
            dir = 2;
        } else if (diffY > 0) {
            dir = 3;
        } else {
            dir = 1;
        }
        MoveTrainerDto dto = createMoveDto(diffX, diffY, dir);
        if (lastMovement != null
                && Objects.equals(lastMovement.direction(), dto.direction())
                && Objects.equals(lastMovement.x(), dto.x())
                && Objects.equals(lastMovement.y(), dto.y())) {
            return;
        }
        move(dto);
    }
}
