package de.uniks.stpmon.k.world;

import de.uniks.stpmon.k.models.map.layerdata.ChunkData;
import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FallbackTilesTest {

    @Test
    public void innerChunk() {
        List<Long> tiles = IntStream.range(0, 255).mapToLong((i) -> i < 7 * 8 ? 0L : 1L).boxed().toList();
        TileLayerData layerData = new TileLayerData(10,
                "dummy",
                List.of(new ChunkData(tiles, 16, 16, 0, 0)),
                null,
                List.of(),
                0, 0,
                16, 16,
                0, 0,
                "", false, List.of());
        FallbackTiles fallbackTiles = new FallbackTiles(new ChunkBuffer(layerData), layerData);
        assertEquals(1, fallbackTiles.getTile(0, 0));
    }

}