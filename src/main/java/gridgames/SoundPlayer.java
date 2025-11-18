package gridgames;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

/**
 * Class which enables sound notifications for the user, as he plays the game.
 * <p>
 * The {@code SoundPlayer} is a listener of a {@code Game} instance, and waits
 * for specific notifications to play a different sound according to the type
 * of event.
 */
public final class SoundPlayer implements GameListener {

    /** 
     * {@code Map} object to link sounds to play, as {@code Clip} instances,
     * to specific {@code EventType} enumerations.
     */
    private final Map<EventType, Clip> clips;
    /** Current or last sound played, as a {@code Clip}. */
    private Clip currentClip;
    /** Flags to mute or unmute sound notifications, as a {@code boolean}. */
    private boolean muted;


    /**
     * Constructs a newly allocated {@code SoundPlayer} object and maps
     * sounds to specific {@link EventType} enumerations. By default,
     * this {@code SoundPlayer} is not muted.
     * 
     * @see #isMuted()
     */
    public SoundPlayer() {
        
        this.muted = false;
        this.clips = new HashMap<>();

        // Temporary Map that links filenames to event types.
        Map<EventType, String> sounds = new HashMap<>();
        
        sounds.put(EventType.WIN, "won.wav");
        sounds.put(EventType.LOSE, "lost.wav");
        sounds.put(EventType.NEXT_MOVE, "valid.wav");
        sounds.put(EventType.SCOREBOARD, "score.wav");
        sounds.put(EventType.HINT, "hint.wav");
        sounds.put(EventType.TEXT, "text.wav");
        sounds.put(EventType.EXIT, "exit.wav");
        sounds.put(EventType.INIT, "begin.wav");
        sounds.put(EventType.RESET, "begin.wav");

        // Maps clips to event types by using the filenames.
        // Allows the SoundPlayer to preload Clip objects.
        for (Map.Entry<EventType, String> entry : sounds.entrySet()) {

            try {
                URL soundURL = getClass()
                               .getResource("/sounds/" + entry.getValue());
                AudioInputStream ais = AudioSystem
                                       .getAudioInputStream(soundURL);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                this.clips.put(entry.getKey(), clip);
            } catch (Exception e) {
            }

        }

    }

    /** Empty implementation, not needed. */
    @Override
    public void setGame(Game game) {
    }

    /** Empty implementation, not needed. */
    @Override
    public void setController(GameController controller) {
    }


    /**
     * Updates this {@code SoundPlayer} according to a given
     * {@code GameEvent}.
     * <p>
     * Plays a sound for this supported {@code EventType}:
     * <ul>
     * <li>{@link EventType#WIN},
     * <li>{@link EventType#LOSE},
     * <li>{@link EventType#NEXT_MOVE},
     * <li>{@link EventType#SCOREBOARD},
     * <li>{@link EventType#HINT},
     * <li>{@link EventType#TEXT},
     * <li>{@link EventType#INIT},
     * <li>{@link EventType#RESET},
     * <li>{@link EventType#EXIT}.
     * </ul>
     * <p>
     * Toggles sound notifications on a {@link EventType#MUTE}
     * {@code EventType}. They are activated by default.
     * 
     * @see GameEvent
     */
    @Override
    public void update(GameEvent event) {

        EventType type = event.getType();

        // Toggle the mute flag.
        if (type == EventType.MUTE) {
            this.muted = !this.muted;
            return;
        }


        // Plays a mapped sound according to the event type.
        this.play(type);
        if (type == EventType.EXIT) {
            // Waits for the exit clip to finish before closing.
            this.waitForClipToFinish();
            this.close();
        }
    }

    /**
     * Returns whether this {@code SoundPlayer} has sound notifications
     * disabled or not.
     * 
     * @return {@code true} if this {@code SoundPlayer} has sound
     *         notifications disabled, {@code false} otherwise.
     */
    public boolean isMuted() {
        return this.muted;
    }

    /**
     * Plays a mapped sound according to the given event type.
     * 
     * @param type The event type as an {@code EventType} enumeration
     * @see EventType
     */
    private void play(EventType type) {

        // Does not play a sound if sound notifications are disabled.
        if (muted) return;

        Clip clip = this.clips.get(type);

        // Returns if there is no clip for this event type.
        if (clip == null) return;

        // Does not play EventType.TEXT if EventType.SCOREBOARD is playing.
        if (type == EventType.TEXT
            && this.currentClip.isRunning()
            && this.clips.get(EventType.SCOREBOARD)
                         .equals(this.currentClip)) {
                return;
        }

        // Otherwise, stops the previous sound if it is playing.
        if (this.currentClip != null
            && this.currentClip.isRunning()) {
                this.currentClip.stop();
        }

        // Plays the new sound.
        this.currentClip = clip;
        this.currentClip.setFramePosition(0);
        this.currentClip.start();

    }

    /**
     * Returns when {@link #currentClip} sound is entirely played.
     * Allows for other actions to wait for the clip. It uses a
     * {@link CountDownLatch} to wait and a {@link LineListener}
     * attached to {@link #currentClip} to release control.
     * 
     * @see CountDownLatch
     * @see LineListener
     */
    private void waitForClipToFinish() {

        if (this.currentClip == null) return;

        CountDownLatch doneSignal = new CountDownLatch(1);

        // Creates a LineListener that executes when the clip finishes.
        LineListener listener = new LineListener() {
            @Override
            public void update(LineEvent event) {
                if (event.getType() == LineEvent.Type.STOP) {
                    // Decrements the latch, so that it reaches 0.
                    doneSignal.countDown();
                    currentClip.removeLineListener(this);
                }
            }
        };

        // Attaches the listener to the clip.
        this.currentClip.addLineListener(listener);

        if (this.currentClip.isRunning()) {
            try {
                // Waits until the clip finishes.
                doneSignal.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    
    }

    /**
     * Closes all clips of this {@code SoundPlayer}.
     */
    private void close() {
        this.clips.values().forEach(Clip::close);
    }

}
