package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.models.Encounter;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.models.builder.OpponentBuilder;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.MonsterCache;
import de.uniks.stpmon.k.service.storage.cache.OpponentCache;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Provider;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Spy
    TrainerStorage trainerStorage;
    @Spy
    EncounterStorage encounterStorage;
    @Mock
    EncounterService encounterService;
    @Mock
    CacheManager cacheManager;
    @Mock
    Provider<OpponentCache> opponentCacheProvider;
    @InjectMocks
    SessionService sessionService;

    @Test
    void loadEncounter() {
        trainerStorage.setTrainer(TrainerBuilder.builder().setId("0").create());
        encounterStorage.setEncounter(new Encounter("", "", false));
        List<Opponent> opponents = List.of(
                OpponentBuilder.builder().setId("0").create(),
                OpponentBuilder.builder().setId("1").create(),
                OpponentBuilder.builder().setId("2").create(),
                OpponentBuilder.builder().setId("3").create()
        );
        OpponentCache cache = Mockito.mock(OpponentCache.class);
        when(cache.getValues()).thenReturn(Observable.just(opponents));
        when(encounterService.getEncounterOpponents()).thenReturn(Observable.just(opponents));
        when(opponentCacheProvider.get()).thenReturn(cache);
        when(cacheManager.requestMonsters(any())).thenReturn(Mockito.mock(MonsterCache.class));

        sessionService.loadEncounter().blockingAwait();
    }
}