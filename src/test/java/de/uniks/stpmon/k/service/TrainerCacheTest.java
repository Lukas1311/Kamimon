package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.dto.MoveTrainerDto;
import de.uniks.stpmon.k.models.Event;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.storage.cache.TrainerCache;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.TestObserver;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerCacheTest {
    @Mock
    RegionService regionService;
    @Mock
    EventListener eventListener;
    @Spy
    @InjectMocks
    TrainerCache cache;

    @BeforeEach
    void setUp() {
        when(eventListener.listen(any(), any(), any())).thenReturn(Observable.empty());
    }

    @Test
    void checkWSEvents() {
        Trainer created = new Trainer(
                "1",
                "id0",
                "userId",
                "TestTrainer",
                "trainerImage",
                0,
                "0",
                0,
                0,
                0,
                null
        );
        Trainer updated = new Trainer(
                "1",
                "id0",
                "userId",
                "UpdatedTrainer",
                "trainerImage",
                0,
                "0",
                0,
                0,
                0,
                null
        );
        Trainer user = new Trainer(
                "0",
                "id0",
                "userId",
                "TestTrainer",
                "trainerImage",
                0,
                "0",
                0,
                0,
                0,
                null
        );

        when(regionService.getTrainers(any(), any())).thenReturn(Observable.just(List.of(user)));
        Subject<Event<Trainer>> userEvents = BehaviorSubject.create();
        when(eventListener.<Trainer>listen(eq(Socket.WS), eq("regions.id0.trainers.*.*"), any())).thenReturn(userEvents);

        // initialise cache with user
        cache.setup("id0", "0").init();
        assertEquals(1, cache.getValues().blockingFirst().size());

        TestObserver<Trainer> testCreation = cache.onCreation().test();
        testCreation.assertValueCount(0);
        userEvents.onNext(new Event<>("region.id0.trainers.1.created", created));
        verify(cache).addValue(any(Trainer.class));
        assertEquals(2, cache.getValues().blockingFirst().size());
        testCreation.assertValueCount(1);

        userEvents.onNext(new Event<>("region.id0.trainers.1.updated", updated));
        verify(cache).updateValue(any(Trainer.class));
        assertEquals(2, cache.getValues().blockingFirst().size());
        Optional<Trainer> first = cache.getValue("1");
        assertTrue(first.isPresent());
        assertEquals("UpdatedTrainer", first.get().name());

        userEvents.onNext(new Event<>("region.id0.trainers.1.deleted", updated));
        verify(cache).removeValue(any(Trainer.class));
        assertEquals(1, cache.getValues().blockingFirst().size());
    }

    @Test
    void checkUDPEvents() {
        MoveTrainerDto moveArea = new MoveTrainerDto(
                "0",
                "area_1",
                0,
                0,
                0
        );
        MoveTrainerDto moveDir = new MoveTrainerDto(
                "0",
                "area_0",
                0,
                0,
                1
        );

        when(regionService.getTrainers(any(), any())).thenReturn(Observable.just(List.of(DummyConstants.TRAINER)));
        Subject<Event<MoveTrainerDto>> userEvents = BehaviorSubject.create();
        when(eventListener.<MoveTrainerDto>listen(eq(Socket.UDP), eq("areas.area_0.trainers.*.moved"), any()))
                .thenReturn(userEvents);

        // initialise cache with user
        cache.setup("0", "area_0").init();
        assertEquals(1, cache.getValues().blockingFirst().size());
        TestObserver<Optional<Trainer>> testListen = cache.listenValue("0").test();
        // First value should be trainer
        testListen.assertValueAt(0, Optional::isPresent);

        userEvents.onNext(new Event<>("areas.area_0.trainers.0.moved", moveDir));
        verify(cache).updateValue(any(Trainer.class));
        assertEquals(1, cache.getValues().blockingFirst().size());
        Optional<Trainer> first = cache.getValue("0");
        assertTrue(first.isPresent());
        assertEquals(1, first.get().direction());
        // First value should be trainer
        testListen.assertValueAt(1, (trainer) -> trainer.isPresent() && trainer.get().direction() == 1);

        // move in another area
        userEvents.onNext(new Event<>("areas.area_0.trainers.0.moved", moveArea));
        // Check if trainer is removed from cache
        verify(cache).removeValue(any(Trainer.class));
        assertEquals(0, cache.getValues().blockingFirst().size());
        // First value should be trainer
        testListen.assertValueAt(2, Optional::isEmpty);
    }

    @Test
    public void notCacheable() {
        when(regionService.getTrainers(any(), any())).thenReturn(Observable.just(List.of()));

        // initialise cache with user
        cache.setup("id0", "0").init();
        assertTrue(cache.areSetupValues("id0", "0"));
        assertFalse(cache.areSetupValues("id0", "1"));
        TestObserver<Optional<Trainer>> testListen = cache.listenValue("0").test();
        testListen.assertValueAt(0, Optional::isEmpty);
        cache.addValue(DummyConstants.TRAINER_OTHER_AREA);
        // Check if no value was added
        testListen.assertValueCount(1);
        cache.updateValue(DummyConstants.TRAINER_OTHER_AREA);
        // Check if no value was updated
        testListen.assertValueCount(1);

        cache.removeValue(DummyConstants.TRAINER_OTHER_AREA);
        // Check if no value was removed
        testListen.assertValueCount(1);

        assertThrows(IllegalArgumentException.class, () -> cache.removeValue(null));
        assertThrows(IllegalArgumentException.class, () -> cache.updateValue(null));
        assertThrows(IllegalArgumentException.class, () -> cache.addValue(null));
    }
}
