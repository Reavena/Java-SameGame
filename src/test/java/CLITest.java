import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import gridgames.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/** 
 * Unit tests for the integration of CLI
 */

public class CLITest {

    private final InputStream originalIn = System.in;

    /** Restores the original standard input after each test.
     *
     * Some tests redefine System.in to simulate user input via a ByteArrayInputStream.
     * It is therefore crucial to restore System.in after each test so as not to affect other tests
     * or the overall behavior of the application.
     */
    @AfterEach
    public void restoreSystemIn() {
        System.setIn(originalIn);
    }

    /** 
     * Tests that the INIT event returns the correct start menu dialog message.
     */
    @Test
    public void testGetMessageInit() {
        CLI cli = new CLI();

        assertEquals("Type:\n"
        + "\t`l` to load last game\n"
        + "\t`p` to play a new game\n"
        + "\t`sb` to see scoreboard\n"
        + "\t`c` to clear scoreboard\n", cli.getMessage(EventType.INIT));
    }

    /** 
     * Tests that input "n" triggers RESET and returns the difficulty selection message. 
     */
    @Test
    public void testgetEventTypeReset() {
        String input = "n\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        CLI cli = new CLI();

        assertEquals(EventType.RESET, cli.getEventType());
        assertEquals("Type:\n"
        + "\t`e` for easy difficulty (2 colors)\n"
        + "\t`m` for medium difficulty (3 colors)\n"
        + "\t`h` for hard difficulty (4 colors)\n", cli.getMessage(EventType.RESET));
        }

    /** 
     * Tests that input "p" triggers BEGIN and returns the difficulty selection message. 
     */
    @Test
    public void testgetEventTypeBegin() {
        String input = "p\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        CLI cli = new CLI();

        assertEquals(EventType.BEGIN, cli.getEventType());
        assertEquals("Type:\n"
        + "\t`e` for easy difficulty (2 colors)\n"
        + "\t`m` for medium difficulty (3 colors)\n"
        + "\t`h` for hard difficulty (4 colors)\n", cli.getMessage(EventType.BEGIN));
        }

    /** 
     * Tests that the NEXT_MOVE event returns the correct message for in-game inputs. 
     */
    @Test
    public void testGetMessageNextMove() {
        CLI cli = new CLI();

        assertEquals("Type:\n"
            + "\t`s` to select a tile\n"
            + "\t`h` to have an hint\n"
            + "\t`g` for game rules\n"
            + "\t`e` to exit\n", cli.getMessage(EventType.NEXT_MOVE));
    }

    /** 
     * Tests that input "s" triggers INPUT and shows coordinate entry prompt. 
     */
    @Test
    public void testgetEventTypeInput() {
        String input = "s\n";  
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        CLI cli = new CLI();

        assertEquals(EventType.INPUT, cli.getEventType());
        assertEquals("Input a row, then a column.\n", cli.getMessage(EventType.INPUT));
    }

    /** 
     * Tests that WIN and LOSE events return the same end game message. 
     */
    @Test
    public void testGetMessageWinandLose() {
        CLI cli = new CLI();

        assertEquals("Type `n` for a new game, or `e` to exit.\n", cli.getMessage(EventType.WIN));
        assertEquals("Type `n` for a new game, or `e` to exit.\n", cli.getMessage(EventType.LOSE));
    }

    /** 
     * Tests that input "l" correctly maps to the LOAD event. 
     */
    @Test
    public void testgetEventTypeLoad() {
        String input = "l\n"; 
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        CLI cli = new CLI();

        assertEquals(EventType.LOAD, cli.getEventType());
    }

    /** 
     * Tests that input "sb" correctly maps to the SCOREBOARD event. 
     */
    @Test
    public void testgetEventTypeScoreboard() {
        String input = "sb\n"; 
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        CLI cli = new CLI();

        assertEquals(EventType.SCOREBOARD, cli.getEventType());
    }

    /** 
     * Tests that input "c" correctly maps to the CLEAR_SCOREBOARD event. 
     */
    @Test
    public void testgetEventTypeClearScoreboard() {
        String input = "c\n";  
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        CLI cli = new CLI();

        assertEquals(EventType.CLEAR_SCOREBOARD, cli.getEventType());
    }

    /** 
     * Tests that input "h" correctly maps to the HINT event. 
     */
    @Test
    public void testgetEventTypeHint() {
        String input = "h\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        CLI cli = new CLI();

        assertEquals(EventType.HINT, cli.getEventType());
    }

    /** 
     * Tests that input "g" correctly maps to the TEXT (rules) event. 
     */
    @Test
    public void testgetEventTypeText() {
        String input = "g\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        CLI cli = new CLI();

        assertEquals(EventType.TEXT, cli.getEventType());
    }

    /** 
     * Tests that input "e" correctly maps to the EXIT event. 
     */
    @Test
    public void testgetEventTypeExit() {
        String input = "e\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        CLI cli = new CLI();

        assertEquals(EventType.EXIT, cli.getEventType());
    }

    /** 
     * Tests that input "m" selects the MEDIUM difficulty level. 
     */
    @Test
    public void testGetDifficultyMedium() {
        String input = "m\n";  
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        CLI cli = new CLI();

        assertEquals(Difficulty.MEDIUM, cli.getDifficulty());
    }

    /** 
     * Tests that user coordinate input (3 and 5) is correctly parsed as [3, 5]. 
     */
    @Test
    public void testGetCoordinates() {
        String input = "3\n5\n"; // simulate row=3, col=5
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        CLI cli = new CLI();

        assertArrayEquals(new int[]{3, 5}, cli.getCoordinates());
    }
}
