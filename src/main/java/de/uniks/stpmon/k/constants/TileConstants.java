package de.uniks.stpmon.k.constants;

/**
 * Constants for tile data.
 */
public class TileConstants {

    /**
     * Bitmask for all flags of the global id.
     */
    @SuppressWarnings("unused")
    public static final long FLAG_ALL = 0xF0000000L;
    /**
     * Bitmask for the tile id of the global id.
     */
    public static final long TILE_ID_MASK = 0x0FFFFFFFL;
    /**
     * Bitmask for flipping the tile horizontally.
     */
    public static final long FLAG_FLIPPED_HORIZONTALLY = 0x80000000L;
    /**
     * Bitmask for flipping the tile vertically.
     */
    public static final long FLAG_FLIPPED_VERTICALLY = 0x40000000L;
    /**
     * Bitmask for flipping the tile diagonally.
     */
    @SuppressWarnings("unused")
    public static final long FLAG_FLIPPED_DIAGONALLY = 0x20000000L;
    /**
     * Bitmask for rotating the tile 90 degrees clockwise.
     */
    @SuppressWarnings("unused")
    public static final long FLAG_ROTATED_HEXAGONAL_120 = 0x10000000L;
    /**
     * Tile size in pixels.
     */
    public static final int TILE_SIZE = 16;
    /**
     * Chunk size in tiles.
     */
    public static final int CHUNK_SIZE = TILE_SIZE * 16;
}
