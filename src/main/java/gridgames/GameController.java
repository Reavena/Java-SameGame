package gridgames;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;

/**
 * Class which handles modifications of a {@link Game} instance.
 * <p>
 * It acts as a bridge between the user and the classes that need the user
 * to do something before they continue executing, or simply need the user
 * to interact with them through elements like buttons.
 * <p>
 * The {@code GameController} methods execute public methods
 * of a {@code Game} instance depending on user's choices,
 * to consistently modify the state of the game. They all rely
 * on an instance of {@link GameInput} to get user's inputs
 * as well as custom messages to send through the game
 * notification system.
 * <p>
 * NOTE: The {@code GameController} instance is th "Controller" of the
 * Model-View-Controller pattern used to design this project.
 * 
 * @see GameInput
 */
public final class GameController {

    /** Instance of a {@code GameInput} to get user's input and custom messages. */
    private GameInput input;
    /** Instance of a {@code Game} to execute its methods and modify its state.  */
    private Game game;

    /**
     * Constructs a newly allocated {@code GameController} object by giving
     * a {@code Game} instance to interact with. This {@code GameController}
     * uses a {@link CLI} as default {@link GameInput} instance, to get
     * user's inputs and associated messages.
     * 
     * @param game The {@code Game} instance to interact with.
     * @see GameInput
     * @see CLI
     */
    public GameController(Game game) {
        this.setGame(game);
        this.input = new CLI();
    }

    /**
     * Sets the instance of {@code Game} this {@code GameController} can interact
     * with.
     * 
     * @param game The instance of {@code Game} to set this {@code GameController}
     *             with.
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Sets the instance of {@code GameInput} this {@code GameController}
     * can use to get user's inputs and information messages.
     * 
     * @param input The instance of {@code GameInput} to set this
     *              {@code GameController} with.
     */
    public void setInput(GameInput input) {
        this.input = input;
    }

    /**
     * Calls {@link Game#validateSelection()} when the user clicks with
     * a mouse on a {@code Swing} graphical user interface component.
     * <p>
     * Relies on the
     * {@link GameInput#getMouseCoordinates(MouseEvent)}
     * implementation of the {@code GameInput} instance of this {@code GameController}.
     * <p>
     * The mouse coordinates need to be normalized to be interpreted as
     * row and column coordinates. This can be achieved using the
     * {@code int normalizer} parameter, which does the following:
     * 
     * <pre>
     * int col = x / normalizer;
     * int row = y / normalizer;
     * </pre>
     * 
     * Where {@code x} and {@code y} are the mouse coordinates,
     * {@code col} and {@code row} are the column and row coordinates.
     * 
     * @param e          {@link MouseEvent} related to user's interaction with a
     *                   mouse.
     * @param normalizer {@code int} coefficient to normalize the mouse coordinates
     *                   with.
     * @see Game#validateSelection()
     */
    public void mouseClicked(MouseEvent e, int normalizer) {

        int[] pos = this.input.getMouseCoordinates(e);
        if (pos == null)
            return;

        normalizer = (normalizer < 1 ? 1 : normalizer);

        int col = pos[0] / normalizer;
        int row = pos[1] / normalizer;

        if (row >= 0 && row < this.game.gridHeight() &&
                col >= 0 && col < this.game.gridWidth()) {

            this.game.validateSelection();
        }

    }

    /**
     * Calls {@link Game#selectionAt(int, int)} when the user moved
     * a mouse on a {@code Swing} graphical user interface component.
     * <p>
     * Relies on the
     * {@link GameInput#getMouseCoordinates(MouseEvent)}
     * implementation of the {@code GameInput} instance of this {@code GameController}.
     * <p>
     * The mouse coordinates need to be normalized to be interpreted as
     * row and column coordinates. This can be achieved using the
     * {@code int normalizer} parameter, which does the following:
     * 
     * <pre>
     * int col = x / normalizer;
     * int row = y / normalizer;
     * </pre>
     * 
     * Where {@code x} and {@code y} are the mouse coordinates,
     * {@code col} and {@code row} are the column and row coordinates.
     * 
     * @param e          {@link MouseEvent} related to user's interaction with a
     *                   mouse.
     * @param normalizer {@code int} coefficient to normalize the mouse coordinates
     *                   with.
     * @see Game#selectionAt(int, int)
     */
    public void mouseMoved(MouseEvent e, int normalizer) {

        int[] pos = this.input.getMouseCoordinates(e);
        
        if (pos == null)
            return;

        int col = pos[0] / normalizer;
        int row = pos[1] / normalizer;

        if (row >= 0 && row < this.game.gridHeight() &&
                col >= 0 && col < this.game.gridWidth()) {

            this.game.selectionAt(row, col);
        }

    }

    /**
     * Initializes this {@code GameController} to interact with its {@code Game} instance.
     * It first sends a {@link EventType#INIT} event type through the game to update its
     * listeners, then starts to poll for user inputs ONLY if its {@code GameInput}
     * instance does not implement the {@link SwingIntegrated} interface.
     * <p>
     * If so, this method
     * relies on the implementation of several methods from
     * the {@code GameInput} instance of this {@code GameController}:
     * <ul>
     * <li>{@link GameInput#getMessage(EventType)},
     * <li>{@link GameInput#getEventType()},
     * <li>{@link GameInput#getDifficulty()},
     * <li>{@link GameInput#getCoordinates()}.
     * </ul>
     * 
     * @see SwingIntegrated
     */
    public void run() {

        this.game.init();

        // No need for a polling loop in that case.
        if (this.input instanceof SwingIntegrated)
            return;

        while (true) {

            GameState state = this.game.getState();

            if (state == GameState.PRESTART) {

                // Starting dialog, before the user can play the game.

                // Asks the input if it has a specific dialog
                // that could be sent.
                String startMessage = this.input.getMessage(EventType.INIT);
                // Sends the dialog, e.g. to be displayed.
                this.game.inputRequest(startMessage);
                // Gets a user input.
                EventType e = this.input.getEventType();

                switch (e) {

                    case LOAD:
                        // User asked to load a previous game.
                        this.game.load();
                        break;

                    case BEGIN:
                        // User wants to begin a new game.
                        String difficultyMessage = this.input.getMessage(e);
                        this.game.inputRequest(difficultyMessage);
                        // Gets the user's choice for the level of difficulty.
                        Difficulty d = this.input.getDifficulty();
                        // Creates the game.
                        this.game.setDifficulty(d);
                        this.game.makeNew();
                        break;

                    case SCOREBOARD:
                        // The user want to see the scoreboard.
                        this.game.loadScores();
                        break;

                    case CLEAR_SCOREBOARD:
                        // The user wants to clear the scoreboard.
                        this.game.clearScores();
                        break;

                    default:
                        // This method does not handle other requests here.
                        break;

                }

            }

            else if (state == GameState.ONGOING) {

                // Playing dialog, when the user can play the game.

                String inputMessage = this.input.getMessage(
                    EventType.NEXT_MOVE
                );
                this.game.inputRequest(inputMessage);
                EventType e = this.input.getEventType();

                switch (e) {

                    case INPUT:
                        // The user wants to select its move.
                        String selectMessage = this.input.getMessage(e);
                        this.game.inputRequest(selectMessage);
                        // Gets the user's selection.
                        int[] pos = this.input.getCoordinates();
                        this.game.selectionAt(pos[0], pos[1]);
                        // Validations.
                        this.game.validateSelection();
                        break;

                    case HINT:
                        // The user requested a hint.
                        this.game.findBestMove();
                        break;

                    case TEXT:
                        // The only text a user can ask for are the
                        // game rules.
                        this.game.sendText(this.game.getGameRules());
                        break;

                    case EXIT:
                        // The user does not want to play anymore.
                        this.game.exit();
                        System.exit(0);
                        break;

                    default:
                        break;

                }

            } else {

                // End game dialog, when the user won or lost.

                EventType lastEvent = (
                    state == GameState.WON ? EventType.WIN : EventType.LOSE
                );
                String endMessage = this.input.getMessage(lastEvent);
                this.game.inputRequest(endMessage);
                EventType e = this.input.getEventType();

                switch (e) {

                    case RESET:
                        // The user wants to make another game.
                        String difficultyMessage = this.input.getMessage(e);
                        this.game.inputRequest(difficultyMessage);
                        Difficulty d = this.input.getDifficulty();
                        this.game.reset(d);
                        break;

                    case EXIT:
                        this.game.exit();
                        System.exit(0);
                        break;

                    default:
                        break;

                }

            }

        }

    }

    /**
     * Asks the user to confirm depending on the strategy sets by the
     * {@code GameInput} instance of this {@code GameController}.
     * <p>
     * Relies on the
     * {@link GameInput#waitAck()}
     * implementation of the {@code GameInput} instance of this {@code GameController}.
     */
    public void waitAck() {
        this.input.waitAck();
    }

    /**
     * Exits the {@code Game} instance, disposes a {@code JFrame} object from
     * {@code Swing}, and calls {@code System.exit(0)}.
     * 
     * @param frame The {@code JFrame} to call {@link JFrame#dispose()} on.
     */
    public void close(JFrame frame) {
        this.game.exit();
        frame.dispose();
        System.exit(0);
    }

    /**
     * Creates a new {@code JButton} button object with the specified parameters.
     * <p>
     * Relies on the {@link GameInput#makeButton(String)}
     * implementation of the {@code GameInput} instance of this
     * {@code GameController}.
     * If it returns {@code null}, the result of this method is an invisible object.
     * 
     * @param text       {@code String} parameter to display on the button.
     * @param fg         Foreground {@code Color} of the button.
     * @param bg         Background {@code Color} of the button.
     * @param paintFocus {@code boolean} to set the {@code paintFocus} property of
     *                   the button.
     * @param l          Adds an {@code ActionListener} to the button.
     * @return A newly allocated {@code JButton} object.
     */
    public JButton makeButton(String text,
                              Color fg,
                              Color bg,
                              boolean paintFocus,
                              ActionListener l) {

        JButton btn = this.input.makeButton(text);

        if (btn == null) {
            // Makes an invisible button.
            btn = new JButton();
            btn.setVisible(false);
            return btn;
        }

        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(paintFocus);
        btn.addActionListener(l);
        return btn;

    }

    /**
     * Creates a new {@code JToggleButton} button object with the
     * specified parameters.
     * <p>
     * Relies on the {@link GameInput#makeToggleButton(String)}
     * implementation of the {@code GameInput} instance of this
     * {@code GameController}. If it returns {@code null},
     * the result of this method is an invisible object.
     * 
     * @param text       {@code String} parameter to display on the button.
     * @param fg         Foreground {@code Color} of the button.
     * @param bg         Background {@code Color} of the button.
     * @param paintFocus {@code boolean} to set the {@code paintFocus} property of
     *                   the button.
     * @param l          Adds an {@code ActionListener} to the button.
     * @return A newly allocated {@code JToggleButton} object.
     */
    public JToggleButton makeToggleButton(String text,
                                          Color fg,
                                          Color bg,
                                          boolean paintFocus,
                                          ActionListener l) {

        JToggleButton btn = this.input.makeToggleButton(text);

        if (btn == null) {
            // Makes an invisible button.
            btn = new JToggleButton();
            btn.setVisible(false);
            return btn;
        }

        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(paintFocus);
        btn.addActionListener(l);
        return btn;

    }

    /**
     * Creates a new {@code JComboBox<String>} combo-box object with the
     * specified parameters.
     * <p>
     * Relies on the {@link GameInput#makeComboBox(String[])}
     * implementation of the {@code GameInput} instance of this
     * {@code GameController}. If it returns {@code null},
     * the result of this method is an invisible object.
     * 
     * @param items {@code String[]} containing labels for the different options.
     * @param fg    Foreground {@code Color} of the combo-box.
     * @param bg    Background {@code Color} of the combo-box.
     * @param l     Adds an {@code ActionListener} to the combo-box
     * @return A newly allocated {@code JComboBox<String>} object.
     */
    public JComboBox<String> makeComboBox(String[] items,
                                          Color fg,
                                          Color bg,
                                          ActionListener l) {

        JComboBox<String> comboBox = this.input.makeComboBox(items);

        if (comboBox == null) {
            // Makes an invisible combo-box.
            comboBox = new JComboBox<String>();
            comboBox.setVisible(false);
            return comboBox;
        }

        comboBox.setForeground(fg);
        comboBox.setBackground(bg);
        comboBox.addActionListener(l);
        return comboBox;

    }

    /**
     * Creates a new {@code JTextArea} text area object with the
     * specified parameters. It is not editable.
     * <p>
     * Relies on the {@link GameInput#makeTextArea(int, int)}
     * implementation of the {@code GameInput} instance of
     * this {@code GameController}. If it returns {@code null},
     * the result of this method is an invisible object.
     * 
     * @param nRow Number of rows for the text area, as an {@code int}.
     * @param nCol Number of columns for the text area, as an {@code int}.
     * @return A newly allocated {@code JTextArea} object.
     */
    public JTextArea makeTextArea(int nRow, int nCol) {

        JTextArea textArea = this.input.makeTextArea(nRow, nCol);

        if (textArea == null) {
            // Makes an invisible text area.
            textArea = new JTextArea();
            textArea.setVisible(false);
            return textArea;
        }

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        return textArea;

    }

    /**
     * Creates a new {@code JDialog} dialog window object
     * with the specified parameters.
     * <p>
     * Relies on the {@link GameInput#makeInfoDialog(String, String)}
     * implementation of the {@code GameInput} instance of
     * this {@code GameController}.
     * 
     * @param title Title of the dialog window as a {@code String}.
     * @param text  Text to display on the dialog window as a {@code String}.
     * @return A newly allocated {@code JDialog} object.
     */
    public JDialog makeInfoDialog(String title, String text) {
        return this.input.makeInfoDialog(title, text);
    }

    /**
     * Brings up a two-choices dialog set with the specified parameters.
     * Relies on the
     * {@link GameInput#showTwoChoicesDialog(String, String, String[], Runnable, Runnable)}
     * implementation of the {@code GameInput} instance of this
     * {@code GameController}.
     * 
     * @param title          Title of the dialog as a {@code String}.
     * @param text           Main text to display on the dialog as a {@code String}.
     * @param options        List of option labels as a {@code String[]}.
     * @param onFirstOption  Operation associated with the first option as a
     *                       {@code Runnable}.
     * @param onSecondOption Operation associated with the second option as a
     *                       {@code Runnable}.
     */
    public void showTwoChoicesDialog(String title,
                                     String text,
                                     String[] options,
                                     Runnable onFirstOption,
                                     Runnable onSecondOption) {

        this.input.showTwoChoicesDialog(
            title,
            text,
            options,
            onFirstOption,
            onSecondOption
        );

    }

}
