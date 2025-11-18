package gridgames;

import java.util.List;

/**
 * Class providing a basic text view of a {@link Game} object.
 * <p>
 * It displays the grid of the {@code Game} with row and column coordinates
 * modulo 10 on the borders, as well as its tiles, the score, the coordinates of
 * the best hit and the number of points it gives. Text-based notifications are
 * also displayed. {@code ConsoleView} is a listener of a {@code Game} instance,
 * and waits for its notification to update itself.
 * <p>
 * The text view of the {@code Game} is automatically printed in {@link System#out},
 * but can also be retrived with {@link #getGameView()}.
 */
public final class ConsoleView implements GameListener {

    /**
     * The {@code String} which holds the text representation. Is printed in
     * {@link System#out}.
     */
    private String gameView;
    /**
     * A separated {@code String} added to {@link #gameView} to display
     * end game messages.
     */
    private String endGameMessage;

    /** Instance of a {@code Game} to display. */
    private Game game;
    /**
     * Instance of a {@code GameController} to ask for user confirmation when a
     * message pops up.
     * 
     * @see GameController#waitAck()
     */
    private GameController controller;

    /**
     * {@code String[]} containing the symboles supported by this
     * {@code ConsoleView} to display the tiles in the grid,
     * according to there different values.
     * 
     * @see Tile
     */
    private static final String[] SYMBOLES = {"o", "x", "#", "$"};

    /**
     * Constructs a newly allocated {@code ConsoleView} object.
     * Does a minor setup of this object.
     */
    public ConsoleView() {
        this.endGameMessage = null;
    }

    /**
     * Returns a text-based view representing a binded {@code Game} object.
     * 
     * @return A {@code String} representing the view of a binded {@code Game}
     *         object.
     * @see #setGame(Game)
     */
    public String getGameView() {
        return this.gameView;
    }

    /** Sets the {@code Game} instance to read information on. */
    @Override
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Sets the {@code GameController} to interact with.
     * The {@code GameController} provides its method
     * {@link GameController#waitAck()}
     * and is used by this {@code ConsoleView} to ask the user for confirmation.
     * 
     * @see GameController#waitAck()
     */
    @Override
    public void setController(GameController controller) {
        this.controller = controller;
    }

    /**
     * Updates this {@code ConsoleView} according to a given {@code GameEvent}.
     * <ul>
     * <li>Displays end game messages on {@link EventType#LOSE} and
     * {@link EventType#WIN},
     * <li>Displays messages on {@link EventType#TEXT} (can be the scoreboard),
     * <li>Displays controller messages on {@link EventType#INPUT},
     * <li>Displays best move and associated number of points on
     * {@link EventType#HINT},
     * <li>Updates the grid display on {@link EventType#BEGIN},
     * {@link EventType#NEXT_MOVE} and {@link EventType#RESET}.
     * </ul>
     * 
     * @see GameEvent
     */
    @Override
    public void update(GameEvent event) {

        EventType type = event.getType();
        String arg = event.getArg();

        switch (type) {

            case BEGIN:
            case NEXT_MOVE:
            case HINT:
                this.buildBasicView(arg);
                break;

            case INPUT:
                if (this.game.getState() == GameState.PRESTART)
                    // Does not show the grid. It is the start menu.
                    this.gameView = arg;
                else this.buildBasicView(arg);
                break;

            case RESET:
                // Resets the end game message.
                this.endGameMessage = null;
                this.buildBasicView(arg);
                break;

            case TEXT:
                // Displays the message, and nothing else.
                this.gameView = arg;
                this.clear();
                this.show();
                // Wait for the user to confirm the message.
                this.controller.waitAck();
                return;

            case WIN:
            case LOSE:
                // Displays the end game message separately
                // to avoid being replaced by input messages.
                this.endGameMessage = arg;
                this.buildBasicView(null);
                break;
        
            default:
                return;
        }  

        // Prints gameView in System.out.
        this.clear();
        this.show();
        
    }

    /**
     * Builds the {@link #gameView} {@code String} with informations from
     * {@link #game}, to be displayed later. It mainly contains cosmetic
     * implementations.
     * 
     * @param arg A {@code String} parameter that completes the information shown by
     *            this {@code ConsoleView}.
     */
    private void buildBasicView(String arg) {

        String str = "    ";

        // Recovers information from the game.
        List<List<Tile>> gameTiles = this.game.tiles();
        int height = this.game.gridHeight();
        int width = this.game.gridWidth();
        int score = this.game.getScore();
        int scoreInc = this.game.getScoreInc();
        int[] bestHit = this.game.getBestMove();

        // Builds the header with scores, grid column coordinates,
        // and grid upper border.
        str += "Score: " + score + (scoreInc == 0 ? "" : " + " + scoreInc) +"\n    ";
        str += buildLine(width, null);
        str += "\n  ┏";
        str += buildLine(width, "━━");
        str += "━┓\n";

        // Builds the grid lateral borders with the tiles and row coordinates.
        for (int i = 0; i < height; i++) {
            str += i % 10 + " ┃ ";
            for (int j = 0; j < width; j++) {
                try {
                    int aValue = gameTiles.get(i).get(j).getValue();
                    str += SYMBOLES[aValue] + " ";
                } catch (IndexOutOfBoundsException e) {
                    str += "  ";
                }
            }
            str += "┃ " + i + "\n";
        }

        // Builds the footer with grid lower border, column coordinates,
        // and some additionnal information (hint, input message from,
        // end game message).
        str += "  ┗";
        str += buildLine(width, "━━");
        str += "━┛\n    ";
        str += buildLine(width, null);
        str += "\n";
        str += (
            bestHit == null ? 
            "\n" : "    Best hit: [" + bestHit[0] + ", " + bestHit[1] + "]\n"
        );
        
        str += (
            this.endGameMessage == null ? "\n" : this.endGameMessage
        );

        str += (
            arg == null ? "" : arg
        );

        this.gameView = str;
    }

    /**
     * Helper method to build a line of specified {@code String} with a given
     * lenght.
     * 
     * @param lenght  The lenght of the line, as an {@code int}.
     * @param symbole The {@code String} to build the line with.
     * @return A {@code String} that is a line made of the given {@code symbole}
     *         with the set lenght.
     *         If {@code symbole} is {@code null}, it returns a line of numbers
     *         modulo 10.
     */
    private static String buildLine(int lenght, String symbole) {
        String str = "";
        for (int i = 0; i < lenght; i++)
            str += (symbole == null ? i % 10 + " " : symbole);
        return str;
    }

    /**
     * Set the position of the cursor in the top left corner of the terminal.
     * It acts as clearing the terminal. It uses ANSI escape codes as follows:
     * 
     * <pre>
     * System.out.print("\033[H\033[2J");
     * </pre>
     * 
     * where:
     * <ul>
     * <li>{@code \033} is the escape character in octal form,
     * <li>{@code [H} is the cursor movement command, moving the cursor to the
     * top-left corner of the terminal,
     * <li>{@code [2J} is the clear screen command.
     * </ul>
     * <p>
     * It works on most standard terminals. Windows terminals might not support it.
     * It assumes that {@link System#out} is binded to the terminal.
     */
    private void clear() {
        System.out.print("\033[H\033[2J");
    }

    /**
     * Prints {@link #gameView} in {@link System#out}.
     */
    private void show() {
        System.out.print(this.gameView);
    }

}
