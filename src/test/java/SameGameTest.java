import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import gridgames.*;

import java.lang.reflect.Field;
import java.util.List;

/** 
 * Unit tests for the integration of SameGame
 */

public class SameGameTest {

    private SameGame game;

    @BeforeEach
    public void Setup() {
        this.game = new SameGame();
        this.game.setDifficulty(Difficulty.EASY);
        this.game.makeNew();
    } 

    // --------------------------
    // USEFUL METHODS FOR TESTING
    // --------------------------

    /**
     * Retrieves the private {@code grid} field from a {@code Game} instance using reflection.
     * This allows direct access to the game grid for test setup and verification.
     *
     * @param game the {@code SameGame} instance to inspect
     * @return the {@code Grid} object contained in the game
     * @throws RuntimeException if the field cannot be accessed
     */
    private Grid getGrid(SameGame game) {
        try {
            Field gridField = Game.class.getDeclaredField("grid");
            gridField.setAccessible(true);
            return (Grid) gridField.get(game);
        } catch (Exception e) {
            throw new RuntimeException("Error accessing the 'grid' field", e);
        }
    }

    /**
     * Retrieves the private {@code tilesArray} field from a {@code Grid} instance using
     * reflection. This allows direct access to the game grid for test setup and verification.
     *
     * @param grid the {@code Grid} instance to inspect
     * @return the {@code tileArray} object contained in the grid
     * @throws RuntimeException if the field cannot be accessed
     */
    @SuppressWarnings("unchecked")
    private List<List<Tile>> getTilesArray(Grid grid) {
        try {
            Field tileField = Grid.class.getDeclaredField("tilesArray");
            tileField.setAccessible(true);
            return (List<List<Tile>>) tileField.get(grid);
        } catch (Exception e) {
            throw new RuntimeException("Error accessing the 'tilesArray' field", e);
        }
    }

    /**
     * Fills the entire grid with a single color. Useful for testing large selections.
     * All tiles will be initialized to the given color value.
     */
    private void fillGridWithOneColor(SameGame game, int color) {
        Grid grid = getGrid(game);
        List<List<Tile>> tiles = getTilesArray(grid);
        for (List<Tile> row : tiles) {
            for (int j = 0; j < row.size(); j++) {
                row.set(j, new Tile(color));
            }
        }
    }

    /**
     * Fills the grid in a checkerboard pattern alternating between two colors.
     * This ensures no adjacent tiles share the same color, simulating an end-of-game scenario.
     */
    private void fillGridCheckerboard(SameGame game, int color0, int color1) {
        Grid grid = getGrid(game);
        List<List<Tile>> tiles = getTilesArray(grid);
        for (int i = 0; i < grid.getHeight(); i++) {
            List<Tile> row = tiles.get(i);
            for (int j = 0; j < row.size(); j++) {
                int color = (i + j) % 2 == 0 ? color0 : color1;
                row.set(j, new Tile(color));
            }
        }
    }

    /**
     * Fills the grid with one pair of identical tiles at (0,0) and (0,1), and the rest 
     * in a checkerboard pattern of two other colors. This helps simulate a situation 
     * where only one valid move remains.
     */
    private void fillGridWithOnePairRestCheckerboard(SameGame game, int color0, int color1, int color2) {
        Grid grid = getGrid(game);
        List<List<Tile>> tiles = getTilesArray(grid);
        for (int i = 0; i < grid.getHeight(); i++) {
            List<Tile> row = tiles.get(i);
            for (int j = 0; j < row.size(); j++) {
                if (i == 0 && (j == 0 || j == 1)) {
                    // The pair of identical tiles at (0,0) and (0,1)
                    row.set(j, new Tile(color0)); 
                } else {
                    int color = (i + j) % 2 == 0 ? color1 : color2;
                    row.set(j, new Tile(color));
                }
            }
        }
    }

    // --------------------------
    // TESTS
    // --------------------------

    /** 
     * Tests the initialization of the game with default parameters.
     * Checks that the grid is not null, and all values are at their initial state.
     */
    @Test
    public void testGameInitialization() {
        assertNotNull(this.game.isGridInitialized());
        assertEquals(0, this.game.getScore());
        assertEquals(0, this.game.getScoreInc());
        assertEquals(null, this.game.getBestMove());
        assertEquals(Difficulty.EASY, this.game.getDifficulty());
        assertEquals(GameState.ONGOING, this.game.getState());    
    }

    /** 
     * Tests the behavior of findBestMove on a grid with only one color.
     * Verifies that a valid move is found (notNull) and the score increment is maximal.
     */
    @Test
    public void testFindBestMoveOnOneColorGrid() {
        fillGridWithOneColor(this.game, 0);
        this.game.findBestMove();
        int[] bestHit = this.game.getBestMove();
        assertNotNull(bestHit);
        assertTrue(bestHit[0] >= 0 && bestHit[0] < this.game.gridHeight() 
                && bestHit[1] >= 0 && bestHit[1] < this.game.gridWidth());
        assertEquals((this.game.gridHeight() * this.game.gridWidth() - 2) * 
                     (this.game.gridHeight() * this.game.gridWidth() - 2), this.game.getScoreInc());
    }

    /** 
     * Tests the win condition when the grid contains only one color.
     * Selecting and validating one tile should clear the grid and lead to a win.
     */
    @Test
    public void testWinConditionOnOneColorGrid() {
        fillGridWithOneColor(this.game, 0);
        assertFalse(this.game.checkLose());
        this.game.selectionAt(0, 0);        
        this.game.validateSelection();
        assertTrue(this.game.checkWin());
        assertEquals(GameState.WON, this.game.getState());    
    }

    /**
     * Tests the computation of the score increment when selecting more than two tiles.
     * Before validation, getScoreInc() should return (n-2)^2 where n is the number of selected tiles.
     * After validating the selection, the score should be updated accordingly, and getScoreInc() reset to 0.
     */
    @Test
    public void testComputeScoreIncWithMoreThan2Tiles() {
        fillGridWithOneColor(this.game, 0);
        this.game.selectionAt(0, 0);
        assertEquals((this.game.gridHeight() * this.game.gridWidth() - 2) * 
                     (this.game.gridHeight() * this.game.gridWidth() - 2), this.game.getScoreInc());
        this.game.validateSelection();
        assertEquals(0, this.game.getScoreInc());
        assertEquals((this.game.gridHeight() * this.game.gridWidth() - 2) * 
                     (this.game.gridHeight() * this.game.gridWidth() - 2), this.game.getScore());
    }

    /** 
     * Tests if the game is properly reset.
     * All state fields should return to their initial values.
     */
    @Test
    public void testGameReset() {
        fillGridWithOneColor(this.game, 0);
        this.game.selectionAt(0, 0);
        this.game.validateSelection();

        this.game.reset();
        assertNotNull(this.game.isGridInitialized());
        assertEquals(0, this.game.getScore());
        assertEquals(0, this.game.getScoreInc());
        assertEquals(null, this.game.getBestMove());
        assertEquals(Difficulty.EASY, this.game.getDifficulty());
        assertEquals(GameState.ONGOING, this.game.getState());    
    }

    /** 
     * Tests that no move is found on a checkerboard pattern (no adjacent matching tiles).
     * The best move should be null and score increment should be -1.
     */
    @Test
    public void testFindBestWhenNoMoveAvailable() {
        fillGridCheckerboard(this.game, 0, 1); 
        this.game.findBestMove();
        int[] bestHit = this.game.getBestMove();
        assertNull(bestHit);
        assertEquals(-1, this.game.getScoreInc()); 
    }

    /** 
     * Tests that the game finds the only pair of tiles left on the grid.
     * Even if score increment is zero, the move should still be detected.
     */
    @Test
    public void testFindBestIfThereAreOnlyTwoTilesLeft() {
        fillGridWithOnePairRestCheckerboard(this.game, 0, 1, 2);
        this.game.findBestMove();
        int[] bestHit = this.game.getBestMove();
        assertNotNull(bestHit);
        assertTrue((bestHit[0] == 0 && bestHit[1] == 0) || 
                   (bestHit[0] == 0 && bestHit[1] == 1));     
        assertEquals(0, this.game.getScoreInc()); 
    }

    /** 
     * Tests the lose condition after playing the only remaining move (a pair).
     * Ensures the game detects that no moves are left and sets the state to LOST.
     */
    @Test 
    public void testLoseConditionWhenNoMoveAvailable() {
        fillGridWithOnePairRestCheckerboard(this.game, 0, 1, 2);
        this.game.selectionAt(0, 0);
        this.game.validateSelection();
        assertFalse(this.game.checkWin());
        assertTrue(this.game.checkLose());
        assertEquals(GameState.LOST, this.game.getState());
    }

    /** 
     * Tests that no score increment is given when selecting less than 3 tiles.
     * Also verifies that the global score remains unchanged.
     */
    @Test
    public void testComputeScoreIncWithLessThan3Tiles() {
        fillGridWithOnePairRestCheckerboard(this.game, 0, 1, 2);
        this.game.selectionAt(0, 0);
        this.game.validateSelection();
        assertEquals(0, this.game.getScoreInc()); 
        assertEquals(0, this.game.getScore()); 
    }
}