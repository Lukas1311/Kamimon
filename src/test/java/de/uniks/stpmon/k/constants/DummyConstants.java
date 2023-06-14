package de.uniks.stpmon.k.constants;

import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.NPCInfo;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.Spawn;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.TilesetData;
import de.uniks.stpmon.k.models.map.TilesetSource;
import de.uniks.stpmon.k.models.map.layerdata.ChunkData;
import de.uniks.stpmon.k.models.map.layerdata.ObjectData;
import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;
import de.uniks.stpmon.k.world.WorldSet;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.IntStream;

public class DummyConstants {

    public static final Trainer TRAINER = new Trainer(
            "0",
            "region_0",
            "user_0",
            "Test Trainer",
            "trainer_0",
            0,
            "area_0",
            0,
            0,
            0,
            null
    );
    public static final Trainer TRAINER_OTHER_AREA = new Trainer(
            "1",
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

    public static final TileMapData AREA_MAP_DATA = new TileMapData(
            16, 16,
            false,
            List.of(
                    new TileLayerData(

                            0, "Ground",
                            List.of(
                                    new ChunkData(
                                            IntStream.range(0, 256).map(i -> 482)
                                                    .boxed().toList(),
                                            16, 16,
                                            0, 0

                                    )
                            ),
                            List.of(new ObjectData(0, null, null, null, false, 0, 0, 0, 0, 0)),
                            0, 0,
                            16, 16,
                            0, 0,
                            "tilelayer",
                            true,
                            List.of()
                    )
            ),
            16, 16,
            List.of(
                    new TilesetSource(1, "../tilesets/Modern_Exteriors_16x16.json")
            ),
            "map");

    public static final TilesetData TILESET_DATA = new TilesetData(
            176,
            "Modern_Exteriors_16x16.png",
            3792,
            2816,
            0,
            "Modern_Exteriors_16x16", 0,
            41712,
            16, 16,
            List.of(),
            "tileset"
    );

    public static final Area AREA = new Area(
            "area_0",
            "region_0",
            "Test Area",
            AREA_MAP_DATA
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
            new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB),
            new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB),
            List.of());
}
