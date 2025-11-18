import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import gridgames.*;

import java.io.File;
import java.util.function.Consumer;

/** 
 * Unit tests for the integration of FileManager
 */

public class FileManagerTest {

    private FileManager fileManager;
    private SameGame game;
    private GameController controller;

    @BeforeEach
    void setUp() {
        this.game = new SameGame();
        this.game.setDifficulty(Difficulty.EASY);
        this.game.makeNew();

        this.controller = new GameController(this.game);
        this.fileManager = new FileManager();
        this.fileManager.setController(this.controller);

        this.game.addListener(fileManager);
    }

    @AfterEach
    void cleanUp() {
        new File("tmp/save.dat").delete();
        new File("tmp/scores.dat").delete();
    }

    // -----------------------------------------------------
    // INTERNAL CLASS DummyListener WITH LAMBDA VERRIFICATION
    // -----------------------------------------------------

    private static class DummyListener implements GameListener {

        private Game game;
        private GameController controller;
        private final Consumer<String> checker;

        public DummyListener(Consumer<String> checker) {
            this.checker = checker;
        }

        @Override
        public void update(GameEvent event) {
            String arg = event.getArg();
            if (arg != null) {
                checker.accept(arg);
            }
        }

        @Override
        public void setGame(Game game) {
            this.game = game;
        }

        @Override
        public void setController(GameController controller) {
            this.controller = controller;
        }
    }

    // -----------------------------------------------------
    // TESTS
    // -----------------------------------------------------

    /**
     * Verify that the backup file is created after a move.
     */
    @Test
    void testSaveFileCreatedOnNextMove() {
        this.fileManager.update(new GameEvent(EventType.NEXT_MOVE, null));
        assertTrue(new File("tmp/save.dat").exists());
    }

    /**
     * Verify that the success message is sent when the game is loaded.
     */
    @Test
    void testLoadGameSuccessMessage() {
        // Creates an empty scores.dat file
        this.fileManager.update(new GameEvent(EventType.NEXT_MOVE, null));

        DummyListener dummy = new DummyListener(text -> {
            assertEquals("Game loaded!\n", text);
        });
        this.game.addListener(dummy);

        this.fileManager.update(new GameEvent(EventType.LOAD, null));
    }

    /**
     * Verify that the backup file is created after a reset.
     */
    @Test
    void testSaveFileCreatedOnReset() {
        this.fileManager.update(new GameEvent(EventType.RESET, null));
        assertTrue(new File("tmp/save.dat").exists());
    }

    /**
     * Verify that the message indicating that there is no backup file is 
     * sent when attempting to load without a file.
     */
    @Test
    void testNoSaveFileToLoad() {
        DummyListener dummy = new DummyListener(text -> {
            assertEquals("No game to load.\n", text);
        });
        this.game.addListener(dummy);

        this.fileManager.update(new GameEvent(EventType.LOAD, null));
    }

    /**
     * Verify that the message indicating the absence of scores is sent when 
     * loading the scoreboard if it does not exist.
     */
    @Test
    void testScoreboardLoadMessage() {
        DummyListener dummy = new DummyListener(text -> {
            // Since the scores.dat file does not exist at the beginning, we receive this message
            assertEquals("No scores to load.\n", text);
        });
        this.game.addListener(dummy);

        this.fileManager.update(new GameEvent(EventType.SCOREBOARD, null));
    }

    /**
     * Verify that the message indicating that the scoreboard is missing is 
     * sent when attempting to clean up without a file.
     */
    @Test
    void testClearScoreboardMessageWhenFileAbsent() {
        DummyListener dummy = new DummyListener(text -> {
            assertEquals("No scoreboard to clear!\n", text);
        });
        this.game.addListener(dummy);

        this.fileManager.update(new GameEvent(EventType.CLEAR_SCOREBOARD, null));
    }

    /**
     * Verify that the score file is deleted and the confirmation message 
     * is sent when cleaning the scoreboard.
     */
    @Test
    void testClearScoreboardDeletesFile() throws Exception {
        // Creates an empty scores.dat file
        new File("tmp/scores.dat").createNewFile();

        DummyListener dummy = new DummyListener(text -> {
            assertEquals("Scoreboard cleared!\n", text);
        });
        this.game.addListener(dummy);

        this.fileManager.update(new GameEvent(EventType.CLEAR_SCOREBOARD, null));
        assertFalse(new File("tmp/scores.dat").exists());
    }

    /**
     * Verify that the backup file is deleted after a defeat.
     */
    @Test
    void testSaveFileDeletedOnLose() {
        this.fileManager.update(new GameEvent(EventType.NEXT_MOVE, null));
        assertTrue(new File("tmp/save.dat").exists());

        this.fileManager.update(new GameEvent(EventType.LOSE, null));
        assertFalse(new File("tmp/save.dat").exists());
    }


    /**
     * Verify that the backup file is deleted after a win.
     */
    @Test
    void testSaveFileDeletedOnWin() {
        this.fileManager.update(new GameEvent(EventType.NEXT_MOVE, null));
        assertTrue(new File("tmp/save.dat").exists());

        this.fileManager.update(new GameEvent(EventType.WIN, null));
        assertFalse(new File("tmp/save.dat").exists());
    }

    /**
     * Verify that the score file is created after a win.
     */
    @Test
    void testScoresFileCreatedOnWin() {
        this.game.findBestMove();
        int[] move = this.game.getBestMove();
        this.game.selectionAt(move[0], move[1]);
        this.game.validateSelection();

        this.fileManager.update(new GameEvent(EventType.WIN, null));
        assertTrue(new File("tmp/scores.dat").exists());
    }
}
