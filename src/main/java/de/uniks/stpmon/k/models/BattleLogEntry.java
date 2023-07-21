package de.uniks.stpmon.k.models;

import de.uniks.stpmon.k.dto.MonsterTypeDto;

public record BattleLogEntry(
        MonsterTypeDto monster,
        Result result,
        String target
) {
}
