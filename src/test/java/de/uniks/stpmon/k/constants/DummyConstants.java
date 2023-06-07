package de.uniks.stpmon.k.constants;

import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.NPCInfo;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.Spawn;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.service.world.WorldSet;

import java.util.Collections;
import java.util.List;

public class DummyConstants {

    public static final Trainer TRAINER = new Trainer(
            "0",
            "region_0",
            "user_0",
            "Test Trainer",
            "trainerImage",
            0,
            "area_0",
            0,
            0,
            0,
            new NPCInfo(true)
    );
    public static final Trainer TRAINER_OTHER_AREA = new Trainer(
            "0",
            "region_0",
            "user_0",
            "Test Trainer",
            "trainerImage",
            0,
            "area_1",
            0,
            0,
            0,
            new NPCInfo(true)
    );

    public static final Trainer TRAINER_OTHER_REGION = new Trainer(
            "0",
            "region_1",
            "user_0",
            "Test Trainer",
            "trainerImage",
            0,
            "area_2",
            0,
            0,
            0,
            new NPCInfo(true)
    );

    public static final TileMapData TILE_MAP_DATA = new TileMapData(
            2, 2,
            false, List.of(),
            1, 1,
            List.of(),
            "map");

    public static final Area AREA = new Area(
            "area_0",
            "region_0",
            "Test Area",
            TILE_MAP_DATA
    );

    public static final Area AREA_NO_MAP = new Area(
            "area_0",
            "region_0",
            "Test Area",
            null
    );

    public static final Region REGION = new Region(
            "region_0",
            "Test Region",
            new Spawn("area_0", 0, 0),
            null
    );

    public static final Monster MONSTER = new Monster(
            "monster_0",
            "trainer_0",
            1,
            0,
            0,
            null,
            null,
            null
    );
    public static final WorldSet WORLD = new WorldSet(
            null,
            null,
            List.of(),
            Collections.emptyMap());
}
