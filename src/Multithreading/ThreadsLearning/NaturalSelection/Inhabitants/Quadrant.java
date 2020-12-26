package Multithreading.ThreadsLearning.NaturalSelection.Inhabitants;

import Multithreading.ThreadsLearning.NaturalSelection.Executors.SimulationExecutor;
import Multithreading.ThreadsLearning.NaturalSelection.Map.Map;

import java.util.Random;
import java.util.concurrent.*;

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
 *        give only one descendant).
 *
 *  Class "Quadrant" also implements a Runnable interface because
 *  each instance of this class will run as a unique thread.
 */
public class Quadrant extends AbstractLivable
        implements Runnable {

    private static final long SLEEP_AFTER_STEP = 75; // is equal to 0.075 seconds
    private static final long PERFORM_ITERATION_TIME = 7000; // is equal to 6.65 seconds

    // a unique id of a current quadrant
    public final int UNIQUE_ID = nextId++;
    private static int nextId = 1;

    /**
     *  This field is used for statistics.
     */
    private int hasEatenCurrentRound;

    /**
     *  A simulation executor object that regulates all
     *  the processes of simulation.
     */
    private final SimulationExecutor simulationExecutor;


    public Quadrant(Map simulationMap, int row, int column,
                    SimulationExecutor simulationExecutor) {
        super(simulationMap, row, column);
        this.simulationExecutor = simulationExecutor;
    }

    public static void main(String[] args) {

        final int NUM_OF_THREAD = 80;

        final Map map = new Map(35,35);
//        final Quadrant operator = new Quadrant(map,0, 0);

        ScheduledExecutorService ses = Executors.newScheduledThreadPool(100);
        for (int i = 0; i < NUM_OF_THREAD; i++) {
//            ses.scheduleAtFixedRate(operator.createOnPerimeterCoordinates(),
//                    0, 7000, TimeUnit.MILLISECONDS);
        }
        ses.scheduleAtFixedRate(() -> {
            System.out.println("!!!Analyzer works.!!!");
        }, 6520, 7000, TimeUnit.MILLISECONDS);
    }


    /**
     *  An implementation of {@code Runnable} interface. Method {@code run} has
     *  all the abilities of the creature that participated in the simulation.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            long enteredTime = System.currentTimeMillis(),
                 curTime     = System.currentTimeMillis(),
                 performTo   = curTime + 6500; // 6.5 seconds

            // update statistics
            hasEatenCurrentRound = 0;

            /*
                This loop is a one cycle of a simulation.
                After looping a creature's param will be
                examined.
             */
            while (curTime < performTo) {
                // perform moving
                move();

                // if a current map unit has food, increase number of eaten food;
                if (simulationMap.getMapUnitWithCoordinates(getRow(), getColumn()).eatFood()) {
                    // increase counter of eaten food;
                    eat();

                    // update statistics
                    hasEatenCurrentRound++;
                }

                // sleep for 0.075 seconds (== 75 milliseconds) after moving;
                sleep(SLEEP_AFTER_STEP);

                // increase "curTime" value
                curTime = System.currentTimeMillis();
            }

            if (die()) {
                // interrupt thread to stop executing;
                Thread.currentThread().interrupt();
            }
            // apply reproducing if it is possible;
            else if (canReproduce()) {
                simulationExecutor.addNewQuadrantToQueue(reproduce(),
                        PERFORM_ITERATION_TIME -                // time which a
                                (System.currentTimeMillis() - enteredTime) + 10);   // new creature
                                                                                    // will wait
                                                                                    // until start;
            }
            // sleep until execution cycle time passes
            sleep(PERFORM_ITERATION_TIME - (System.currentTimeMillis() - enteredTime));
        }
    }

    /**
     * A helper method that makes a thread to fall asleep.
     * @param time - time of sleeping;
     */
    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


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
        } else if (isInCorner()) {
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
        // quadrant is next to side
        else {
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
     * A class that represents a pair of coordinates {row - column};
     */
    private static record Pair(int row, int column) {}

    private static Pair getPerimeterCoordinates(Map simulationMap) {
        final var random = new Random();

        int newCreatureRow;
        int newCreatureColumn;

        boolean defineSpawnRow = random.nextBoolean();
        if (defineSpawnRow) {
            /*
             *  A new creature will be spawned somewhere at defined places (shown as "#").
             *
             *        -------------------------------
             *        | ########################### |
             *
             *         .............................
             *
             *        | ########################### |
             *        -------------------------------
             */
            newCreatureColumn = random.nextInt(simulationMap.getNumOfColumns());

            // if topOrBottomRow == true, the new creature will be spawned at first row;
            // Otherwise, the one will be spawned at last row;
            boolean topOrBottomRow = random.nextBoolean();
            newCreatureRow = (topOrBottomRow) ? 0 : simulationMap.getNumOfRows() - 1;
        } else {
            /*
             *  A new creature will be spawned somewhere at defined places (shown as "#").
             *
             *        ---         .          ---
             *        | #         .          # |
             *        | #         .          # |
             *        | #         .          # |
             *        | #         .          # |
             *        | #         .          # |
             *        | #         .          # |
             *        | #         .          # |
             *        ---         .          ---
             */
            newCreatureRow = random.nextInt(simulationMap.getNumOfRows());

            // if leftOrRightColumn == true, the new creature will be spawned at first column;
            // Otherwise, the one will be spawned at last column;
            boolean leftOrRightColumn = random.nextBoolean();
            newCreatureColumn = (leftOrRightColumn) ? 0 : simulationMap.getNumOfColumns() - 1;
        }
        return new Pair(newCreatureRow, newCreatureColumn);
    }

    /**
     * The creature should perform a reproduce action.
     */
    @Override
    public Quadrant reproduce() {
        Pair newCoordinates = getPerimeterCoordinates(this.simulationMap);
        return new Quadrant(this.simulationMap, newCoordinates.row(),
                newCoordinates.column(), simulationExecutor);
    }

    /**
     * The creature should stop participating in the simulation if it hasn't eaten
     * any food-units during a simulation cycle.
     */
    @Override
    public boolean die() {
        return numHasEaten() == 0;
    }

    /* Object methods */
    @Override
    public String toString() {
        return "Creature " + UNIQUE_ID + " {row: " + getRow()
                + " column: " + getColumn() + "}";
    }


    /* Getters */
    public int getHasEatenCurrentRound() {
        return hasEatenCurrentRound;
    }
}
