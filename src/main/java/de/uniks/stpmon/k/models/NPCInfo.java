package de.uniks.stpmon.k.models;

import java.util.List;

public record NPCInfo(
        boolean walkRandomly,
        boolean encounterOnTalk,
        boolean canHeal,
        List<Integer> sells,
        List<String> starters,
        List<String> encountered
) {

}
