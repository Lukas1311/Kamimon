package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.dto.MoveTrainerDto;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.utils.Direction;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.Objects;

public class MovementDispatcher extends MovementHandler {
    private MoveTrainerDto lastMovement;
    private boolean movementBlocked = false;

    @Inject
    public MovementDispatcher() {
    }

    protected Observable<Trainer> onMoveReceived(Trainer newTrainer) {
        Trainer trainer = trainerProvider.getTrainer();
        // Check if area changed
        if (trainerProvider.isMain()
                && !Objects.equals(newTrainer.area(), trainer.area())) {
            movementBlocked = true;
            return worldLoader.tryEnterArea(newTrainer)
                    .doOnComplete(() -> movementBlocked = false);
        }
        return Observable.just(newTrainer);
    }

    private MoveTrainerDto createMoveDto(int diffX, int diffY, int dir) {
        Trainer trainer = trainerProvider.getTrainer();
        return new MoveTrainerDto(trainer._id(), trainer.area(),
                trainer.x() + diffX, trainer.y() + diffY,
                dir);
    }

    private void move(MoveTrainerDto dto) {
        if (movementBlocked) {
            return;
        }
        lastMovement = dto;
        listener.send(Socket.UDP, eventName, dto);
    }

    public void moveDirection(Direction direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Direction cannot be null");
        }
        if (trainerProvider == null) {
            throw new IllegalStateException("Trainer provider not set");
        }
        if (eventName == null) {
            throw new IllegalStateException("Event name not set");
        }
        Trainer trainer = trainerProvider.getTrainer();
        int lastDir = lastMovement != null ? lastMovement.direction() : trainer.direction();
        // First change look direction
        if (lastDir != direction.ordinal()) {
            MoveTrainerDto dto = createMoveDto(0, 0, direction.ordinal());
            move(dto);
            return;
        }
        int diffX = 0;
        int diffY = 0;
        switch (direction) {
            case TOP -> diffY -= 1;
            case BOTTOM -> diffY += 1;
            case LEFT -> diffX -= 1;
            case RIGHT -> diffX += 1;
        }
        MoveTrainerDto dto = createMoveDto(diffX, diffY, direction.ordinal());
        if (lastMovement != null
                && Objects.equals(lastMovement.direction(), dto.direction())
                && Objects.equals(lastMovement.x(), dto.x())
                && Objects.equals(lastMovement.y(), dto.y())) {
            return;
        }
        move(dto);
    }
}
