package Multithreading.ThreadsLearning.NaturalSelection.Inhabitants;

import Multithreading.ThreadsLearning.NaturalSelection.Map.Map;

import java.util.Random;

/**
 *  A Quadrant is an imaginary creature that will participate in the simulation.
 *  Class "Quadrant" implements interface "Livable" that has some basic
 *  methods for simple simulation.
 *
 *  Simulation rules:
 *      - if the quadrant hasn't eaten any food-units during the simulation cycle
 *        it will stop participating in the simulation.
 *      - if the quadrant has eaten ONE food-unit it will continue surviving in
 *        the next round.
 *      - if the quadrant has eaten at least TWO food-units it will continue
 *        surviving in the next round and will give a descendant (потомок).
 *        (No difference has many food-units the quadrant has eaten he will
 *        give only     one descendant).
 *
 *  Class "Quadrant" also implements a Runnable interface because
 *  each instance of this class will run as a unique thread.
 */
public class Quadrant extends AbstractLivable {

    private static final long SLEEP_AFTER_STEP = 1500; // is equal to 1.5 seconds

    // a unique id of a current quadrant
    public final int UNIQUE_ID = nextId++;
    private static int nextId = 1;


    public Quadrant(Map simulationMap, int row, int column) {
        super(simulationMap, row, column);
    }

    /* Move helper methods */

    /**
     * The implementor of this interface should
     * implement the ability to move.
     *
     *  # - is a positions where a Q (Quadrant) can make a step.
     *  Moreover, a quadrant can continue staying at its
     *  current position. (It is made to simplify the implementation).
     */
    @Override
    public void move() {
        final var random = new Random();
        int newRow;
        int newColumn;
        if (!isOnSide() && !isInCorner()) {
            /*
             * A quadrant can make a step left, right, up, down and diagonally.
             *
             *      #  #  #
             *      #  Q  #
             *      #  #  #
             */
            newRow = simulationMap.getRandomValue(random,
                    this.getRow() - 1, this.getRow() + 1);
            newColumn = simulationMap.getRandomValue(random,
                    this.getColumn() - 1, this.getColumn() + 1);
        } else if (isOnSide()) {
            // the current side that is next to the creature
            Orientation sideType = getSideType();

            if (sideType == Orientation.LEFT_SIDE) {
                /*
                 * A quadrant can make a step up, down, right and diagonally right.
                 *
                 *      | #  #
                 *      | Q  #
                 *      | #  #
                 */
                newRow = simulationMap.getRandomValue(random,
                        this.getRow() - 1, this.getRow() + 1);
                newColumn = simulationMap.getRandomValue(random,
                        this.getColumn(), this.getColumn() + 1);
            } else if (sideType == Orientation.RIGHT_SIDE) {
                /*
                 * A quadrant can make a step up, down and diagonally left.
                 *
                 *      #  # |
                 *      #  Q |
                 *      #  # |
                 */
                newRow = simulationMap.getRandomValue(random,
                        this.getRow() - 1, this.getRow() + 1);
                newColumn = simulationMap.getRandomValue(random,
                        this.getColumn() - 1, this.getColumn());
            } else if (sideType == Orientation.TOP_SIDE) {
                /*
                 * A quadrant can make a step down, left, right and diagonally left/right.
                 *
                 *     ---------
                 *      #  Q  #
                 *      #  #  #
                 */
                newRow = simulationMap.getRandomValue(random,
                        this.getRow(), this.getRow() + 1);
                newColumn = simulationMap.getRandomValue(random,
                        this.getColumn() - 1, this.getColumn() + 1);
            }
            // Bottom side
            else {
                /*
                 * A quadrant can make a step down, left, right and diagonally left/right.
                 *
                 *      #  #  #
                 *      #  Q  #
                 *     ---------
                 */
                newRow = simulationMap.getRandomValue(random,
                        this.getRow() - 1, this.getRow());
                newColumn = simulationMap.getRandomValue(random,
                        this.getColumn() - 1, this.getColumn() + 1);
            }
        }
        // quadrant is in corner
        else {
            // the current corner where the creature is situated
            Orientation cornerType = getCornerType();

            if (cornerType == Orientation.LEFT_BOTTOM_CORNER) {
                /*
                 * A quadrant can make a step up, right and diagonally right.
                 *
                 *     |  #  #
                 *     |  Q  #
                 *     ---------
                 */
                newRow = simulationMap.getRandomValue(random,
                        this.getRow() - 1, this.getRow());
                newColumn = simulationMap.getRandomValue(random,
                        this.getColumn(), this.getColumn() + 1);
            } else if (cornerType == Orientation.LEFT_TOP_CORNER) {
                /*
                 * A quadrant can make a step down, right and diagonally right.
                 *
                 *     ---------
                 *     |  Q  #
                 *     |  #  #
                 */
                newRow = simulationMap.getRandomValue(random,
                        this.getRow(), this.getRow() + 1);
                newColumn = simulationMap.getRandomValue(random,
                        this.getColumn(), this.getColumn() + 1);

            } else if (cornerType == Orientation.RIGHT_TOP_CORNER) {
                /*
                 * A quadrant can make a step down, right and diagonally right.
                 *
                 *     --------
                 *      #  Q  |
                 *      #  #  |
                 */
                newRow = simulationMap.getRandomValue(random,
                        this.getRow(), this.getRow() + 1);
                newColumn = simulationMap.getRandomValue(random,
                        this.getColumn() - 1, this.getColumn());
            }
            // right bottom corner
            else {
                /*
                 * A quadrant can make a step up, left and diagonally left.
                 *
                 *      #  #  |
                 *      #  Q  |
                 *     --------
                 */
                newRow = simulationMap.getRandomValue(random,
                        this.getRow() - 1, this.getRow());
                newColumn = simulationMap.getRandomValue(random,
                        this.getColumn() - 1, this.getColumn());
            }
        }
        // set new coordinates
        setRow(newRow);
        setColumn(newColumn);
    }

    /**
     * The implementor of this interface should
     * increase a counter of eaten food-units when
     * this method is invoked.
     */
    @Override
    public void eat() {
        this.increaseNumOfEatenFood();
    }

    /**
     * This method should be used to create a new creature if a return type is "true".
     *
     * @return "true" if a current creature can reproduce a new one.
     * Otherwise, false;
     */
    @Override
    public boolean canReproduce() {
        return this.numHasEaten() >= 2;
    }

    /**
     * The creature should perform a reproduce action.
     */
    @Override
    public Thread reproduce() {
        // TODO: 25.12.2020
        return null;
    }

    /**
     * The creature should stop participating in the simulation if it hasn't eaten
     * any food-units during a simulation cycle.
     */
    @Override
    public boolean die() {
        return numHasEaten() == 0;
    }


    @Override
    public void run() {

    }
}
