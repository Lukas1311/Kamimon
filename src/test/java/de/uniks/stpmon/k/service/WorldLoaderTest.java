package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.constants.NoneConstants;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.sources.IPortalController;
import de.uniks.stpmon.k.service.sources.PortalSource;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.WorldRepository;
import de.uniks.stpmon.k.service.world.PreparationService;
import de.uniks.stpmon.k.service.world.WorldLoader;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.TestObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorldLoaderTest {

    @Spy
    RegionStorage regionStorage;
    @Spy
    TrainerStorage trainerStorage;
    @Spy
    PortalSource loadingSource;
    @Spy
    WorldRepository worldRepository;
    @Mock
    RegionService regionService;
    @InjectMocks
    @Spy
    WorldLoader worldLoader;
    @Mock
    IPortalController portalController;
    @Mock
    PreparationService preparationService;

    @BeforeEach
    void setUp() {
        loadingSource.setPortalController(portalController);
    }


    @Test
    void loadWorldNoRegion() {
        // Check if the world is null
        assertNull(regionStorage.getRegion());

        Completable world = worldLoader.loadWorld();
        TestObserver<Void> testObserver = world.test();
        // Check if nothing was loaded
        testObserver.assertNotComplete();
        testObserver.assertNoErrors();
    }

    @Test
    void loadWorldNoArea() {
        regionStorage.setRegion(DummyConstants.REGION);
        assertNotNull(regionStorage.getRegion());
        // Check if the area is null
        assertNull(regionStorage.getArea());

        Completable world = worldLoader.loadWorld();
        TestObserver<Void> testObserver = world.test();
        // Check if nothing was loaded
        testObserver.assertNotComplete();
        testObserver.assertNoErrors();
    }

    @Test
    void loadWorld() {
        when(preparationService.prepareWorld()).thenReturn(Completable.complete());
        regionStorage.setRegion(DummyConstants.REGION);
        regionStorage.setArea(DummyConstants.AREA);
        assertNotNull(regionStorage.getRegion());
        assertNotNull(regionStorage.getArea());

        Completable world = worldLoader.loadWorld();
        TestObserver<Void> testObserver = world.test();
        // Check if the preparation service was called
        testObserver.assertComplete();
    }

    @Test
    void enterNewRegion() {
        when(regionService.getMainTrainer(any())).thenReturn(Observable.just(DummyConstants.TRAINER));
        when(regionService.getArea("id0", "area_0")).thenReturn(Observable.just(DummyConstants.AREA));
        Observable<Trainer> world = worldLoader.tryEnterRegion(DummyConstants.REGION);
        // Check if all values are set correctly
        assertEquals(DummyConstants.TRAINER, world.blockingFirst());
        assertEquals(DummyConstants.REGION, regionStorage.getRegion());
        assertEquals(DummyConstants.AREA, regionStorage.getArea());
        // Check if the world was loaded
        verify(portalController).loadWorld();
    }

    @Test
    void enterRegionWhileTeleporting() {
        loadingSource.setTeleporting(true);
        // Check if all values are null at start
        assertNull(regionStorage.getRegion());
        assertNull(regionStorage.getArea());

        Observable<Trainer> world = worldLoader.tryEnterRegion(DummyConstants.REGION);
        TestObserver<Trainer> testObserver = world.test();
        // Check if nothing was loaded
        testObserver.assertComplete();
        testObserver.assertNoValues();
        // Check if all values are still null
        assertNull(regionStorage.getRegion());
        assertNull(regionStorage.getArea());
        verify(portalController, never()).loadWorld();
    }

    @Test
    void enterRegionAlreadyInRegion() {
        trainerStorage.setTrainer(DummyConstants.TRAINER);
        Observable<Trainer> world = worldLoader.tryEnterRegion(DummyConstants.REGION);
        TestObserver<Trainer> testObserver = world.test();
        // Check if loading failed
        testObserver.assertError(IllegalStateException.class);
        verify(portalController, never()).loadWorld();
    }

    @Test
    void enterRegionDirty() {
        trainerStorage.setTrainer(DummyConstants.TRAINER_OTHER_REGION);
        Observable<Trainer> world = worldLoader.tryEnterRegion(DummyConstants.REGION);
        TestObserver<Trainer> testObserver = world.test();
        // Check if loading failed
        testObserver.assertError(IllegalStateException.class);
        verify(portalController, never()).loadWorld();
    }

    @Test
    void enterNewAreaNoTrainer() {
        Observable<Trainer> world = worldLoader.tryEnterArea(NoneConstants.NONE_TRAINER);
        TestObserver<Trainer> testObserver = world.test();
        // Check if loading failed
        testObserver.assertError(IllegalArgumentException.class);
        verify(portalController, never()).loadWorld();
    }

    @Test
    void enterNewAreaNoRegion() {
        trainerStorage.setTrainer(DummyConstants.TRAINER);
        Observable<Trainer> world = worldLoader.tryEnterArea(DummyConstants.TRAINER);
        TestObserver<Trainer> testObserver = world.test();
        // Check if loading failed
        testObserver.assertError(IllegalStateException.class);
        verify(portalController, never()).loadWorld();
    }

    @Test
    void enterNewArea() {
        trainerStorage.setTrainer(DummyConstants.TRAINER);
        regionStorage.setRegion(DummyConstants.REGION);
        when(regionService.getArea("region_0", "area_0")).thenReturn(Observable.just(DummyConstants.AREA));
        Observable<Trainer> world = worldLoader.tryEnterArea(DummyConstants.TRAINER);
        // Check if all values are set correctly
        assertEquals(DummyConstants.TRAINER, world.blockingFirst());
        assertEquals(DummyConstants.REGION, regionStorage.getRegion());
        assertEquals(DummyConstants.AREA, regionStorage.getArea());
        verify(portalController).loadWorld();
        // Reset after world is loaded
        verify(worldRepository).reset(true);
        worldLoader.destroy();
        // Reset if world loader is destroyed
        verify(worldRepository, times(2)).reset(true);
    }

    @Test
    void enterAreaWhileTeleporting() {
        loadingSource.setTeleporting(true);
        // Check if all values are null at start
        assertNull(regionStorage.getRegion());
        assertNull(regionStorage.getArea());

        Observable<Trainer> world = worldLoader.tryEnterArea(DummyConstants.TRAINER);
        TestObserver<Trainer> testObserver = world.test();
        // Check if nothing was loaded
        testObserver.assertComplete();
        testObserver.assertNoValues();
        // Check if all values are still null
        assertNull(regionStorage.getRegion());
        assertNull(regionStorage.getArea());
        verify(portalController, never()).loadWorld();
    }

}
