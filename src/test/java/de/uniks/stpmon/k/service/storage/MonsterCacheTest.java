package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.MonsterCache;
import de.uniks.stpmon.k.service.storage.cache.TrainerCache;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.TestObserver;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MonsterCacheTest {

    @Mock
    EventListener eventListener;
    @Spy
    RegionStorage regionStorage;
    @Mock
    RegionService regionService;
    @Mock
    CacheManager cacheManager;
    @Mock
    TrainerCache trainerCache;

    @Spy
    @InjectMocks
    MonsterCache cache;

    @BeforeEach
    void setUp() {
        when(eventListener.listen(any(), any(), any())).thenReturn(Observable.empty());
        when(cacheManager.trainerCache()).thenReturn(trainerCache);
    }

    @Test
    void checkInvalid() {
        when(regionService.getMonsters(any(), any())).thenReturn(Observable.empty());
        when(trainerCache.listenValue(any())).thenReturn(Observable.empty());
        // Can't init cache without region set
        assertThrows(IllegalStateException.class, () -> cache.init());
        regionStorage.setRegion(DummyConstants.REGION);
        // Can't init cache without trainer set
        assertThrows(IllegalStateException.class, () -> cache.init());

        cache.setTrainerId("test");

        // Can init cache with region and trainer set
        assertDoesNotThrow(() -> cache.init());
    }

    @Test
    void emptyTeam() {
        regionStorage.setRegion(DummyConstants.REGION);
        when(regionService.getMonsters(any(), any())).thenReturn(Observable.just(
                List.of(
                        MonsterBuilder.builder().setId(0).create()
                )
        ));
        BehaviorSubject<Optional<Trainer>> trainer = BehaviorSubject.createDefault(
                Optional.of(TrainerBuilder.builder().setId("test").create()));
        when(trainerCache.listenValue(eq("test"))).thenReturn(trainer);
        cache.setTrainerId("test");
        cache.init();

        TestObserver<List<Monster>> teamObservable = cache.getTeam().getValues().test();
        teamObservable.assertValue(List.of());
    }

    @Test
    void updateTeam() {
        regionStorage.setRegion(DummyConstants.REGION);
        Monster first = MonsterBuilder.builder().setId(0).create();
        Monster second = MonsterBuilder.builder(first).setId(1).create();
        Monster third = MonsterBuilder.builder(first).setId(2).create();
        when(regionService.getMonsters(any(), any())).thenReturn(Observable.just(
                List.of(
                        first,
                        second,
                        third
                )
        ));
        Trainer trainer = TrainerBuilder.builder().setId("test").addTeam("0").create();
        BehaviorSubject<Optional<Trainer>> trainerSubject = BehaviorSubject.createDefault(
                Optional.of(trainer));
        when(trainerCache.listenValue(eq("test"))).thenReturn(trainerSubject);
        cache.setTrainerId("test");
        cache.init();

        TestObserver<List<Monster>> teamObservable = cache.getTeam().getValues().test();
        // Check initial value is just the first monster
        teamObservable.assertValueAt(0, List.of(first));

        // Add second monster to team
        trainer = TrainerBuilder.builder(trainer).addTeam("1").create();
        trainerSubject.onNext(Optional.of(trainer));
        // Check if team is updated
        teamObservable.assertValueAt(1, List.of(first, second));

        // Remove first monster from team and add third
        trainer = TrainerBuilder.builder(trainer).removeTeam("0").addTeam("2").create();
        trainerSubject.onNext(Optional.of(trainer));
        // Check if team is updated
        teamObservable.assertValueAt(2, List.of(second, third));


    }

}
