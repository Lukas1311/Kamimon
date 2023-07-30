package de.uniks.stpmon.k.models.builder;

import de.uniks.stpmon.k.dto.CreateTrainerDto;
import de.uniks.stpmon.k.dto.MoveTrainerDto;
import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.models.NPCInfo;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.utils.Direction;

import java.util.*;

@SuppressWarnings("unused")
public class TrainerBuilder {

    public static TrainerBuilder builder() {
        return new TrainerBuilder();
    }

    public static TrainerBuilder builder(Trainer trainer) {
        return builder().apply(trainer);
    }

    private final List<String> team = new LinkedList<>();
    private final Set<Integer> encounteredMonsterTypes = new HashSet<>();
    private final Set<String> visitedAreas = new HashSet<>();
    private NPCInfo npc = NPCInfoBuilder.builder().create();
    private String _id = "";
    private String region = "";
    private String user = "";
    private String name = "";
    private String image = "";
    private Integer coins = 0;
    private String area = "";
    private int x = 0;
    private int y = 0;
    private int direction = 0;

    private TrainerBuilder() {
    }

    public TrainerBuilder setId(String _id) {
        this._id = _id;
        return this;
    }

    public TrainerBuilder setId(int id) {
        return setId(Integer.toString(id));
    }

    public TrainerBuilder setRegion(String region) {
        this.region = region;
        return this;
    }

    public TrainerBuilder setRegion(Region region) {
        return setRegion(region._id());
    }

    public TrainerBuilder setArea(String area) {
        this.area = area;
        return this;
    }

    public TrainerBuilder setArea(Area area) {
        return setArea(area._id());
    }

    public TrainerBuilder setCoins(Integer coins) {
        this.coins = coins;
        return this;
    }

    public TrainerBuilder addEncountered(Collection<Integer> types) {
        this.encounteredMonsterTypes.addAll(types);
        return this;
    }

    public TrainerBuilder addEncountered(Integer type) {
        this.encounteredMonsterTypes.add(type);
        return this;
    }

    public TrainerBuilder addVisited(Collection<String> areas) {
        this.visitedAreas.addAll(areas);
        return this;
    }

    public TrainerBuilder addVisited(String area) {
        this.visitedAreas.add(area);
        return this;
    }

    public TrainerBuilder setDirection(int direction) {
        this.direction = direction;
        return this;
    }

    public TrainerBuilder setDirection(Direction direction) {
        this.direction = direction.ordinal();
        return this;
    }

    public TrainerBuilder setImage(String image) {
        this.image = image;
        return this;
    }

    public TrainerBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public TrainerBuilder setNpc(NPCInfo npc) {
        this.npc = npc;
        return this;
    }

    public TrainerBuilder addTeam(Collection<String> team) {
        this.team.clear();
        this.team.addAll(team);
        return this;
    }

    public TrainerBuilder addTeam(String team) {
        this.team.add(team);
        return this;
    }

    public TrainerBuilder removeTeam(String team) {
        this.team.remove(team);
        return this;
    }

    public TrainerBuilder setUser(String user) {
        this.user = user;
        return this;
    }

    public TrainerBuilder setX(int x) {
        this.x = x;
        return this;
    }

    public TrainerBuilder setY(int y) {
        this.y = y;
        return this;
    }

    public TrainerBuilder applyMove(MoveTrainerDto move) {
        return setX(move.x())
                .setY(move.y())
                .setDirection(move.direction())
                .setArea(move.area());
    }

    public TrainerBuilder applyCreate(CreateTrainerDto create) {
        return setName(create.name())
                .setImage(create.image());
    }

    public TrainerBuilder apply(Trainer trainer) {
        return applyWithoutMove(trainer)
                .setX(trainer.x())
                .setY(trainer.y())
                .setDirection(trainer.direction())
                .setArea(trainer.area());
    }

    public TrainerBuilder applyWithoutMove(Trainer trainer) {
        return setArea(trainer.area())
                .setRegion(trainer.region())
                .setCoins(trainer.coins())
                .addEncountered(trainer.encounteredMonsterTypes())
                .addVisited(trainer.visitedAreas())
                .setId(trainer._id())
                .setImage(trainer.image())
                .setName(trainer.name())
                .addTeam(trainer.team())
                .setUser(trainer.user())
                .setNpc(trainer.npc());
    }

    public Trainer create() {
        return new Trainer(_id, region, user, name, image, coins, area,
                x, y, direction, npc, team, encounteredMonsterTypes, visitedAreas);
    }

}
