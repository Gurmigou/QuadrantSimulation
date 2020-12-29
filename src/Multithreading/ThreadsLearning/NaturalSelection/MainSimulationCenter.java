package Multithreading.ThreadsLearning.NaturalSelection;

import Multithreading.ThreadsLearning.NaturalSelection.Executors.SimulationExecutor;
import Multithreading.ThreadsLearning.NaturalSelection.Inhabitants.Quadrant;
import Multithreading.ThreadsLearning.NaturalSelection.Map.Map;
import Multithreading.ThreadsLearning.NaturalSelection.Utilities.StatisticsCollector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 *  This is the main class that starts and runs the simulation.
 */
public class MainSimulationCenter {

    // There are initial parameters of simulation;
    private static final int INIT_NUMBER_OF_CREATURES = 10;

    // Map parameters;
    private static final int ROWS = 25;
    private static final int COLUMNS = 25;

    // Time parameters
    private static final long CREATURE_INIT_DELAY = 0;
    private static final long CREATURE_PERIOD_TIME = 2000; // 2 seconds

    /**
     * The main method that starts the simulation.
     * @throws InterruptedException if a thread was interrupted while sleeping, waiting e.c.;
     * @throws TimeoutException if a simulation wasn't competed before timeout terminates;
     */
    public static void main(String[] args) throws InterruptedException, TimeoutException {
        // create a simulation map
        Map simulationMap = new Map(ROWS, COLUMNS);

        // create a simulation executor
        SimulationExecutor simulationExecutor = new SimulationExecutor(INIT_NUMBER_OF_CREATURES,
                CREATURE_INIT_DELAY, CREATURE_PERIOD_TIME, TimeUnit.MILLISECONDS);

        // create statistics collector
        StatisticsCollector statisticsCollector = new StatisticsCollector();

        // create a list of creatures
        List<Quadrant> quadrantList = createQuadrantsList(simulationMap,
                simulationExecutor, statisticsCollector);

        // start execution of creatures
        simulationExecutor.addPeriodicExecutorTasks(quadrantList);

        // start execution of statistics collector
        simulationExecutor.addPeriodicExecutorTask(() -> {
            if (!simulationExecutor.getSes().isTerminated()) {
                statisticsCollector.performStatsCollection();
                // apply a helper method to notify waiters
                // of this object's monitor;
                statisticsCollector.notifyWaiters();

                // print a round number;
                System.out.println("(*) Performing round: " +
                        statisticsCollector.getRoundsCollected());
            }
        },820, CREATURE_PERIOD_TIME, TimeUnit.MILLISECONDS);

        // start execution of map operator
        simulationExecutor.addPeriodicExecutorTask(() -> {
            if (!Thread.currentThread().isInterrupted()) {
                simulationMap.clearMapFood();
                simulationMap.fillMapWithFood();
            }
        },820, CREATURE_PERIOD_TIME, TimeUnit.MILLISECONDS);


        // perform a simulation n times;
        executeCycles(9, statisticsCollector);
        simulationExecutor.getSes().shutdown();

        /*
         * {@code executionCompleted} is true if a simulation completed successfully
         *  before time terminated. Otherwise, it is false;
         */
        boolean executionCompleted = simulationExecutor.getSes().awaitTermination(
                CREATURE_PERIOD_TIME * 3, TimeUnit.MILLISECONDS);
        if (!executionCompleted)
            throw new TimeoutException("Not enough time to complete.");

        // perform last collecting of statistics
        statisticsCollector.performStatsCollection();

        // print statistics
        System.out.println(statisticsCollector.toString());
    }

    /**
     * This method makes a main thread to sleep until execution of simulation will end.
     * @param cyclesPerform a number of days to perform a simulation;
     * @param statsColl an object that collects a statistics about simulation;
     */
    private static void executeCycles(int cyclesPerform, StatisticsCollector statsColl) {
        statsColl.waitUntilRounds(rounds -> rounds != cyclesPerform);
    }

    /**
     * Creates a {@code List} of {@code Quadrant} objects.
     * @param simulationMap a map where the simulation is happening;
     * @param simulationExecutor executor of the simulation;
     * @param statisticsCollector a collector of simulation statistics;
     * @return {@code List} of initialized {@code Quadrant} objects;
     */
    private static List<Quadrant> createQuadrantsList(Map simulationMap,
                                                      SimulationExecutor simulationExecutor,
                                                      StatisticsCollector statisticsCollector)
    {
        List<Quadrant> quadrantList = new ArrayList<>(MainSimulationCenter.INIT_NUMBER_OF_CREATURES);

        // fill quadrantList
        for (int i = 0; i < MainSimulationCenter.INIT_NUMBER_OF_CREATURES; i++) {
            Quadrant.Pair<Integer, Integer> coordinates = Quadrant.getPerimeterCoordinates(simulationMap);
            quadrantList.add(new Quadrant(simulationMap, coordinates.firstValue(),
                    coordinates.secondValue(), simulationExecutor, statisticsCollector));
        }
        return quadrantList;
    }
}
