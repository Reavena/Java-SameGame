package gridgames;

/**
 * Class representing an event in the game.
 * <p>
 * Events are a way for classes interacting with the game to communicate
 * with each other. They can be seen as signals that each class could
 * interpret in its own way.
 * 
 * @see EventType
 */
public final class GameEvent {

    /** The type of the occuring event as an {@code EventType}. */
    private final EventType type;
    /** An additional {@code String} argument that could be read as a message. */
    private final String arg;

    /**
     * Constructs a newly allocated {@code GameEvent} object based on an event type
     * {@code EventType} enumeration, and a {@code String} argument which could be
     * interpreted as a message to carry.
     * 
     * @param type The type of event.
     * @param arg  An additionnal argument related to the event.
     */
    public GameEvent(EventType type, String arg) {
        this.type = type;
        this.arg = arg;
    }

    /**
     * Returns the type of event belonging to this {@code GameEvent} object as an
     * {@code EventType}.
     * 
     * @return The type of event belonging to this {@code GameEvent} object.
     */
    public EventType getType() {
        return this.type;
    }

    /**
     * Returns the argument belonging to this {@code GameEvent} object as a
     * {@code String}.
     * 
     * @return The argument belonging to this {@code GameEvent} object.
     */
    public String getArg() {
        return this.arg;
    }
}
