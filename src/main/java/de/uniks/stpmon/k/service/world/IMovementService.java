package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.dto.MoveTrainerDto;
import de.uniks.stpmon.k.models.Trainer;
import io.reactivex.rxjava3.core.Observable;

public interface IMovementService {

    Runnable registerSender(Trainer trainer);

    MoveTrainerDto getLastMovement();

    Observable<MoveTrainerDto> onMovements(Trainer trainer);

    Observable<MoveTrainerDto> enterArea(MoveTrainerDto dto);

    void move(MoveTrainerDto dto);
}
