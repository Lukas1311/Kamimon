package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.dto.MoveTrainerDto;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.InputHandler;
import de.uniks.stpmon.k.service.WorldLoader;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.input.KeyEvent;

import javax.inject.Inject;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class MovementHandler {

    private final Deque<MoveTrainerDto> movements = new LinkedList<>();
    private final Timer timer = new Timer();
    private boolean movementBlocked = false;

    @Inject
    RegionStorage regionStorage;
    @Inject
    TrainerStorage trainerStorage;
    @Inject
    InputHandler inputHandler;
    @Inject
    EventListener listener;
    @Inject
    WorldLoader worldLoader;

    @Inject
    public MovementHandler() {

    }

    public Runnable addKeyHandler() {
        Runnable cancelKey = inputHandler.addKeyHandler(this::keyPressed);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (movements.isEmpty()) {
                    return;
                }
                MoveTrainerDto dto = movements.poll();
                listener.send(Socket.UDP, getEventName(), dto);
            }
        };
        timer.schedule(task, 0, 50);
        return () -> {
            task.cancel();
            cancelKey.run();
        };
    }

    public void initialPosition(Consumer<Trainer> callback) {
        Trainer trainer = trainerStorage.getTrainer();
        callback.accept(trainer);
    }

    public Observable<MoveTrainerDto> addMoveListener() {
        return listener.listen(Socket.UDP,
                getEventName(),
                MoveTrainerDto.class).flatMap((event) -> {
            MoveTrainerDto dto = event.data();
            return onMoveReceived(dto);
        });
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
            movements.clear();
            movementBlocked = true;
            return worldLoader.enterArea()
                    .map((t) -> dto)
                    .doOnComplete(() -> movementBlocked = false);
        }
        return Observable.just(dto);
    }

    private MoveTrainerDto createMoveDto(int diffX, int diffY, int dir) {
        Trainer trainer = trainerStorage.getTrainer();
        String areaId = regionStorage.getArea()._id();
        return new MoveTrainerDto(trainer._id(), areaId,
                trainer.x() + diffX, trainer.y() + diffY,
                dir);
    }

    private void keyPressed(KeyEvent event) {
        int diffX = 0;
        int diffY = 0;
        switch (event.getCode()) {
            case W -> diffY -= 1;
            case S -> diffY += 1;
            case A -> diffX -= 1;
            case D -> diffX += 1;
            default -> {

            }
        }
        if (diffX == 0 && diffY == 0) {
            return;
        }
        int dir;
        if (diffX > 0) {
            dir = 1;
        } else if (diffX < 0) {
            dir = 3;
        } else if (diffY > 0) {
            dir = 2;
        } else {
            dir = 0;
        }
        MoveTrainerDto dto = createMoveDto(diffX, diffY, dir);
        MoveTrainerDto lastMove = movements.peekLast();
        if (movementBlocked ||
                lastMove != null
                        && Objects.equals(lastMove.direction(), dto.direction())
                        && Objects.equals(lastMove.x(), dto.x())
                        && Objects.equals(lastMove.y(), dto.y())) {
            return;
        }
        movements.add(dto);
    }

    private String getEventName() {
        return String.format("areas.%s.trainers.%s.moved", regionStorage.getArea()._id(), trainerStorage.getTrainer()._id());
    }
}
