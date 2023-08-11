package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.dto.ItemTypeDto;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.rest.PresetApiService;
import de.uniks.stpmon.k.service.storage.cache.AbilityCache;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.ItemTypeCache;
import de.uniks.stpmon.k.service.storage.cache.MonsterTypeCache;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;

public class PresetService {

    @Inject
    PresetApiService presetApiService;
    @Inject
    Provider<CacheManager> cacheManagerProvider;
    private List<String> characters;

    @Inject
    public PresetService() {
    }

    public Observable<ResponseBody> getFile(String filename) {
        return presetApiService.getFile(filename);
    }

    public Observable<List<String>> getCharacters() {
        if (characters == null) {
            return presetApiService.getCharacters().doOnNext(list -> characters = list);
        }
        return Observable.just(characters);
    }

    public Observable<ResponseBody> getCharacterFile(String filename) {
        return presetApiService.getCharacterFile(filename);
    }

    public Observable<List<MonsterTypeDto>> getMonsters() {
        CacheManager manager = cacheManagerProvider.get();
        MonsterTypeCache typeCache = manager.monsterTypeCache();
        return typeCache.getValues();
    }

    public Observable<MonsterTypeDto> getMonster(int id) {
        return getMonster(Integer.toString(id));
    }

    public Observable<MonsterTypeDto> getMonster(String id) {
        CacheManager manager = cacheManagerProvider.get();
        MonsterTypeCache typeCache = manager.monsterTypeCache();
        return typeCache.getLazyValue(id).flatMap(op ->
                op.map(Observable::just).orElse(Observable.empty())
        );
    }

    public Observable<ResponseBody> getMonsterImage(String id) {
        return presetApiService.getMonsterImage(id);
    }

    public Observable<List<AbilityDto>> getAbilities() {
        CacheManager manager = cacheManagerProvider.get();
        AbilityCache abilityCache = manager.abilityCache();
        return abilityCache.getValues();
    }

    public Observable<AbilityDto> getAbility(int id) {
        return getAbility(Integer.toString(id));
    }

    public Observable<AbilityDto> getAbility(String id) {
        CacheManager manager = cacheManagerProvider.get();
        AbilityCache abilityCache = manager.abilityCache();
        return abilityCache.getLazyValue(id).flatMap(op ->
                op.map(Observable::just).orElse(Observable.empty())
        );
    }

    public Observable<ItemTypeDto> getItem(int id) {
        return getItem(Integer.toString(id));
    }

    public Observable<ItemTypeDto> getItem(String itemId) {
        CacheManager manager = cacheManagerProvider.get();
        ItemTypeCache typeCache = manager.itemTypeCache();
        return typeCache.getLazyValue(itemId).flatMap(op ->
                op.map(Observable::just).orElse(Observable.empty())
        );
    }

    public Observable<ResponseBody> getItemImage(String itemId) {
        return presetApiService.getItemImage(itemId);
    }


}
