package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.dto.UpdateTrainerDto;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.rest.RegionApiService;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceTest {

    @Spy
    TrainerStorage trainerStorage;
    @Mock
    RegionApiService regionApiService;
    @InjectMocks
    TrainerService trainerService;

    @Test
    public void getMe() {
        Trainer trainer = new Trainer(
                "1", "0", "0", "0", "0", 0, "0", 0, 0, 0, DummyConstants.NPC_INFO);
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
        Trainer trainer = new Trainer(
                "1", "0", "0", "0", "0", 0, "0", 0, 0, 0, DummyConstants.NPC_INFO);
        when(trainerStorage.getTrainer()).thenReturn(trainer);

        when(regionApiService.deleteTrainer("0", "1")).thenReturn(Observable.just(trainer));

        //action
        Observable<Trainer> deletedTrainer = trainerService.deleteMe();

        //check values
        assertEquals(trainer, deletedTrainer.blockingFirst());

        //check mocks
        verify(regionApiService).deleteTrainer("0", "1");
    }

    @Test
    public void setTrainerName() {
        //test when oldTrainer is null
        final Observable<Trainer> nullUser = trainerService.setTrainerName("Bob");
        //check value
        assertTrue(nullUser.isEmpty().blockingGet());

        //setting up trainer which will be updated
        Trainer trainer = new Trainer(
                "1", "0", "0", "0", "0", 0, "0", 0, 0, 0, DummyConstants.NPC_INFO);
        when(trainerStorage.getTrainer()).thenReturn(trainer);

        //define mock
        final ArgumentCaptor<UpdateTrainerDto> captor = ArgumentCaptor.forClass(UpdateTrainerDto.class);
        when(regionApiService.updateTrainer(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), captor.capture()))
                .thenReturn(Observable.just(
                        new Trainer("1", "0", "0", "Bob", "0", 0, "0", 0, 0, 0, DummyConstants.NPC_INFO)
                ));

        //action
        final Trainer newTrainer = trainerService.setTrainerName("Bob").blockingFirst();

        //check values
        assertEquals("Bob", newTrainer.name());

        //check mocks
        verify(regionApiService).updateTrainer("0", "1", captor.getValue());
    }

    @Test
    public void setImage() {
        //test when oldTrainer is null
        final Observable<Trainer> nullUser = trainerService.setImage("101");
        //check value
        assertTrue(nullUser.isEmpty().blockingGet());

        //setting up trainer which will be updated
        Trainer trainer = new Trainer(
                "1", "0", "0", "0", "0", 0, "0", 0, 0, 0, DummyConstants.NPC_INFO);
        when(trainerStorage.getTrainer()).thenReturn(trainer);

        //define mock
        final ArgumentCaptor<UpdateTrainerDto> captor = ArgumentCaptor.forClass(UpdateTrainerDto.class);
        when(regionApiService.updateTrainer(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), captor.capture()))
                .thenReturn(Observable.just(
                        new Trainer("1", "0", "0", "0", "101", 0, "0", 0, 0, 0, DummyConstants.NPC_INFO)
                ));

        //action
        final Trainer newTrainer = trainerService.setImage("101").blockingFirst();

        //check values
        assertEquals("101", newTrainer.image());

        //check mocks
        verify(regionApiService).updateTrainer("0", "1", captor.getValue());
    }
}
