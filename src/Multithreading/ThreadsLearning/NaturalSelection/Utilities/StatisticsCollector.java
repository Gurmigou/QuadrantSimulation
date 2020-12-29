package Multithreading.ThreadsLearning.NaturalSelection.Utilities;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.ArrayList;

/**
 *  This class is used to collect statistics about creatures;
 */
public class StatisticsCollector {
    /**
     *  This list contains a number of eaten food at each day.
     *  An index is (a day number + 1). For ex. index 0 is a first day,
     *  index 1 is seconds day...
     */
    private final ArrayList<Integer> statistics = new ArrayList<>();

    /**
     *  Atomic integer field that will be modified by multiple threads.
     *  This field reflects a number of creatures that are alive at this round.
     */
    private final AtomicInteger numOfAlive = new AtomicInteger(0);

    /**
     * This method adds a new day to {@code statistics} list and set a current
     * value of {@code numOfAlive} to zero.
     */
    public void performStatsCollection() {
        statistics.add(numOfAlive.getAndSet(0));
    }

    /**
     * This method increases a number of alive creatures at the current round.
     */
    public void increaseNumOfAlive() {
        numOfAlive.incrementAndGet();
    }

    /**
     * @return number of rounds collected by this {@code StatisticsCollector}.
     */
    public int getRoundsCollected() {
        return statistics.size();
    }

    /**
     * A helper method of {@code MainSimulationCenter}.
     * This is a helper method to make a thread wait until
     * a condition in {@code Predicate} object is true.
     * @param predicate the condition to check;
     */
    public synchronized void waitUntilRounds(Predicate<Integer> predicate) {
        while (predicate.test(statistics.size())) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * A helper method of {@code MainSimulationCenter}.
     * This is a helper method to notify the other threads (which fall asleep
     * using monitor of the current object).
     */
    public synchronized void notifyWaiters() {
        this.notify();
    }

    /**
     * Returns a string representation of the object.
     */
    @Override
    public String toString() {
        var sb = new StringBuilder();
        int round = 1;
        for (int value : statistics) {
            if (round != statistics.size())
                sb.append(round++).append(") день: ").append(value).append(" существ.\n");
        }
        return new String(sb);
    }
}
