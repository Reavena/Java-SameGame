import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import gridgames.*;
import java.util.List;

/** 
 * Unit tests for the integration of Grid
 */

public class GridTest {

    /**
     * Tests the initialization of the grid with given dimensions and seed values.
     * Verifies that the grid's height, width, and tile list sizes are correctly set.
     */
    @Test
    public void testGridInitialization() {
        int[] valueseed = {1, 2};
        Grid grid = new Grid(3, 3, valueseed);

        assertEquals(3, grid.getHeight());
        assertEquals(3, grid.getWidth());

        List<List<Tile>> tiles = grid.getTiles();

        assertEquals(3, tiles.size());
    }

    /**
     * Tests selecting tiles starting from a position.
     * After selection, all tiles should be marked as selected,
     * and the count of selected tiles should match the total number of tiles.
     */
    @Test
    public void testSelectTiles() {
        int[] valueseed = {1};
        Grid grid = new Grid(3, 3, valueseed);
        grid.selectTiles(0, 0);

        assertEquals(9, grid.getSelectedTilesNumber());

        for (List<Tile> row : grid.getTiles()) {
            for (Tile tile : row) {
                assertTrue(tile.isSelected());
            }
        }
    }      

    /**
     * Tests unselecting tiles after a selection.
     * Verifies that all tiles are unselected and the count of selected tiles resets to zero.
     */
    @Test
    public void testunselectTiles() {
        int[] valueseed = {1};
        Grid grid = new Grid(3, 3, valueseed);
        grid.selectTiles(0, 0);
        grid.unselectTiles();

        for (List<Tile> row : grid.getTiles()) {
            for (Tile tile : row) {
                assertFalse(tile.isSelected());
            }
        }

        assertEquals(0, grid.getSelectedTilesNumber());
    }

    /**
     * Tests removing the selected tiles from the grid.
     * After removal, the grid should be empty.
     */
    @Test
    public void testremoveSelection() {
        int[] valueseed = {1};
        Grid grid = new Grid(3, 3, valueseed);
        grid.selectTiles(0, 0);
        grid.removeSelection();

        assertTrue(grid.isEmpty());       
    }
}
