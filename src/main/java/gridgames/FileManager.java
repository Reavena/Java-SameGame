package gridgames;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class which adds saving and loading mechanics for a {@link Game} instance.
 * <p>
 * The {@code FileManager} class is responsible of serializing relevant data
 * belonging to a {@code Game} object, allowing the user to load an unfinished
 * game when starting to play. It also uses serialization to save a list of
 * high scores the user can review and clear.
 * <p>
 * The {@code FileManager} is a listener of a {@code Game} instance, and waits
 * for specific notifications to save and load the {@code Game} instance or
 * the high-scores list.
 * <p>
 * NOTE: This class might become aggregated or composed inside
 * the {@code Game} class in the futur, as it currently requires the method
 * {@link Game#getListeners()} to work, which is kind of unsafe to expose
 * to other listeners.
 * 
 * @see Game
 * @see Game#getListeners()
 */
public final class FileManager implements GameListener {

    /** File where to save a serialized {@code Game} instance. */
    private final File gameFile;
    /** File where to save a list of user's high scores. */
    private final File scoresFile;

    /**
     * A {@code Game} instance, used for serialization and deserialization
     * of the needed data to provide the loading and saving mechanics of
     * the {@code Game} and the list of high scores. Also provide the
     * original list of listeners to add back to a deserialized
     * {@code Game}. These listeners also needs the new reference
     * to the {@code Game} after deserialization.
     * 
     * @see Game
     */
    private Game game;
    /**
     * A {@code GameController} instance, which needs to have the
     * reference to the new {@code Game} after deserialization.
     * 
     * @see GameController
     */
    private GameController controller;

    /**
     * Constructs a newly allocated {@code FileManager} object and creates new
     * {@code File} instances for {@code Game} and high scores serialization.
     */
    public FileManager() {
        this.gameFile = new File("tmp/save.dat");
        this.scoresFile = new File("tmp/scores.dat");
    }

    /** {@inheritDoc} */
    @Override
    public void setGame(Game game) {
        this.game = game;
    }

    /** {@inheritDoc} */
    @Override
    public void setController(GameController controller) {
        this.controller = controller;
    }

    /**
     * Updates this {@code FileManager} according to a given {@code GameEvent}.
     * <ul>
     * <li>Saves its {@code Game} instance on {@link EventType#NEXT_MOVE} and
     * {@link EventType#RESET},
     * <li>Loads a {@code Game} instance on {@link EventType#LOAD},
     * <li>Loads the scoreboard file on {@link EventType#SCOREBOARD},
     * <li>Deletes the scoreboard file on {@link EventType#CLEAR_SCOREBOARD},
     * <li>Deletes the {@code Game} serialization file on {@link EventType#LOSE},
     * <li>Deletes the {@code Game} serialization file and adds a new score to the
     * scoreboard on {@link EventType#WIN}.
     * </ul>
     * 
     * @see GameEvent
     */
    @Override
    public void update(GameEvent event) {

        EventType type = event.getType();

        switch (type) {

            case NEXT_MOVE:
            case RESET:
                this.save();
                break;

            case LOAD:
                this.load();
                break;

            case SCOREBOARD:
                this.loadScores();
                break;

            case CLEAR_SCOREBOARD:
                this.deleteScoreFile();
                break;

            case LOSE:
                this.deleteSaveFile();
                break;

            case WIN:
                this.deleteSaveFile();
                this.addScore(this.game.getScore());
                break;

            default:
                break;

        }

    }

    /**
     * Serializes {@link #game} into {@link #gameFile}. Sends a message using
     * {@link Game#sendText(String)} if it failed:
     * 
     * <pre>
     *      {@code
     * "Failed to save the game.\n"
     * }
     * </pre>
     */
    private void save() {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(this.gameFile))) {
            out.writeObject(this.game);
        } catch (Exception e) {
            this.game.sendText("Failed to save the game.\n");
            return;
        }
    }

    /**
     * Deserializes a {@code Game} object from {@link #gameFile}. Adds the listeners
     * back to the new {@code Game} instance and changes there old internal
     * {@code Game} references to point to the new one. Also changes the old
     * internal {@code Game} reference of the {@code GameController}.
     * <p>
     * Sends messages using {@link Game#sendText(String)} depending on
     * the outcome:
     * <ul>
     * <li>{@code "No game to load.\n"} if the {@code .dat} serialization file does
     * not exists,
     * <li>{@code "Game loaded!\n"} if the {@code Game} has been successfully
     * deserialized,
     * <li>{@code "Error while loading game.\n"} if an error occurs trying to create
     * a {@code ObjectInputStream} to deserialize the {@code Game}.
     * </ul>
     * 
     */
    private void load() {

        if (!this.gameFile.exists()) {
            this.game.sendText("No game to load.\n");
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(this.gameFile))) {

            // Game deserialization.
            Game newGame = (Game) in.readObject();
            // Enables the Game to add listeners.
            newGame.makeListenable();
            List<GameListener> listeners = this.game.getListeners();

            // Adds the listeners of the old Game to the new Game.
            // Also changes there references to point to the new Game.
            if (listeners != null) {
                for (GameListener listener : listeners)
                    newGame.addListener(listener);
            }

            // Changes the GameController reference to point to the new Game.
            this.controller.setGame(newGame);

            newGame.sendText("Game loaded!\n");
            newGame.begin();

        } catch (Exception e) {
            this.game.sendText("Error while loading game.\n");
        }

    }

    /**
     * Serializes a list of high scores into {@link #scoresFile}.
     * 
     * @param scores The list of high scores to serialize, as a
     *               {@code List<Integer>}.
     */
    private void saveScores(List<Integer> scores) {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(this.scoresFile))) {
            out.writeObject(scores);
        } catch (Exception e) {
            this.game.sendText("Failed to save the score.\n");
            return;
        }
    }

    /**
     * Deserializes a list of high scores from {@link #scoresFile}.
     * Sends messages using {@link Game#sendText(String)} depending on
     * the outcome:
     * <ul>
     * <li>{@code "No scores to load.\n"} if the {@code .dat} serialization file
     * does not exists,
     * <li>A {@code String} containging the scores if the deserialization has been
     * successful,
     * <li>{@code "Error while loading scores.\n"} if an error occurs trying to
     * create a {@code ObjectInputStream} to deserialize the list of high scores.
     * </ul>
     * 
     * @see #loadScoresQuiet()
     */
    @SuppressWarnings("unchecked")
    private void loadScores() {

        if (!this.scoresFile.exists()) {
            this.game.sendText("No scores to load.\n");
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(this.scoresFile))) {
            List<Integer> scoreboard = (List<Integer>) in.readObject();
            this.game.sendText(this.buildScoreBoard(scoreboard));
        } catch (Exception e) {
            this.game.sendText("Error while loading scores.\n");
        }

    }

    /**
     * Deserializes a list of high scores from {@link #scoresFile}.
     * Does not send any messages.
     *
     * @return The list of high score as a {@code List<Integer>}. This list
     *         is empty if the {@code .dat} serialization file does not exists,
     *         or an error occurs trying to create a {@code ObjectInputStream}
     *         to deserialize the list of high scores.
     * @see #loadScores()
     */
    @SuppressWarnings("unchecked")
    private List<Integer> loadScoresQuiet() {

        if (!this.scoresFile.exists())
            return new ArrayList<>();

        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(this.scoresFile))) {
            return (List<Integer>) in.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }

    }

    /**
     * Builds a {@code String} of the list of high scores.
     * 
     * @param scoreboard The list of high scores, as a {@code List<Integer>}.
     * @return A {@code String} containing the list of high scores.
     */
    private String buildScoreBoard(List<Integer> scoreboard) {
        String str = "Scoreboard\n\n";
        for (int i = 0; i < scoreboard.size(); i++)
            str += "\t" + (i + 1) + ". " + scoreboard.get(i) + "\n";
        return str;
    }

    /**
     * Adds a score to the list of high scores and serializes it.
     * The list of high scores is sorted to display the scores from
     * the highest to the lowest.
     * 
     * @param score the score to serialize, as an {@code int}.
     */
    public void addScore(int score) {
        List<Integer> scores = loadScoresQuiet();
        scores.add(score);
        Collections.sort(scores, Collections.reverseOrder());
        saveScores(scores);
    }

    /**
     * Deletes the {@code .dat} file containing a serialized {@code Game}.
     */
    private void deleteSaveFile() {
        if (this.gameFile.exists()) {
            this.gameFile.delete();
        }
    }

    /**
     * Deletes the {@code .dat} file containing the serialized list of high
     * scores. Allows the user to reset the scoreboard.
     * <p>
     * Sends messages using {@link Game#sendText(String)} depending on
     * the outcome:
     * <ul>
     * <li>{@code "Scoreboard cleared!\n"} on success,
     * <li>{@code "No scoreboard to clear!\n"} if the {@code .dat}
     * serialization file does not exists.
     * </ul>
     */
    public void deleteScoreFile() {
        if (this.scoresFile.exists()) {
            this.scoresFile.delete();
            this.game.sendText("Scoreboard cleared!\n");
        } else {
            this.game.sendText("No scoreboard to clear!\n");
        }
    }

}
