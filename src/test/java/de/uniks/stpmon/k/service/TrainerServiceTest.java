package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.dto.UpdateTrainerDto;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.rest.RegionApiService;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.TrainerAreaCache;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceTest {

    @Spy
    TrainerStorage trainerStorage;
    @Mock
    RegionApiService regionApiService;
    @Mock
    CacheManager cacheManager;
    @InjectMocks
    TrainerService trainerService;

    final Trainer trainer = DummyConstants.TRAINER;

    @Test
    public void getMe() {
        when(trainerStorage.getTrainer()).thenReturn(trainer);
        Trainer test = trainerService.getMe();
        assertEquals(trainer, test);
    }

    @Test
    public void deleteMe() {
        //test when oldTrainer is null
        final Observable<Trainer> nullUser = trainerService.deleteMe();
        //check value
        assertTrue(nullUser.isEmpty().blockingGet());

        //test when oldTrainer is not null
        when(trainerStorage.getTrainer()).thenReturn(trainer);
        when(regionApiService.deleteTrainer(anyString(), anyString())).thenReturn(Observable.just(trainer));

        //action
        Observable<Trainer> deletedTrainer = trainerService.deleteMe();

        //check values
        assertEquals(trainer, deletedTrainer.blockingFirst());

        //check mocks
        verify(regionApiService).deleteTrainer("region_0", "0");
    }

    @Test
    public void setTrainerName() {
        //test when oldTrainer is null
        final Observable<Trainer> nullUser = trainerService.setTrainerName("Bob");
        //check value
        assertTrue(nullUser.isEmpty().blockingGet());

        //setting up trainer which will be updated
        when(trainerStorage.getTrainer()).thenReturn(trainer);

        //define mock
        final ArgumentCaptor<UpdateTrainerDto> captor = ArgumentCaptor.forClass(UpdateTrainerDto.class);
        when(regionApiService.updateTrainer(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString(),
            captor.capture()
        )).thenReturn(
            Observable.just(TrainerBuilder.builder().setId("1").setName("Bob").create()
        ));

        //action
        final Trainer newTrainer = trainerService.setTrainerName("Bob").blockingFirst();

        //check values
        assertEquals("Bob", newTrainer.name());

        //check mocks
        verify(regionApiService).updateTrainer("region_0", "0", captor.getValue());
    }

    @Test
    public void setImage() {
        //test when oldTrainer is null
        final Observable<Trainer> nullUser = trainerService.setImage("101");
        //check value
        assertTrue(nullUser.isEmpty().blockingGet());

        //setting up trainer which will be updated
        when(trainerStorage.getTrainer()).thenReturn(trainer);

        //define mock
        final ArgumentCaptor<UpdateTrainerDto> captor = ArgumentCaptor.forClass(UpdateTrainerDto.class);
        when(regionApiService.updateTrainer(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), captor.capture()))
                .thenReturn(Observable.just(
                        new Trainer("1", "0", "0", "0", "101", 0, "0",
                                0, 0, 0, DummyConstants.NPC_INFO,
                                List.of(), Set.of(), Set.of()
                        )
                ));

        //action
        final Trainer newTrainer = trainerService.setImage("101").blockingFirst();

        //check values
        assertEquals("101", newTrainer.image());

        //check mocks
        verify(regionApiService).updateTrainer("region_0", "0", captor.getValue());
    }

    @Test
    void checkSurrounding() {
        TrainerAreaCache trainerCache = Mockito.mock(TrainerAreaCache.class);
        when(cacheManager.trainerAreaCache()).thenReturn(trainerCache);

        // Default direction is right
        trainerStorage.setTrainer(DummyConstants.TRAINER);

        Trainer firstTrainer = TrainerBuilder.builder().setId("1").setRegion("0").setX(2).create();
        Trainer secondTrainer = TrainerBuilder.builder().setId("1").setRegion("0").setX(1).create();
        // First no trainer returned
        when(trainerCache.getTrainerAt(2, 0)).thenReturn(Optional.of(secondTrainer));
        when(trainerCache.getTrainerAt(1, 0)).thenReturn(Optional.of(firstTrainer));
        // Retrieve player itself
        Optional<Trainer> emptyNpc = trainerService.getFacingTrainer(0);
        // Should always be empty
        assertTrue(emptyNpc.isEmpty());

        // Retrieve trainer one step away
        Optional<Trainer> firstNpc = trainerService.getFacingTrainer(1);
        // Should return first trainer
        assertTrue(firstNpc.isPresent());
        assertEquals(firstTrainer, firstNpc.get());

        // Retrieve trainer two steps away
        Optional<Trainer> secondNpc = trainerService.getFacingTrainer(2);
        // Should return second trainer
        assertTrue(secondNpc.isPresent());
        assertEquals(secondTrainer, secondNpc.get());

    }

    @Test
    void testFastTravel() {
        // test when oldTrainer is null
        final Observable<Trainer> nullUser = trainerService.fastTravel("1");
        // check value
        assertTrue(nullUser.isEmpty().blockingGet());

        when(trainerStorage.getTrainer()).thenReturn(trainer);

        // define mock
        final ArgumentCaptor<UpdateTrainerDto> captor = ArgumentCaptor.forClass(UpdateTrainerDto.class);
        when(regionApiService.updateTrainer(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString(),
            captor.capture()
        )).thenReturn(
            Observable.just(TrainerBuilder.builder().setId("1").setArea(DummyConstants.AREA).create()
        ));

        // action
        final Trainer newTrainer = trainerService.fastTravel(DummyConstants.AREA._id()).blockingFirst();

        // check values
        assertEquals("area_0", newTrainer.area());

        verify(regionApiService).updateTrainer("region_0", "0", captor.getValue());
    }

}
