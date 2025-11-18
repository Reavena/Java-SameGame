package gridgames;

import java.io.Serializable;

/**
 * Class which describes the element the user can interact with to play
 * the game. This elements is called <em>a tile</em>, as it is very often a piece
 * of grid in most 2D-syle puzzle games.
 * <p>
 * A {@code Tile} object is represented by a value and can be indirectly
 * selected or unselected by the user during the game.
 * 
 * @see Grid
 */
public final class Tile implements Serializable, Cloneable {

    /** Value of this {@code Tile} object, as an {@code int}. */
    private int value;
    /**
     * Flag marking this {@code Tile} object to have been selected by the user or not, as a
     * {@code boolean}. Always {@code false} when instanciating a {@code Tile}
     * object.
     */
    private boolean isSelected;
    /** Arbitrary serialization number set to {@value}. */
    private final static long serialVersionUID = 1L;

    /**
     * Constructs a newly allocated {@code Tile} object based on an {@code int}
     * value.
     * 
     * @param v The value to instanciate this {@code Tile} object with.
     */
    public Tile(int v) {
        this.value = v;
        this.isSelected = false;
    }

    /**
     * Selects this {@code Tile} object.
     */
    public void select() {
        this.isSelected = true;
    }

    /**
     * Unselects this {@code Tile} object.
     */
    public void unselect() {
        this.isSelected = false;
    }

    /**
     * Returns the value of this {@code Tile} object, as an {@code int}.
     * 
     * @return The value of this {@code Tile} object.
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Returns the selection state of this {@code Tile} object, as a {@code boolean}.
     * 
     * @return {@code true} if this {@code Tile} object is selected, {@code false} otherwise.
     */
    public boolean isSelected() {
        return this.isSelected;
    }

    /**
     * Compares the values of two {@code Tile} objects.
     * 
     * @param aTile Tile to compare with this {@code Tile} object.
     * @return {@code true} if the tiles have the same value, {@code false}
     *         otherwise.
     */
    public boolean hasSameValue(Tile aTile) {
        return this.value == aTile.getValue();
    }

    /**
     * Compares the selection states of two {@code Tile} objects.
     * 
     * @param aTile Tile to compare with this {@code Tile} object.
     * @return {@code true} if the tiles have the same selection state,
     *         {@code false} otherwise.
     */
    public boolean hasSameState(Tile aTile) {
        return this.isSelected == aTile.isSelected();
    }

    /**
     * Returns a deep copy of this {@code Tile} object.
     * 
     * @return A clone of this {@code Tile} object.
     */
    @Override
    public Tile clone() {
        try {
            return (Tile) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

}
