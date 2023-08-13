package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.models.NPCInfo;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.NPCInfoBuilder;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.models.dialogue.Dialogue;
import de.uniks.stpmon.k.service.storage.InteractionStorage;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Provider;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InteractionServiceTest {

        @Spy
        InteractionStorage interactionStorage;
        @Mock
        TrainerService trainerService;
        @Mock
        PresetService presetService;
        @Mock
        MonsterService monsterService;
        @Mock
        UserService userService;
        @Mock
        EncounterService encounterService;
        @Spy
        final ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
        @Mock
        Provider<ResourceBundle> resourceBundleProvider;

        @InjectMocks
        InteractionService interactionService;

    @Test
    void emptyDialogue() {
        // Empty at first
        when(trainerService.getFacingTrainer(1)).thenReturn(Optional.empty());

        // Search for dialogue in facing trainer
        interactionService.tryUpdateDialogue().blockingAwait();
        // No dialogue found, still empty
        assertNull(interactionStorage.getDialogue());
    }

        @Test
        void noDialogue() {
                // Empty at first
                assertNull(interactionStorage.getDialogue());

                when(trainerService.getFacingTrainer(1)).thenReturn(
                                Optional.of(TrainerBuilder.builder()
                                                .setNpc(NPCInfoBuilder.builder().create()).create()));

                // Search for dialogue in facing trainer
                interactionService.tryUpdateDialogue().blockingAwait();
                // Not found dialogue, still empty
                assertNull(interactionStorage.getDialogue());
        }

        @Test
        void playerDialogue() {
                // Empty at first
                assertNull(interactionStorage.getDialogue());

                // Setup mocked values
                when(resourceBundleProvider.get()).thenReturn(resources);
                when(trainerService.getMe()).thenReturn(DummyConstants.TRAINER);
                when(encounterService.getTrainerOpponents(anyString()))
                                .thenReturn(Observable.just(List.of(DummyConstants.OPPONENT)));
                when(encounterService.getTrainerOpponents(anyString()))
                                .thenReturn(Observable.just(List.of(DummyConstants.OPPONENT)));
                when(trainerService.getFacingTrainer(1)).thenReturn(Optional.of(DummyConstants.TRAINER));
                when(trainerService.getFacingTrainer(1)).thenReturn(Optional.of(DummyConstants.TRAINER_OTHER));
                when(userService.isOnline(anyString())).thenReturn(Observable.just(true));
                when(monsterService.anyMonsterAlive(anyString())).thenReturn(Observable.just(true));
                when(monsterService.anyMonsterAlive()).thenReturn(true);
                // Search for dialogue in facing trainer
                interactionService.tryUpdateDialogue().blockingAwait();
                // Dialogue found, trainer is another player
                assertNotNull(interactionStorage.getDialogue());
        }

        @Test
        void monsterDialogue() {
                // Empty at first
                assertNull(interactionStorage.getDialogue());

                Trainer trainer = TrainerBuilder.builder()
                                .setNpc(NPCInfoBuilder.builder().addStarters(List.of("monster_0", "monster_1"))
                                                .create())
                                .create();

                when(trainerService.getFacingTrainer(1)).thenReturn(Optional.of(trainer));
                when(trainerService.getMe()).thenReturn(DummyConstants.TRAINER);
                when(resourceBundleProvider.get()).thenReturn(resources);
                when(presetService.getMonster(anyString())).thenReturn(Observable.just(DummyConstants.MONSTER_TYPE));

                // Search for dialogue in facing trainer
                interactionService.tryUpdateDialogue().blockingAwait();
                // Found dialogue
                Dialogue dialogue = interactionStorage.getDialogue();
                assertNotNull(dialogue);
        }

        @Test
        void checkPossibleDialogue() {
                // Mock values
                Trainer firstTrainer = TrainerBuilder.builder()
                                .setId("first")
                                .setNpc(NPCInfoBuilder.builder().addStarters(List.of("monster_0", "monster_1"))
                                                .create())
                                .create();
                Trainer secondTrainer = TrainerBuilder.builder(firstTrainer)
                                .setId("second")
                                .create();
                // Trainer with no dialogue
                Trainer dummyTrainer = TrainerBuilder.builder()
                                .setId("dummy")
                                .setNpc(NPCInfoBuilder.builder().create())
                                .create();

                when(trainerService.getMe()).thenReturn(DummyConstants.TRAINER);
                when(resourceBundleProvider.get()).thenReturn(resources);
                when(presetService.getMonster(anyString())).thenReturn(Observable.just(DummyConstants.MONSTER_TYPE));

                // First no trainer returned, no dialogue should be found
                when(trainerService.getFacingTrainer(2)).thenReturn(Optional.empty());
                when(trainerService.getFacingTrainer(1)).thenReturn(Optional.empty());

                // No dialogue should be found
                interactionService.getPossibleDialogue().test().assertNoValues();

                // Now second trainer should be found
                when(trainerService.getFacingTrainer(2)).thenReturn(Optional.of(secondTrainer));

                Dialogue secondDialogue = interactionService.getPossibleDialogue().blockingFirst();
                // Trainer should now be found
                assertNotNull(secondDialogue);
                assertEquals("second", secondDialogue.getTrainerId());

                // Add dummy trainer should not be found
                when(trainerService.getFacingTrainer(1)).thenReturn(Optional.of(dummyTrainer));

                Dialogue thirdDialogue = interactionService.getPossibleDialogue().blockingFirst();
                // Should still use second trainer, dummy should have no dialogue
                assertNotNull(thirdDialogue);
                assertEquals("second", thirdDialogue.getTrainerId());

                // Now first trainer should be found
                when(trainerService.getFacingTrainer(1)).thenReturn(Optional.of(firstTrainer));

                Dialogue fourthDialogue = interactionService.getPossibleDialogue().blockingFirst();
                // Should now use dialogue from first trainer
                assertNotNull(fourthDialogue);
                assertEquals("first", fourthDialogue.getTrainerId());
        }

        @Test
        void healDialogue() {
                // Empty at first
                assertNull(interactionStorage.getDialogue());

                Trainer trainer = TrainerBuilder.builder()
                                .setNpc(new NPCInfo(false,
                                                false,
                                                true,
                                                List.of(),
                                                null,
                                                List.of()))
                                .create();

                when(trainerService.getFacingTrainer(1)).thenReturn(Optional.of(trainer));
                when(trainerService.getMe()).thenReturn(DummyConstants.TRAINER);
                when(resourceBundleProvider.get()).thenReturn(resources);

                // If any monster is not fully healed, a heal dialogue should be found
                when(monsterService.anyMonsterDamaged()).thenReturn(true);

                // Search for dialogue in facing trainer
                interactionService.tryUpdateDialogue().blockingAwait();
                // Found dialogue
                Dialogue dialogue = interactionStorage.getDialogue();
                assertNotNull(dialogue);
        }
}
