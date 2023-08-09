package de.uniks.stpmon.k.world;

import de.uniks.stpmon.k.world.rules.IncludedCandidateRule;
import de.uniks.stpmon.k.world.rules.TileInfo;
import de.uniks.stpmon.k.world.rules.TilesetCandidateRule;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PropRuleTest {

    @Test
    public void candidateTest() {
        // Create rule which connects tiles with id 0 and 2
        IncludedCandidateRule includedCandidateRule = new IncludedCandidateRule("tileSet", 0, 2);
        TileInfo tile = new TileInfo(0, 0, 0, 0, "tileSet");
        TileInfo first = new TileInfo(0, 0, 0, 1, "tileSet");
        TileInfo second = new TileInfo(0, 0, 0, 2, "tileSet");
        TileInfo result = includedCandidateRule.apply(tile, List.of(first, second), List.of());
        // Check if the second tile is returned
        assertEquals(second, result);
    }

    @Test
    public void tilesetTest() {
        // Create a rule which check if the ids are connected on the tileset
        TilesetCandidateRule tilesetCandidateRule = new TilesetCandidateRule("tileSet", 2, 1);
        TileInfo tile = new TileInfo(0, 0, 0, 1, "tileSet");
        TileInfo first = new TileInfo(0, 0, 0, 6, "tileSet");
        TileInfo second = new TileInfo(0, 0, 0, 2, "");
        TileInfo third = new TileInfo(0, 0, 0, 3, "tileSet");
        TileInfo result = tilesetCandidateRule.apply(tile, List.of(first, second, third), List.of());
        // Check if the second tile is returned
        assertEquals(third, result);
    }
}
