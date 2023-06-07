package de.uniks.stpmon.k.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.map.TilesetData;
import de.uniks.stpmon.k.rest.PresetApiService;
import de.uniks.stpmon.k.utils.ResponseUtils;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.List;

public class PresetService {

    @Inject
    PresetApiService presetApiService;

    @Inject
    ObjectMapper mapper;

    @Inject
    public PresetService() {
    }

    public Observable<ResponseBody> getFile(String filename) {
        return presetApiService.getFile(filename);
    }

    public Observable<List<String>> getCharacters() {
        return presetApiService.getCharacters();
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

    public Observable<String> getMonsterImage(String id) {
        return presetApiService.getMonsterImage(id);
    }

    public Observable<List<AbilityDto>> getAbilities() {
        return presetApiService.getAbilities();
    }

    public Observable<AbilityDto> getAbility(String id) {
        return presetApiService.getAbility(id);
    }

    public Observable<BufferedImage> getImage(String fileName) {
        return ResponseUtils.readImage(getFile(fileName));
    }

    public Observable<TilesetData> getTileset(String fileName) {
        return ResponseUtils.readJson(getFile(fileName), mapper, TilesetData.class);
    }

}
