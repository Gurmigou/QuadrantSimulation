package Multithreading.ThreadsLearning.NaturalSelection.Executors;

import Multithreading.ThreadsLearning.NaturalSelection.Inhabitants.Quadrant;
import java.util.List;
import java.util.concurrent.*;

/**
 *  A class that executes all runnable tasks that
 *  are passed through params. Moreover, this class
 *  provides a mechanism for processing new threads
 *  that are added to the blocking queue.
 */
public class SimulationExecutor {
    /**
     *  An executor of threads (creatures).
     */
    private final ScheduledExecutorService ses;

    // a queue that contains new quadrants that have to be added to executor service;
    private final BlockingQueue<Quadrant> waitingQuadrants =
            new LinkedBlockingQueue<>();

    // a flag that is true if an updater of waiting the queue works. Otherwise, it is false;
    private boolean updaterWorks = false;

    // params of execution
    private final long initDelay;
    private final long periodTime;
    private final TimeUnit timeUnit;

    public SimulationExecutor(int numberOfThreads, long initDelay,
                              long periodTime, TimeUnit timeUnit) {
        this.ses = Executors.newScheduledThreadPool(numberOfThreads * 2);
        this.initDelay = initDelay;
        this.periodTime = periodTime;
        this.timeUnit = timeUnit;
    }

    /**
     *  Adds to execution a list of commands.
     */
    public void addPeriodicExecutorTasks(List<Quadrant> quadrantList) {
        for (var quadrant : quadrantList)
            ses.scheduleAtFixedRate(quadrant, initDelay, periodTime, timeUnit);
    }

    /**
     *  Adds to execution a command.
     */
    public void addPeriodicExecutorTask(Runnable command, long initDelay,
                                        long periodTime, TimeUnit timeUnit) {
        ses.scheduleAtFixedRate(command, initDelay, periodTime, timeUnit);
    }

    /**
     *  Adds a new quadrant object to waiting queue. Then this object
     *  will start its execution. Invokes a method update SES to to execution
     *  of added object;
     */
    public void addNewQuadrantToQueue(Quadrant quadrantToAdd, long timePeriodBeforeStartMillis) {
        waitingQuadrants.add(quadrantToAdd);
        if (!updaterWorks) {
            updaterWorks = true;
            startUpdating(timePeriodBeforeStartMillis);
        }
    }

    /**
     * This method starts a new thread which will operate with new creatures
     * that a pushed into a blocking queue. This thread will wait until a queue
     * is empty. Moreover, this thread will run until it is interrupted.
     *
     * This field is set once when a method
     * {@code startUpdating(long timePeriodBeforeStart, TimeUnit timeUnit)} is
     * invoked at first time.This is made because everything in a program
     * happens periodically and there is no difference in this param during the
     * life of the program. In other words, this param is constant.
     * @param timePeriodBeforeStartMillis time in millis which all
     *                                    new creatures will wait
     *                                    until start their execution.
     */
    private void startUpdating(long timePeriodBeforeStartMillis) {
        while (!ses.isTerminated()) {
            new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Quadrant newQuadrant = waitingQuadrants.take();

                        ses.scheduleAtFixedRate(newQuadrant, timePeriodBeforeStartMillis,
                                periodTime, timeUnit);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
