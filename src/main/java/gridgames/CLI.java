package gridgames;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.awt.Dialog;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;

import java.awt.event.MouseEvent;

/**
 * Class implementing the {@link GameInput} interface to provide a default
 * terminal-based support for a {@link GameController} object. The user
 * enters predefined characters to make choices, as well as numbers,
 * from {@link System#in}.
 * <p>
 * The name of this class is derivated from
 * <em>Command Line Interface</em>.
 * 
 * @see GameInput
 * @see GameController
 */
public class CLI implements GameInput {
    

    /**  Text {@code Scanner} to get user's inputs from {@link System#in}. */
    private static Scanner read;

    /**
     * {@code String} to send when the user has to choose a level of difficulty.
     * This {@code String} is generatedusing
     * {@link #makeDifficultyDialAndOpt()}.
     */
    private String difficultyDial;

    /**
     * {@code Map} to associate specific {@code String} tokens for the user to
     * enter, to specific levels of difficulty {@code Difficulty} enumerations.
     */
    private Map<String, Difficulty> difficultiesMap;

    /**
     * {@code JDialog} dialog window to display on graphical user interface
     * when a message pops up.
     */
    private JDialog dialog;

    /** {@code String} to send as game start instructions. */
    private final static String START_MENU_DIAL
        = "Type:\n"
        + "\t`l` to load last game\n"
        + "\t`p` to play a new game\n"
        + "\t`sb` to see scoreboard\n"
        + "\t`c` to clear scoreboard\n";

    /** {@code String} to send as playing instructions. */
    private final static String GAME_INPUT_DIAL
        = "Type:\n"
        + "\t`s` to select a tile\n"
        + "\t`h` to have an hint\n"
        + "\t`g` for game rules\n"
        + "\t`e` to exit\n";

    /** {@code String} to send as en game instructions. */
    private final static String END_GAME_DIAL
        = "Type `n` for a new game, or `e` to exit.\n";

    /** {@code String} to send as move selection instructions. */
    private final static String TILE_INPUT_DIAL
        = "Input a row, then a column.\n";

    /**
     * Constructs a newly allocated {@code CLI} object.
     * Also makes a minor initilization setup.
     */
    public CLI() {
        this.difficultiesMap = new HashMap<>();
        CLI.read = new Scanner(System.in);
        this.difficultyDial = this.makeDifficultyDialAndOpt();
        this.dialog = null;
    }

    /**
     * Returns a {@code String} corresponding to a message this {@code GameInput}
     * associates to a given {@code EventType}.
     * These messages are instructions for the user, so that he can input
     * specific tokens to make specific choices.
     * Currently supported event types are:
     * <ul>
     * <li>{@link EventType#INIT},
     * <li>{@link EventType#RESET},
     * <li>{@link EventType#BEGIN},
     * <li>{@link EventType#NEXT_MOVE},
     * <li>{@link EventType#INPUT},
     * <li>{@link EventType#WIN},
     * <li>{@link EventType#LOSE}.
     * </ul>
     * 
     * @param e An input event type as an {@code EventType} enumeration.
     * @return A {@code String} associated with the {@code EventType},
     *         or {@code null} if the event type is not supported.
     */
    @Override
    public String getMessage(EventType e) {

        switch (e) {

            case INIT:
                return START_MENU_DIAL;
        
            case RESET:
            case BEGIN:
                return this.difficultyDial;

            case NEXT_MOVE:
                return GAME_INPUT_DIAL;

            case INPUT:
                return TILE_INPUT_DIAL;

            case WIN:
            case LOSE:
                return END_GAME_DIAL;

            default:
                return null;

        }

    }

    /**
     * Returns an event type {@code EventType} enumeration triggered by a user's
     * input.
     * This method does not return until the user entered a valid predefined token.
     * The returned {@code EventType} objects are associated with
     * the input tokens as follows:
     * <ul>
     * <li>Returns {@link EventType#LOAD} on {@code "l"} input token,
     * <li>{@link EventType#BEGIN} on {@code "p"},
     * <li>{@link EventType#SCOREBOARD} on {@code "sb"},
     * <li>{@link EventType#CLEAR_SCOREBOARD} on {@code "c"},
     * <li>{@link EventType#INPUT} on {@code "s"},
     * <li>{@link EventType#HINT} on {@code "h"},
     * <li>{@link EventType#TEXT} on {@code "g"},
     * <li>{@link EventType#EXIT} on {@code "e"},
     * <li>{@link EventType#RESET} on {@code "n"}.
     * </ul>
     * <p>
     * The tokens are the first letters of the instruction key words
     * sent by this {@code CLI} instance.
     * 
     * @return One of the presented {@code EventType} enumeration.
     */
    @Override
    public EventType getEventType() {

        while (true) {

            String userInput = this.readString();

            switch (userInput) {

                case "l":
                    return EventType.LOAD;

                case "p":
                    return EventType.BEGIN;

                case "sb":
                    return EventType.SCOREBOARD;

                case "c":
                    return EventType.CLEAR_SCOREBOARD;

                case "s":
                    return EventType.INPUT;

                case "h":
                    return EventType.HINT;

                case "g":
                    return EventType.TEXT;

                case "e":
                    return EventType.EXIT;

                case "n":
                    return EventType.RESET;
            
                default:
                    break;

            }
        }
        
    }

    /**
     * Returns a level of difficulty {@code Difficulty} enumeration choosen
     * by the user.
     * <p>
     * This method does not return until the user entered a valid predefined token.
     * The default supported tokens are given by the lower-case first letter of
     * each {@code Difficulty} enumerations. For example:
     * <ul>
     * <li>{@code e} for {@link Difficulty#EASY},
     * <li>{@code m} for {@link Difficulty#MEDIUM},
     * <li>{@code h} for {@link Difficulty#HARD}.
     * </ul>
     * This is done automatically by {@code CLI} helper methods.
     * 
     * @return A level of difficulty as a {@code Difficulty} enumeration.
     */
    @Override
    public Difficulty getDifficulty() {

        while (true) {
            String userInput = this.readString();
            if (this.difficultiesMap.containsKey(userInput)) {
                return this.difficultiesMap.get(userInput);
            } else {
                continue;
            }
        }

    }

    /**
     * Returns coordinates of the user's selection as an array of two {@code int}.
     * These coordinates are returned following the specifications
     * given by {@link GameInput#getCoordinates()}.
     * 
     * @return Coordinates of the user's selection as an {@code int[]}.
     */
    @Override
    public int[] getCoordinates() {

        int targetRow = this.readInt();
        int targetCol = this.readInt();
        return new int[]{targetRow, targetCol};
    }

    /**
     * Waits for the user to press the <em>Enter</em> key, e.g to confirm something.
     * Also closes any dialog window created by this {@code CLI} object.
     * 
     * @see #makeInfoDialog(String, String)
     * @see GameController#waitAck()
     */
    @Override
    public void waitAck() {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            br.readLine();
        } catch (Exception e) {
        }

        if (this.dialog != null) {
            this.dialog.dispose();
            this.dialog = null;
        }

    }

    /**
     * Returns a {@code String} containg instructions for the user to choose
     * any level of difficulty for the game. The {@code String} is built
     * uppon the name of the {@code Difficulty} enumerations in lower case.
     * The tokens the user can type to make a choice are the first letters,
     * in lower case, of the name of the {@code Difficulty} enumerations.
     * <p>
     * For example, with {@link Difficulty#EASY},
     * {@link Difficulty#MEDIUM} and {@link Difficulty#HARD}, it would
     * give:
     * 
     * <pre>
     *  `e` for easy difficulty (2 colors)
     *  `m` for medium difficulty (3 colors)
     *  `h` for hard difficulty (4 colors)
     * </pre>
     * 
     * It is used to build the {@link #difficultyDial} {@code String}.
     * <p>
     * This method also associates the tokens with the corresponding
     * {@code Difficulty} objects inside {@link #difficultiesMap}.
     * 
     * @return A {@code String} containg instructions for the user to choose
     *         any level of difficulty for the game.
     */
    private String makeDifficultyDialAndOpt() {

        String str = "Type:\n";

        for (Difficulty d : Difficulty.values()) {
            String firstLetter = d.name().substring(0, 1).toLowerCase();
            str += "\t`"
                + firstLetter
                + "` for "
                + d.name().toLowerCase()
                + " difficulty ("
                + (d.ordinal() + 2)
                + " colors)\n";
            difficultiesMap.put(firstLetter, d);
        }
        return str;
    }

    /**
     * Returns a trimmed and lower-cased {@code String} token from
     * a user's input captured in {@link System#in}.
     * 
     * @return A trimmed and lower-cased {@code String} token.
     */
    private String readString() {
        String userInput = CLI.read.next().trim().toLowerCase();
        return userInput;
    }

    /**
     * Returns an {@code int} token from
     * a user's input captured in {@link System#in}.
     * 
     * @return An {@code int} token.
     */
    private int readInt() {
        String userInput = CLI.read.next().trim().toLowerCase();
        int integer = 0;
        try {
            integer = Integer.parseInt(userInput);
        } catch (NumberFormatException e) {
            return -1;
        }
        return integer;
    }

    /** Returns {@code null}, not needed. */
    @Override
    public JButton makeButton(String text) {
        return null;
    }

    /** Returns {@code null}, not needed. */
    @Override
    public JToggleButton makeToggleButton(String text) {
        return null;
    }

    /** Returns {@code null}, not needed. */
    @Override
    public JComboBox<String> makeComboBox(String[] items) {
        return null;
    }

    /**
     * Creates a new {@code JTextArea} text area object with the
     * specified parameters. Allows for graphical user interface
     * support, to display intructions.
     * 
     * @param nRow Number of rows for the text area, as an {@code int}.
     * @param nCol Number of columns for the text area, as an {@code int}.
     * @return A newly allocated {@code JTextArea} object.
     */
    @Override
    public JTextArea makeTextArea(int nRow, int nCol) {
        JTextArea textArea = new JTextArea(nRow, nCol);
        return textArea;
    }

    /**
     * Creates a new {@code JDialog} dialog window object
     * with the specified parameters. The dialog window
     * does not provide any closing button, but it will
     * be automatically closed when {@link #waitAck()}
     * completes.
     * 
     * @param title Title of the dialog window as a {@code String}.
     * @param text  Text to display on the dialog window as a {@code String}.
     * @return A newly allocated {@code JDialog} object.
     * 
     * @see #waitAck()
     * @see GameController#waitAck()
     */
    @Override
    public JDialog makeInfoDialog(String title, String text) {

        JOptionPane pane = new JOptionPane(
            text, JOptionPane.INFORMATION_MESSAGE,
            JOptionPane.DEFAULT_OPTION, null,
            new Object[] {}, null
        );

        JDialog dialog = pane.createDialog(title);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setModalityType(Dialog.ModalityType.MODELESS);

        dialog.setVisible(true);
        this.dialog = dialog;
        return this.dialog;
    }

    /** Empty implementation, not needed. */
    @Override
    public void showTwoChoicesDialog(String title, 
                                     String text,
                                     String[] options,
                                     Runnable onFirstOption,
                                     Runnable onSecondOption) { }
                                
    /** Returns {@code null}, not needed. */                                 
    @Override
    public int[] getMouseCoordinates(MouseEvent e) {
        return null;
    }


}
