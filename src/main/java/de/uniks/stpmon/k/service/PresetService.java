package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.rest.PresetApiService;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Path;

import javax.inject.Inject;
import java.util.List;

public class PresetService {

    @Inject
    PresetApiService presetApiService;

    public Observable<ResponseBody> getFile(String filename){
        return presetApiService.getFile(filename);
    }

    public Observable<List<String>> getCharacters(){
        return presetApiService.getCharacters();
    }

    public Observable<ResponseBody> getCharacterFile(String filename){
        return presetApiService.getCharacterFile(filename);
    }

    public Observable<List<MonsterTypeDto>> getMonsters(){
        return presetApiService.getMonsters();
    }

    public Observable<MonsterTypeDto> getMonster(String id){
        return presetApiService.getMonster(id);
    }

    public Observable<String> getMonsterImage(String id){
        return presetApiService.getMonsterImage(id);
    }

    public Observable<List<AbilityDto>> getAbilities(){
        return presetApiService.getAbilities();
    }

    public Observable<AbilityDto> getAbility(String id){
        return presetApiService.getAbility(id);
    }


}
