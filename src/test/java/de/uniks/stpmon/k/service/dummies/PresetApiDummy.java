package de.uniks.stpmon.k.service.dummies;

import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.dto.ItemTypeDto;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.Event;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.models.ItemUse;
import de.uniks.stpmon.k.rest.PresetApiService;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@SuppressWarnings("unused")
@Singleton
public class PresetApiDummy implements PresetApiService {
    @Inject
    EventDummy eventDummy;
    @Inject
    TrainerStorage trainerStorage;
    final List<String> characters = new ArrayList<>();
    final List<MonsterTypeDto> monsters = new ArrayList<>();
    final List<AbilityDto> abilities = new ArrayList<>();
    final List<ItemTypeDto> items = new ArrayList<>();
    final String MONBALL_ID = "0";
    final String[] types = {"fire", "water", "grass", "electro", "physics", "ground",
            "dragon", "poison", "ice", "normal", "dark", "flying", "fighting", "rock", "steel", "bug"};

    @Inject
    public PresetApiDummy() {
    }

    /**
     * Initializes 3 DummyCharacters when getCharacters()
     * is called but no characters are in the list
     */
    private void initDummyCharacters() {
        if (!characters.isEmpty()) {
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
        if (!monsters.isEmpty()) {
            throw new IllegalStateException("Monsters already initialized");
        }
        int amount = 5;

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
            int power = 5 * i;
            abilities.add(new AbilityDto(i, name, description, type, maxUses, accuracy, power));
        }
    }

    private void initDummyItems() {
        if (!items.isEmpty()) {
            throw new IllegalStateException("Monsters already initialized");
        }

        int amount = 8;
        for (int i = 0; i < amount; i++) {
            String image = "ItemImage_" + i;
            String name = "Item_" + i;
            int price = 5 + i;
            String description = "ItemDescription_" + i;
            ItemUse use = switch (i % 4) {
                case 0 -> ItemUse.BALL;
                case 1 -> ItemUse.EFFECT;
                case 2 -> ItemUse.ITEM_BOX;
                case 3 -> ItemUse.MONSTER_BOX;
                default -> throw new IllegalStateException("Unexpected value: " + i % 4);
            };

            items.add(new ItemTypeDto(i, image, name, price, description, use));
        }


    }

    @Override
    public Observable<ResponseBody> getFile(String filename) {
        // Provider in resource service
        return Observable.empty();
    }

    @Override
    public Observable<List<String>> getCharacters() {
        if (characters.isEmpty()) {
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
    public Observable<List<ItemTypeDto>> getItems() {
        if (items.isEmpty()) {
            initDummyItems();
        }

        return Observable.just(items);
    }

    @Override
    public Observable<ItemTypeDto> getItem(String id) {
        if (items.isEmpty()) {
            initDummyItems();
        }

        Optional<ItemTypeDto> returnItem = items.stream()
                .filter(m -> Integer.parseInt(id) == m.id())
                .findFirst();

        return returnItem.map(r -> Observable.just(returnItem.get())).orElseGet(()
                -> Observable.error(new Throwable("404 Not found")));
    }

    @Override
    public Observable<ResponseBody> getItemImage(String id) {
        // Provider in resource service
        return Observable.empty();
    }

    @Override
    public Observable<List<MonsterTypeDto>> getMonsters() {
        if (monsters.isEmpty()) {
            initDummyMonsters();
        }
        return Observable.just(monsters);
    }

    @Override
    public Observable<MonsterTypeDto> getMonster(String id) {
        if (monsters.isEmpty()) {
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
        if (abilities.isEmpty()) {
            initDummyAbilities();
        }
        return Observable.just(abilities);
    }

    @Override
    public Observable<AbilityDto> getAbility(String id) {
        if (abilities.isEmpty()) {
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

    public void getMonBall() {
        String trainerId = trainerStorage.getTrainer()._id();
        Item monBall = new Item(MONBALL_ID, trainerId, Integer.parseInt(MONBALL_ID), 1);
        eventDummy.sendEvent(new Event<>("trainers.%s.items.%s.updated".formatted(trainerId, MONBALL_ID), monBall));
    }
}
