package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.dto.CreateTrainerDto;
import de.uniks.stpmon.k.models.*;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.rest.RegionApiService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.UserStorage;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    @InjectMocks
    RegionService regionService;

    private void initUser() {
        userStorage.setUser(new User("0",
                "Test",
                "offline",
                null,
                new ArrayList<>()));
    }

    private Trainer getDummyTrainer() {
        return new Trainer(
                "0",
                "regionId",
                "userId",
                "TestTrainer",
                "trainerImage",
                0,
                "areaId",
                0,
                0,
                0,
                DummyConstants.NPC_INFO,
                Set.of(), Set.of()
        );
    }

    private Area getDummyArea() {
        return new Area(
                "areaId",
                "regionId",
                "areaTest",
                null
        );
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
                .thenReturn(Observable.just(getDummyTrainer()));
        //action
        final Trainer trainer = regionService
                .createTrainer("regionId", "TestTrainer", "trainerImage")
                .blockingFirst();

        //check values
        assertEquals("0", trainer._id());
        assertEquals("TestTrainer", trainer.name());

        //check mocks
        verify(regionApiService).createTrainer(any(), captor.capture());
    }

    @Test
    void getAreaTrainers() {
        initUser();
        Trainer trainer = getDummyTrainer();
        //define mocks
        List<Trainer> trainers = new ArrayList<>();
        trainers.add(trainer);
        when(regionApiService.getTrainers(any(), any()))
                .thenReturn(Observable.just(trainers));

        //action
        final List<Trainer> returnTrainers = regionService
                .getTrainers("regionId", "areaId")
                .blockingFirst();

        //check values
        assertEquals(1, returnTrainers.size());
        assertEquals("0", returnTrainers.get(0)._id());
        assertEquals("TestTrainer", returnTrainers.get(0).name());

        //check mocks
        verify(regionApiService).getTrainers(any(), any());
    }

    @Test
    void getRegionTrainers() {
        initUser();
        Trainer trainer = getDummyTrainer();
        //define mocks
        List<Trainer> trainers = new ArrayList<>();
        trainers.add(trainer);
        when(regionApiService.getTrainers(any())).thenReturn(Observable.just(trainers));

        //action
        final List<Trainer> returnTrainers = regionService
                .getTrainers("regionId")
                .blockingFirst();

        //check values
        assertEquals(1, returnTrainers.size());
        assertEquals("0", returnTrainers.get(0)._id());
        assertEquals("TestTrainer", returnTrainers.get(0).name());

        //check mocks
        verify(regionApiService).getTrainers(any());
    }

    @Test
    void getTrainer() {
        initUser();
        Trainer trainer = getDummyTrainer();
        //define mocks
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        when(regionApiService.getTrainer(any(String.class), any(String.class)))
                .thenReturn(Observable.just(trainer));

        //action
        final Trainer returnTrainer = regionService
                .getTrainer("regionId", "trainerId")
                .blockingFirst();

        //check values
        assertEquals("0", returnTrainer._id());
        assertEquals("TestTrainer", returnTrainer.name());

        //check mocks
        verify(regionApiService).getTrainer(captor.capture(), captor.capture());
    }

    @Test
    void deleteTrainer() {
        initUser();
        regionStorage.setRegion(new Region(
                "regionId",
                "Test",
                new Spawn("regionId", 0, 0),
                null
        ));
        Trainer trainer = getDummyTrainer();
        //define mocks
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        when(regionApiService.deleteTrainer(any(), any()))
                .thenReturn(Observable.just(trainer));

        //action
        final Trainer returnTrainer = regionService
                .deleteTrainer("regionId", "trainerId")
                .blockingFirst();

        //check values
        assertEquals("0", returnTrainer._id());
        assertEquals("TestTrainer", returnTrainer.name());

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

        when(regionApiService.getRegion("1"))
                .thenReturn(Observable.just(region));

        //action
        Region regionList = regionService.getRegion("1").blockingFirst();

        //check values
        assertEquals("Test", regionList.name());
        assertEquals("1", regionList._id());
        //check mock
        verify(regionApiService).getRegion("1");
    }

    //---------------- Region Areas ------------------------------
    @Test
    void getArea() {
        Area area = getDummyArea();
        //define mocks
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        when(regionApiService.getArea(any(), any(String.class)))
                .thenReturn(Observable.just(area));

        //action
        final Area returnArea = regionService
                .getArea("regionId", "areaId")
                .blockingFirst();

        //check values
        assertEquals("areaId", returnArea._id());
        assertEquals("areaTest", returnArea.name());

        //check mocks
        verify(regionApiService).getArea(any(), captor.capture());
    }

    @Test
    void getAreas() {
        Area area = getDummyArea();
        List<Area> areas = new ArrayList<>();
        areas.add(area);
        //define mocks
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        when(regionApiService.getAreas(any(String.class)))
                .thenReturn(Observable.just(areas));

        //action
        final List<Area> returnAreas = regionService
                .getAreas("regionId")
                .blockingFirst();

        //check values
        assertEquals(1, returnAreas.size());
        assertEquals("areaId", returnAreas.get(0)._id());
        assertEquals("areaTest", returnAreas.get(0).name());

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
                .getMonsters("regionId", "trainerId")
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
        final Monster returnMonster = regionService.getMonster("regionId", "trainerId", "monsterId")
                .blockingFirst();

        //check values
        assertEquals("monsterId", returnMonster._id());
        assertEquals("trainerId", returnMonster.trainer());

        //check mocks
        verify(regionApiService).getMonster(anyString(), anyString(), anyString());

    }

}
