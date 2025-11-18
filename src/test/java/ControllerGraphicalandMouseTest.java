import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import gridgames.*;

import java.lang.reflect.Field;
import java.util.List;

/** 
 * Unit tests for the integration of GameController, GraphicalView, and MouseInput.
 * 
 * Unfortunately, we were unable to test each of these components separately,
 * as they are tightly coupled and depend on one another to function properly.
 */

public class ControllerGraphicalandMouseTest {

    private SameGame game;
    private GameController controller;
    private GraphicalView graphicalView;

    @BeforeEach
    public void Setup() {
        this.game = new SameGame();
        this.controller = new GameController(this.game);
        
        this.game.setDifficulty(Difficulty.EASY);
        this.game.makeNew();

        this.graphicalView = new GraphicalView();
        this.controller.setInput(new MouseInput());
        this.graphicalView.setController(this.controller);
        this.game.addListener(this.graphicalView);
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
     * Simulates mouse movement and a click on a uniformly colored grid.
     * 
     * This test ensures that when the player clicks on any tile in a grid where all tiles 
     * share the same color, the entire grid is selected and removed. 
     * The resulting game state should be WIN and the score should reflect the full clear.
     */
    @Test
    public void testClickOnUniformGridSelectsAllTilesAndWinsGame() {

        GameEvent event = new GameEvent(EventType.BEGIN, null);
        this.graphicalView.update(event);

        fillGridWithOneColor(this.game, 1); 

        // Coordinates to click roughly on tile (4, 4)
        int tileSize = GraphicalView.TILE_SIZE;
        int padding = GraphicalView.PADDING;
        int x = 4 * (tileSize + padding) + tileSize / 2;
        int y = 4 * (tileSize + padding) + tileSize / 2;

        // Create a dummy MouseEvent for movement over (4,4)
        java.awt.event.MouseEvent moveEvt = new java.awt.event.MouseEvent(
            new javax.swing.JButton(),
            java.awt.event.MouseEvent.MOUSE_MOVED,
            System.currentTimeMillis(),
            0, x, y, 0, false
        );

        // Create a dummy MouseEvent for clicking at (4,4)
        java.awt.event.MouseEvent clickEvt = new java.awt.event.MouseEvent(
            new javax.swing.JButton(),
            java.awt.event.MouseEvent.MOUSE_CLICKED,
            System.currentTimeMillis(),
            0, x, y, 1, false
        );
        
        this.controller.mouseMoved(moveEvt, (GraphicalView.TILE_SIZE + GraphicalView.PADDING));
        this.controller.mouseClicked(clickEvt, (GraphicalView.TILE_SIZE + GraphicalView.PADDING));

        assertEquals((this.game.gridHeight() * this.game.gridWidth() - 2) * 
                     (this.game.gridHeight() * this.game.gridWidth() - 2), this.game.getScore());
        assertTrue(this.game.checkWin());
        assertEquals(GameState.WON, this.game.getState());  
    }

    /**
     * Simulates mouse movement and a click on a tile that has no adjacent tiles 
     * of the same color (i.e., a group of size 1).
     * 
     * The test verifies that such a click has no effect: the game state remains ONGOING 
     * and the score remains unchanged.
     */
    @Test
    public void testClickOnSingleTileDoesNotChangeState() {

        GameEvent event = new GameEvent(EventType.BEGIN, null);
        this.graphicalView.update(event);
    
        fillGridWithOnePairRestCheckerboard(this.game, 0, 1, 2);

        // Coordinates to click roughly on tile (4, 4)
        int tileSize = GraphicalView.TILE_SIZE;
        int padding = GraphicalView.PADDING;
        int x = 4 * (tileSize + padding) + tileSize / 2;
        int y = 4 * (tileSize + padding) + tileSize / 2;

        // Create a dummy MouseEvent for movement over (4,4)
        java.awt.event.MouseEvent moveEvt = new java.awt.event.MouseEvent(
            new javax.swing.JButton(),
            java.awt.event.MouseEvent.MOUSE_MOVED,
            System.currentTimeMillis(),
            0, x, y, 0, false
        );

        // Create a dummy MouseEvent for clicking at (4,4)
        java.awt.event.MouseEvent clickEvt = new java.awt.event.MouseEvent(
            new javax.swing.JButton(),
            java.awt.event.MouseEvent.MOUSE_CLICKED,
            System.currentTimeMillis(),
            0, x, y, 1, false
        );
        
        this.controller.mouseMoved(moveEvt, (GraphicalView.TILE_SIZE + GraphicalView.PADDING));
        this.controller.mouseClicked(clickEvt, (GraphicalView.TILE_SIZE + GraphicalView.PADDING));
 
        assertEquals(0, this.game.getScore()); 
        assertEquals(GameState.ONGOING, this.game.getState());  
    }
}
