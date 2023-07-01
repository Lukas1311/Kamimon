package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.rest.PresetApiService;
import de.uniks.stpmon.k.service.storage.cache.AbilityCache;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.MonsterTypeCache;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PresetServiceTest {

    @Mock
    PresetApiService presetApiService;
    @InjectMocks
    PresetService presetService;
    @Mock
    CacheManager cacheManager;
    @Mock
    Provider<CacheManager> cacheManagerProvider;
    @InjectMocks
    AbilityCache abilityCache;
    @InjectMocks
    MonsterTypeCache monsterTypeCache;

    private ResponseBody getDummyResponseBody() {
        return ResponseBody.create(null, "file");
    }

    private MonsterTypeDto getDummyMonsterTypeDto() {
        return new MonsterTypeDto(0, "monsterTest", "image", null, null);
    }

    private AbilityDto getDummyAbilityDto() {
        return new AbilityDto(0, "abilityTest", null, null, null, null, null);
    }

    @Test
    void getFile() {
        ResponseBody responseBody = getDummyResponseBody();
        //define mocks
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        when(presetApiService.getFile(any(String.class)))
                .thenReturn(Observable.just(responseBody));

        //action
        try (ResponseBody returnResponseBody = presetService
                .getFile("file")
                .blockingFirst()) {
            //check values
            assertEquals("file", returnResponseBody.string());

            //check mocks
            verify(presetApiService).getFile(captor.capture());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getCharacters() {
        String characterId = "characterId";
        List<String> characters = new ArrayList<>();
        characters.add(characterId);
        //define mocks
        when(presetApiService.getCharacters())
                .thenReturn(Observable.just(characters));

        //action
        List<String> returnCharacters = presetService.getCharacters().blockingFirst();

        //check values
        assertEquals(1, returnCharacters.size());
        assertEquals("characterId", returnCharacters.get(0));

        //check mocks
        verify(presetApiService).getCharacters();
    }

    @Test
    void getCharacterFile() {
        ResponseBody responseBody = getDummyResponseBody();
        //define mocks
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        when(presetApiService.getCharacterFile(any(String.class)))
                .thenReturn(Observable.just(responseBody));

        //action
        try (ResponseBody returnResponseBody = presetService
                .getCharacterFile("file")
                .blockingFirst()) {
            //check values
            assertEquals("file", returnResponseBody.string());

            //check mocks
            verify(presetApiService).getCharacterFile(captor.capture());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getMonsters() {
        when(cacheManagerProvider.get()).thenReturn(cacheManager);
        when(cacheManager.monsterTypeCache()).thenReturn(monsterTypeCache);

        MonsterTypeDto monster = getDummyMonsterTypeDto();
        List<MonsterTypeDto> monsterTypeDtos = new ArrayList<>();
        monsterTypeDtos.add(monster);
        //define mocks
        when(presetApiService.getMonsters())
                .thenReturn(Observable.just(monsterTypeDtos));

        // init cache, we need to do this because the cache is not initialized by the cache manager in the test
        monsterTypeCache.init();

        //action
        List<MonsterTypeDto> returnMonsters = presetService.getMonsters().blockingFirst();

        //check values
        assertEquals(1, returnMonsters.size());
        assertEquals(0, returnMonsters.get(0).id());

        //check mocks
        verify(presetApiService).getMonsters();
    }

    @Test
    void getMonster() {
        when(cacheManagerProvider.get()).thenReturn(cacheManager);
        when(cacheManager.monsterTypeCache()).thenReturn(monsterTypeCache);

        when(presetApiService.getMonsters())
                .thenReturn(Observable.empty());

        // init cache, we need to do this because the cache is not initialized by the cache manager in the test
        monsterTypeCache.init();

        MonsterTypeDto monster = getDummyMonsterTypeDto();
        //define mocks
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        when(presetApiService.getMonster("0"))
                .thenReturn(Observable.just(monster));

        //action
        MonsterTypeDto returnMonster = presetService.getMonster("0").blockingFirst();

        //check values
        assertEquals(0, returnMonster.id());

        //check mocks
        verify(presetApiService).getMonster(captor.capture());
    }

    @Test
    void getMonsterImage() {
        ResponseBody responseBody = getDummyResponseBody();
        //define mocks
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        when(presetApiService.getMonsterImage(any(String.class)))
                .thenReturn(Observable.just(responseBody));

        //action
        try (ResponseBody returnResponseBody = presetService
                .getMonsterImage("file")
                .blockingFirst()) {
            //check values
            assertEquals("file", returnResponseBody.string());

            //check mocks
            verify(presetApiService).getMonsterImage(captor.capture());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAbilities() {
        when(cacheManagerProvider.get()).thenReturn(cacheManager);
        when(cacheManager.abilityCache()).thenReturn(abilityCache);

        AbilityDto abilityDto = getDummyAbilityDto();
        List<AbilityDto> abilityDtos = new ArrayList<>();
        abilityDtos.add(abilityDto);
        //define mocks
        when(presetApiService.getAbilities())
                .thenReturn(Observable.just(abilityDtos));

        // init cache, we need to do this because the cache is not initialized by the cache manager in the test
        abilityCache.init();

        //action
        List<AbilityDto> returnAbilities = presetService.getAbilities().blockingFirst();

        //check values
        assertEquals(1, returnAbilities.size());
        assertEquals(0, returnAbilities.get(0).id());

        //check mocks
        verify(presetApiService).getAbilities();
    }

    @Test
    void getAbility() {
        when(cacheManagerProvider.get()).thenReturn(cacheManager);
        when(cacheManager.abilityCache()).thenReturn(abilityCache);

        when(presetApiService.getAbilities())
                .thenReturn(Observable.empty());

        // init cache, we need to do this because the cache is not initialized by the cache manager in the test
        abilityCache.init();

        AbilityDto abilityDto = getDummyAbilityDto();

        //define mocks
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        when(presetApiService.getAbility("0"))
                .thenReturn(Observable.just(abilityDto));

        //action
        AbilityDto returnAbility = presetService.getAbility("0").blockingFirst();

        //check values
        assertEquals(0, returnAbility.id());

        //check mocks
        verify(presetApiService).getAbility(captor.capture());
    }

}