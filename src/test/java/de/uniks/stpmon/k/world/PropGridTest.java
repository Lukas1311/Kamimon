package de.uniks.stpmon.k.world;

import de.uniks.stpmon.k.utils.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PropGridTest {

    @Test
    public void checkSize() {
        PropGrid grid = new PropGrid(10, 5);
        assertEquals(5, grid.getHeight());
        assertEquals(10, grid.getWidth());
    }

    @Test
    public void setVisited() {
        PropGrid grid = new PropGrid(1, 2);
        // Set left to visited
        grid.setVisited(0, 0, Direction.LEFT);

        // Check if only left is visited
        assertTrue(grid.hasVisited(0, 0, Direction.LEFT));
        assertFalse(grid.hasVisited(0, 0, Direction.RIGHT));
        assertFalse(grid.hasVisited(0, 0, Direction.BOTTOM));
        assertFalse(grid.hasVisited(0, 0, Direction.TOP));
    }

    @Test
    public void setGroup() {
        PropGrid grid = new PropGrid(1, 1);
        // Set group to 12
        grid.setGroup(0, 0, 12);
        // Check if group is 12
        assertEquals(12, grid.getGroup(0, 0));
        // Set group to 1
        grid.setGroup(0, 0, 1);
        // Check if group changed to 1
        assertEquals(1, grid.getGroup(0, 0));
    }

}