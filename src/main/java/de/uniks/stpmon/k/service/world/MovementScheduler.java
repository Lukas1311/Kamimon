package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.dto.MoveTrainerDto;
import de.uniks.stpmon.k.models.Event;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.WorldLoader;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

@Singleton
public class MovementScheduler implements IMovementService {

    public static final int MOVEMENT_PERIOD = 70;
    private final Deque<MoveTrainerDto> movements = new LinkedList<>();
    private final Timer timer = new Timer();
    private boolean movementBlocked = false;

    @Inject
    protected EventListener listener;
    @Inject
    protected WorldLoader worldLoader;

    @Inject
    public MovementScheduler() {
    }

    @Override
    public Runnable registerSender(Trainer trainer) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (movements.isEmpty()) {
                    return;
                }
                MoveTrainerDto dto = movements.poll();
                listener.send(Socket.UDP, String.format("areas.%s.trainers.%s.moved",
                        trainer.area(),
                        trainer._id()), dto);
            }
        };
        timer.schedule(task, 0, MOVEMENT_PERIOD);
        return task::cancel;
    }

    @Override
    public Observable<MoveTrainerDto> enterArea(MoveTrainerDto dto) {
        movements.clear();
        movementBlocked = true;
        return worldLoader.tryEnterArea()
                .map((t) -> dto)
                .doOnComplete(() -> movementBlocked = false);
    }

    @Override
    public Observable<MoveTrainerDto> onMovements(Trainer trainer) {
        return listener.listen(Socket.UDP, String.format("areas.%s.trainers.%s.moved",
                        trainer.area(),
                        trainer._id()), MoveTrainerDto.class)
                .map(Event::data);
    }

    @Override
    public MoveTrainerDto getLastMovement() {
        return movements.peekLast();
    }

    @Override
    public void move(MoveTrainerDto dto) {
        if (movementBlocked) {
            return;
        }
        movements.add(dto);
    }
}
