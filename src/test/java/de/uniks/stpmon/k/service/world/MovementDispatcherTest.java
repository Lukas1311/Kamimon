package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.dto.MoveTrainerDto;
import de.uniks.stpmon.k.models.Event;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.TrainerAreaCache;
import de.uniks.stpmon.k.service.storage.cache.TrainerCache;
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MovementDispatcherTest {

    @InjectMocks
    public MovementDispatcher movementDispatcher;
    @Mock
    public WorldLoader worldLoader;
    @Mock
    public CacheManager cacheManager;
    @Mock
    protected EventListener listener;
    @InjectMocks
    protected TrainerAreaCache areaCache;
    @Mock
    protected Provider<TrainerAreaCache> areaCacheProvider;
    @Spy
    @SuppressWarnings("unused")
    protected EffectContext effectContext = new EffectContext();
    @InjectMocks
    protected TrainerCache trainerCache;
    @Spy
    protected final TrainerStorage trainerStorage = new TrainerStorage();

    @Test
    void providerNull() {
        assertThrows(IllegalArgumentException.class, () -> movementDispatcher.setInitialTrainer(null));
    }


    @Test
    void receiveMoves() {
        Subject<MoveTrainerDto> movements = ReplaySubject.create();
        when(listener.listen(any(), any(), any())).thenReturn(movements.map((dto) -> new Event<>("", dto)));
        // Setup cache
        when(areaCacheProvider.get()).thenReturn(areaCache);
        trainerCache.areaCache("area_0");
        when(cacheManager.trainerAreaCache()).thenReturn(areaCache);
        trainerCache.addValue(DummyConstants.TRAINER);

        trainerStorage.setTrainer(DummyConstants.TRAINER);
        movementDispatcher.setInitialTrainer(trainerStorage);

        TestObserver<Trainer> result = movementDispatcher.onMovements().test();
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

        // Setup cache
        when(areaCacheProvider.get()).thenReturn(areaCache);
        trainerCache.areaCache("area_0");
        when(cacheManager.trainerAreaCache()).thenReturn(areaCache);
        trainerCache.addValue(DummyConstants.TRAINER);


        trainerStorage.setTrainer(DummyConstants.TRAINER);
        movementDispatcher.setInitialTrainer(trainerStorage);

        TestObserver<Trainer> result = movementDispatcher.onMovements().test();
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
        return TrainerBuilder.builder(DummyConstants.TRAINER)
                .setX(x)
                .setY(y)
                .setDirection(direction)
                .create();
    }

    @Test
    void moveDirection() {
        ArgumentCaptor<MoveTrainerDto> captor = ArgumentCaptor.forClass(MoveTrainerDto.class);
        when(listener.send(any(), any(), captor.capture())).thenReturn(Observable.empty());
        when(listener.listen(any(), any(), any())).thenReturn(Observable.empty());

        // should throw exception if initial trainer is not set
        assertThrows(IllegalStateException.class, () -> movementDispatcher.moveDirection(Direction.BOTTOM));

        // Setup cache
        when(areaCacheProvider.get()).thenReturn(areaCache);
        trainerCache.areaCache("area_0");
        when(cacheManager.trainerAreaCache()).thenReturn(areaCache);

        assertThrows(IllegalArgumentException.class, () -> movementDispatcher.setInitialTrainer(trainerStorage));
        trainerStorage.setTrainer(DummyConstants.TRAINER);
        movementDispatcher.setInitialTrainer(trainerStorage);

        trainerStorage.setTrainer(DummyConstants.TRAINER);
        // should throw exception if direction is null
        assertThrows(IllegalArgumentException.class, () -> movementDispatcher.moveDirection(null));


        // move one to bottom
        movementDispatcher.moveDirection(Direction.BOTTOM, 2);
        // y should be 1 and direction should be 3
        assertEquals(new MoveTrainerDto("0", "area_0", 0, 1, 3), captor.getValue());

        // move one to left
        movementDispatcher.moveDirection(Direction.LEFT, 2);
        // x should be -1 and direction should be 2
        assertEquals(new MoveTrainerDto("0", "area_0", -1, 0, 2), captor.getValue());

        // move one to top
        movementDispatcher.moveDirection(Direction.TOP, 2);
        // y should be -1 and direction should be 1
        assertEquals(new MoveTrainerDto("0", "area_0", 0, -1, 1), captor.getValue());

        // move one to right
        movementDispatcher.moveDirection(Direction.RIGHT, 2);
        // x should be 1 and direction should be 0
        assertEquals(new MoveTrainerDto("0", "area_0", 1, 0, 0), captor.getValue());

        // moved 8 times, 4 direction changes, 4 actual movements
        assertEquals(4, captor.getAllValues().size());

        // move one to right again
        movementDispatcher.moveDirection(Direction.RIGHT);
        // should not move two times into same direction
        assertEquals(4, captor.getAllValues().size());

    }

}
