package de.uniks.stpmon.k.world.rules;

import static de.uniks.stpmon.k.world.rules.BasicRules.*;

public class WoodRules {
    public static RuleRegistry registerRules() {
        RuleRegistry registry = new RuleRegistry();
        // Entangle (moncenter)
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(577 + 13067, 14, 9, 176));

        // Entangle (moncenter shield)
        registry.markEntangled(TILESET_MODERN_EXTERIORS_EXTENDED,
                new IdSource.Rectangle(42289, 6, 2, 16));

        // tree house
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(577 + 1500, 4, 4, 176));

        // tree fence
        registry.markEntangled(TILESET_CAVES,
                new IdSource.Rectangle(328, 3, 2, 24));

        // Entangle (store)
        registry.markEntangled(TILESET_MODERN_EXTERIORS,
                new IdSource.Rectangle(577 + 11483, 10, 9, 176));

        registry.addConnection(new EntangledRule(registry));
        return registry;
    }
}
