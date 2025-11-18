package gridgames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class providing a graphical user interface for a {@link Game} object.
 * <p>
 * It displays a special menu when the game is not started yet, then a playing
 * menu rendering the grid of the {@code Game} with coloured tiles, the current
 * score, the best move, points the user can make with the best move, and so on.
 * {@code GraphicalView} is a listener of a {@code Game} instance,
 * and waits for its notification to update itself.
 * <p>
 * The {@code GraphicalView} adapts itself depending on the elements provided by
 * its associated {@link GameController}, thus the displayed features mostly
 * depend on what functionnalities are implemented by the {@link GameInput}
 * instance of the {@code GameController}.
 * <p>
 * The {@code GraphicalView} supports buttons and other {@code Swing}
 * components with attached listeners to perform actions, through
 * the {@code GameController} methods.
 * 
 * @see GameController
 * @see GameInput
 */
public class GraphicalView extends JFrame implements GameListener {

    /** {@code Game} instance to read information on and/or to interact with. */
    private Game game;
    /** {@code GameController} instance to get GUI elements from. */
    private GameController controller;

    /** Size of a tile on the GUI, set to an {@code int} value of {@value}. */
    public static final int TILE_SIZE = 40;
    /**
     * Padding size between tiles on the GUI,
     * set to an {@code int} value of {@value}.
     */
    public static final int PADDING = 3;

    /** GUI background {@code Color}. */
    private static final Color BG_COLOR = new Color(50, 50, 50);
    /** GUI texts {@code Color}. */
    private static final Color TEXT_COLOR = Color.WHITE;
    /** GUI buttons {@code Color}. */
    private static final Color BUTTON_COLOR = new Color(70, 70, 70);

    /**
     * {@code Color[]} containing the colors supported by this
     * {@code GraphicalView} to display the tiles in the grid,
     * according to there different values.
     * 
     * @see Tile
     */
    private static final Color[] COLORMAP = {
            Color.RED,
            Color.BLUE,
            Color.YELLOW,
            Color.GREEN
    };

    /** Row index of the best move, as an {@code int}. */
    private int hintRow = -1;
    /** Column index of the best move, as an {@code int}. */
    private int hintCol = -1;

    /** {@code JLabel} to display the current score. */
    private JLabel scoreLabel;

    /**
     * {@code JTextArea} to display instructions if the {@code GameController}
     * as a fully implemented {@link GameController#makeTextArea(int, int)}.
     */
    private JTextArea inputTextArea;

    /** {@code GamePanel} instance holding the tiles to display. */
    private GamePanel gamePanel;

    /**
     * {@code Map} to associate specific {@code String} options for the user to
     * select in the {@link #difficultyCombo} comb-box, to choose specific levels
     * of difficulty {@code Difficulty} enumerations.
     * 
     * @see #makeDifficultiesMap()
     */
    private Map<String, Difficulty> difficultiesMap;
    /**
     * {@code JComboBox<String>} which contains labels in reference to
     * the different levels of difficulty the user can play with.
     */
    private JComboBox<String> difficultyCombo;

    /**
     * Constructs a newly allocated {@code GraphicalView} object.
     * Does a minor setup of this object.
     */
    public GraphicalView() {
        this.difficultiesMap = makeDifficultiesMap();
        this.difficultyCombo = null;
        this.inputTextArea = null;
    }

    /** Sets the {@code Game} instance to read information on. */
    @Override
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Sets the {@code GameController} to interact with.
     * The {@code GameController} provides its method
     * to wait for user confirmation and to build
     * GUI elements.
     * 
     * @see GameController
     */
    @Override
    public void setController(GameController controller) {
        this.controller = controller;
    }

    /**
     * Updates this {@code GraphicalView} according to a given {@code GameEvent}.
     * <ul>
     * <li>Displays initilization menu on {@link EventType#INIT},
     * <li>Displays end game messages/dialogs on {@link EventType#LOSE} and
     * {@link EventType#WIN},
     * <li>Displays messages/dialog on {@link EventType#TEXT} (can be the
     * scoreboard),
     * <li>Displays controller messages on {@link EventType#INPUT},
     * <li>Displays best move and associated number of points on
     * {@link EventType#HINT},
     * <li>Updates the grid display on {@link EventType#BEGIN},
     * {@link EventType#PRE_VALID}, {@link EventType#NEXT_MOVE} and
     * {@link EventType#RESET}.
     * </ul>
     * 
     * @see GameEvent
     */
    @Override
    public void update(GameEvent event) {

        switch (event.getType()) {

            case INIT:
                showStartupMenu();
                break;

            case BEGIN:
                initializeUI();
                updateGameDisplay();
                break;

            case NEXT_MOVE:
                this.resetHint();
                updateGameDisplay();
                break;

            case PRE_VALID:
                this.updateGameDisplay();
                break;

            case INPUT:
                this.inputTextArea.setText(event.getArg());
                this.repaint();
                break;

            case HINT:
                this.showHint();
                this.updateGameDisplay();
                break;

            case RESET:
                updateGameDisplay();
                break;

            case WIN:
            case LOSE:
                updateGameDisplay();
                this.controller.showTwoChoicesDialog(
                        "Game Over",
                        event.getArg() + "\nWhat would you like to do?",
                        new String[] { "Play Again", "Exit" },
                        () -> this.game.reset(),
                        () -> this.controller.close(this));
                break;

            case TEXT:
                this.controller.makeInfoDialog("Message", event.getArg());
                this.controller.waitAck();
                break;

            default:
                break;
        }

    }

    /**
     * Returns a {@code Map<String, Difficulty>} mapping visible options
     * in {@link #difficultyCombo} to {@code Difficulty} objects.
     * 
     * @return A {@code Map<String, Difficulty>} mapping visible options
     * in {@link #difficultyCombo} to {@code Difficulty} objects.
     */
    private Map<String, Difficulty> makeDifficultiesMap() {

        Map<String, Difficulty> map = new HashMap<>();
        for (Difficulty d : Difficulty.values()) {
            String str = "" + (d.ordinal() + 2) + " colors";
            map.put(str, d);
        }

        return map;

    }

    /**
     * Creates a new window to display the game when
     * the user can play, after making a new game,
     * after loading a game, or after a reset.
     */
    private void initializeUI() {

        // Removes the initilization menu if it exists
        getContentPane().removeAll();
        setTitle("SameGame");
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.close(GraphicalView.this);
            }
        });

        // Score Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setBackground(BG_COLOR);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setForeground(TEXT_COLOR);

        topPanel.add(scoreLabel);
        topPanel.add(Box.createHorizontalStrut(50));

        add(topPanel, BorderLayout.NORTH);

        // Game Panel with tiles
        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(
                game.gridWidth() * (TILE_SIZE + PADDING) + PADDING,
                (game.gridHeight() + 2) * (TILE_SIZE + PADDING) + PADDING));

        // Add the possibility to select tiles using the mouse
        gamePanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                controller.mouseClicked(
                        e, (GraphicalView.TILE_SIZE + GraphicalView.PADDING));
            }
        });
        // Add the possibility to validate the selection using the mouse
        gamePanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                controller.mouseMoved(
                        e, (GraphicalView.TILE_SIZE + GraphicalView.PADDING));
            }
        });

        add(gamePanel, BorderLayout.CENTER);

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlPanel.setBackground(BG_COLOR);

        // Text area to display instructions given by the controller
        this.inputTextArea = this.controller.makeTextArea(1, 30);
        this.inputTextArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Button to request the best move
        JButton hintButton = this.controller.makeButton(
                "Hint",
                TEXT_COLOR,
                BUTTON_COLOR,
                false,
                e -> this.game.findBestMove());

        // Combo box to choose the difficulty and reset the game
        this.difficultyCombo = this.controller.makeComboBox(
                this.difficultiesMap.keySet().toArray(new String[0]),
                TEXT_COLOR,
                BUTTON_COLOR,
                e -> applySelectedDifficulty());

        // Button to show the game rules
        JButton rulesButton = this.controller.makeButton(
                "Game Rules",
                TEXT_COLOR,
                BUTTON_COLOR,
                false,
                e -> this.game.sendText(this.game.getGameRules()));

        // Button to display the scoreboard
        JButton scoreboardButton = this.controller.makeButton(
                "Scoreboard",
                TEXT_COLOR,
                BUTTON_COLOR,
                false,
                e -> this.game.loadScores());

        // Button to mute sound notifications
        JToggleButton muteButton = this.controller.makeToggleButton(
                "🔊",
                TEXT_COLOR,
                BUTTON_COLOR,
                false,
                e -> this.game.mute());

        controlPanel.setForeground(TEXT_COLOR);

        controlPanel.add(hintButton);
        controlPanel.add(Box.createHorizontalStrut(0));
        controlPanel.add(difficultyCombo);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(rulesButton);
        controlPanel.add(this.inputTextArea);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(scoreboardButton);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(muteButton);

        add(controlPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);

        // Refreshes the layout.
        revalidate();
        repaint();
    }

    /**
     * Reset {@link #hintRow} and {@link #hintCol} to -1, to be outside a
     * displayable range until an hint request is made.
     */
    private void resetHint() {
        this.hintRow = -1;
        this.hintCol = -1;
    }

    /**
     * Returns the level of difficulty that is selected by the
     * {@link #difficultyCombo}, by comparing its selected
     * label to those stored in the {@link #difficultiesMap}.
     * 
     * @return A level of difficulty as a {@code Difficulty} enumeration
     */
    private Difficulty getSelectedDifficulty() {
        String selected = (String) this.difficultyCombo.getSelectedItem();
        return this.difficultiesMap.get(selected);
    }

    /**
     * Helper methods that calls {@link #getSelectedDifficulty()}
     * and resets the {@code Game} instance with the returned
     * difficulty level.
     */
    private void applySelectedDifficulty() {
        Difficulty difficulty = getSelectedDifficulty();
        this.game.reset(difficulty);
    }

    /**
     * Updates {@link #hintRow} and {@link #hintCol}
     * to the postion of the best move the user can in the
     * current round, or reset them using
     * {@link #resetHint()} if there is no
     * possible move.
     */
    private void showHint() {
        int[] bestMove = this.game.getBestMove();
        if (bestMove != null) {
            this.hintRow = bestMove[0];
            this.hintCol = bestMove[1];
        } else {
            this.resetHint();
        }
    }

    /**
     * Sets the {@link #scoreLabel} to display the new score value
     * and repaint the gamePanel to refresh the GUI, so that
     * the modifications can be seen.
     */
    private void updateGameDisplay() {
        scoreLabel.setText(
                "Score: " + game.getScore()
                        + (game.getScoreInc() == 0 ? "" : " + " + game.getScoreInc()));
        gamePanel.repaint();
    }

    /**
     * Class which holds the tiles to display from the game grid.
     */
    class GamePanel extends JPanel {

        /**
         * Paints the tiles on this {@code GamePanel} as well
         * as the background color and such.
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            if (!game.isGridInitialized()) {
                g2d.setColor(TEXT_COLOR);
                g2d.drawString("Select New Game or Load Game", 20, 20);
                return;
            }

            if (game == null || game.tiles() == null) {
                g2d.setColor(TEXT_COLOR);
                g2d.drawString("Loading game...", 20, 20);
                return;
            }

            // Draw background
            g2d.setColor(BG_COLOR);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Draw tiles
            List<List<Tile>> tiles = game.tiles();
            for (int row = 0; row < tiles.size(); row++) {
                List<Tile> rowTiles = tiles.get(row);
                for (int col = 0; col < rowTiles.size(); col++) {
                    drawTile(g2d, row, col, rowTiles.get(col));
                }
            }

        }

        /**
         * Method used to paint the tiles with colors matching
         * there values, as well as border colors depending
         * on their states.
         * 
         * @param g2d A {@code Graphics2D} object.
         * @param row {@code int} Row index to paint the tile on the grid.
         * @param col {@code int} Column index to paint the tile on the grid.
         * @param tile The {@code Tile} object to paint.
         */
        private void drawTile(Graphics2D g2d, int row, int col, Tile tile) {

            int x = col * (TILE_SIZE + PADDING) + PADDING;
            int y = row * (TILE_SIZE + PADDING) + PADDING;

            // Safely gets color: subtract 1 from value but ensure it's within bounds
            int colorIndex = tile.getValue();
            if (colorIndex < 0 || colorIndex >= COLORMAP.length) {
                // default to first color if invalid
                colorIndex = 0;
            }

            // Draw tile body
            g2d.setColor(COLORMAP[tile.getValue()]);
            g2d.fillRoundRect(x, y, TILE_SIZE, TILE_SIZE, 8, 8);

            // Draw border based on state

            if (row == hintRow && col == hintCol) {
                // If the tile is the best move
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(3));
            } else if (tile.isSelected()) {
                // If the tile is selected
                g2d.setColor(Color.GREEN);
                g2d.setStroke(new BasicStroke(2));
            } else {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1));
            }
            g2d.drawRoundRect(x, y, TILE_SIZE, TILE_SIZE, 8, 8);

        }

    }

    /**
     * Creates a new window displayed as an initialization menu,
     * before the user can actually play the game.
     */
    private void showStartupMenu() {
        setTitle("SameGame");
        // Set initial preferred window size
        setSize(400, 300);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.close(GraphicalView.this);
            }
        });

        JPanel startupPanel = new JPanel();
        startupPanel.setLayout(new BoxLayout(startupPanel, BoxLayout.Y_AXIS));
        startupPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        startupPanel.setBackground(BG_COLOR);

        JLabel titleLabel = new JLabel("SameGame");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Combo box to choose difficulty
        this.difficultyCombo = this.controller.makeComboBox(
                this.difficultiesMap.keySet().toArray(new String[0]),
                TEXT_COLOR,
                BUTTON_COLOR,
                null);
        difficultyCombo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Button to start a brand new game
        JButton newGameButton = this.controller.makeButton(
                "Start New Game",
                TEXT_COLOR,
                BUTTON_COLOR,
                false,
                e -> {
                    this.game.setDifficulty(getSelectedDifficulty());
                    this.game.makeNew();
                });
        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Button to load the previous unfinished game
        JButton loadGameButton = this.controller.makeButton(
                "Load Saved Game",
                TEXT_COLOR,
                BUTTON_COLOR,
                false,
                e -> this.game.load());
        loadGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Button to see the scoreboard
        JButton scoreboardButton = this.controller.makeButton(
                "Scoreboard",
                TEXT_COLOR,
                BUTTON_COLOR,
                false,
                e -> this.game.loadScores());
        scoreboardButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Button to clear the scoreboard
        JButton clearScoreboardButton = this.controller.makeButton(
                "Clear Scoreboard",
                TEXT_COLOR,
                BUTTON_COLOR,
                false,
                e -> this.game.clearScores());
        clearScoreboardButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // A text area to display instructions from the controller
        this.inputTextArea = this.controller.makeTextArea(1, 30);
        this.inputTextArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        startupPanel.add(Box.createVerticalStrut(5));
        startupPanel.add(titleLabel);
        startupPanel.add(Box.createVerticalStrut(15));
        startupPanel.add(difficultyCombo);
        startupPanel.add(Box.createVerticalStrut(20));
        startupPanel.add(newGameButton);
        startupPanel.add(Box.createVerticalStrut(5));
        startupPanel.add(loadGameButton);
        startupPanel.add(Box.createVerticalStrut(10));
        startupPanel.add(scoreboardButton);
        startupPanel.add(Box.createVerticalStrut(5));
        startupPanel.add(clearScoreboardButton);
        startupPanel.add(this.inputTextArea);

        setContentPane(startupPanel);
        revalidate();
        repaint();

    }

}
