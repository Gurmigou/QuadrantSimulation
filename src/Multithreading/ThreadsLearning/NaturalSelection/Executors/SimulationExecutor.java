package Multithreading.ThreadsLearning.NaturalSelection.Executors;

import Multithreading.ThreadsLearning.NaturalSelection.Inhabitants.Quadrant;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *  A class that executes all {@code Runnable} tasks that
 *  are passed through parameters. Moreover, this class
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
    // using an AtomicBoolean to provide non-blocking access
    private final AtomicBoolean updaterWorks = new AtomicBoolean(false);

    // params of execution
    private final long initDelay;
    private final long periodTime;
    private final TimeUnit timeUnit;

    public SimulationExecutor(int numberOfThreads, long initDelay,
                              long periodTime, TimeUnit timeUnit)
    {
        // set the max number of executing threads depending on param "numberOfThreads";
        this.ses = Executors.newScheduledThreadPool((numberOfThreads < 50) ?
                                                    50 : numberOfThreads * 50);
        this.initDelay = initDelay;
        this.periodTime = periodTime;
        this.timeUnit = timeUnit;
    }

    /**
     *  Adds a list of commands to execution.
     */
    public void addPeriodicExecutorTasks(List<Quadrant> quadrantList) {
        ConcurrentHashMap<Quadrant, ScheduledFuture<?>> scheduledFutureMap
                = new ConcurrentHashMap<>();
        for (var quadrant : quadrantList) {
            // add a future map to quadrant object;
            quadrant.setScheduledFutureMap(scheduledFutureMap);

            // start an execution;
            scheduledFutureMap.put(quadrant, ses.scheduleAtFixedRate(
                    quadrant, initDelay, periodTime, timeUnit));
        }
    }

    /**
     *  Adds a command to execution.
     */
    public void addPeriodicExecutorTask(Runnable command, long initDelay,
                                        long periodTime, TimeUnit timeUnit) {
        ses.scheduleAtFixedRate(command, initDelay, periodTime, timeUnit);
    }

    /**
     *  Adds a new quadrant object to the waiting queue. Then this object
     *  will start its execution. Starts a new thread that will operate the
     *  requests of adding the new {@code Quadrant} objects to {@code ScheduledExecutorService}.
     *  Moreover, this thread is started once when this method is invoked for the first time.
     */
    public void addNewQuadrantToQueue(Quadrant quadrantToAdd,
                                      long timePeriodBeforeStartMillis,
                                      ConcurrentHashMap<Quadrant, ScheduledFuture<?>>
                                                                  scheduledFutureMap)
    {
        waitingQuadrants.add(quadrantToAdd);
        // using CAS mechanism
        if (!updaterWorks.getAndSet(true)) {
            startUpdating(timePeriodBeforeStartMillis, scheduledFutureMap);
        }
    }

    /**
     * This method starts a new thread which will operate with new creatures
     * that are pushed into a blocking queue. This thread will wait until a queue
     * is empty. Moreover, this thread will run until it is interrupted.
     *
     * This field is set once when a method {@code startUpdating(long timePeriodBeforeStart,
     * TimeUnit timeUnit)} is invoked for the first time. This is made because everything in
     * a program happens periodically and there is no difference in this param during the
     * life of the program. In other words, this param is constant.
     * @param timePeriodBeforeStartMillis time in millis which all
     *                                    new creatures will wait
     *                                    until start their execution.
     */
    private void startUpdating(long timePeriodBeforeStartMillis,
                               ConcurrentHashMap<Quadrant, ScheduledFuture<?>>
                                                           scheduledFutureMap)
    {
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted() && !ses.isShutdown()) {
                try {
                    // Tries to poll the first element in the blocking queue. The thread stops
                    // waiting if there is no adding of element until the "periodTime" elapses;
                    Quadrant newQuadrant = waitingQuadrants.poll(periodTime, TimeUnit.MILLISECONDS);

                    if (!ses.isShutdown() && newQuadrant != null) {
                        // add a pair key - value of an object and a related future object;
                        scheduledFutureMap.put(newQuadrant, ses.scheduleAtFixedRate(
                                newQuadrant, timePeriodBeforeStartMillis,
                                periodTime, timeUnit));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /* Getter */
    public ScheduledExecutorService getSes() {
        return ses;
    }
}
