package de.uniks.stpmon.k.models.builder;

import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.MonsterAttributes;
import de.uniks.stpmon.k.models.MonsterStatus;

import java.util.*;

@SuppressWarnings("unused")
public class MonsterBuilder {

    public static MonsterBuilder builder() {
        return new MonsterBuilder();
    }

    public static MonsterBuilder builder(Monster monster) {
        return builder().apply(monster);
    }

    private String _id = "";
    private String trainer = "";
    private Integer type = 0;
    private Integer level = 0;
    private Integer experience = 0;
    private SortedMap<String, Integer> abilities = Collections.emptySortedMap();
    private MonsterAttributes attributes = new MonsterAttributes(0f, 0f, 0f, 0f);
    private MonsterAttributes currentAttributes = new MonsterAttributes(0f, 0f, 0f, 0f);
    private final List<MonsterStatus> status = new LinkedList<>();

    private MonsterBuilder() {
    }

    public MonsterBuilder setId(int _id) {
        return setId(Integer.toString(_id));
    }

    public MonsterBuilder setId(String _id) {
        this._id = _id;
        return this;
    }

    public MonsterBuilder setTrainer(String trainer) {
        this.trainer = trainer;
        return this;
    }

    public MonsterBuilder setType(Integer type) {
        this.type = type;
        return this;
    }

    public MonsterBuilder setLevel(Integer level) {
        this.level = level;
        return this;
    }

    public MonsterBuilder setExperience(Integer experience) {
        this.experience = experience;
        return this;
    }

    public MonsterBuilder setAttributes(MonsterAttributes attributes) {
        this.attributes = attributes;
        return this;
    }

    public MonsterBuilder setCurrentAttributes(MonsterAttributes currentAttributes) {
        this.currentAttributes = currentAttributes;
        return this;
    }

    public MonsterBuilder setAbilities(SortedMap<String, Integer> abilities) {
        this.abilities = abilities;
        return this;
    }

    public MonsterBuilder setStatus(List<MonsterStatus> status) {
        this.status.clear();
        this.status.addAll(status);
        return this;
    }

    public MonsterBuilder addStatus(MonsterStatus... status) {
        this.status.addAll(Arrays.asList(status));
        return this;
    }

    private MonsterBuilder apply(Monster monster) {
        return setId(monster._id())
                .setTrainer(monster.trainer())
                .setType(monster.type())
                .setLevel(monster.level())
                .setExperience(monster.experience())
                .setAbilities(monster.abilities())
                .setAttributes(monster.attributes())
                .setCurrentAttributes(monster.currentAttributes())
                .setStatus(monster.status());
    }

    public Monster create() {
        return new Monster(_id, trainer, type, level, experience, abilities, attributes, currentAttributes, status);
    }

}
