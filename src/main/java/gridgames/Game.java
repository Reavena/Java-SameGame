package gridgames;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class which holds the common logic for all games built uppon the
 * {@code gridgames} package.
 * <p>
 * The class repsonsabilities are to notify its listeners classes through the event
 * mechanism, make common operations on a {@code Grid} object and make common
 * operations determining the state of the game.
 * <p>
 * NOTE: This class has been created with the idea of making a framework
 * allowing the user to build its own 2D-style puzzle games, but it has not been
 * achieved yet.
 * <p>
 * NOTE: The {@code Game} instance is the "Model" of the Model-View-Controller
 * pattern used to design this project.
 */
public abstract class Game implements Serializable {

    /** Current score made by the user while playing, as an {@code int}. */
    protected int score;
    /** Score that the user could make by validating its move, as an {@code int}. */
    protected int scoreInc;
    /**
     * Current level of difficulty the user is playing on, as a {@code Difficulty}
     * enumeration.
     */
    protected Difficulty difficulty;
    /**
     * {@code} Grid object on which this {@code Game} and its subclasses can operate
     * on.
     */
    protected Grid grid;

    /** {@code String} which could contain the game rules. */
    protected String gameRules;
    /**
     * {@code String} which could contain a message to display when the user won.
     */
    protected String winMessage;
    /**
     * {@code String} which could contain a message to display when the user lost.
     */
    protected String loseMessage;

    /**
     * Best move the user can choose to gain the highest number of points for the
     * current round, as an array of two {@code int} values.
     */
    private int[] bestMove;

    /** current state of the game, as a {@code GameState} enumeration. */
    private GameState state;

    /**
     * Listeners of this {@code Game} object, as a {@code List<GameListener>}.
     * 
     * @see GameListener
     */
    private transient List<GameListener> gameListeners;

    /** Default {@code String} for the game rules. */
    private final static transient String GAME_RULES
        = "These are the game rules. It is a very simple game. Please play!\n";
    /** Default {@code String} for a message when the user won. */
    private final static transient String WIN_MESSAGE = "You won!\n";
    /** Default {@code String} for a message when the user lost. */
    private final static transient String LOSE_MESSAGE = "You lost ...\n";

    /** Arbitrary serialization number set to {@value}. */
    private final static long serialVersionUID = 3L;

    /**
     * Provides the ability to make a custom implementation
     * to initialize this {@code Game}.
     * <p>
     * E.g. to replace default values set by this {@code Game}.
     */
    protected abstract void setGameParameters();

    /**
     * Builds and returns a new {@code Grid} for this {@code Game}.
     * <p>
     * E.g. to make a different {@code Grid} according to the level of difficulty.
     * 
     * @return A newly allocated {@code Grid} object.
     */
    protected abstract Grid generateGrid();

    /**
     * Checks whether the user won the game or not.
     * 
     * @return {@code true} if the user won the game, {@code false} otherwise.
     */
    public abstract boolean checkWin();

    /**
     * Checks whether the user lost the game or not.
     * 
     * @return {@code true} if the user lost the game, {@code false} otherwise.
     */
    public abstract boolean checkLose();

    /**
     * Computes the amount of points given by the user selection.
     * <p>
     * Allows the programmer to provide cutom scoring rules.
     */
    protected abstract void computeScoreInc();

    /**
     * Constructor for {@code Game} class. Must be used by all subclasses.
     * <p>
     * The work of this constructor is to initialize the game state and make it
     * possible to add listeners to it.
     * 
     * @see #addListener(GameListener)
     */
    public Game() {
        this.state = GameState.PRESTART;
        this.difficulty = Difficulty.EASY;
        this.gameListeners = new ArrayList<GameListener>();
    }

    /**
     * Re-enables the possibility to add listeners to this {@code Game} after
     * deserialization.
     * <p>
     * The listeners of this {@code Game} are not serialized, thus must be added
     * again to the deserialized game.
     * 
     * @see #addListener(GameListener)
     * @see FileManager
     */
    public final void makeListenable() {
        if (this.gameListeners == null)
            this.gameListeners = new ArrayList<GameListener>();
    }

    /**
     * Adds a new listener object to this {@code Game} and set it
     * properly to listen to this {@code Game}.
     * 
     * @param listener An object which implements the {@code GameListener}
     *                 interface.
     * 
     * @see GameListener
     */
    public final void addListener(GameListener listener) {
        listener.setGame(this);
        this.gameListeners.add(listener);
    }

    /**
     * Sets a new playable environment held by this {@code Game}, for the user.
     * <p>
     * This method requires an implentation of {@link #setGameParameters()} and
     * {@link #generateGrid()} to be used by {@code Game} subclasses.
     * <p>
     * Also notifies all listeners of an {@link EventType#BEGIN} event.
     * 
     * @see #setGameParameters()
     * @see #generateGrid()
     * @see #addListener(GameListener)
     * @see EventType
     */
    public final void makeNew() {

        this.score = 0;
        this.scoreInc = 0;
        this.gameRules = GAME_RULES;
        this.winMessage = WIN_MESSAGE;
        this.loseMessage = LOSE_MESSAGE;
        this.bestMove = null;

        // Additionnal implementations supplied by subclasses.
        this.setGameParameters();
        this.grid = generateGrid();

        this.state = GameState.ONGOING;
        this.notifyListeners(EventType.BEGIN, null);

    }

    /**
     * Resets the current playable environment by setting the score to 0 and
     * generating a new {@code Grid}.
     * <p>
     * This method requires an implentation of {@link #generateGrid()} to be used by
     * {@code Game} subclasses.
     * <p>
     * Also notifies all listeners of an {@link EventType#RESET} event.
     * 
     * @see #generateGrid()
     * @see #addListener(GameListener)
     * @see EventType
     */
    public final void reset() {

        this.score = 0;
        this.scoreInc = 0;
        this.bestMove = null;
        this.grid = generateGrid();

        this.state = GameState.ONGOING;
        this.notifyListeners(EventType.RESET, null);

    }

    /**
     * Resets the current playable environment by setting the score to 0 and
     * generating a new {@code Grid} according to a given level of difficulty.
     * <p>
     * This method requires an implentation of {@link #generateGrid()} to be used by
     * {@code Game} subclasses.
     * <p>
     * Also notifies all listeners of an {@link EventType#RESET} event.
     * 
     * @param difficulty The level of difficulty to reset this {@code Game} with, as
     *                   a {@code Difficulty} enumeration.
     * @see #generateGrid()
     * @see #addListener(GameListener)
     * @see EventType
     */
    public final void reset(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.reset();
    }

    /**
     * Notifies all listeners of an {@link EventType#INIT} event.
     * 
     * @see #addListener(GameListener)
     * @see EventType
     */
    public final void init() {
        this.notifyListeners(EventType.INIT, null);
    }

    /**
     * Notifies all listeners of an {@link EventType#BEGIN} event.
     * 
     * @see #addListener(GameListener)
     * @see EventType
     */
    public final void begin() {
        this.notifyListeners(EventType.BEGIN, null);
    }

    /**
     * Notifies all listeners of an {@link EventType#EXIT} event.
     * 
     * @see #addListener(GameListener)
     * @see EventType
     */
    public final void exit() {
        this.notifyListeners(EventType.EXIT, null);
    }

    /**
     * Finds the best move the user can play to score the highest number of points
     * on the current round.
     * <p>
     * Also saves the amount of points in {@link #scoreInc} and notifies all
     * listeners of an {@link EventType#HINT} event.
     * 
     * @see #scoreInc
     * @see #addListener(GameListener)
     * @see EventType
     */
    public final void findBestMove() {

        // Avoids issue when tiles have been previously selected
        // by other methods.
        this.grid.unselectTiles();

        int finalInc = -1;

        // Gets the bounds of the grid.
        int h = this.gridHeight();
        int w = this.gridWidth();

        // Simulate a selection on all tiles.
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {

                this.grid.selectTiles(i, j);

                if (this.selectedTilesNumber() > 1) {

                    // Computes the number of points given by the selection.
                    this.computeScoreInc();

                    if (finalInc < this.scoreInc) {
                        // Saves the amount of points given by the new best move.
                        finalInc = this.scoreInc;
                        // Saves the coordinates of the new best move.
                        this.bestMove = new int[2];
                        this.bestMove[0] = i;
                        this.bestMove[1] = j;
                    }

                }

            }
        }

        this.grid.unselectTiles();
        // Saves the given maximum number of points.
        this.scoreInc = finalInc;
        this.notifyListeners(EventType.HINT, null);
    }

    /**
     * Selects tiles on the {@code Grid} of this {@code Game} based on the user's
     * choice and computes the amount of points made by the user's selection.
     * <p>
     * Relies on a {@link #computeScoreInc()} implementation to be used by
     * {@code Game} subclasses.
     * <p>
     * Also notifies all listeners of an {@link EventType#PRE_VALID} event.
     * 
     * @param r The row coordinate of the user's selection, as an {@code int}.
     * @param c The column coordinate of the user's selection, as an {@code int}.
     * @see #computeScoreInc()
     * @see #addListener(GameListener)
     * @see EventType
     */
    public final void selectionAt(int r, int c) {

        // Avoids issue when tiles have been previously selected
        // by other methods.
        if (this.selectedTilesNumber() != 0) {
            this.grid.unselectTiles();
        }

        this.grid.selectTiles(r, c);

        // Avoids notification of listeners for invalid moves.
        if (this.selectedTilesNumber() < 2)
            return;

        // Computes the amount of points generated by the selection.
        this.computeScoreInc();
        this.notifyListeners(EventType.PRE_VALID, null);

    }

    /**
     * Removes selected tiles on the {@code Grid} of this {@code Game}, increases
     * the score and checks for end game conditions.
     * <p>
     * Relies on {@link #checkWin()} and {@link #checkLose()} implementations to be
     * used by {@code Game} subclasses.
     * <p>
     * Also notifies all listeners of:
     * <ul>
     * <li>an {@link EventType#WIN} event if the user won the game,
     * <li>an {@link EventType#LOSE} event if the user lost the game,
     * <li>an {@link EventType#NEXT_MOVE} event if none of the above conditions is
     * met.
     * </ul>
     * 
     * @see #checkWin()
     * @see #checkLose()
     * @see #addListener(GameListener)
     * @see EventType
     */
    public final void validateSelection() {

        // Avoids notification of listeners for invalid moves.
        if (this.selectedTilesNumber() < 2)
            return;

        // Increases the score and removes selected tiles.
        this.bestMove = null;
        this.increaseScore();
        this.grid.removeSelection();

        // Checks for end game conditions.
        if (this.checkWin()) {
            this.state = GameState.WON;
            this.notifyListeners(EventType.WIN, this.winMessage);
        } else if (this.checkLose()) {
            this.state = GameState.LOST;
            this.notifyListeners(EventType.LOSE, this.loseMessage);
        } else {
            this.notifyListeners(EventType.NEXT_MOVE, null);
        }

    }

    /**
     * Notifies all listeners of an {@link EventType#INPUT} event,
     * with an additional text to send.
     * 
     * @param requestText Additionnal text to send, as a {@code String}.
     * @see #addListener(GameListener)
     * @see EventType
     */
    public final void inputRequest(String requestText) {
        this.notifyListeners(EventType.INPUT, requestText);
    }

    /**
     * Notifies all listeners of an {@link EventType#TEXT} event, with a specified
     * message.
     * 
     * @param text The message to send, as a {@code String}.
     * 
     * @see #addListener(GameListener)
     * @see EventType
     */
    public final void sendText(String text) {
        this.notifyListeners(EventType.TEXT, text);
    }

    /**
     * Notifies all listeners of an {@link EventType#LOAD} event.
     * 
     * @see #addListener(GameListener)
     * @see EventType
     */
    public final void load() {
        this.notifyListeners(EventType.LOAD, null);
    }

    /**
     * Notifies all listeners of an {@link EventType#SCOREBOARD} event.
     * 
     * @see #addListener(GameListener)
     * @see EventType
     */
    public final void loadScores() {
        this.notifyListeners(EventType.SCOREBOARD, null);
    }

    /**
     * Notifies all listeners of an {@link EventType#CLEAR_SCOREBOARD} event.
     * 
     * @see #addListener(GameListener)
     * @see EventType
     */
    public final void clearScores() {
        this.notifyListeners(EventType.CLEAR_SCOREBOARD, null);
    }

    /**
     * Returns the current level of difficulty the user is playing on, as a
     * {@code Difficulty} enumeration.
     * 
     * @return The current level of difficulty the user is playing on.
     * @see #difficulty
     */
    public final Difficulty getDifficulty() {
        return this.difficulty;
    }

    /**
     * Sets the level of difficulty of this {@code Game} to a given one.
     * 
     * @param difficulty The level of difficulty to set this {@code Game} with, as a
     *                   {@code Difficulty} enumeration.
     * @see #difficulty
     */
    public final void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Returns the current state of this {@code Game}, as a {@code GameState}
     * enumeration.
     * 
     * @return The current state of this {@code Game}.
     * @see #state
     */
    public final GameState getState() {
        return this.state;
    }

    /**
     * Returns the current score made by the user while playing, as an {@code int}.
     * 
     * @return The current score made by the user while playing.
     * @see #score
     */
    public final int getScore() {
        return this.score;
    }

    /**
     * Returns the score that the user could make by validating its move, as an
     * {@code int}.
     * 
     * @return the score that the user could make by validating its move.
     * 
     * @see #scoreInc
     */
    public final int getScoreInc() {
        return this.scoreInc;
    }

    /**
     * Returns the height of the {@code Grid} of this {@code Game} as an
     * {@code int}.
     * 
     * @return The height of the {@code Grid} of this {@code Game}.
     */
    public final int gridHeight() {
        return this.grid.getHeight();
    }

    /**
     * Returns the width of the {@code Grid} of this {@code Game} as an {@code int}.
     * 
     * @return The width of the {@code Grid} of this {@code Game}.
     */
    public final int gridWidth() {
        return this.grid.getWidth();
    }

    /**
     * Returns the best move the user can choose to gain the highest number of
     * points for the current round, as an array of two {@code int} values.
     * 
     * @return the best move the user can choose to gain the highest number of
     *         points for the current round.
     */
    public final int[] getBestMove() {
        return this.bestMove;
    }

    /**
     * Returns the game rules of this {@code Game}.
     * 
     * @return The game rules of this {@code Game}, as a {@code String}.
     * 
     * @see #gameRules
     */
    public final String getGameRules() {
        return this.gameRules;
    }

    /**
     * Returns copies of all the {@code Tile} objects within
     * the {@code Grid} of this {@code Game} as a {@code List<List<Tile>>}.
     * 
     * @return Copies of all the {@code Tile} objects within the {@code Grid} of
     *         this {@code Game}.
     */
    public final List<List<Tile>> tiles() {
        return this.grid.getTiles();
    }

    /**
     * Returns an unmodifiable list of the listeners belonging to this
     * {@code Game}, as a {@code List<GameListener>}.
     * <p>
     * NOTE: The list this method returns is unmodifiable, meaning that listeners
     * cannot be added, removed, or replaced. However, it does not avoid mutable
     * listeners to be modified. The {@link #getListeners()} method exists only
     * to allow the {@link FileManager} class to be able to recover the listeners
     * of this {@code Game} to add them back to a deserialized {@code Game},
     * but it is kind of a security breach. A solution would be to aggregate the
     * {@link FileManager} class in the {@code Game}, instead of making it a
     * listener, and to modifiy its methods to manipulate less data and achieve a
     * better encapsulation.
     * 
     * @return An unmodifiable list of the listeners belonging to this
     *         {@code Game}, or {@code null} if the list of listeners is
     *         {@code null}.
     * @see GameListener
     * @see FileManager
     */
    public final List<GameListener> getListeners() {
        if (this.gameListeners == null)
            return null;
        return List.copyOf(this.gameListeners);
    }

    /**
     * Increases the score of this {@code Game} by a known amount of points set by
     * {@link #scoreInc}.
     */
    protected final void increaseScore() {
        this.score += this.scoreInc;
        this.scoreInc = 0;
    }

    /**
     * Returns the number of selected {@code Tiles} object within the {@code Grid}
     * of this {@code Game} as an {@code int}.
     * 
     * @return The number of selected {@code Tiles} object within the {@code Grid}
     *         of this {@code Game}.
     */
    protected final int selectedTilesNumber() {
        return this.grid.getSelectedTilesNumber();
    }

    /**
     * Checks whether the {@code Grid} object of this {@code Game} is {@code null}
     * or not.
     * 
     * @return {@code true} if the {@code Grid} of this {@code Game} is not
     *         {@code null}, {@code false} otherwise.
     */
    public final boolean isGridInitialized() {
        return this.grid != null;
    }

    /**
     * Notifies all listeners of an {@link EventType#MUTE} event.
     * 
     * @see #addListener(GameListener)
     * @see EventType
     */
    public final void mute() {
        this.notifyListeners(EventType.MUTE, null);
    }

    /**
     * Notifies all the listeners of this {@code Game} for a particular event.
     * 
     * @param type The type of event as an {@code EventType} enumeration.
     * @param arg  An additionnal {@code String} argument related to the event.
     * 
     * @see #addListener(GameListener)
     * @see GameEvent
     * @see EventType
     */
    private void notifyListeners(EventType type, String arg) {
        if (this.gameListeners == null)
            return;
        GameEvent event = new GameEvent(type, arg);
        for (GameListener listener : this.gameListeners) {
            listener.update(event);
        }
    }

}
