package de.uniks.stpmon.k.constants;

import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.*;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.models.builder.NPCInfoBuilder;
import de.uniks.stpmon.k.models.builder.OpponentBuilder;
import de.uniks.stpmon.k.models.builder.TileMapBuilder;
import de.uniks.stpmon.k.models.map.RegionImage;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.TilesetData;
import de.uniks.stpmon.k.models.map.layerdata.ChunkData;
import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Set;
import java.util.stream.LongStream;

@SuppressWarnings("unused")
public class DummyConstants {

    public static final User USER_ALICE = new User(
            "id_alice",
            "Alice",
            "online",
            null,
            null
    );

    public static final User USER_BOB = new User(
            "id_bob",
            "Bob",
            "offline",
            null,
            null
    );

    public static final User USER_EVE = new User(
            "id_eve",
            "Eve",
            "online",
            null,
            null
    );

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
            null,
            List.of(), Set.of(), Set.of()
    );
    public static final Trainer TRAINER_OTHER = new Trainer(
            "attacker",
            "region_0",
            "user_1",
            "Test Trainer",
            "trainer_1",
            0,
            "area_0",
            0,
            0,
            0,
            null,
            List.of(), Set.of(), Set.of()
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
            DummyConstants.NPC_INFO,
            List.of(), Set.of(), Set.of()
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
            DummyConstants.NPC_INFO,
            List.of(), Set.of(), Set.of()
    );
    public static final Trainer TRAINER_W_VISITED_AREAS = new Trainer(
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
            null,
            List.of(),
            Set.of(),
            Set.of("area_0", "area_1", "area_2")
    );
    public static final TileMapData EMPTY_MAP_DATA = TileMapBuilder.builder()
            .startTileLayer()
            .setName(TileLayerData.GROUND_TYPE)
            .setChunks(List.of())
            .setWidth(1).setHeight(1)
            .endLayer()
            .create();


    public static final TileMapData AREA_MAP_DATA = TileMapBuilder.builder()
            .startTileLayer()
            .setName(TileLayerData.GROUND_TYPE)
            .setChunks(List.of(
                    new ChunkData(
                            LongStream.range(0, 256).map(i -> 1)
                                    .boxed().toList(),
                            16, 16,
                            0, 0
                    )
            ))
            .startObject().endObject()
            .setWidth(16).setHeight(16)
            .endLayer()
            .addProperty("Spawn", "", "")
            .addTileSet(1, "../tilesets/Modern_Exteriors_16x16.json")
            .create();

    public static final TilesetData TILESET_DATA = new TilesetData(
            176,
            "Modern_Exteriors_16x16.png",
            16,
            16,
            0,
            "Modern_Exteriors_16x16", 0,
            1,
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
            "id0",
            "Test Region",
            new Spawn("area_0", 0, 0),
            AREA_MAP_DATA
    );

    public static final Monster MONSTER = MonsterBuilder.builder()
            .setId("monster_0")
            .setTrainer("trainer_0")
            .setType(1)
            .create();
    public static final MonsterTypeDto MONSTER_TYPE = new MonsterTypeDto(
            0,
            "monster",
            "",
            List.of("type1"),
            "");
    public static final NPCInfo NPC_INFO = NPCInfoBuilder.builder().create();
    public static final BufferedImage EMPTY_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    public static final RegionImage IMAGE = new RegionImage("0", EMPTY_IMAGE, EMPTY_IMAGE);

    public static final Opponent OPPONENT = OpponentBuilder.builder().setTrainer(DummyConstants.TRAINER._id()).setMonster(DummyConstants.MONSTER._id()).create();
}
