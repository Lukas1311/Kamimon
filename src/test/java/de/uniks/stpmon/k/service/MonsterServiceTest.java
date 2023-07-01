package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.ICache;
import de.uniks.stpmon.k.service.storage.cache.MonsterCache;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.TestObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MonsterServiceTest {

    @Mock
    CacheManager cacheManager;
    @Spy
    TrainerStorage trainerStorage;
    @InjectMocks
    MonsterService monsterService;
    @Mock
    ICache<Monster, String> teamCache;


    @Test
    void testEmpty() {
        TestObserver<List<Monster>> monsterObserver = monsterService.getMonsters().test();
        monsterObserver.assertNoValues();
        TestObserver<List<Monster>> teamObserver = monsterService.getTeam().test();
        teamObserver.assertNoValues();
    }

    @Test
    void testMonsters() {
        MonsterCache cache = Mockito.mock(MonsterCache.class);
        when(cacheManager.requestMonsters(any())).thenReturn(cache);
        when(cache.getTeam()).thenReturn(teamCache);
        when(teamCache.getValues()).thenReturn(Observable.empty());
        assertThrows(IllegalStateException.class, () -> monsterService.getMonsterCache());
        assertThrows(IllegalStateException.class, () -> monsterService.getTeamCache());
        assertDoesNotThrow(() -> monsterService.getMonster("test"));

        trainerStorage.setTrainer(DummyConstants.TRAINER);

        assertDoesNotThrow(() -> monsterService.getMonsterCache());
        assertDoesNotThrow(() -> monsterService.getTeamCache());

        monsterService.getMonsters();
        verify(cache).getValues();

        monsterService.getTeam();
        verify(cache.getTeam()).getValues();

        monsterService.getMonster("test");
        verify(cache).listenValue(any());
    }

}
