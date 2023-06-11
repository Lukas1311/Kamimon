package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.service.storage.TrainerProvider;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.IFriendCache;
import de.uniks.stpmon.k.service.storage.cache.MonsterCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Provider;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CacheManagerTest {

    @InjectMocks
    CacheManager cacheManager;
    @Mock
    protected Provider<MonsterCache> monsterCacheProvider;
    @Mock
    protected Provider<IFriendCache> friendCacheProvider;

    @Test
    void destroy() {
        when(monsterCacheProvider.get()).thenAnswer((t) -> Mockito.mock(MonsterCache.class));
        when(friendCacheProvider.get()).thenAnswer(invocation -> Mockito.mock(IFriendCache.class));
        IFriendCache friends = cacheManager.requestFriends("test1");
        MonsterCache monsters = cacheManager.requestMonsters("test1");
        cacheManager.requestTrainer("test1");
        cacheManager.destroy();
        // All caches should be destroyed
        assertFalse(cacheManager.hasFriends("test1"));
        assertFalse(cacheManager.hasMonsters("test1"));
        assertFalse(cacheManager.hasTrainer("test1"));
        verify(friends).destroy();
        verify(monsters).destroy();
    }

    @Test
    void requestTrainer() {
        // No cache should be created
        assertFalse(cacheManager.hasTrainer("test1"));

        TrainerProvider provider = cacheManager.requestTrainer("test1");
        // Cache should be created
        assertTrue(cacheManager.hasTrainer("test1"));
        TrainerProvider other = cacheManager.requestTrainer("test2");
        // Cache should not be same
        assertNotEquals(provider, other);

        assertThrows(IllegalArgumentException.class, () -> cacheManager.requestTrainer(null));
    }

    @Test
    void requestMonsters() {
        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        when(monsterCacheProvider.get()).thenAnswer((t) -> Mockito.mock(MonsterCache.class));

        // No cache should be created
        assertFalse(cacheManager.hasFriends("test1"));

        MonsterCache cache = cacheManager.requestMonsters("test1");
        // Cache should be created
        assertTrue(cacheManager.hasMonsters("test1"));
        // verify that the cache is initialized correctly
        verify(cache).setTrainerId("test1");
        verify(cache).init();
        verify(cache).addOnDestroy(captor.capture());
        // Cache should be reused
        assertEquals(cache, cacheManager.requestMonsters("test1"));

        MonsterCache cacheOther = cacheManager.requestMonsters("test2");
        // Cache should be created
        assertTrue(cacheManager.hasMonsters("test2"));
        // Cache should be different because the old one was destroyed
        assertNotEquals(cache, cacheOther);

        // mock destroy behaviour
        captor.getValue().run();
        cache.destroy();
        // Cache should be removed after destroy
        assertFalse(cacheManager.hasMonsters("test1"));

        // Request cache for same trainer
        MonsterCache cache2 = cacheManager.requestMonsters("test1");
        // Cache should be different because the old one was destroyed
        assertNotEquals(cache, cache2);

        assertThrows(IllegalArgumentException.class, () -> cacheManager.requestMonsters(null));
    }

    @Test
    void requestFriends() {
        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        when(friendCacheProvider.get()).thenAnswer(invocation -> {
            IFriendCache cache = Mockito.mock(IFriendCache.class);
            ArgumentCaptor<String> mainUser = ArgumentCaptor.forClass(String.class);
            when(cache.setMainUser(mainUser.capture())).thenReturn(cache);
            when(cache.isMainUser(any())).thenAnswer(invocation1 ->
                    invocation1.getArgument(0).equals(mainUser.getValue()));
            return cache;
        });
        // No cache should be created
        assertFalse(cacheManager.hasFriends("test1"));
        // Request cache
        IFriendCache cache = cacheManager.requestFriends("test1");
        // Cache should be created
        assertTrue(cacheManager.hasFriends("test1"));
        // verify that the cache is initialized correctly
        verify(cache).setMainUser("test1");
        verify(cache).init();
        // Capture the destroy callback
        verify(cache).addOnDestroy(captor.capture());
        // Cache should be reused
        assertEquals(cache, cacheManager.requestFriends("test1"));

        IFriendCache cacheOther = cacheManager.requestFriends("test2");
        // Should not be possible to request cache for other user if main user is still not destroyed
        assertFalse(cacheManager.hasFriends("test2"));
        // Cache should be the same because the old one was not destroyed
        assertEquals(cache, cacheOther);

        // mock destroy behaviour
        captor.getValue().run();
        cache.destroy();
        // Cache should be removed after destroy
        assertFalse(cacheManager.hasFriends("test1"));

        // Request cache for same user
        IFriendCache cacheNew = cacheManager.requestFriends("test1");
        // Cache should be different because the old one was destroyed
        assertNotEquals(cache, cacheNew);
        // Cache should be created
        assertTrue(cacheManager.hasFriends("test1"));

        assertThrows(IllegalArgumentException.class, () -> cacheManager.requestFriends(null));
    }
}