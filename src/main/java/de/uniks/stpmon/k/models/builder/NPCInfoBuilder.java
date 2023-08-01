package de.uniks.stpmon.k.models.builder;

import de.uniks.stpmon.k.models.NPCInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
public class NPCInfoBuilder {
    public static NPCInfoBuilder builder() {
        return new NPCInfoBuilder();
    }

    public static NPCInfoBuilder builder(NPCInfo info) {
        return builder().applyNPCInfo(info);
    }

    private boolean walkRandomly = false;
    private boolean encounterOnTalk = false;
    private boolean canHeal = false;
    private final List<Integer> sells = new ArrayList<>();
    private final List<String> starters = new ArrayList<>();
    private final List<String> encountered = new ArrayList<>();

    private NPCInfoBuilder() {
    }

    public NPCInfoBuilder setWalkRandomly(boolean walkRandomly) {
        this.walkRandomly = walkRandomly;
        return this;
    }

    public NPCInfoBuilder setEncounterOnTalk(boolean encounterOnTalk) {
        this.encounterOnTalk = encounterOnTalk;
        return this;
    }

    public NPCInfoBuilder setCanHeal(boolean canHeal) {
        this.canHeal = canHeal;
        return this;
    }

    public NPCInfoBuilder addSells(Integer sells) {
        this.sells.add(sells);
        return this;
    }

    public NPCInfoBuilder addSells(Collection<Integer> sells) {
        this.sells.clear();
        this.sells.addAll(sells);
        return this;
    }

    public NPCInfoBuilder addEncountered(String encountered) {
        this.encountered.add(encountered);
        return this;
    }

    public NPCInfoBuilder addEncountered(Collection<String> encountered) {
        this.encountered.clear();
        this.encountered.addAll(encountered);
        return this;
    }

    public NPCInfoBuilder addStarters(String starter) {
        this.starters.add(starter);
        return this;
    }

    public NPCInfoBuilder addStarters(Collection<String> starters) {
        this.starters.clear();
        this.starters.addAll(starters);
        return this;
    }

    public NPCInfoBuilder applyNPCInfo(NPCInfo info) {
        return this.setWalkRandomly(info.walkRandomly())
                .setEncounterOnTalk(info.encounterOnTalk())
                .setCanHeal(info.canHeal())
                .addSells(info.sells())
                .addEncountered(info.encountered())
                .addStarters(info.starters());
    }

    public NPCInfo create() {
        return new NPCInfo(walkRandomly,
                encounterOnTalk,
                canHeal,
                sells,
                starters,
                encountered);
    }


}
