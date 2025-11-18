package gridgames;

/**
 * Enumeration type which helps to represent different type of events that could
 * occur during the life of the game.
 * <p>
 * Events are a way for classes interacting with the game to communicate
 * with each other. They can be seen as signals that each class could
 * interpret in its own way.
 * 
 * @see GameEvent
 */
public enum EventType {
    /** The user can play its next move. */
    NEXT_MOVE,
    /** A text to be read is sent. */
    TEXT,
    /**
     * The user can (or requested to) start to interact with the game, but cannot
     * play yet.
     */
    INIT,
    /** The user can (or requested to) start to play. */
    BEGIN,
    /** The user requested to load a previous unfinished game. */
    LOAD,
    /** The user requested to see the scoreboard. */
    SCOREBOARD,
    /** The user requested to clear the scoreboard. */
    CLEAR_SCOREBOARD,
    /** The user requested to reset the current game. */
    RESET,
    /** The user requested to select its next move. */
    INPUT,
    /** The move made by the user is cached and is ready to be validated. */
    PRE_VALID,
    /** The user requested to have some help to choose its next move. */
    HINT,
    /** The user won the game. */
    WIN,
    /** The user lost the game. */
    LOSE,
    /** The user requested to quit the game. */
    EXIT,
    /** The user requested to mute (or unmute) the sound effects. */
    MUTE;
}
