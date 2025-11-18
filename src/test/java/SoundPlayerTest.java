import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import gridgames.*;

/** 
 * Unit tests for the integration of SoundPlayer
 */

public class SoundPlayerTest {

    /**
     * Tests the mute toggle functionality.
     * The user should switch between muted and unmuted states
     * when receiving a MUTE event.
     */
    @Test
    public void testMuteToggle() {
        SoundPlayer player = new SoundPlayer();
        assertFalse(player.isMuted());

        player.update(new GameEvent(EventType.MUTE, null));
        assertTrue(player.isMuted());

        player.update(new GameEvent(EventType.MUTE, null));
        assertFalse(player.isMuted());
    }

    /**
     * Tests the behavior when receiving an event
     * that has no associated sound clip.
     * Ensures the user remains unmuted and does not crash.
     */
    @Test
    public void testEventWithNoAssociatedClip() {
        SoundPlayer player = new SoundPlayer();
        player.update(new GameEvent(EventType.PRE_VALID, null));
        assertFalse(player.isMuted());
    }


    /**
     * Tests the behavior when receiving an EXIT event.
     * Ensures the user does not crash or get muted.
     */
    @Test
    public void testExitEventDoesNotMuteOrCrash() {
        SoundPlayer player = new SoundPlayer(); 
        player.update(new GameEvent(EventType.EXIT, null));
        assertFalse(player.isMuted());
    }
}
