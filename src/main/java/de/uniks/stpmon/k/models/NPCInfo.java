package de.uniks.stpmon.k.models;

import java.util.List;

public record NPCInfo(
        Boolean walkRandomly,
        boolean encounterOnTalk,
        boolean canHeal,
        List<String> starters,
        List<String> encountered
) {

}
