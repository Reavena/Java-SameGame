package gridgames;

/**
 * Tagging interface for classes implementing the {@link GameInput} interface,
 * but which are related to events supported by the {@code Swing} framework.
 * E.g. mouse-related events like for the {@link MouseInput} class,
 * or key-related events.
 * <p>
 * These classes have methods from the {@link GameInput} interface which are
 * more convenient to use within the {@code Swing} framework. Tagging them with
 * the {@link SwingIntegrated} interface tells a {@link GameController}
 * object not to create a specific loop to poll for user inputs. Instead, classes
 * related to the {@code Swing} framework, e.g. {@link GraphicalView},
 * can call {@link GameController} methods to handle user inputs.
 */
public interface SwingIntegrated {

}
