package gridgames;

/**
 * Enumeration type which helps to keep track of the state of the current game.
 */
public enum GameState {
    /** State of the game when just instanciated. */
    PRESTART,
    /**
     * State of the game when not terminated.
     * <p>
     * i.e. the user could make another move.
     */
    ONGOING,
    /** State of the game when terminated and the user won. */
    WON,
    /** State of the game when terminated and the user lost. */
    LOST;
}
