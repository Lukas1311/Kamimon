package de.uniks.stpmon.k.rest;

import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.dto.ItemTypeDto;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

import java.util.List;

@SuppressWarnings("unused")
public interface PresetApiService {

    @GET("presets/tilesets/{filename}")
    @Streaming
        //streaming is needed for receiving (larger) files
    Observable<ResponseBody> getFile(@Path("filename") String filename);

    @GET("presets/characters")
    Observable<List<String>> getCharacters();

    @GET("presets/characters/{filename}")
    @Streaming
    Observable<ResponseBody> getCharacterFile(@Path("filename") String filename);

    @GET("presets/items")
    Observable<List<ItemTypeDto>> getItems();

    @GET("presets/items/{id}")
    Observable<ItemTypeDto> getItem(@Path("id") String id);

    @GET("presets/items/{id}/image")
    @Streaming
    Observable<ResponseBody> getItemImage(@Path("id") String id);

    @GET("presets/monsters")
    Observable<List<MonsterTypeDto>> getMonsters();

    @GET("presets/monsters/{id}")
    Observable<MonsterTypeDto> getMonster(@Path("id") String id);

    @GET("presets/monsters/{id}/image")
    @Streaming
    Observable<ResponseBody> getMonsterImage(@Path("id") String id);

    @GET("presets/abilities")
    Observable<List<AbilityDto>> getAbilities();

    @GET("presets/abilities/{id}")
    Observable<AbilityDto> getAbility(@Path("id") String id);

}
