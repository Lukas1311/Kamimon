package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.models.Monster;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class MonsterService {
    private final Map<String, Monster> monsters;

    @Inject
    public MonsterService() {
        monsters = new HashMap<>();
    }

    public void addMonster(Monster monster) {
        monsters.put(monster._id(), monster);
    }

    public void removeMonster(String id) {
        monsters.remove(id);
    }

    public Monster getMonster(String id) {
        return monsters.get(id);
    }

    public int getMonsterList() {
        return monsters.size();
    }
}
