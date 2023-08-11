package de.uniks.stpmon.k.service.dummies;

import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.di.TestComponent;
import de.uniks.stpmon.k.dto.TalkTrainerDto;
import de.uniks.stpmon.k.models.Event;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.models.builder.NPCInfoBuilder;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.WorldRepository;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.TrainerCache;
import de.uniks.stpmon.k.utils.Direction;
import io.reactivex.rxjava3.subjects.PublishSubject;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TestHelper {

    public static void addWorldDummy(WorldRepository repository) {
        repository.regionMap().setValue(DummyConstants.EMPTY_IMAGE);
        repository.minimapImage().setValue(DummyConstants.EMPTY_IMAGE);
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
    public static void listenStarterMonster(TrainerStorage storage, TestComponent component) {
        EventDummy eventDummy = component.eventDummy();
        EncounterApiDummy encounterApi = component.encounterApi();
        RegionApiDummy regionApi = component.regionApi();
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
            if (dto.target().equals("attacker1")) {
                encounterApi.startEncounter(true, true);
                return;
            }
            if (dto.target().equals("wild")) {
                encounterApi.startEncounter(false, true);
                return;
            }
            if (dto.target().equals("nurse")) {
                for (Monster monster : regionApi.getMonsters(RegionApiDummy.REGION_ID, trainer._id()).blockingFirst()) {
                    regionApi.updateMonster(MonsterBuilder.builder(monster)
                            .setCurrentAttributes(monster.attributes())
                            .create());
                }
                return;
            }

            // Not same as trainer, subscription could be called to a totally different time so the trainer could be changed
            Trainer currentTrainer = storage.getTrainer();
            regionApi.addMonster(currentTrainer._id(), "0", true);
            regionApi.addMonster(currentTrainer._id(), "3", true);
        });

    }

    public static void addEncounteredMonsters(TrainerStorage storage, TestComponent component) {
        RegionApiDummy regionApi = component.regionApi();
        Trainer trainer = storage.getTrainer();
        regionApi.addEncounteredMonsters(trainer._id(), 0);
        regionApi.addEncounteredMonsters(trainer._id(), 2);
    }

    public static void addMonster(TrainerStorage storage, TestComponent component) {
        RegionApiDummy regionApi = component.regionApi();
        Trainer trainer = storage.getTrainer();
        regionApi.addMonster(trainer._id(), "0", true);
    }

    public static void addTestNpcsV3(TestComponent component) {
        CacheManager cacheManager = component.cacheManager();
        TrainerCache trainerCache = cacheManager.trainerCache();

        Trainer prof = TrainerBuilder.builder()
                .setId("prof")
                .setX(4)
                .setRegion("id0")
                .setArea("id0_0")
                .setDirection(Direction.LEFT)
                .setNpc(NPCInfoBuilder.builder().addStarters(List.of("0", "1", "2")).create())
                .create();

        trainerCache.addValue(prof);

        Trainer attacker = TrainerBuilder.builder()
                .setId("attacker")
                .setX(3)
                .setY(3)
                .setRegion("id0")
                .setArea("id0_0")
                .setDirection(Direction.TOP)
                .setNpc(NPCInfoBuilder.builder().setEncounterOnTalk(true).create())
                .create();
        Trainer attacker1 = TrainerBuilder.builder()
                .setId("attacker1")
                .setX(4)
                .setY(4)
                .setRegion("id0")
                .setArea("id0_0")
                .setDirection(Direction.TOP)
                .setNpc(NPCInfoBuilder.builder().setEncounterOnTalk(true).create())
                .create();

        component.regionApi().addTrainer(attacker);
        component.regionApi().addMonster("attacker", "1", true);
        component.regionApi().addTrainer(attacker1);
        component.regionApi().addMonster("attacker1", "2", true);

        Trainer nurse = TrainerBuilder.builder()
                .setId("nurse")
                .setX(1)
                .setY(5)
                .setRegion("id0")
                .setArea("id0_0")
                .setDirection(Direction.TOP)
                .setNpc(NPCInfoBuilder.builder().setCanHeal(true).create())
                .create();

        component.regionApi().addTrainer(nurse);
    }

    public static void addTestNpcsV4(TestComponent component) {
        Trainer attacker = TrainerBuilder.builder()
                .setId("attacker")
                .setX(2)
                .setRegion("id0")
                .setArea("id0_0")
                .setDirection(Direction.BOTTOM)
                .setNpc(NPCInfoBuilder.builder().setEncounterOnTalk(true).create())
                .create();

        Trainer clerk = TrainerBuilder.builder()
                .setId("clerk")
                .setX(3)
                .setRegion("id0")
                .setArea("id0_0")
                .setDirection(Direction.BOTTOM)
                .setNpc(NPCInfoBuilder.builder().addSells(0).addSells(1).addSells(2).create())
                .create();

        Trainer wildEncounter = TrainerBuilder.builder()
                .setId("wild")
                .setX(2)
                .setY(3)
                .setRegion("id0")
                .setArea("id0_0")
                .setDirection(Direction.BOTTOM)
                .setNpc(NPCInfoBuilder.builder()
                        .setEncounterOnTalk(true)
                        .create())
                .create();


        component.regionApi().addTrainer(attacker);
        component.regionApi().addTrainer(clerk);
        component.regionApi().addTrainer(wildEncounter);
        component.regionApi().addMonster("attacker", "1", true);
        component.regionApi().addMonster("wild", "5", true);
    }

}
