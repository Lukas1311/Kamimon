package de.uniks.stpmon.k.world;

import de.uniks.stpmon.k.models.map.layerdata.ChunkData;
import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;

import java.util.List;

public class ChunkBuffer {
    public static final int ID_DEFAULT = 0;
    public static final int ID_INVALID = -1;
    /**
     * The offset of the buffer. This is used to determine whether a chunk is present or not.
     */
    private static final int BUFFER_OFFSET = 1;
    private final int[] buffer;
    private final TileLayerData layerData;

    public ChunkBuffer(TileLayerData layerData) {
        this.buffer = new int[layerData.width() * layerData.height() / 256];
        this.layerData = layerData;
        List<ChunkData> chunks = layerData.chunks();
        for (int j = 0; j < chunks.size(); j++) {
            ChunkData chunk = chunks.get(j);
            int index = (int) Math.floor((chunk.x() - layerData.startx()) / 16f) +
                    (int) Math.floor((chunk.y() - layerData.starty()) / 16f) * (layerData.width() / 16);
            buffer[index] = BUFFER_OFFSET + j;
        }
    }

    public int getId(int x, int y) {
        if (!layerData.checkBounds(x, y)) {
            return ID_INVALID;
        }
        ChunkData chunk = getChunk(x, y);
        if (chunk == null) {
            return 0;
        }
        return chunk.getId(x, y);
    }

    public ChunkData getChunk(int x, int y) {
        int index = (int) Math.floor((x - layerData.startx()) / 16f) +
                (int) Math.floor((y - layerData.starty()) / 16f) * (layerData.width() / 16);
        int reference = buffer[index];
        if (reference == ID_DEFAULT) {
            return null;
        }
        return layerData.chunks().get(reference - BUFFER_OFFSET);
    }
}
