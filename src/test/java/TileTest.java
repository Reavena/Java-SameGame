import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import gridgames.*;

/** 
 * Unit tests for the integration of Tile
 */

public class TileTest {

    /**
     * Tests the selection and unselection behavior of a tile.
     * Verifies that a tile is not selected by default,
     * becomes selected after calling select(),
     * and becomes unselected after calling unselect().
     */
    @Test
    public void testSelectAndUnselect() {
        Tile tile = new Tile(5);
        assertFalse(tile.isSelected());

        tile.select();
        assertTrue(tile.isSelected());

        tile.unselect();
        assertFalse(tile.isSelected());
    }

    /**
     * Tests the hasSameValue method.
     * Verifies that two tiles with the same value return true,
     * and tiles with different values return false.
     */
    @Test
    public void testHasSameValue() {
        Tile tile1 = new Tile(7);
        Tile tile2 = new Tile(7);
        Tile tile3 = new Tile(3);

        assertTrue(tile1.hasSameValue(tile2));
        assertFalse(tile1.hasSameValue(tile3));
    }

    /**
     * Tests the hasSameState method.
     * Verifies that two tiles with the same selection state return true,
     * and that the result becomes false if the selection states differ.
     */
    @Test
    public void testHasSameState() {
        Tile tile1 = new Tile(1);
        Tile tile2 = new Tile(1);

        tile1.select();
        tile2.select();
        assertTrue(tile1.hasSameState(tile2));

        tile2.unselect();
        assertFalse(tile1.hasSameState(tile2));
    }
}
