package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.dto.MoveTrainerDto;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.utils.Direction;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.Objects;

public class MovementHandler {

    @Inject
    TrainerStorage trainerStorage;
    @Inject
    IMovementService movementService;

    @Inject
    public MovementHandler() {

    }

    public Runnable registerSender() {
        return movementService.registerSender(trainerStorage.getTrainer());
    }

    public Observable<MoveTrainerDto> onMovements(Trainer trainer) {
        return movementService.onMovements(trainer)
                .flatMap(this::onMoveReceived);
    }

    private Observable<MoveTrainerDto> onMoveReceived(MoveTrainerDto dto) {
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
        trainerStorage.setTrainer(newTrainer);
        // Check if area changed
        if (!Objects.equals(newTrainer.area(), trainer.area())) {
            return movementService.enterArea(dto);
        }
        return Observable.just(dto);
    }

    private MoveTrainerDto createMoveDto(int diffX, int diffY, int dir) {
        Trainer trainer = trainerStorage.getTrainer();
        return new MoveTrainerDto(trainer._id(), trainer.area(),
                trainer.x() + diffX, trainer.y() + diffY,
                dir);
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
        MoveTrainerDto lastMove = movementService.getLastMovement();
        if (lastMove != null
                && Objects.equals(lastMove.direction(), dto.direction())
                && Objects.equals(lastMove.x(), dto.x())
                && Objects.equals(lastMove.y(), dto.y())) {
            return;
        }
        movementService.move(dto);
    }
}
