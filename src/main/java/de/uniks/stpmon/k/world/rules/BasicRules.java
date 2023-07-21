package de.uniks.stpmon.k.world.rules;

public class BasicRules {
    public static final String TILESET_BASE = "../../tilesets/";
    public static final String TILESET_MODERN_EXTERIORS = TILESET_BASE + "Modern_Exteriors_16x16.json";

    public static final String TILESET_MODERN_EXTERIORS_EXTENDED = TILESET_BASE + "Modern_Exteriors_extended_16x16.json";
    public static final String TILESET_MODERN_INTERIORS = TILESET_BASE + "Modern_Interiors_16x16.json";

    public static final String TILESET_ROOM_BUILDER = TILESET_BASE + "Room_Builder_16x16.json";
    public static final String TILESET_PIXEL_WOODS = TILESET_BASE + "Pixel_Woods_16x16.json";

    public static RuleRegistry registerRules() {
        RuleRegistry registry = new RuleRegistry();
        // City Fence exclusion
        registry.markDecoration(TILESET_MODERN_EXTERIORS, 2141, 2143, 3194, 3193);
        // modular fence exclusion
        registry.markDecoration(TILESET_MODERN_EXTERIORS, 32999, 33002, 33175, 33178);
        // forest fence entangle
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(1336, 3, 1, 176));
        // garden fence exclusion
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(2473, 6, 1, 176));
        // garden fence entangle
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(2297, 5, 4, 176));
        // start house porch
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(10118, 8, 2, 176),
                new IdSource.Rectangle(9422, 1, 6, 176));

        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(10128, 8, 2, 176),
                new IdSource.Rectangle(9432, 1, 6, 176));

        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(10138, 8, 2, 176),
                new IdSource.Rectangle(9442, 1, 6, 176));

        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(12582, 8, 2, 176),
                new IdSource.Rectangle(11886, 1, 6, 176));

        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(12592, 8, 2, 176),
                new IdSource.Rectangle(11896, 1, 6, 176));

        // underpass
        registry.markDecoration(
                TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(9180, 8, 15, 176)
        );

        // crosswalk
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(182, 2, 4, 176),
                new IdSource.Rectangle(913, 15, 5, 176));

        // hanging lamp entangle
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(4760, 2, 2, 176),
                new IdSource.Rectangle(5112, 1, 2, 176));
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(4762, 2, 2, 176),
                new IdSource.Rectangle(5115, 1, 2, 176));

        //  wire fence
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(35140, 3, 1, 176));
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(34785, 8, 8, 176));

        // construction site fence
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(5303, 3, 1, 176),
                new IdSource.Rectangle(5307, 3, 1, 176),
                new IdSource.Rectangle(6535, 3, 2, 176));
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(4599, 3, 11, 176),
                new IdSource.Rectangle(4603, 3, 7, 176));

        // City Fence entangled
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(2137, 8, 8, 176),
                new IdSource.Rectangle(1789, 3, 2, 176),
                new IdSource.Rectangle(383, 2, 9, 176));
        // Modular fence exclusion
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(32999, 4, 1, 176),
                new IdSource.Rectangle(33011, 6, 1, 176),
                new IdSource.Rectangle(33024, 4, 1, 176));
        // Modular fence entangled
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(32294, 37, 7, 176));
        // Farm fence entangled
        registry.markDecoration(TILESET_MODERN_EXTERIORS_EXTENDED,
                new IdSource.Rectangle(41713 + 310, 4, 4, 16),
                new IdSource.Rectangle(41713 + 256, 1, 4, 16));
        registry.markEntangled(TILESET_MODERN_EXTERIORS_EXTENDED,
                new IdSource.Rectangle(41713 + 256, 4, 6, 16),
                new IdSource.Rectangle(41713 + 308, 4, 4, 16));

        // Lantern entangled
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(3001, 1, 4, 176));
        // house carpets
        registry.markDecoration(TILESET_MODERN_INTERIORS,
                new IdSource.Rectangle(8285 + 247, 4, 3, 16),
                new IdSource.Rectangle(8285 + 173, 4, 14, 16),
                new IdSource.Rectangle(8285 + 251, 2, 6, 16));

        // house exit
        registry.markDecoration(TILESET_ROOM_BUILDER,
                new IdSource.Rectangle(210, 3, 2, 76));

        // house counter
        registry.markDecoration(TILESET_MODERN_INTERIORS,
                new IdSource.Rectangle(8285, 5, 5, 16),
                new IdSource.Rectangle(8285 + 10, 5, 5, 16),
                new IdSource.Rectangle(8285 + 80, 5, 5, 16),
                new IdSource.Single(8285 + 407),
                new IdSource.Single(8285 + 406),
                new IdSource.Single(8285 + 405),
                new IdSource.Single(8285 + 60));

        // house decoration
        registry.markEntangled(TILESET_MODERN_INTERIORS,
                new IdSource.Rectangle(8285 + 224, 2, 2, 16));

        // picnic
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(1135, 5, 8, 176));

        // soccer pitches
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(30109, 17, 12, 176),
                new IdSource.Rectangle(32766, 12, 17, 176));

        // basketball pitch
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(32078, 13, 13, 176),
                new IdSource.Rectangle(32091, 13, 13, 176),
                new IdSource.Rectangle(34366, 14, 11, 176),
                new IdSource.Rectangle(34380, 14, 11, 176));

        // basketball benches
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(32811, 2, 4, 176),
                new IdSource.Rectangle(32817, 2, 4, 176));

        // containers vertical
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(33714, 6, 4, 176),
                new IdSource.Rectangle(33757, 12, 9, 176));

        // wooden pier
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(1200, 3, 5, 176),
                new IdSource.Rectangle(2069, 4, 1, 176),
                new IdSource.Rectangle(2242, 10, 6, 176),
                new IdSource.Rectangle(2604, 2, 2, 176),
                new IdSource.Single(3298),
                new IdSource.Rectangle(3300, 6, 8, 176),
                new IdSource.Rectangle(2954, 6, 7, 176),
                new IdSource.Rectangle(4189, 1, 3, 176),
                new IdSource.Rectangle(4708, 6, 5, 176));

        // boats
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(5586, 15, 6, 176),
                new IdSource.Rectangle(6641, 12, 4, 176),
                new IdSource.Rectangle(4716, 5, 5, 176));

        // shafts
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(22230, 10, 6, 176));

        // ground decorations
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(7330, 30, 15, 176));

        // cliff
        registry.markDecoration(TILESET_MODERN_EXTERIORS, 2078);
        // ground moose
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(52, 10, 1, 176));
        // ground flowers
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(192, 1, 5, 176));
        // ground path stones
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(1819, 4, 1, 176));

        // ground path dirt
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(228, 13, 5, 176),
                new IdSource.Rectangle(1119, 3, 5, 176),
                new IdSource.Rectangle(9972, 12, 6, 176));


        registry.markBottom(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(177, 2, 1, 176),
                new IdSource.Rectangle(1598, 2, 1, 176));

        // hedge exclusion
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(37490, 4, 1, 176),
                new IdSource.Rectangle(37849, 4, 1, 176),
                new IdSource.Rectangle(38193, 4, 1, 176),
                new IdSource.Rectangle(39081, 4, 1, 176),
                new IdSource.Single(38025),
                new IdSource.Single(38906),
                new IdSource.Single(38908));
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(37138, 13, 14, 176));
        // hedge arch exclusion
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(38559, 15, 4, 176));

        // Victory road tree
        registry.markEntangled(TILESET_PIXEL_WOODS,
                new IdSource.Rectangle(801, 3, 3, 43));
        registry.markEntangled(TILESET_PIXEL_WOODS,
                new IdSource.Rectangle(973, 3, 3, 43));
        registry.markEntangled(TILESET_PIXEL_WOODS,
                new IdSource.Rectangle(754, 4, 4, 43));
        registry.markEntangled(TILESET_PIXEL_WOODS,
                new IdSource.Rectangle(926, 4, 4, 43));
        registry.markEntangled(TILESET_PIXEL_WOODS,
                new IdSource.Rectangle(839, 1, 2, 43));
        registry.markEntangled(TILESET_PIXEL_WOODS,
                new IdSource.Rectangle(1011, 1, 2, 43));
        registry.markEntangled(TILESET_PIXEL_WOODS,
                new IdSource.Rectangle(794, 2, 3, 43));
        registry.markEntangled(TILESET_PIXEL_WOODS,
                new IdSource.Rectangle(966, 2, 3, 43));
        registry.markBottom(TILESET_PIXEL_WOODS,
                new IdSource.Rectangle(880, 10, 1, 43));
        registry.markBottom(TILESET_PIXEL_WOODS,
                new IdSource.Rectangle(1052, 10, 1, 43));

        // Victory road bridge
        registry.markDecoration(TILESET_PIXEL_WOODS,
                new IdSource.Rectangle(734, 7, 5, 43));
        registry.markDecoration(TILESET_PIXEL_WOODS,
                new IdSource.Rectangle(870, 2, 2, 43));

        // Prioritized candidate rules
        registry.addCandidate(new IncludedCandidateRule(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(16537, 4, 1, 176),
                new IdSource.Rectangle(17417, 4, 1, 176)));

        // Mark dead trees as entangled
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(3857, 8, 6, 176));

        // add decoration rules
        registry.addConnection(new BottomExclusivelyRule(registry));
        registry.addConnection(new EntangledRule(registry));
        registry.addConnection(new ImageConnectionRule());
        registry.addLone(new ImageEmptyRule());
        // add missing candidate rules
        registry.addCandidate(new TilesetCandidateRule(TILESET_MODERN_EXTERIORS,
                176));

        registry.addCandidate(new TilesetCandidateRule(TILESET_MODERN_EXTERIORS_EXTENDED,
                16, 41713));
        registry.addCandidate(new IncludedCandidateRule(TILESET_MODERN_EXTERIORS,
                12141, 13021));
        registry.addCandidate(new IncludedCandidateRule(TILESET_MODERN_EXTERIORS,
                10553, 10377));
        registry.addCandidate(new IncludedCandidateRule(TILESET_MODERN_EXTERIORS,
                10553, 10377));

        registry.addCandidate(new IncludedCandidateRule(TILESET_MODERN_EXTERIORS_EXTENDED,
                41713 + 178, 41713 + 176));
        registry.addCandidate(new IncludedCandidateRule(TILESET_MODERN_EXTERIORS_EXTENDED,
                41713 + 162, 41713 + 160));
        return registry;
    }
}
