package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.dto.MoveTrainerDto;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
import de.uniks.stpmon.k.utils.Direction;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.*;

public class MovementDispatcher extends MovementHandler {
    private MoveTrainerDto lastMovement;
    private boolean movementBlocked = false;
    private boolean sprinting = false;
    private boolean sneaking = false;
    private final Set<Direction> pressedDirs = new HashSet<>();
    private final Stack<Direction> movementStack = new Stack<>();
    private Timer movementTimer;
    private TimerTask currentTask;

    @Inject
    protected EffectContext effectContext;
    @Inject
    protected EncounterStorage encounterStorage;

    @Inject
    public MovementDispatcher() {
    }

    public void setSprinting(boolean sprinting) {
        if (sprinting == this.sprinting) {
            return;
        }
        this.sprinting = sprinting;
        unscheduleMove();
        scheduleMove();
    }

    public void setSneaking(boolean sneaking) {
        if (sneaking == this.sneaking) {
            return;
        }
        this.sneaking = sneaking;
        unscheduleMove();
        scheduleMove();
    }


    public boolean isSneaking() {
        return sneaking;
    }

    public boolean isSprinting() {
        return sprinting;
    }

    public void pushKey(Direction dir) {
        if (!pressedDirs.add(dir)) {
            return;
        }
        movementStack.push(dir);
        scheduleMove();
    }

    public void releaseKey(Direction dir) {
        if (!pressedDirs.remove(dir)) {
            return;
        }
        movementStack.remove(dir);
        if (movementStack.isEmpty()) {
            unscheduleMove();
        }
    }

    private void stopMoving() {
        movementStack.clear();
        pressedDirs.clear();
        sprinting = false;
        sneaking = false;
        unscheduleMove();
    }

    public Runnable init() {
        movementTimer = new Timer();
        return () -> {
            stopMoving();
            movementTimer.cancel();
        };
    }

    private void unscheduleMove() {
        if (currentTask != null) {
            currentTask.cancel();
            currentTask = null;
        }
    }

    private void scheduleMove() {
        if (currentTask != null) {
            return;
        }
        currentTask = new TimerTask() {
            @Override
            public void run() {
                if (movementStack.isEmpty() || !encounterStorage.isEmpty()) {
                    return;
                }
                Direction dir = movementStack.lastElement();
                moveDirection(dir);
            }
        };
        if (effectContext.getWalkingSpeed() * 0.25f < 1) {
            currentTask.run();
            return;
        }
        float period = effectContext.getWalkingSpeed();
        if (sprinting) {
            period *= effectContext.getSprintingFactor();
        } else if (sneaking) {
            period /= effectContext.getSprintingFactor();
        }
        movementTimer.schedule(currentTask, (int) (effectContext.getWalkingSpeed() * 0.25f), (int) period);
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

    public void moveDirection(Direction direction, int times) {
        if (times < 1) {
            throw new IllegalArgumentException("Times cannot be less than 1");
        }
        for (int i = 0; i < times; i++) {
            moveDirection(direction);
        }
    }

    public void lookDirection(Direction direction) {
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
        if (lastDir == direction.ordinal()) {
            return;
        }
        MoveTrainerDto dto = createMoveDto(0, 0, direction.ordinal());
        move(dto);
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
