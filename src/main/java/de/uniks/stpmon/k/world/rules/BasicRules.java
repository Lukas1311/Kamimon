package de.uniks.stpmon.k.world.rules;

public class BasicRules {
    public static final String TILESET_BASE = "../../tilesets/";
    public static final String TILESET_MODERN_EXTERIORS = TILESET_BASE + "Modern_Exteriors_16x16.json";
    public static final String TILESET_MODERN_INTERIORS = TILESET_BASE + "Modern_Interiors_16x16.json";

    public static RuleRegistry registerRules() {
        RuleRegistry registry = new RuleRegistry();
        // City Fence exclusion
        registry.addConnection(new ExclusionRule(TILESET_MODERN_EXTERIORS,
                2141, 2143, 3194, 3193));
        // modular fence exclusion
        registry.addConnection(new ExclusionRule(TILESET_MODERN_EXTERIORS,
                32999, 33002, 33175, 33178));
        // forest fence entangle
        registry.addConnection(new EntangledRule(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(1336, 3, 1, 176)));
        // garden fence entangle
        registry.addConnection(new EntangledRule(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(2297, 5, 4, 176)));
        // start house porch
        registry.addConnection(new ExclusionRule(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(10118, 8, 2, 176),
                new IdSource.Rectangle(9422, 1, 6, 176)));

        registry.addConnection(new ExclusionRule(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(10128, 8, 2, 176),
                new IdSource.Rectangle(9432, 1, 6, 176)));

        registry.addConnection(new ExclusionRule(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(10138, 8, 2, 176),
                new IdSource.Rectangle(9442, 1, 6, 176)));

        registry.addConnection(new ExclusionRule(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(12582, 8, 2, 176),
                new IdSource.Rectangle(11886, 1, 6, 176)));

        registry.addConnection(new ExclusionRule(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(12592, 8, 2, 176),
                new IdSource.Rectangle(11896, 1, 6, 176)));
        // City Fence entangled
        registry.addConnection(new EntangledRule(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(2137, 8, 8, 176),
                new IdSource.Rectangle(1789, 3, 2, 176),
                new IdSource.Rectangle(383, 2, 9, 176)));
        // Modular fence entangled
        registry.addConnection(new EntangledRule(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(32294, 37, 7, 176)));
        // Lantern entangled
        registry.addConnection(new EntangledRule(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(3001, 1, 4, 176)));
        // house carpets
        registry.addConnection(new ExclusionRule(TILESET_MODERN_INTERIORS,
                new IdSource.Rectangle(8285 + 247, 4, 3, 16),
                new IdSource.Rectangle(8285 + 173, 4, 14, 16),
                new IdSource.Rectangle(8285 + 251, 2, 6, 16)));
        // house counter
        registry.addConnection(new ExclusionRule(TILESET_MODERN_INTERIORS,
                new IdSource.Rectangle(8285 + 10, 5, 5, 16),
                new IdSource.Single(8285 + 407),
                new IdSource.Single(8285 + 60)));
        // picnic
        registry.addConnection(new ExclusionRule(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(1135, 5, 8, 176)));

        // soccer pitches
        registry.addConnection(new ExclusionRule(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(30109, 17, 12, 176),
                new IdSource.Rectangle(32766, 12, 17, 176)));

        // basketball pitch
        registry.addConnection(new ExclusionRule(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(32078, 13, 13, 176),
                new IdSource.Rectangle(32091, 13, 13, 176),
                new IdSource.Rectangle(34366, 14, 11, 176),
                new IdSource.Rectangle(34380, 14, 11, 176)));

        // containers vertical
        registry.addConnection(new ExclusionRule(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(33714, 6, 4, 176),
                new IdSource.Rectangle(33757, 12, 9, 176)));

        // cliff
        registry.addConnection(new ExclusionRule(TILESET_MODERN_EXTERIORS,
                2078));
        // ground moose
        ExclusionRule mooseRule = new ExclusionRule(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(52, 10, 1, 176));
        registry.addConnection(mooseRule);
        registry.addSingle(mooseRule);
        // ground flowers
        registry.addConnection(new ExclusionRule(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(192, 1, 5, 176)));
        // ground path stones
        registry.addConnection(new ExclusionRule(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(1819, 4, 1, 176)));

        // ground path dirt
        ExclusionRule groundPath = new ExclusionRule(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(228, 13, 5, 176),
                new IdSource.Rectangle(1119, 3, 5, 176),
                new IdSource.Rectangle(9972, 12, 6, 176));
        registry.addConnection(groundPath);
        registry.addSingle(groundPath);

        registry.addConnection(new ImageConnectionRule());
        registry.addSingle(new ImageEmptyRule());
        registry.addCandidate(new TilesetCandidateRule(TILESET_MODERN_EXTERIORS,
                176));
        registry.addCandidate(new IncludedCandidateRule(TILESET_MODERN_EXTERIORS,
                12141, 13021));
        registry.addCandidate(new IncludedCandidateRule(TILESET_MODERN_EXTERIORS,
                10553, 10377));
        return registry;
    }
}
