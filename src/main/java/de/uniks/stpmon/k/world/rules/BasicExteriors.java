package de.uniks.stpmon.k.world.rules;

import static de.uniks.stpmon.k.world.rules.BasicRules.TILESET_MODERN_EXTERIORS;

public class BasicExteriors {
    public static void registerRules(RuleRegistry registry) {
        // blend roofs
        registry.markBlend(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(10665, 16, 14, 176));
        registry.markBlend(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(38787, 10, 9, 176));
        registry.markBlend(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(36742, 11, 8, 176));

        // City Fence exclusion
        registry.markDecoration(TILESET_MODERN_EXTERIORS, 2141, 2143, 3194, 3193);

        // bus stop
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(11459, 2, 6, 176));
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(11467, 2, 6, 176));

        // playground
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(12797, 17, 15, 176),
                new IdSource.Rectangle(11741, 5, 7, 176),
                new IdSource.Rectangle(255, 9, 5, 176));

        // Gas station
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(19751, 7, 4, 176));

        // italy sign
        registry.markBlend(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(30209, 1, 1, 176));
        registry.markBlend(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(31434, 1, 1, 176));

        // City decoration exclusion
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(2642, 2, 3, 176),
                new IdSource.Rectangle(3171, 2, 3, 176));

        // city cars
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(18358, 4, 5, 176));
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(18367, 4, 5, 176));
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(18376, 4, 5, 176));
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(18385, 4, 5, 176));
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(17311, 4, 5, 176));
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(17320, 4, 5, 176));
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(17329, 4, 5, 176));
        // parking spots
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(177, 5, 14, 176));

        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(19302, 4, 5, 176));

        // drying rack
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(19239, 14, 9, 176));

        // City trees
        registry.markBottom(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(1077, 4, 1, 176));
        registry.markBottom(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(1959, 6, 1, 176));
        registry.markBottom(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(2126, 2, 1, 176));
        registry.markBottom(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(1244, 2, 1, 176));
        registry.markBottom(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(1596, 2, 1, 176));
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(551, 2, 4, 176));
        // street signs
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(18487, 9, 6, 176));


        // Swing
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(3708, 5, 3, 176),
                new IdSource.Rectangle(4237, 3, 2, 176));

        // boxwood
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(38223, 1, 2, 176));
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(38215, 1, 2, 176));
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(38216, 1, 2, 176));
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(38217, 1, 2, 176));
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(38218, 1, 2, 176));
        registry.markBottom(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(38391, 12, 1, 176));
        registry.markBottom(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(38039, 1, 1, 176));

        // water
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(1362, 9, 4, 176),
                new IdSource.Rectangle(306, 17, 6, 176));

        // small houses
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(66, 4, 7, 176));
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(70, 4, 7, 176));
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(1298, 4, 7, 176));
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(1302, 4, 7, 176));


        //sandbox
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(2704, 12, 4, 176));

        // garden fence
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(41265, 3, 1, 176));

        // pool
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(34862, 15, 8, 176),
                new IdSource.Rectangle(36270, 14, 4, 176),
                new IdSource.Rectangle(30097, 7, 12, 176),
                new IdSource.Rectangle(30280, 5, 9, 176));

        // helicopter
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(19143, 6, 6, 176));

        // building stairs
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(33229, 12, 3, 176));
        registry.markBottom(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(33047, 24, 1, 176));

        // building giolaxen
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(8097, 6, 13, 176));
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(8103, 6, 13, 176));
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(10385, 6, 13, 176));
        registry.markBottom(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(9857, 12, 1, 176));
        registry.markBottom(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(12145, 6, 1, 176));

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

        // Lantern entangled
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(3001, 1, 4, 176));

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
        // tree house
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(1 + 1500, 4, 4, 176));
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
                new IdSource.Single(38908),
                new IdSource.Single(37674),
                new IdSource.Single(37676));
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(37138, 13, 14, 176));
        registry.markBottom(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(37322, 3, 1, 176),
                new IdSource.Rectangle(37675, 1, 1, 176),
                new IdSource.Rectangle(38201, 4, 1, 176),
                new IdSource.Rectangle(38554, 3, 1, 176),
                new IdSource.Rectangle(39433, 4, 1, 176));
        // hedge arch exclusion
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(38559, 15, 4, 176));

        // hedge animals
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(38574, 8, 7, 176),
                new IdSource.Rectangle(39271, 7, 3, 176));

        // cypresses
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(1594, 2, 4, 176),
                new IdSource.Rectangle(1068, 2, 2, 176),
                new IdSource.Rectangle(1064, 2, 4, 176),
                new IdSource.Rectangle(1068, 2, 2, 176),
                new IdSource.Rectangle(1070, 3, 4, 176));
        registry.markBottom(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(1592, 2, 1, 176),
                new IdSource.Rectangle(2122, 2, 1, 176),
                new IdSource.Rectangle(1244, 2, 1, 176),
                new IdSource.Rectangle(1598, 3, 1, 176));

        // fir bottoms
        registry.markBottom(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(2275, 6, 3, 176),
                new IdSource.Rectangle(3331, 6, 3, 176),
                new IdSource.Rectangle(7027, 6, 3, 176),
                new IdSource.Rectangle(8083, 6, 3, 176),
                new IdSource.Rectangle(11603, 6, 3, 176),
                new IdSource.Rectangle(12659, 6, 3, 176),
                new IdSource.Rectangle(16179, 6, 3, 176),
                new IdSource.Rectangle(17235, 6, 3, 176));

        // Prioritized candidate rules
        registry.addCandidate(new IncludedCandidateRule(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(16537, 4, 1, 176),
                new IdSource.Rectangle(17417, 4, 1, 176)));

        // Mark dead trees as entangled
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(3857, 8, 6, 176));

        // Entangle (store)
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(11484, 10, 9, 176));

        // Ground block trees
        registry.markBottom(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(18810, 15, 1, 176));
        registry.markBottom(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(19690, 15, 1, 176));
        registry.markBottom(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(20570, 15, 1, 176));

        // Brick walls
        registry.markDecoration(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(24869, 6, 1, 176),
                new IdSource.Rectangle(24676, 6, 1, 176),
                new IdSource.Rectangle(24697, 1, 1, 176));
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(24341, 6, 7, 176));
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(24348, 6, 7, 176));
    }
}
