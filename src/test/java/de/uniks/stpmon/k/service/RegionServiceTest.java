package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.CreateTrainerDto;
import de.uniks.stpmon.k.models.*;
import de.uniks.stpmon.k.rest.RegionApiService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegionServiceTest {
    @Mock
    RegionApiService regionApiService;

    @Spy
    UserStorage userStorage;
    @InjectMocks
    RegionService regionService;


    //---------------- Region Trainers ----------------------------
    @Test
    void createTrainer() {
        userStorage.setUser(new User("0",
                "Test",
                "offline",
                null,
                new ArrayList<>()));
        //define mocks
        final ArgumentCaptor<CreateTrainerDto> captor = ArgumentCaptor.forClass(CreateTrainerDto.class);
        when(regionApiService.createTrainer(any(), any(CreateTrainerDto.class)))
                .thenReturn(Observable.just(new Trainer(
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
                        new NPCInfo(true)
                )));
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

}
