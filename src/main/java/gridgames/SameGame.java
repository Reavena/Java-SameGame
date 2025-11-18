package gridgames;

/**
 * Class which holds the logic related to the SameGame.
 * <p>
 * The class repsonsabilities are inherited from its superclass {@link Game}.
 * 
 * @see Game
 */
public class SameGame extends Game {

    /**
     * {@code int} constant set to {@value}, used to define the width of the
     * {@code Grid} created by this {@code SameGame} with the
     * {@link #generateGrid()} method.
     * 
     * @see #generateGrid()
     */
    private final static int GRID_WIDTH = 15;
    /**
     * {@code int} constant set to {@value}, used to define the height of the
     * {@code Grid} created by this {@code SameGame} with the
     * {@link #generateGrid()} method.
     * 
     * @see #generateGrid()
     */
    private final static int GRID_HEIGHT = 10;

    /**
     * {@code String} constant set to {@value}, used to override the default
     * {@code Game} message displayed when the user won.
     */
    private final static String WIN_MESSAGE = "You won!\n";
    /**
     * {@code String} constant set to {@value}, used to override the default
     * {@code Game} message displayed when the user lost.
     */
    private final static String LOSE_MESSAGE = "You lost ...\n";
    /**
     * {@code String} constant set to {@value}, used to override the default
     * {@code Game} game rules.
     */
    private final static String GAME_RULES 
        = "                SameGame Rules \n\n"
            + "   Click on groups of 2+ same-colored tiles   \n"
            + "   > Selected tiles will be removed   \n"
            + "   > Remaining tiles collapse left    \n"
            + "   > Remaining columns collapse up  \n"
            + "   > Game ends when no moves remain   \n";

    /** 
     * Constructs a newly allocated {@code SameGame} object.
     * Simply calls the superclass {@code Game} constructor.
     * 
     * @see Game#Game()
     */
    public SameGame() {
        super();
    }

    /**
     * Replaces the defaults game rules {@code String}, the winning message
     * {@code String} and the losing message {@code String}.
     * 
     * @see Game#setGameParameters()
     */
    @Override
    protected void setGameParameters() {
        this.gameRules = GAME_RULES;
        this.winMessage = WIN_MESSAGE;
        this.loseMessage = LOSE_MESSAGE;
    }

    /**
     * Generates a new {@code Grid} for this {@code SameGame} depending on the set
     * difficulty level.
     * <p>
     * The level of difficulty affects the number of different values a {@code Tile}
     * can take within the {@code Grid} of this {@code SameGame}:
     * <ul>
     * <li>{@link Difficulty#EASY} 2 values,
     * <li>{@link Difficulty#MEDIUM} 3 values,
     * <li>{@link Difficulty#HARD} 4 values,
     * <li>The default is 3 values.
     * </ul>
     * 
     * @see Game#generateGrid()
     * @see Game#difficulty
     * @see Difficulty
     */
    @Override
    protected Grid generateGrid() {

        // Pick a number of values depending on
        // the difficulty level.
        int nColor = 0;
        switch (this.difficulty) {
            case EASY:
                nColor = 2;
                break;

            case MEDIUM:
                nColor = 3;
                break;

            case HARD:
                nColor = 4;
                break;

            default:
                nColor = 3;
                break;
        }

        // Generates a new set of values
        int[] valueSeed = new int[nColor];
        for (int i = 0; i < nColor; i++)
            valueSeed[i] = i;

        // Constructs a new Grid with the set of values.
        return new Grid(GRID_HEIGHT, GRID_WIDTH, valueSeed);

    }

    /**
     * Checks if the user won the game. The user wins only if there is no tiles left.
     * 
     * @return {@code true} if the user won, {@code false} otherwise.
     * 
     * @see Game#checkWin()
     */
    @Override
    public boolean checkWin() {
        if (this.grid.isEmpty())
            return true;
        else
            return false;
    }

    /**
     * Checks if the user lost the game.
     * The user loses if any tiles remain but no further movement is possible.
     * 
     * @return {@code true} if the user lost, {@code false} otherwise.
     * 
     * @see Game#checkLose()
     */
    @Override
    public boolean checkLose() {

        if (this.grid.isEmpty()) return false;

        int h = this.gridHeight();
        int w = this.gridWidth();

        // Tries to select a tile on the grid.
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                this.grid.selectTiles(i, j);
                if (this.grid.getSelectedTilesNumber() > 1) {
                    // The user could play another move.
                    this.grid.unselectTiles();
                    return false;
                }
            }
        }

        return true;

    }

    /**
     * Computes the amount of points given by the user selection.
     * <p>
     * The number of points {@literal p} is linked to the size {@literal n}
     * of a group of tiles with the same color by the following relationship:
     * <p>
     * {@literal p = (n - 2)}{@literal ²}
     * 
     * @see Game#computeScoreInc()
     */
    @Override
    protected void computeScoreInc() {
        int nTiles = this.selectedTilesNumber();
        if (nTiles < 3) {
            this.scoreInc = 0;
            return;
        }
        this.scoreInc = (nTiles - 2) * (nTiles - 2);
    }

}
