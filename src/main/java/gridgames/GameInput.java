package gridgames;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;

import java.awt.event.MouseEvent;

/**
 * Interface for classes which gather user's input for the
 * {@link GameController} class.
 * <p>
 * This interface allows a {@code GameController} instance to manipulate a
 * {@link Game} object without knowing the implementation required to actually
 * gather the user's inputs. Thus the {@code GameController} has an attribute
 * implementing this interface which tranlsate the actual user's inputs in a
 * form of events that it can recognize, for most of its methods.
 * <p>
 * This events mechanism relies on the {@link EventType} enuerations.
 * 
 * @see EventType
 * @see GameController
 */
public interface GameInput {

    /**
     * Requests the user to confirm something.
     * E.g. confirm the reading of a displayed message.
     */
    public void waitAck();

    /**
     * Returns a {@code String} corresponding to a message this {@code GameInput}
     * associates to a given {@code EventType}.
     * E.g. a dialog to display when the user must make a choice between different
     * options.
     * 
     * @param e An input event type as an {@code EventType} enumeration.
     * @return A {@code String} associated with the {@code EventType}.
     */
    public String getMessage(EventType e);

    /**
     * Returns an event type {@code EventType} enumeration triggered by a user's input.
     * <p>
     * This method must not return {@code null} if {@link SwingIntegrated} IS NOT implemented
     * by this {@code GameInput} instance.
     * 
     * @return An {@code EventType} enumeration derived from a user's input.
     * @see SwingIntegrated
     */
    public EventType getEventType();

    /**
     * Returns a level of difficulty {@code Difficulty} enumeration choosen by the user.
     * <p>
     * This method must not return {@code null} if {@link SwingIntegrated} IS NOT implemented
     * by this {@code GameInput} instance.
     * 
     * @return A level of difficulty as {@code Difficulty} enumeration.
     * @see SwingIntegrated
     */
    public Difficulty getDifficulty();

    /**
     * Returns coordinates of the user's selection as an array of two {@code int}.
     * These coordinates need to be returned as follows:
     * 
     * <pre>
     * return new int[] {row, col};
     * </pre>
     * 
     * Where {@code row} is the row index of the {@code Grid} from a {@code Game}
     * instance,
     * and {@code col} is the column index of the {@code Grid} from a {@code Game}
     * instance.
     * <p>
     * This method must not return {@code null} if {@link SwingIntegrated} IS NOT
     * implemented by this {@code GameInput} instance.
     * 
     * @return Coordinates of the user's selection as an {@code int[]}.
     * @see SwingIntegrated
     */
    public int[] getCoordinates();

    /**
     * Returns mouse coordinates of the user's selection as an array of two
     * {@code int}. These coordinates need to be returned as follows:
     * 
     * <pre>
     * return new int[] {x, y};
     * </pre>
     * 
     * Where {@code x} is the horizontal position of the event relative to the mouse
     * and {@code y} is the vertical position of the event relative to the mouse.
     * 
     * @param e A {@code MouseEvent} triggered by the user with a mouse.
     * @return Coordinates of the user's selection as an {@code int[]}.
     * @see SwingIntegrated
     */
    public int[] getMouseCoordinates(MouseEvent e);

    /**
     * Creates a new {@code JButton} button object with a text on it.
     * If the implentation is not needed, this method can return
     * {@code null}.
     * 
     * @param text {@code String} parameter to display on the button.
     * @return A newly allocated {@code JButton} object.
     */
    public JButton makeButton(String text);

    /**
     * Creates a new {@code JToggleButton} button object with a text on it.
     * If the implentation is not needed, this method can return
     * {@code null}.
     * 
     * @param text {@code String} parameter to display on the button.
     * @return A newly allocated {@code JToggleButton} object.
     */
    public JToggleButton makeToggleButton(String text);

    /**
     * Creates a new {@code JComboBox<String>} combo-box object with a
     * list of option labels.
     * If the implentation is not needed, this method can return
     * {@code null}.
     * 
     * @param items {@code String[]} containing labels for the different options.
     * @return A newly allocated {@code JComboBox<String>} object.
     */
    public JComboBox<String> makeComboBox(String[] items);

    /**
     * Creates a new {@code JTextArea} text area object with the
     * specified parameters.
     * If the implentation is not needed, this method can return
     * {@code null}.
     * 
     * @param nRow Number of rows for the text area, as an {@code int}.
     * @param nCol Number of columns for the text area, as an {@code int}.
     * @return A newly allocated {@code JTextArea} object.
     */
    public JTextArea makeTextArea(int nRow, int nCol);

    /**
     * Creates a new {@code JDialog} dialog window object
     * with the specified parameters.
     * 
     * @param title Title of the dialog window as a {@code String}.
     * @param text  Text to display on the dialog window as a {@code String}.
     * @return A newly allocated {@code JDialog} object.
     */
    public JDialog makeInfoDialog(String title, String text);

    /**
     * Brings up a two-choices dialog set with the specified parameters.
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
                                     Runnable onSecondOption);

}