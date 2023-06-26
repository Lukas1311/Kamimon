package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.rest.PresetApiService;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;

import javax.inject.Inject;
import java.util.List;

public class PresetService {

    @Inject
    PresetApiService presetApiService;
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
        return presetApiService.getMonsters();
    }

    public Observable<MonsterTypeDto> getMonster(String id) {
        return presetApiService.getMonster(id);
    }

    public Observable<ResponseBody> getMonsterImage(String id) {
        return presetApiService.getMonsterImage(id);
    }

    public Observable<List<AbilityDto>> getAbilities() {
        return presetApiService.getAbilities();
    }

    public Observable<AbilityDto> getAbility(String id) {
        return presetApiService.getAbility(id);
    }

}
