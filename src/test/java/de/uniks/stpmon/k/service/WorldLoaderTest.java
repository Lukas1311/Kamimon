package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.sources.IPortalController;
import de.uniks.stpmon.k.service.sources.PortalSource;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.WorldStorage;
import de.uniks.stpmon.k.service.world.TileMapService;
import de.uniks.stpmon.k.service.world.World;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.TestObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
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
    WorldStorage worldStorage;
    @Mock
    RegionService regionService;
    @Mock
    TileMapService tileMapService;
    @InjectMocks
    WorldLoader worldLoader;
    @Mock
    IPortalController portalController;

    @BeforeEach
    void setUp() {
        loadingSource.setPortalController(portalController);
    }

    @Test
    void loadNewWorld() {
        when(tileMapService.loadTilemap()).thenReturn(Observable.just(DummyConstants.WORLD));
        // Check if the world is null
        assertNull(worldStorage.getWorld());

        Observable<World> world = worldLoader.getOrLoadWorld();
        // Check if the world was loaded
        assertEquals(DummyConstants.WORLD, world.blockingFirst());
        assertEquals(DummyConstants.WORLD, worldStorage.getWorld());
    }

    @Test
    void getStoredWorld() {
        worldStorage.setWorld(DummyConstants.WORLD);
        // Check if the world is null
        assertNull(worldStorage.getWorld());
        Observable<World> world = worldLoader.getOrLoadWorld();
        // check if the world was loaded
        assertEquals(DummyConstants.WORLD, world.blockingFirst());
        // Check if tileMapService was not called
        verify(tileMapService, never()).loadTilemap();
    }

    @Test
    void enterNewRegion() {
        when(regionService.getMainTrainer(any())).thenReturn(Observable.just(DummyConstants.TRAINER));
        when(regionService.getArea("region_0", "area_0")).thenReturn(Observable.just(DummyConstants.AREA));
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
        Observable<Trainer> world = worldLoader.tryEnterArea();
        TestObserver<Trainer> testObserver = world.test();
        // Check if loading failed
        testObserver.assertError(IllegalStateException.class);
        verify(portalController, never()).loadWorld();
    }

    @Test
    void enterNewAreaNoRegion() {
        trainerStorage.setTrainer(DummyConstants.TRAINER);
        Observable<Trainer> world = worldLoader.tryEnterArea();
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
        Observable<Trainer> world = worldLoader.tryEnterArea();
        // Check if all values are set correctly
        assertEquals(DummyConstants.TRAINER, world.blockingFirst());
        assertEquals(DummyConstants.REGION, regionStorage.getRegion());
        assertEquals(DummyConstants.AREA, regionStorage.getArea());
        verify(portalController).loadWorld();
    }

    @Test
    void enterAreaWhileTeleporting() {
        loadingSource.setTeleporting(true);
        // Check if all values are null at start
        assertNull(regionStorage.getRegion());
        assertNull(regionStorage.getArea());

        Observable<Trainer> world = worldLoader.tryEnterArea();
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
