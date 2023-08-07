package de.uniks.stpmon.k.world;

import de.uniks.stpmon.k.models.builder.TileLayerBuilder;
import de.uniks.stpmon.k.models.map.layerdata.ChunkData;
import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static de.uniks.stpmon.k.world.FallbackTiles.liesOnEdge;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FallbackTilesTest {

    @Test
    public void checkReplacement() {
        List<Long> tiles = IntStream.range(0, 256).mapToLong((i) -> 308).boxed().toList();
        TileLayerData layerData = TileLayerBuilder
                .builderTiles()
                .addChunk(new ChunkData(tiles, 16, 16, 0, 0))
                .setWidth(16).setHeight(16)
                .create();
        FallbackTiles fallbackTiles = new FallbackTiles(new ChunkBuffer(layerData), layerData);
        assertEquals(483, fallbackTiles.getTile(0, 0));
    }

    @Test
    public void checkBuilder() {
        TileLayerData oldData = TileLayerBuilder.builderTiles().setType(TileLayerData.GROUND_TYPE).create();
        assertEquals(TileLayerData.GROUND_TYPE, oldData.type());

        TileLayerData newData = TileLayerBuilder.builder(oldData).create();
        assertEquals(TileLayerData.GROUND_TYPE, newData.type());
    }

    @Test
    public void innerChunk() {
        List<Long> tiles = IntStream.range(0, 256).mapToLong((i) -> i < 7 * 8 ? 0L : 1L).boxed().toList();
        TileLayerData layerData = TileLayerBuilder
                .builderTiles()
                .addChunk(new ChunkData(tiles, 16, 16, 0, 0))
                .setWidth(16).setHeight(16)
                .create();
        FallbackTiles fallbackTiles = new FallbackTiles(new ChunkBuffer(layerData), layerData);
        assertEquals(1, fallbackTiles.getTile(0, 0));
    }

    @Test
    public void outerChunk() {
        List<Long> tiles = IntStream.range(1, 257).asLongStream().boxed().toList();
        TileLayerData layerData = TileLayerBuilder
                .builderTiles()
                .setName("dummy")
                .addChunk(new ChunkData(tiles, 16, 16, 0, 0))
                .setWidth(16).setHeight(16)
                .create();
        FallbackTiles fallbackTiles = new FallbackTiles(new ChunkBuffer(layerData), layerData);
        // Check top side
        for (int i = 0; i < 16; i++) {
            assertEquals(i + 1, fallbackTiles.getTile(i, -1));
        }

        // Check right side
        for (int i = 0; i < 16; i++) {
            assertEquals(i * 16 + 16, fallbackTiles.getTile(16, i));
        }

        // Check bottom side
        for (int i = 0; i < 16; i++) {
            assertEquals(i + 15 * 16 + 1, fallbackTiles.getTile(i, 16));
        }

        // Check left side
        for (int i = 0; i < 16; i++) {
            assertEquals(i * 16 + 1, fallbackTiles.getTile(-1, i));
        }
    }

    @Test
    public void testLiesIn() {
        // Check top edge
        assertEquals(liesOnEdge(1, 0.2, 2, 1), 0);
        // Check right edge
        assertEquals(liesOnEdge(1.5, 0.5, 2, 1), 1);
        // Check bottom edge
        assertEquals(liesOnEdge(1, 0.8, 2, 1), 2);
        // Check left edge
        assertEquals(liesOnEdge(0.5, 0.5, 2, 1), 3);
        // Check on diagonal
        assertEquals(liesOnEdge(1, 0.5, 2, 1), 4);
    }

}