package de.uniks.stpmon.k.service.dummies;

import de.uniks.stpmon.k.dto.MoveTrainerDto;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.WorldLoader;
import de.uniks.stpmon.k.service.world.IMovementService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import javax.inject.Inject;

public class MovementDummy implements IMovementService {
    private final BehaviorSubject<MoveTrainerDto> movements = BehaviorSubject.create();
    @Inject
    protected WorldLoader worldLoader;

    @Inject
    public MovementDummy() {
    }

    @Override
    public Runnable registerSender(Trainer trainer) {
        return movements::onComplete;
    }

    @Override
    public MoveTrainerDto getLastMovement() {
        return movements.getValue();
    }

    @Override
    public Observable<MoveTrainerDto> onMovements(Trainer trainer) {
        return movements;
    }

    @Override
    public Observable<MoveTrainerDto> enterArea(MoveTrainerDto dto) {
        return worldLoader.tryEnterArea()
                .map((t) -> dto);
    }

    @Override
    public void move(MoveTrainerDto dto) {
        movements.onNext(dto);
    }
}
