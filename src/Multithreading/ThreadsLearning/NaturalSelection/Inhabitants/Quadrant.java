package Multithreading.ThreadsLearning.NaturalSelection.Inhabitants;

import Multithreading.ThreadsLearning.NaturalSelection.Executors.SimulationExecutor;
import Multithreading.ThreadsLearning.NaturalSelection.Map.Map;
import Multithreading.ThreadsLearning.NaturalSelection.Utilities.LoggerUtility;
import Multithreading.ThreadsLearning.NaturalSelection.Utilities.StatisticsCollector;
import Multithreading.ThreadsLearning.NaturalSelection.Utilities.TimeSyn;

import java.util.Objects;
import java.util.Random;;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

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
public class Quadrant extends AbstractLivable implements Runnable {
    // execution time parameters
    private static final long SLEEP_AFTER_STEP = 8;
    private static final long PERFORM_ITERATION_TIME = 2000; // is equal to 2 seconds

    // a unique id of a current quadrant
    public final int UNIQUE_ID = nextId++;
    private static int nextId = 1;

    // these are the unique random values which are used to calculate a hash code of the object
    private final Random random = new Random();
    private final long uniqueForHashCode1 = random.nextLong();
    private final long uniqueForHashCode2 = random.nextLong();
    private final long uniqueForHashCode3 = random.nextLong();

    /**
     *  A simulation executor object that regulates all
     *  the processes of simulation.
     */
    private final SimulationExecutor simulationExecutor;

    /**
     *  A statistics object that collects date (a day number - a number of creatures
     *  that are alive) during the execution.
     */
    private final StatisticsCollector statisticsCollector;

    /**
     *  A map that contains {@code ScheduledFuture} objects. These objects
     *  are linked with the thread that executes them. If there is a task
     *  that have to be stopped then this task will be found and stopped
     *  using a method {@code cancel()}.
     */
    private ConcurrentHashMap<Quadrant, ScheduledFuture<?>> scheduledFutureMap;

    /**
     *  A logger that is used to log actions during the execution of the simulation.
     */
    private static final LoggerUtility loggerUtility = new LoggerUtility(
            "LoggerQuadrant", LoggerUtility.LoggerType.FILE_LOGGER);


    /**
     *  This method set a {@code ConcurrentHashMap} as a value of a field of the object;
     *  @param scheduledFutureMap a map that is set as a field value;
     */
    public void setScheduledFutureMap(ConcurrentHashMap<Quadrant, ScheduledFuture<?>>
                                                               scheduledFutureMap)
    {
        this.scheduledFutureMap = scheduledFutureMap;
    }

    /* Constructors */
    public Quadrant(Map simulationMap,
                    int row, int column,
                    SimulationExecutor simulationExecutor,
                    StatisticsCollector statisticsCollector)
    {
        super(simulationMap, row, column);
        this.simulationExecutor = simulationExecutor;
        this.statisticsCollector = statisticsCollector;
    }

    public Quadrant(Map simulationMap, int row, int column,
                    SimulationExecutor simulationExecutor,
                    StatisticsCollector statisticsCollector,
                    ConcurrentHashMap<Quadrant, ScheduledFuture<?>> scheduledFutureHashMap)
    {
        this(simulationMap, row, column, simulationExecutor, statisticsCollector);
        this.scheduledFutureMap = scheduledFutureHashMap;
    }

    /**
     *  An implementation of {@code Runnable} interface. Method {@code run} has
     *  all the abilities of the creature that participated in the simulation.
     */
    @Override
    public void run() {
        if (!Thread.currentThread().isInterrupted()) {

            // provide time synchronization of time
            TimeSyn.enter();

            long enteredTime = TimeSyn.getTimeEntered(),
                 curTime     = TimeSyn.getTimeEntered(),
                 performTo   = curTime + 820;

            // set number of eaten food to zero;
            setHasEatenCurrentRound(0);

            // update statistics
            statisticsCollector.increaseNumOfAlive();


            /*
                This loop emulates a one round of the simulation.
                The data will collected by statistics collector after looping .
             */
            while (curTime < performTo) {
                // perform moving
                move();

                // increase a number of eaten food if a current map unit has food
                if (simulationMap.getMapUnitWithCoordinates(getRow(), getColumn()).eatFood()) {
                    // increase counter of eaten food in the current round
                    eat();
                }

                // sleep for 8 milliseconds after moving;
                try {
                    Thread.sleep(SLEEP_AFTER_STEP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // update "curTime" value
                curTime = System.currentTimeMillis();
            }

            if (die()) {
                // cancel a current task if a current creature has dead;
                scheduledFutureMap.remove(this).cancel(true);
            }
            // apply reproducing process if it is possible;
            else if (canReproduce()) {
                simulationExecutor.addNewQuadrantToQueue(reproduce(),
                         PERFORM_ITERATION_TIME -                         // a period of time which
                         (System.currentTimeMillis() - enteredTime) - 20, // a new creature will wait
                         scheduledFutureMap);                             // until starts execution;
            }

            // stop synchronization timer
            TimeSyn.close();
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
        this.hasEatenCurrentRoundIncrement();
    }

    /**
     * This method should be used to create a new creature if a return type is "true".
     *
     * @return "true" if a current creature can reproduce a new one.
     * Otherwise, false;
     */
    @Override
    public boolean canReproduce() {
        return this.numHasEatenCurrentRound() >= 2;
    }

    /**
     * A class that represents a pair of coordinates {row - column};
     */
    public static record Pair<T, Q>
            (T firstValue, Q secondValue) {}

    public static Pair<Integer, Integer> getPerimeterCoordinates(Map simulationMap) {
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
        return new Pair<>(newCreatureRow, newCreatureColumn);
    }

    /**
     * The creature should perform a reproduce action.
     */
    @Override
    public Quadrant reproduce() {
        Pair<Integer, Integer> newCoordinates = getPerimeterCoordinates(this.simulationMap);
        return new Quadrant(this.simulationMap, newCoordinates.firstValue(),
                            newCoordinates.secondValue(), simulationExecutor,
                            statisticsCollector, scheduledFutureMap);
    }

    /**
     * The creature should stop participating in the simulation if it hasn't eaten
     * any food-units during a simulation cycle.
     */
    @Override
    public boolean die() {
        return numHasEatenCurrentRound() == 0;
    }


    /* Object methods */
    @Override
    public String toString() {
        return "Creature " + UNIQUE_ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quadrant quadrant = (Quadrant) o;
        return UNIQUE_ID == quadrant.UNIQUE_ID &&
               uniqueForHashCode1 == quadrant.uniqueForHashCode1 &&
               uniqueForHashCode2 == quadrant.uniqueForHashCode2 &&
               uniqueForHashCode3 == quadrant.uniqueForHashCode3;
    }

    @Override
    public int hashCode() {
        return Objects.hash(UNIQUE_ID, uniqueForHashCode1,
                            uniqueForHashCode2, uniqueForHashCode3);
    }
}
