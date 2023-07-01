package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.dto.MoveTrainerDto;
import de.uniks.stpmon.k.models.Event;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.storage.cache.TrainerAreaCache;
import de.uniks.stpmon.k.service.storage.cache.TrainerCache;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.TestObserver;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Provider;
import java.util.List;
import java.util.Objects;
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
    @Mock
    RegionStorage regionStorage;
    @Mock
    Provider<TrainerAreaCache> trainerAreaProvider;
    @Spy
    @InjectMocks
    TrainerAreaCache trainerAreaCache;
    @Spy
    TrainerStorage trainerStorage;

    @BeforeEach
    void setUp() {
        when(eventListener.listen(any(), any(), any())).thenReturn(Observable.empty());
        trainerStorage.setTrainer(DummyConstants.TRAINER);
    }

    @Test
    void checkWSEvents() {
        Trainer created = TrainerBuilder.builder()
                .setId(1)
                .setRegion("id0")
                .setName("TestTrainer")
                .setImage("trainerImage")
                .setArea("0")
                .create();
        Trainer updated = TrainerBuilder.builder(created)
                .setName("UpdatedTrainer")
                .create();
        Trainer user = TrainerBuilder.builder()
                .setId(0)
                .setRegion("id0")
                .setName("TestTrainer")
                .setImage("trainerImage")
                .setArea("0")
                .create();

        TrainerAreaCache otherCache = Mockito.mock(TrainerAreaCache.class);
        when(trainerAreaProvider.get()).thenReturn(trainerAreaCache, otherCache);
        when(regionStorage.onEvents()).thenReturn(Observable.empty());
        when(regionService.getTrainers(any())).thenReturn(Observable.just(List.of(user)));
        Subject<Event<Trainer>> userEvents = BehaviorSubject.create();
        when(eventListener.<Trainer>listen(eq(Socket.WS), eq("regions.id0.trainers.*.*"), any())).thenReturn(userEvents);

        // initialise cache with user
        cache.setup("id0").init();
        TrainerAreaCache areaCache = cache.areaCache("0");
        assertTrue(areaCache.areSetupValues("id0", "0"));

        assertEquals(1, cache.getValues().blockingFirst().size());

        TestObserver<Trainer> testCreation = cache.onCreation().test();
        testCreation.assertValueCount(0);
        userEvents.onNext(new Event<>("region.id0.trainers.1.created", created));
        verify(cache).addValue(any(Trainer.class));
        verify(areaCache).addValue(any(Trainer.class));
        assertEquals(2, cache.getValues().blockingFirst().size());
        testCreation.assertValueCount(1);

        userEvents.onNext(new Event<>("region.id0.trainers.1.updated", updated));
        verify(cache).updateValue(any(Trainer.class));
        verify(areaCache).updateValue(any(Trainer.class));
        assertEquals(2, cache.getValues().blockingFirst().size());
        Optional<Trainer> first = cache.getValue("1");
        assertTrue(first.isPresent());
        assertEquals("UpdatedTrainer", first.get().name());

        TestObserver<Trainer> testDeletion = cache.onDeletion().test();
        testDeletion.assertValueCount(0);
        userEvents.onNext(new Event<>("region.id0.trainers.1.deleted", updated));
        verify(cache).removeValue(any(Trainer.class));
        verify(areaCache).removeValue(any(Trainer.class));
        assertEquals(1, cache.getValues().blockingFirst().size());
        testDeletion.assertValueCount(1);
    }

    @Test
    void checkUDPEvents() {
        MoveTrainerDto moveArea = new MoveTrainerDto(
                "1",
                "area_0",
                0,
                0,
                0
        );
        MoveTrainerDto moveDir = new MoveTrainerDto(
                "1",
                "area_1",
                0,
                0,
                1
        );

        TrainerAreaCache otherCache = Mockito.mock(TrainerAreaCache.class);
        when(trainerAreaProvider.get()).thenReturn(trainerAreaCache, otherCache);
        when(regionStorage.onEvents()).thenReturn(Observable.empty());
        when(regionService.getTrainers(any())).thenReturn(Observable.just(List.of(DummyConstants.TRAINER_OTHER_AREA)));
        Subject<Event<MoveTrainerDto>> userEvents = BehaviorSubject.create();
        when(eventListener.<MoveTrainerDto>listen(eq(Socket.UDP), eq("areas.area_1.trainers.*.moved"), any()))
                .thenReturn(userEvents);

        // initialise cache with user
        cache.setup("0").init();
        TrainerAreaCache areaCache = cache.areaCache("area_1");
        assertEquals(1, areaCache.getValues().blockingFirst().size());
        TestObserver<Optional<Trainer>> originalListen = cache.listenValue("1").test();
        TestObserver<Optional<Trainer>> testListen = areaCache.listenValue("1").test();
        // First value should be trainer
        testListen.assertValueAt(0, Optional::isPresent);

        userEvents.onNext(new Event<>("areas.area_1.trainers.1.moved", moveDir));
        verify(areaCache).updateValue(any(Trainer.class));
        assertEquals(1, areaCache.getValues().blockingFirst().size());
        Optional<Trainer> first = areaCache.getValue("1");
        assertTrue(first.isPresent());
        assertEquals(1, first.get().direction());
        // First value should be trainer
        testListen.assertValueAt(1, (trainer) -> trainer.isPresent() && trainer.get().direction() == 1);

        // move in another area
        userEvents.onNext(new Event<>("areas.area_1.trainers.1.moved", moveArea));
        // Check if trainer is removed from cache
        verify(areaCache).removeValue(any(Trainer.class));
        assertEquals(0, areaCache.getValues().blockingFirst().size());
        // Area trainer cache should be empty
        testListen.assertValueAt(2, Optional::isEmpty);

        // Region trainer cache should be updated
        originalListen.assertValueAt(2, (trainer) -> trainer.isPresent()
                && Objects.equals(trainer.get().area(), "area_0"));
    }

    @Test
    public void changeArea() {
        Subject<RegionStorage.RegionEvent> events = PublishSubject.create();
        when(regionStorage.onEvents()).thenReturn(events);

        TrainerAreaCache otherCache = Mockito.mock(TrainerAreaCache.class);
        when(otherCache.areSetupValues(any(), any())).thenAnswer(
                invocation -> invocation.getArgument(1).equals("area_0")
        );
        when(trainerAreaProvider.get()).thenReturn(trainerAreaCache, otherCache);
        when(regionService.getTrainers(any())).thenReturn(Observable.just(List.of()));
        Subject<Event<MoveTrainerDto>> userEvents = BehaviorSubject.create();
        when(eventListener.<MoveTrainerDto>listen(eq(Socket.UDP), eq("areas.area_1.trainers.*.moved"), any()))
                .thenReturn(userEvents);

        // initialise cache with user
        cache.setup("0").init();
        TrainerAreaCache oldAreaCache = cache.areaCache("area_1");
        // Change area
        events.onNext(new RegionStorage.RegionEvent(DummyConstants.REGION, DummyConstants.AREA,
                DummyConstants.AREA_NO_MAP));
        verify(oldAreaCache).destroy();
        assertThrows(IllegalStateException.class, () -> cache.areaCache("area_1"));
        // Request new cache
        TrainerAreaCache areaCache = cache.areaCache("area_0");
        // Check if new cache was provided
        assertEquals(otherCache, areaCache);
    }

    @Test
    public void notCacheable() {
        TrainerAreaCache otherCache = Mockito.mock(TrainerAreaCache.class);
        when(trainerAreaProvider.get()).thenReturn(trainerAreaCache, otherCache);
        when(regionStorage.onEvents()).thenReturn(Observable.empty());
        when(regionService.getTrainers(any())).thenReturn(Observable.just(List.of()));

        // initialise cache with user
        cache.setup("id0").init();
        assertTrue(cache.areSetupValues("id0"));
        assertFalse(cache.areSetupValues("id1"));


        TrainerAreaCache areaCache = cache.areaCache("area_0");
        TestObserver<Optional<Trainer>> playerListen = areaCache.listenValue("0").test();
        TestObserver<Optional<Trainer>> testListen = areaCache.listenValue("1").test();
        // Check if trainers with same id but other area are cached
        cache.addValue(DummyConstants.TRAINER);
        playerListen.assertValueCount(2);

        // Check if area listener is still empty
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

        // check for errors
        assertThrows(IllegalArgumentException.class, () -> cache.removeValue(null));
        assertThrows(IllegalArgumentException.class, () -> cache.updateValue(null));
        assertThrows(IllegalArgumentException.class, () -> cache.addValue(null));
    }

}
