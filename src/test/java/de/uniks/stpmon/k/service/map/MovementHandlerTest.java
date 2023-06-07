package de.uniks.stpmon.k.service.map;

import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.dto.MoveTrainerDto;
import de.uniks.stpmon.k.models.Event;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.world.MovementHandler;
import de.uniks.stpmon.k.service.world.WorldLoader;
import de.uniks.stpmon.k.utils.Direction;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.TestObserver;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MovementHandlerTest {

    @InjectMocks
    public MovementHandler movementHandler;
    @Mock
    public WorldLoader worldLoader;
    @Mock
    protected EventListener listener;

    @Test
    void receiveNoInitial() {
        TestObserver<Trainer> result = movementHandler.onMovements().test();
        result.assertError(IllegalStateException.class);
    }

    @Test
    void providerNull() {
        assertThrows(IllegalArgumentException.class, () -> movementHandler.setInitialTrainer(null));
    }

    @Test
    void receiveMoves() {
        Subject<MoveTrainerDto> movements = ReplaySubject.create();
        when(listener.listen(any(), any(), any())).thenReturn(movements.map((dto) -> new Event<>("", dto)));

        TrainerStorage trainerStorage = new TrainerStorage();
        trainerStorage.setTrainer(DummyConstants.TRAINER);
        movementHandler.setInitialTrainer(trainerStorage);

        TestObserver<Trainer> result = movementHandler.onMovements().test();
        result.assertNoErrors();

        // walk right
        movements.onNext(new MoveTrainerDto("0", "area_0", 1, 0, 0));
        // check if trainer has moved right
        result.assertValueAt(0, trainerForDto(1, 0, 0));
        // walk left
        movements.onNext(new MoveTrainerDto("0", "area_0", -1, 0, 0));
        // check if trainer has moved left
        result.assertValueAt(1, trainerForDto(-1, 0, 0));

        // walk top
        movements.onNext(new MoveTrainerDto("0", "area_0", 0, 1, 0));
        // check if trainer has moved top
        result.assertValueAt(2, trainerForDto(0, 1, 0));

        // walk bottom
        movements.onNext(new MoveTrainerDto("0", "area_0", 0, -1, 0));
        // check if trainer has moved bottom
        result.assertValueAt(3, trainerForDto(0, -1, 0));
    }

    @Test
    void receiveDirOrArea() {
        Subject<MoveTrainerDto> movements = ReplaySubject.create();
        when(listener.listen(any(), any(), any())).thenReturn(movements.map((dto) -> new Event<>("", dto)));
        when(worldLoader.tryEnterArea(any())).thenReturn(Observable.empty());

        TrainerStorage trainerStorage = new TrainerStorage();
        trainerStorage.setTrainer(DummyConstants.TRAINER);
        movementHandler.setInitialTrainer(trainerStorage);

        TestObserver<Trainer> result = movementHandler.onMovements().test();
        result.assertNoErrors();

        // look bottom
        movements.onNext(new MoveTrainerDto("0", "area_0", 0, 0, 3));
        // check if trainer has looks bottom
        result.assertValueAt(0, trainerForDto(0, 0, 3));

        // change area
        movements.onNext(new MoveTrainerDto("0", "area_1", 0, 0, 0));
        // no dto is emitted on area change
        result.assertValueCount(1);
    }

    private Trainer trainerForDto(int x, int y, int direction) {
        return new Trainer(DummyConstants.TRAINER._id(), DummyConstants.TRAINER.region(), DummyConstants.TRAINER.user(),
                DummyConstants.TRAINER.name(), DummyConstants.TRAINER.image(), DummyConstants.TRAINER.coins(),
                DummyConstants.TRAINER.area(), x, y, direction,
                DummyConstants.TRAINER.npc());
    }

    @Test
    void moveDirection() {
        ArgumentCaptor<MoveTrainerDto> captor = ArgumentCaptor.forClass(MoveTrainerDto.class);
        when(listener.send(any(), any(), captor.capture())).thenReturn(Observable.empty());

        // should throw exception if initial trainer is not set
        assertThrows(IllegalStateException.class, () -> movementHandler.moveDirection(Direction.BOTTOM));

        TrainerStorage trainerStorage = new TrainerStorage();
        assertThrows(IllegalArgumentException.class, () -> movementHandler.setInitialTrainer(trainerStorage));
        trainerStorage.setTrainer(DummyConstants.TRAINER);
        movementHandler.setInitialTrainer(trainerStorage);

        trainerStorage.setTrainer(DummyConstants.TRAINER);
        // should throw exception if direction is null
        assertThrows(IllegalArgumentException.class, () -> movementHandler.moveDirection(null));


        // move one to bottom
        movementHandler.moveDirection(Direction.BOTTOM);
        // y should be 1 and direction should be 3
        assertEquals(new MoveTrainerDto("0", "area_0", 0, 1, 3), captor.getValue());

        // move one to left
        movementHandler.moveDirection(Direction.LEFT);
        // x should be -1 and direction should be 2
        assertEquals(new MoveTrainerDto("0", "area_0", -1, 0, 2), captor.getValue());

        // move one to top
        movementHandler.moveDirection(Direction.TOP);
        // y should be -1 and direction should be 1
        assertEquals(new MoveTrainerDto("0", "area_0", 0, -1, 1), captor.getValue());

        // move one to right
        movementHandler.moveDirection(Direction.RIGHT);
        // x should be 1 and direction should be 0
        assertEquals(new MoveTrainerDto("0", "area_0", 1, 0, 0), captor.getValue());

        // moved 4 times
        assertEquals(4, captor.getAllValues().size());

        // move one to right again
        movementHandler.moveDirection(Direction.RIGHT);
        // should not move two times into same direction
        assertEquals(4, captor.getAllValues().size());

    }
}
