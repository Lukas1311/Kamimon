package de.uniks.stpmon.k.di;

import dagger.Module;
import dagger.Provides;
import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.rest.PresetApiService;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Module
public class PresetsTestModule {

    @Provides
    @Singleton
    static PresetApiService presetApiService() {
        return new PresetApiService() {
            final List<String> characters = new ArrayList<>();
            final List<MonsterTypeDto> monsters = new ArrayList<>();
            final List<AbilityDto> abilities = new ArrayList<>();
            final String[] types = {"fire", "water", "grass", "electro", "physics", "ground",
                    "dragon", "poison", "ice", "normal", "dark", "flying", "fighting", "rock", "steel", "bug"};

            /**
             * Initializes 3 DummyCharacters when getCharacters()
             * is called but no characters are in the list
             */
            private void initDummyCharacters() {
                if (characters.size() > 0) {
                    throw new IllegalStateException("Characters already initialized");
                }
                String[] c = {"trainer_0.png", "trainer_1.png", "trainer_2.png", "trainer_3.png", "Premade_Character_01.png"};
                characters.addAll(Arrays.stream(c).toList());
            }

            /**
             * Initializes 3 DummyMonsters when getMonsters()
             * is called but no monsters are in the list
             * TestMonster0 of type fire
             * TestMonster1 of type water, fire
             * TestMonster2 of type grass
             */
            private void initDummyMonsters() {
                if (monsters.size() > 0) {
                    throw new IllegalStateException("Monsters already initialized");
                }
                int amount = 3;

                for (int i = 0; i < amount; i++) {
                    String name = "TestMonster" + i;
                    String image = "MonsterImage" + i;
                    List<String> monsterTypes = new ArrayList<>();
                    monsterTypes.add(types[i]);
                    if (i == 1) {
                        monsterTypes.add(types[i - 1]);
                    }
                    String description = "description" + i;
                    monsters.add(new MonsterTypeDto(i, name, image, monsterTypes, description));
                }
            }

            /**
             * Initializes 5 DummyAbilities
             */
            private void initDummyAbilities() {
                int amount = 5;
                for (int i = 0; i < amount; i++) {
                    String type = types[i % types.length];
                    String name = type + "Ability" + i;
                    String description = "description" + i;
                    int maxUses = 3;
                    double accuracy = 0.5;
                    int power = 5;
                    abilities.add(new AbilityDto(i, name, description, type, maxUses, accuracy, power));
                }

            }

            @Override
            public Observable<ResponseBody> getFile(String filename) {
                // Provider in resource service
                return Observable.empty();
            }

            @Override
            public Observable<List<String>> getCharacters() {
                if (characters.size() == 0) {
                    initDummyCharacters();
                }
                return Observable.just(characters);
            }

            @Override
            public Observable<ResponseBody> getCharacterFile(String filename) {
                // Provider in resource service
                return Observable.empty();
            }

            @Override
            public Observable<List<MonsterTypeDto>> getMonsters() {
                if (monsters.size() == 0) {
                    initDummyMonsters();
                }
                return Observable.just(monsters);
            }

            @Override
            public Observable<MonsterTypeDto> getMonster(String id) {
                if (monsters.size() == 0) {
                    initDummyMonsters();
                }
                Optional<MonsterTypeDto> returnMonster = monsters.stream()
                        .filter(m -> Integer.parseInt(id) == m.id())
                        .findFirst();

                return returnMonster.map(r -> Observable.just(returnMonster.get())).orElseGet(()
                        -> Observable.error(new Throwable("404 Not found")));
            }

            @Override
            public Observable<ResponseBody> getMonsterImage(String id) {
                // Provider in resource service
                return Observable.empty();
            }

            @Override
            public Observable<List<AbilityDto>> getAbilities() {
                if (abilities.size() == 0) {
                    initDummyAbilities();
                }
                return Observable.just(abilities);
            }

            @Override
            public Observable<AbilityDto> getAbility(String id) {
                if (abilities.size() == 0) {
                    initDummyAbilities();
                }

                if (id.equals("10") || id.equals("7")) {
                    return Observable.just(abilities.get(0));
                }

                Optional<AbilityDto> returnAbility = abilities.stream()
                        .filter(a -> Integer.parseInt(id) == a.id())
                        .findFirst();

                return returnAbility.map(r -> Observable.just(returnAbility.get())).orElseGet(()
                        -> Observable.error(new Throwable("404 Not found")));
            }
        };
    }

}
