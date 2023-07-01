package de.uniks.stpmon.k.service.dummies;

import de.uniks.stpmon.k.dto.TalkTrainerDto;
import de.uniks.stpmon.k.models.Event;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.storage.TrainerStorage;

public class MonsterDummy {
    public static void addMonsterDummy(TrainerStorage storage, EventDummy eventDummy) {
        Trainer trainer = storage.getTrainer();
        eventDummy.listen(Socket.UDP,
                "areas.%s.trainers.%s.talked".formatted(trainer.area(), trainer._id()),
                TalkTrainerDto.class).subscribe(event -> {
            TalkTrainerDto dto = event.data();
            int selection = dto.selection();

            // Not same as trainer, subscription could be called to a totally different time so the trainer could be changed
            Trainer currentTrainer = storage.getTrainer();
            eventDummy.sendEvent(new Event<>("trainers.%s.monsters.%s.created"
                    .formatted(currentTrainer._id(), "monster_0"),
                    MonsterBuilder.builder()
                            .setId("monster_label_0")
                            .setType(selection)
                            .create()));
            eventDummy.sendEvent(new Event<>("regions.%s.trainers.%s.updated"
                    .formatted(currentTrainer.region(), currentTrainer._id()),
                    TrainerBuilder.builder(currentTrainer)
                            .addTeam("monster_label_0")
                            .create()));
        });

    }
}