import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import gridgames.*;

import java.lang.reflect.Field;
import java.util.List;

/** 
 * Unit tests for the integration of ConsoleView
 */

public class ConsoleViewTest {

    private SameGame game;
    private GameController controller;
    private ConsoleView consoleView;

    @BeforeEach
    void setUp() {
        this.game = new SameGame();
        this.game.setDifficulty(Difficulty.EASY);
        this.game.makeNew();

        this.controller = new GameController(this.game);
        this.consoleView = new ConsoleView();
        this.consoleView.setController(this.controller);         
        game.addListener(consoleView);
    }

    // --------------------------
    // USEFUL METHODS FOR TESTING
    // --------------------------

    /**
     * Retrieves the private {@code gameView} field from a {@code ConsoleView} instance using reflection.
     * This allows direct inspection of the internal game view for testing purposes.
     *
     * @param consoleView the {@code ConsoleView} instance to inspect
     * @return the value of the {@code gameView} field
     * @throws RuntimeException if the field cannot be accessed
     */
    private String getGameView(ConsoleView consoleView) {
        try {
            Field field = ConsoleView.class.getDeclaredField("gameView");
            field.setAccessible(true);
            return (String) field.get(consoleView);
        } catch (Exception e) {
            throw new RuntimeException("Unable to access the gameView field", e);
        }
    }

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

    /**
     * Fills the grid with three identical tiles at (0,0), (0,1), and (0,2), 
     * and the rest in a checkerboard pattern of two other colors.
     * This helps simulate a situation with exactly one group of 3 removable tiles.
     */
    private void fillGridWithThreeAlignedTilesRestCheckerboard(SameGame game, int color0, int color1, int color2) {
        Grid grid = getGrid(game);
        List<List<Tile>> tiles = getTilesArray(grid);
        for (int i = 0; i < grid.getHeight(); i++) {
            List<Tile> row = tiles.get(i);
            for (int j = 0; j < row.size(); j++) {
                if (i == 0 && (j == 0 || j == 1 || j == 2)) {
                    // The 3 identical tiles in the top-left corner
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
     * Tests that the view is properly initialized and updated 
     * when a BEGIN event occurs.
     */
    @Test
    void testUpdateOnBegin() {
        GameEvent event = new GameEvent(EventType.BEGIN, null);
        this.consoleView.update(event);
        // simulate a BEGIN event

        String view = getGameView(this.consoleView);
        assertNotNull(view);
        assertTrue(view.contains("Score: 0"));
    }

    /**
     * Tests that the view is correctly updated after a NEXT_MOVE event,
     * following the validation of a 3-tile selection.
     */
    @Test
    void testUpdateOnNextMove() {
        fillGridWithThreeAlignedTilesRestCheckerboard(this.game, 0, 1 ,2);
        this.game.selectionAt(0, 0);        
        this.game.validateSelection();
        // simulate a NEXT_MOVE event

        String view = getGameView(this.consoleView);
        assertNotNull(view);
        assertTrue(view.contains("Score: " + (3-2)*(3-2)));
    }

    /**
     * Tests that the best move is correctly displayed 
     * after a HINT event.
     */
    @Test
    void testBestMoveDisplayed() {
        this.game.findBestMove();
        int[] bestHit = this.game.getBestMove();

        GameEvent event = new GameEvent(EventType.HINT, null);
        this.consoleView.update(event);
        // simulate a HINT event

        String view = getGameView(this.consoleView);
        assertTrue(view.contains("Best hit: [" + bestHit[0] + ", " + bestHit[1] + "]"));
    }

    /**
     * Tests that the view is reset correctly after a RESET event,
     * and that the score returns to 0.
     */
    @Test
    void testUpdateOnReset() {
        fillGridWithOneColor(this.game, 0);
        this.game.selectionAt(0, 0);
        this.game.validateSelection();
        this.game.reset();
        // simulate a RESET event

        String view = getGameView(this.consoleView);
        assertNotNull(view);
        assertTrue(view.contains("Score: 0"));
    }

    /**
     * Tests that the WIN message is displayed correctly 
     * after the player clears the board.
     */
    @Test
    void testUpdateOnWin() {
        fillGridWithOneColor(this.game, 0);
        this.game.selectionAt(0, 0);        
        this.game.validateSelection();
        // simulate a WIN event
        assertEquals(GameState.WON, this.game.getState()); 

        String view = getGameView(this.consoleView);
        assertNotNull(view);
        assertTrue(view.contains("You won!"));
    }

    /**
     * Tests that the LOSE message is displayed correctly 
     * when no more valid moves remain.
     */
    @Test
    void testUpdateOnLose() {
        fillGridWithOnePairRestCheckerboard(this.game, 0, 1, 2);
        this.game.selectionAt(0, 0);
        this.game.validateSelection();
        // simulate a LOSE event
        assertEquals(GameState.LOST, this.game.getState());

        String view = getGameView(this.consoleView);
        assertNotNull(view);
        assertTrue(view.contains("You lost ..."));
    }
}

