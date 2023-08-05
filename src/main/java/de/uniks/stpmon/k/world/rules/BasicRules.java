package de.uniks.stpmon.k.world.rules;

public class BasicRules {
    public static final String TILESET_BASE = "../../tilesets/";
    public static final String TILESET_MODERN_EXTERIORS = TILESET_BASE + "Modern_Exteriors_16x16.json";

    public static final String TILESET_MODERN_EXTERIORS_EXTENDED = TILESET_BASE + "Modern_Exteriors_extended_16x16.json";
    public static final String TILESET_MODERN_INTERIORS = TILESET_BASE + "Modern_Interiors_16x16.json";

    public static final String TILESET_ROOM_BUILDER = TILESET_BASE + "Room_Builder_16x16.json";
    public static final String TILESET_PIXEL_WOODS = TILESET_BASE + "Pixel_Woods_16x16.json";
    public static final String TILESET_CAVES = TILESET_BASE + "Caves_And_Mountains_16x16.json";

    public static RuleRegistry registerRules() {
        RuleRegistry registry = new RuleRegistry();
        BasicExteriors.registerRules(registry);
        // blend signs
        registry.markBlend(TILESET_MODERN_EXTERIORS_EXTENDED,
                new IdSource.Rectangle(41713, 6, 2, 16),
                new IdSource.Rectangle(41713 + 32, 4, 2, 16),
                new IdSource.Rectangle(41713 + 368, 7, 4, 16));

        // snow field
        registry.markDecoration(TILESET_MODERN_EXTERIORS_EXTENDED,
                new IdSource.Rectangle(41713 + 208, 6, 3, 16));
        registry.markDecoration(TILESET_MODERN_EXTERIORS_EXTENDED,
                new IdSource.Rectangle(41713 + 154, 6, 3, 16));

        // snow trees
        registry.markBottom(TILESET_MODERN_EXTERIORS_EXTENDED,
                new IdSource.Rectangle(41713 + 152, 2, 2, 16));

        // mines
        registry.markDecoration(TILESET_MODERN_EXTERIORS_EXTENDED,
                new IdSource.Rectangle(41713 + 260, 6, 3, 16));

        // mountain
        registry.markDecoration(TILESET_MODERN_EXTERIORS_EXTENDED,
                new IdSource.Rectangle(41713 + 202, 6, 12, 16));

        // metal bridge
        registry.markDecoration(TILESET_MODERN_EXTERIORS_EXTENDED,
                new IdSource.Rectangle(41713 + 214, 3, 3, 16));

        // cave and mountain
        registry.markDecoration(TILESET_CAVES,
                new IdSource.Rectangle(1, 22, 23, 24));
        registry.markDecoration(TILESET_CAVES,
                new IdSource.Rectangle(380, 5, 8, 24));

        // Farm fence entangled
        registry.markDecoration(TILESET_MODERN_EXTERIORS_EXTENDED,
                new IdSource.Rectangle(41713 + 310, 4, 4, 16),
                new IdSource.Rectangle(41713 + 288, 1, 4, 16));
        registry.markEntangled(TILESET_MODERN_EXTERIORS_EXTENDED,
                new IdSource.Rectangle(41713 + 256, 4, 6, 16),
                new IdSource.Rectangle(41713 + 308, 4, 4, 16));
        registry.markBottom(TILESET_MODERN_EXTERIORS_EXTENDED,
                new IdSource.Rectangle(41713 + 336, 4, 1, 16),
                new IdSource.Rectangle(41713 + 356, 2, 1, 16),
                new IdSource.Rectangle(41713 + 304, 4, 1, 16),
                new IdSource.Rectangle(41713 + 324, 2, 1, 16));

        // house carpets
        registry.markDecoration(TILESET_MODERN_INTERIORS,
                new IdSource.Rectangle(8285 + 247, 4, 3, 16),
                new IdSource.Rectangle(8285 + 173, 4, 14, 16),
                new IdSource.Rectangle(8285 + 251, 2, 6, 16),
                new IdSource.Rectangle(8285 + 11860, 4, 3, 16),
                new IdSource.Rectangle(8285 + 11671, 9, 6, 16),
                new IdSource.Rectangle(8285 + 11757, 2, 10, 16),
                new IdSource.Rectangle(8285 + 12326, 9, 3, 16),
                new IdSource.Rectangle(8285 + 9024, 8, 4, 16),
                new IdSource.Rectangle(8285 + 6169, 4, 3, 16),
                new IdSource.Rectangle(8285 + 4544, 6, 6, 16),
                new IdSource.Rectangle(8285 + 3026, 9, 2, 16),
                new IdSource.Rectangle(8285 + 16064, 12, 3, 16),
                new IdSource.Rectangle(8285 + 13163, 3, 3, 16));

        // house exit
        registry.markDecoration(TILESET_ROOM_BUILDER,
                new IdSource.Rectangle(210, 3, 2, 76));
        registry.markEntangled(TILESET_MODERN_INTERIORS,
                new IdSource.Rectangle(8285 + 136, 1, 2, 16));
        registry.markEntangled(TILESET_MODERN_INTERIORS,
                new IdSource.Rectangle(8285 + 137, 1, 2, 16));

        // house couches
        registry.markDecoration(TILESET_MODERN_INTERIORS,
                new IdSource.Rectangle(8285 + 5524, 12, 4, 16));
        registry.markDecoration(TILESET_MODERN_INTERIORS,
                new IdSource.Rectangle(8285 + 5588, 4, 4, 16));

        // house counter
        registry.markDecoration(TILESET_MODERN_INTERIORS,
                new IdSource.Rectangle(8285, 5, 5, 16),
                new IdSource.Rectangle(8285 + 10, 5, 5, 16),
                new IdSource.Rectangle(8285 + 80, 5, 5, 16),
                new IdSource.Single(8285 + 407),
                new IdSource.Single(8285 + 406),
                new IdSource.Single(8285 + 405),
                new IdSource.Single(8285 + 60));
        registry.markDecoration(TILESET_MODERN_INTERIORS,
                new IdSource.Rectangle(8285 + 9460, 12, 6, 16),
                new IdSource.Rectangle(8285 + 9604, 6, 3, 16));

        // house decoration
        registry.markEntangled(TILESET_MODERN_INTERIORS,
                new IdSource.Rectangle(8285 + 224, 2, 2, 16));

        // picnic
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(1135, 5, 8, 176));

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

        // Victory road grass
        registry.markEntangled(TILESET_PIXEL_WOODS,
                new IdSource.Rectangle(962, 1, 1, 43));
        registry.markDecoration(TILESET_PIXEL_WOODS,
                new IdSource.Rectangle(963, 2, 2, 43));
        registry.markEntangled(TILESET_PIXEL_WOODS,
                new IdSource.Rectangle(1048, 1, 1, 43));
        registry.markEntangled(TILESET_PIXEL_WOODS,
                new IdSource.Rectangle(1049, 1, 1, 43));
        registry.markEntangled(TILESET_PIXEL_WOODS,
                new IdSource.Rectangle(1092, 1, 1, 43));
        registry.markDecoration(TILESET_PIXEL_WOODS,
                new IdSource.Rectangle(1050, 1, 2, 43));

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

        // connect firs
        registry.addCandidate(new IncludedCandidateRule(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(1925, 2, 1, 176),
                new IdSource.Rectangle(2103, 2, 1, 176)), false);
        registry.addCandidate(new IncludedCandidateRule(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(1927, 2, 1, 176),
                new IdSource.Rectangle(2101, 2, 1, 176)), false);
        return registry;
    }
}
