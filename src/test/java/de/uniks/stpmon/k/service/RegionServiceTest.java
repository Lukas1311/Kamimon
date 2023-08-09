package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.dto.CreateTrainerDto;
import de.uniks.stpmon.k.models.*;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.rest.RegionApiService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.UserStorage;
import de.uniks.stpmon.k.service.storage.cache.RegionCache;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegionServiceTest {

    @Spy
    UserStorage userStorage;
    @Spy
    RegionStorage regionStorage;
    @Mock
    RegionApiService regionApiService;
    @Mock
    Provider<RegionCache> regionCacheProvider;
    @InjectMocks
    RegionCache regionCache;
    @InjectMocks
    RegionService regionService;

    private void initUser() {
        userStorage.setUser(new User("0",
                "Test",
                "offline",
                null,
                new ArrayList<>()));
    }

    private Monster makeMonster() {
        return MonsterBuilder.builder()
                .setId("monsterId")
                .setTrainer("trainerId")
                .setType(1)
                .create();
    }

    //---------------- Region Trainers ----------------------------
    @Test
    void createTrainer() {
        initUser();
        //define mocks
        final ArgumentCaptor<CreateTrainerDto> captor = ArgumentCaptor.forClass(CreateTrainerDto.class);
        when(regionApiService.createTrainer(any(), any(CreateTrainerDto.class)))
                .thenReturn(Observable.just(DummyConstants.TRAINER));
        //action
        final Trainer trainer = regionService
                .createTrainer("region_0", "Test Trainer", "trainerImage")
                .blockingFirst();

        //check values
        assertEquals("0", trainer._id());
        assertEquals("Test Trainer", trainer.name());

        //check mocks
        verify(regionApiService).createTrainer(any(), captor.capture());
    }

    @Test
    void getAreaTrainers() {
        initUser();
        //define mocks
        List<Trainer> trainers = new ArrayList<>();
        trainers.add(DummyConstants.TRAINER);
        when(regionApiService.getTrainers(any(), any()))
                .thenReturn(Observable.just(trainers));

        //action
        final List<Trainer> returnTrainers = regionService
                .getTrainers("region_0", "area_0")
                .blockingFirst();

        //check values
        assertEquals(1, returnTrainers.size());
        assertEquals("0", returnTrainers.get(0)._id());
        assertEquals("Test Trainer", returnTrainers.get(0).name());

        //check mocks
        verify(regionApiService).getTrainers(any(), any());
    }

    @Test
    void getRegionTrainers() {
        initUser();
        //define mocks
        List<Trainer> trainers = new ArrayList<>();
        trainers.add(DummyConstants.TRAINER);
        when(regionApiService.getTrainers(any())).thenReturn(Observable.just(trainers));

        //action
        final List<Trainer> returnTrainers = regionService
                .getTrainers("region_0")
                .blockingFirst();

        //check values
        assertEquals(1, returnTrainers.size());
        assertEquals("0", returnTrainers.get(0)._id());
        assertEquals("Test Trainer", returnTrainers.get(0).name());

        //check mocks
        verify(regionApiService).getTrainers(any());
    }

    @Test
    void getTrainer() {
        initUser();
        //define mocks
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        when(regionApiService.getTrainer(any(String.class), any(String.class)))
                .thenReturn(Observable.just(DummyConstants.TRAINER));

        //action
        final Trainer returnTrainer = regionService
                .getTrainer("region_0", "trainerId")
                .blockingFirst();

        //check values
        assertEquals("0", returnTrainer._id());
        assertEquals("Test Trainer", returnTrainer.name());

        //check mocks
        verify(regionApiService).getTrainer(captor.capture(), captor.capture());
    }

    @Test
    void deleteTrainer() {
        initUser();
        regionStorage.setRegion(new Region(
                "region_0",
                "Test",
                new Spawn("region_0", 0, 0),
                null
        ));
        //define mocks
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        when(regionApiService.deleteTrainer(any(), any()))
                .thenReturn(Observable.just(DummyConstants.TRAINER));

        //action
        final Trainer returnTrainer = regionService
                .deleteTrainer("region_0", "trainerId")
                .blockingFirst();

        //check values
        assertEquals("0", returnTrainer._id());
        assertEquals("Test Trainer", returnTrainer.name());

        //check mocks
        verify(regionApiService).deleteTrainer(captor.capture(), captor.capture());
    }

    //------------------- Regions ---------------------------------

    @Test
    void getRegions() {
        Region region = new Region(
                "1",
                "Test",
                new Spawn("1", 0, 0),
                null
        );
        ArrayList<Region> regions = new ArrayList<>();
        regions.add(region);


        when(regionApiService.getRegions())
                .thenReturn(Observable.just(regions));
        when(regionCacheProvider.get()).thenReturn(regionCache);

        //action
        List<Region> regionList = regionService.getRegions().blockingFirst();

        //check values
        assertEquals(1, regionList.size());
        assertEquals("1", regionList.get(0)._id());
        //check mock
        verify(regionApiService).getRegions();
    }

    @Test
    void getRegion() {
        Region region = new Region(
                "1",
                "Test",
                new Spawn("1", 0, 0),
                null
        );
        when(regionApiService.getRegions())
                .thenReturn(Observable.just(List.of(region)));
        when(regionCacheProvider.get()).thenReturn(regionCache);

        //action
        Region regionList = regionService.getRegion("1").blockingFirst();

        //check values
        assertEquals("Test", regionList.name());
        assertEquals("1", regionList._id());
        //check mock
        verify(regionApiService).getRegions();
    }

    //---------------- Region Areas ------------------------------
    @Test
    void getArea() {
        //define mocks
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        when(regionApiService.getArea(any(), any(String.class)))
                .thenReturn(Observable.just(DummyConstants.AREA_NO_MAP));

        //action
        final Area returnArea = regionService
                .getArea("region_0", "area_0")
                .blockingFirst();

        //check values
        assertEquals("area_0", returnArea._id());
        assertEquals("Test Area", returnArea.name());

        //check mocks
        verify(regionApiService).getArea(any(), captor.capture());
    }

    @Test
    void getAreas() {
        List<Area> areas = new ArrayList<>();
        areas.add(DummyConstants.AREA_NO_MAP);
        //define mocks
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        when(regionApiService.getAreas(any(String.class)))
                .thenReturn(Observable.just(areas));

        //action
        final List<Area> returnAreas = regionService
                .getAreas("region_0")
                .blockingFirst();

        //check values
        assertEquals(1, returnAreas.size());
        assertEquals("area_0", returnAreas.get(0)._id());
        assertEquals("Test Area", returnAreas.get(0).name());

        //check mocks
        verify(regionApiService).getAreas(captor.capture());
    }

    //------------- Trainer Monsters -------------------------------
    @Test
    void getMonsters() {
        Monster monster = makeMonster();
        List<Monster> monsters = new ArrayList<>();
        monsters.add(monster);
        //define mocks
        when(regionApiService.getMonsters(anyString(), anyString()))
                .thenReturn(Observable.just(monsters));

        //action
        final List<Monster> returnMonsters = regionService
                .getMonsters("region_0", "trainerId")
                .blockingFirst();

        //check values
        assertEquals(1, returnMonsters.size());
        assertEquals("monsterId", returnMonsters.get(0)._id());
        assertEquals("trainerId", returnMonsters.get(0).trainer());

        //check mocks
        verify(regionApiService).getMonsters(anyString(), anyString());
    }

    @Test
    void getMonster() {
        Monster monster = makeMonster();
        //define mocks
        when(regionApiService.getMonster(anyString(), anyString(), anyString()))
                .thenReturn(Observable.just(monster));

        //action
        final Monster returnMonster = regionService.getMonster("region_0", "trainerId", "monsterId")
                .blockingFirst();

        //check values
        assertEquals("monsterId", returnMonster._id());
        assertEquals("trainerId", returnMonster.trainer());

        //check mocks
        verify(regionApiService).getMonster(anyString(), anyString(), anyString());

    }

}
