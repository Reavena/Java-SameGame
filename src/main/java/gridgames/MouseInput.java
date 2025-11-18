package gridgames;

import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

/**
 * Class implementing the {@link GameInput} interface to provide mouse support
 * for a {@link GameController} object. Also implements {@link SwingIntegrated}
 * since mouse events are well supported by the {@code Swing} framework.
 * 
 * @see GameInput
 * @see GameController
 * @see SwingIntegrated
 */
public final class MouseInput implements GameInput, SwingIntegrated {

    /**
     * Constructs a newly allocated {@code MouseInput} object. The implementation is
     * empty. Could have been provided by Java.
     */
    public MouseInput() {
    }

    /** Empty implementation, not needed. */
    @Override
    public void waitAck() {
    }

    /** Returns {@code null}, not needed. */
    @Override
    public String getMessage(EventType e) {
        return null;
    }

    /** Returns {@code null}, not needed. */
    @Override
    public EventType getEventType() {
        return null;
    }

    /** Returns {@code null}, not needed. */
    @Override
    public Difficulty getDifficulty() {
        return null;
    }

    /** Returns {@code null}, not needed. */
    @Override
    public int[] getCoordinates() {
        return null;
    }

    /**
     * Returns mouse coordinates of the user's selection as an array of two
     * {@code int}. These coordinates are returned following the
     * specifications given by
     * {@link GameInput#getMouseCoordinates(MouseEvent)}.
     * 
     * @param e A {@code MouseEvent} triggered by the user with a mouse.
     * @return Coordinates of the user's selection as an {@code int[]}.
     */
    @Override
    public int[] getMouseCoordinates(MouseEvent e) {
        return new int[]{e.getX(), e.getY()};
    }

    /**
     * Creates a new {@code JButton} button object with a text on it.
     * 
     * @param text {@code String} parameter to display on the button.
     * @return A newly allocated {@code JButton} object.
     */
    @Override
    public JButton makeButton(String text) {
        JButton btn = new JButton(text);
        return btn;
    }

    /**
     * Creates a new {@code JToggleButton} button object with a text on it.
     * 
     * @param text {@code String} parameter to display on the button.
     * @return A newly allocated {@code JToggleButton} object.
     */
    @Override
    public JToggleButton makeToggleButton(String text) {
        JToggleButton btn = new JToggleButton(text);
        return btn;
    }

    /**
     * Creates a new {@code JComboBox<String>} combo-box object with a
     * list of option labels.
     * 
     * @param items {@code String[]} containing labels for the different options.
     * @return A newly allocated {@code JComboBox<String>} object.
     */
    @Override
    public JComboBox<String> makeComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        return comboBox;
    }

    /** Returns {@code null}, not needed. */
    @Override
    public JTextArea makeTextArea(int nRow, int nCol) {
        return null;
    }

    /**
     * Creates a new {@code JDialog} dialog window object
     * with the specified parameters. The user can close
     * the dialog window by pressing its `OK` button.
     * 
     * @param title Title of the dialog window as a {@code String}.
     * @param text  Text to display on the dialog window as a {@code String}.
     * @return A newly allocated {@code JDialog} object.
     */
    @Override
    public JDialog makeInfoDialog(String title, String text) {

        JOptionPane pane = new JOptionPane(text, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog(title);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        SwingUtilities.invokeLater(() -> {
            dialog.setVisible(true);
        });

        return dialog;

    }

    /** {@inheritDoc} */
    @Override
    public void showTwoChoicesDialog(String title,
                                     String text,
                                     String[] options,
                                     Runnable onFirstOption,
                                     Runnable onSecondOption) {

        SwingUtilities.invokeLater(() -> {

            int result = JOptionPane.showOptionDialog(
                null, text, title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]
            );

            if (result == 0 && onFirstOption != null) {
                onFirstOption.run();
            } else if (onSecondOption != null) {
                onSecondOption.run();
            }

        });

    }

}
