package gridgames;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * Class representing the container for {@link Tile} objects. Its
 * repsonsablities are related to low-level operations on {@code Tile} objects,
 * that are selecting them, unselecting them and removing them.
 * <p>
 * This container is called <em>a grid</em> due to its 2D array representation
 * in most 2D-style puzzle games. It has information about its bounds and
 * the number of selected {@code Tile}.
 * <p>
 * NOTE: The {@code Grid} class actual implementation may be not suited for
 * other 2D-style puzzle games than the SameGame. An idea would be to make this
 * class and a few of its methods as {@code abstract} to support more different
 * kinds of games, and make other methods {@code final} to avoid any custom
 * implementation.
 * 
 * @see Tile
 */
public class Grid implements Serializable {

    /**
     * The height of this this {@code Grid}, mostly used for bound chrecking and
     * displaying.
     */
    private int height;
    /**
     * The width of this this {@code Grid}, mostly used for bound chrecking and
     * displaying.
     */
    private int width;

    /** The {@code Tile} objects on which this {@code Grid} can interact with. */
    private List<List<Tile>> tilesArray;
    /** Number of selected {@code Tile} objects. */
    private int nSelectedTiles;

    /** Arbitrary serialization number set to {@value}. */
    private final static long serialVersionUID = 2L;

    /**
     * Constructs a newly allocated {@code Grid} object based on a height, a width
     * and an array of {@code int} values.
     * <p>
     * This {@code Grid} creates its {@code Tile} objects whose values are only
     * randomly taken inside the given {@code int[]} parameter.
     * 
     * @param h         Height of this {@code Grid} object, as an {@code int}.
     * @param w         Width of this {@code Grid} object, as an {@code int}.
     * @param valueSeed Values taken by the created {@code Tile} objects, as an
     *                  {@code int[]}.
     */
    public Grid(int h, int w, int[] valueSeed) {

        // The grid must at least contains one tile.
        this.height = (h > 0 ? h : 1);
        this.width = (w > 0 ? w : 1);

        this.nSelectedTiles = 0;
        this.tilesArray = new ArrayList<List<Tile>>(this.height);
        Random random = new Random();

        for (int i = 0; i < this.height; i++) {
            List<Tile> aRow = new ArrayList<Tile>(this.width);
            for (int j = 0; j < this.width; j++) {
                // Tile created with a random value from the array.
                Tile aTile = new Tile(valueSeed[random.nextInt(valueSeed.length)]);
                aRow.add(aTile);
            }
            this.tilesArray.add(aRow);
        }

    }

    /**
     * Iteratively selects a group of same color {@code Tile} objects within this
     * {@code Grid}, starting from given row and column coordinates.
     * <p>
     * If there is a first {@code Tile} at the given coordinates, this method
     * selects it as well as all its closest neighbors {@code Tile} objects of same
     * color
     * that are aligned in the same row or same column, then repeats the process for
     * each
     * selected {@code Tile} until there is no more possibilities.
     * 
     * @param startRow The row coordinate to start with, as an {@code int}.
     * @param startCol The column coordinate to start with, as an {@code int}.
     */
    public void selectTiles(int startRow, int startCol) {

        // Tries to pick the tile at (startRow, startCol).
        Tile target;
        try {
            target = this.tilesArray.get(startRow).get(startCol);
        } catch (IndexOutOfBoundsException e) {
            // Resets the number of selected tiles.
            this.nSelectedTiles = 0;
            return;
        }

        // Creates a stack of tiles coordinates.
        Stack<List<Integer>> stack = new Stack<List<Integer>>();
        // Pushes the selected tile coordinates in the stack.
        stack.push(Arrays.asList(startRow, startCol));
        this.nSelectedTiles = 0;

        while (!stack.isEmpty()) {

            // Pops a tile coordinates from the stack.
            List<Integer> aPos = stack.pop();
            int aRow = aPos.get(0).intValue();
            int aCol = aPos.get(1).intValue();

            // Tries to pick the tile at (aRow, aCol).
            Tile aTile;
            try {
                aTile = this.tilesArray.get(aRow).get(aCol);
            } catch (IndexOutOfBoundsException e) {
                // Skips to the next iteration.
                continue;
            }

            // Skips to the next iteration if the tile is already selected
            // or have a different color.
            if (aTile.isSelected())
                continue;
            if (!aTile.hasSameValue(target))
                continue;

            // Selects the tile and updates the number of selected tiles.
            aTile.select();
            this.nSelectedTiles++;

            // Pushes the closest potential neighbors coordinates
            // which are aligned by row or column to the previous
            // selected tile.
            stack.push(Arrays.asList(aRow - 1, aCol));
            stack.push(Arrays.asList(aRow + 1, aCol));
            stack.push(Arrays.asList(aRow, aCol - 1));
            stack.push(Arrays.asList(aRow, aCol + 1));

        }

        // If only one tile is selected, it must be the very first one.
        // In SameGame, we cannot select less than two tiles.
        if (this.nSelectedTiles == 1) {
            target.unselect();
            this.nSelectedTiles = 0;
        }

    }

    /**
     * Unselects all {@code Tile} objects within this {@code Grid}.
     */
    public final void unselectTiles() {
        for (List<Tile> aRow : this.tilesArray) {
            for (Tile aTile : aRow)
                aTile.unselect();
        }
        // Resets the number of selected tiles.
        this.nSelectedTiles = 0;
    }

    /**
     * Returns copies of all the {@code Tile} objects within
     * this {@code Grid} as a {@code List<List<Tile>>}.
     * 
     * @return Copies of all the {@code Tile} objects within this {@code Grid}.
     */
    public final List<List<Tile>> getTiles() {

        List<List<Tile>> copiedTilesArray = new ArrayList<>();

        for (List<Tile> aRow : this.tilesArray) {
            List<Tile> copiedRow = new ArrayList<Tile>();
            for (Tile aTile : aRow) {
                // Copies the tile.
                Tile copiedTile = aTile.clone();
                copiedRow.add(copiedTile);
            }
            copiedTilesArray.add(copiedRow);
        }

        return copiedTilesArray;

    }

    /**
     * Returns the height of this {@code Grid} as an {@code int}.
     * 
     * @return The height of this {@code Grid}.
     */
    public final int getHeight() {
        return this.height;
    }

    /**
     * Returns the width of this {@code Grid} as an {@code int}.
     * 
     * @return The width of this {@code Grid}.
     */
    public final int getWidth() {
        return this.width;
    }

    /**
     * Returns the number of selected {@code Tiles} object within this {@code Grid}
     * as an {@code int}.
     * 
     * @return The number of selected {@code Tiles} object within this {@code Grid}.
     */
    public final int getSelectedTilesNumber() {
        return this.nSelectedTiles;
    }

    /**
     * Removes all selected {@code Tile} objects within this {@code Grid}.
     */
    public void removeSelection() {
        for (List<Tile> aRow : this.tilesArray) {
            aRow.removeIf(aTile -> aTile.isSelected());
        }
        // Removes empty rows.
        this.tilesArray.removeIf(aRow -> aRow.isEmpty());
        // Resets the number of selected tiles.
        this.nSelectedTiles = 0;
    }

    /**
     * Checks if there are any tiles left within this {@code Grid}.
     * 
     * @return {@code true} if this {@code Grid} does not contain any {@code Tile},
     *         {@code false} otherwise.
     */
    public final boolean isEmpty() {
        return this.tilesArray.isEmpty();
    }

}
