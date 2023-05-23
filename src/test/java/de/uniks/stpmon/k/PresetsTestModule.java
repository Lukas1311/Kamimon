package de.uniks.stpmon.k;

import dagger.Module;
import dagger.Provides;
import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.rest.PresetApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Singleton;
import java.util.List;

@Module
public class PresetsTestModule {

    @Provides
    @Singleton
    static PresetApiService presetApiService(){
        return new PresetApiService() {
            @Override
            public Observable<String> getFile(String filename) {
                return null;
            }

            @Override
            public Observable<List<String>> getCharacters() {
                return null;
            }

            @Override
            public Observable<String> getCharacterFile(String filename) {
                return null;
            }

            @Override
            public Observable<List<MonsterTypeDto>> getMonsters() {
                return null;
            }

            @Override
            public Observable<MonsterTypeDto> getMonster(String id) {
                return null;
            }

            @Override
            public Observable<String> getMonsterImage(String id) {
                return null;
            }

            @Override
            public Observable<List<AbilityDto>> getAbilities() {
                return null;
            }

            @Override
            public Observable<AbilityDto> getAbility(String id) {
                return null;
            }
        };
    }
}
