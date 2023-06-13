package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.dto.MoveTrainerDto;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.storage.TrainerProvider;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.TrainerCache;
import de.uniks.stpmon.k.utils.Direction;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

public class MovementHandler {

    private MoveTrainerDto lastMovement;
    private boolean movementBlocked = false;

    @Inject
    protected EventListener listener;
    @Inject
    protected WorldLoader worldLoader;
    protected TrainerCache trainerCache;
    @Inject
    protected CacheManager cacheManager;
    // not injected, provider by parent user
    private TrainerProvider trainerProvider;
    private String trainerId;
    private String eventName;

    @Inject
    public MovementHandler() {

    }

    public void setInitialTrainer(TrainerProvider trainerProvider) {
        if (trainerProvider == null) {
            throw new IllegalArgumentException("Trainer provider cannot be null");
        }
        Trainer trainer = trainerProvider.getTrainer();
        if (trainer == null) {
            throw new IllegalArgumentException("Trainer cannot be null");
        }
        this.trainerProvider = trainerProvider;
        this.eventName = String.format("areas.%s.trainers.%s.moved",
                trainer.area(),
                trainer._id());
        this.trainerId = trainer._id();
        trainerCache = cacheManager.trainerCache();
    }

    public Observable<Trainer> onMovements() {
        if (trainerProvider == null) {
            return Observable.error(new IllegalStateException("Trainer provider not set"));
        }
        return trainerCache
                .listenValue(trainerId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .flatMap(this::onMoveReceived);
    }

    private Observable<Trainer> onMoveReceived(Trainer newTrainer) {
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
        int diffX = 0;
        int diffY = 0;
        switch (direction) {
            case TOP -> diffY -= 1;
            case BOTTOM -> diffY += 1;
            case LEFT -> diffX -= 1;
            case RIGHT -> diffX += 1;
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
