package de.uniks.stpmon.k.rest;

import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

import javax.json.Json;
import java.util.List;

public interface PresetApiService {
    @GET("presets/tilesets/{filename}")
    @Streaming //streaming is needed for receiving (larger) files
    Observable<ResponseBody> getFile(@Path("filename") String filename);

    @GET("presets/characters")
    Observable<List<String>> getCharacters();

    @GET("presets/characters/{filename}")
    @Streaming
    Observable<ResponseBody> getCharacterFile(@Path("filename") String filename);

    @GET("presets/monsters")
    Observable<List<MonsterTypeDto>> getMonsters();

    @GET("presets/monsters/{id}")
    Observable<MonsterTypeDto> getMonster(@Path("id") String id);

    @GET("presets/monsters/{id}/image")
    @Streaming
    //TODO: save file
    Observable<String> getMonsterImage(@Path("id") String id);

    @GET("presets/abilities")
    Observable<List<AbilityDto>> getAbilities();

    @GET("presets/abilities/{id}")
    Observable<AbilityDto> getAbility(@Path("id") String id);

}
