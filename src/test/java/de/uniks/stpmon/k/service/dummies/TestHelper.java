package de.uniks.stpmon.k.service.dummies;

import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.dto.TalkTrainerDto;
import de.uniks.stpmon.k.models.Event;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.WorldRepository;
import io.reactivex.rxjava3.subjects.PublishSubject;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TestHelper {

    public static void addWorldDummy(WorldRepository repository) {
        repository.regionMap().setValue(DummyConstants.EMPTY_IMAGE);
        repository.minimapImage().setValue(DummyConstants.EMPTY_IMAGE);
        repository.floorImage().setValue(DummyConstants.EMPTY_IMAGE);
        repository.props().setValue(List.of());
    }

    public static void addMovementDummy(EventListener listener) {
        PublishSubject<Event<Object>> subject = PublishSubject.create();
        when(listener.listen(any(), any(), any())).thenReturn(subject);
        when(listener.send(any(), any(), any())).thenAnswer((invocation) -> {
            subject.onNext(new Event<>(invocation.getArgument(1), invocation.getArgument(2)));
            return null;
        });
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    public static void listenStarterMonster(TrainerStorage storage, EventDummy eventDummy, EncounterApiDummy encounterApi) {
        Trainer trainer = storage.getTrainer();
        // suppresses observable result - is never disposed fo the test time
        eventDummy.listen(Socket.UDP,
                "areas.%s.trainers.%s.talked".formatted(trainer.area(), trainer._id()),
                TalkTrainerDto.class).subscribe(event -> {
            TalkTrainerDto dto = event.data();
            if (dto.target().equals("attacker")) {
                encounterApi.startEncounter();
                return;
            }

            int selection = dto.selection();

            // Not same as trainer, subscription could be called to a totally different time so the trainer could be changed
            Trainer currentTrainer = storage.getTrainer();
            eventDummy.sendEvent(new Event<>("trainers.%s.monsters.%s.created"
                    .formatted(currentTrainer._id(), "monster_0"),
                    MonsterBuilder.builder()
                            .setId("monster_0")
                            .setType(selection)
                            .create()));
            eventDummy.sendEvent(new Event<>("regions.%s.trainers.%s.updated"
                    .formatted(currentTrainer.region(), currentTrainer._id()),
                    TrainerBuilder.builder(currentTrainer)
                            .addTeam("monster_0")
                            .create()));
        });

    }
}
