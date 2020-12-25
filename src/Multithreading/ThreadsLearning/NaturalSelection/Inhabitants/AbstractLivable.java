package Multithreading.ThreadsLearning.NaturalSelection.Inhabitants;

import Multithreading.ThreadsLearning.NaturalSelection.Map.Map;
import Multithreading.ThreadsLearning.NaturalSelection.Map.MapUnit;

/**
 * An abstract class that contains basic data about a creature
 * and some methods which provide moving abilities.
 */
public abstract class AbstractLivable implements Livable, Runnable {
    private int numOfEatenFood = 0;
    private int row;
    private int column;

    // a map where a simulation takes place;
    private final Map simulationMap;

    public AbstractLivable(Map simulationMap, int row, int column) {
        this.simulationMap = simulationMap;
        this.row = row;
        this.column = column;
    }

    /**
     * The implementor of this interface should
     * implement the ability to move.
     */
    public abstract void move();

    /**
     * The implementor of this interface should
     * increase a counter of eaten food-units when
     * this method is invoked.
     */
    public abstract void eat();

    /**
     * This method displays how many food-units the creature has eaten.
     *
     * @return number of eaten food-units;
     */
    @Override
    public int numHasEaten() {
        return this.numOfEatenFood;
    }

    /**
     * This method should be used to create a new creature if a return type is "true".
     *
     * @return "true" if a current creature can reproduce a new one.
     * Otherwise, false;
     */
    public abstract boolean canReproduce();

    /**
     * The creature should stop participating in the simulation if it hasn't eaten
     * any food-units during a simulation cycle.
     */
    public abstract void die();


    public abstract void run();


    /* Getters */
    public int getNumOfEatenFood() {
        return numOfEatenFood;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    /* Setters */
    public void increaseNumOfEatenFood() {
        this.numOfEatenFood++;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    /* Moving helper methods */

    /**
     * Enum contains the possible locations of creatures including sides and corners.
     */
    protected enum Orientation {

        // possible side positions
        LEFT_SIDE, RIGHT_SIDE, TOP_SIDE, BOTTOM_SIDE,

        // possible corner positions
        LEFT_BOTTOM_CORNER, LEFT_TOP_CORNER,
        RIGHT_BOTTOM_CORNER, RIGHT_TOP_CORNED;
    }

    /**
     * This implementation only works correctly for a square matrix.
     *
     *      row - column || row + column == 0 or
     *      abs(row - column) = map.row (is equal to map.column) --> return true;
     */
    protected boolean isInCorner() {
        return row - column == 0 || row + column == 0
                || Math.abs(row - column) == simulationMap.getNumOfRows();
    }

    /**
     * This implementation (as a method "boolean isInCorner()") only works
     * correctly for a square matrix.
    */
    protected boolean isOnSide() {
        return row == 0 || column == 0 || row == simulationMap.getNumOfRows() - 1
                || column == simulationMap.getNumOfColumns() - 1;
    }

    /**
     * This implementation (as a method "boolean isInCorner()") only works
     * correctly for a square matrix.
     * @return null if a creature isn't in a corner. Otherwise, it returns
     *  a corner in which a creature is.
     */
    protected Orientation getCornerType() {
        if (!isInCorner())
            return null;

        // check all 4 variants
        if (row + column == 0)
            return Orientation.LEFT_TOP_CORNER;
        if (row - column == 0)
            return Orientation.RIGHT_BOTTOM_CORNER;

        // the next 2 checks work correctly. This is because the other
        // cases were excluded in the check "!isInCorner()".
        if (row == 0)
            return Orientation.RIGHT_TOP_CORNED;
        else
            return Orientation.LEFT_BOTTOM_CORNER; // left bottom corner
    }

    /**
     * This implementation (as a method "boolean isInCorner()") only works
     * correctly for a square matrix.
     * @return null if a creature isn't in a corner. Otherwise, it returns
     *  a side near which a creature is.
     */
    protected Orientation getSideType() {
        if (!isOnSide())
            return null;

        if (row == 0)
            return Orientation.TOP_SIDE;
        if (column == 0)
            return Orientation.LEFT_SIDE;
        if (row == simulationMap.getNumOfRows() - 1)
            return Orientation.BOTTOM_SIDE;
        else
            return Orientation.RIGHT_SIDE;
    }
}
