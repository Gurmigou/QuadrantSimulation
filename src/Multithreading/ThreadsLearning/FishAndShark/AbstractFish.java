package Multithreading.ThreadsLearning.FishAndShark;

import java.util.logging.Logger;

public abstract class AbstractFish implements Runnable
{
    private static final long timeStart = System.currentTimeMillis();
    private static long timeEnd;
    private final Lake lake;

    private int curRow;
    private int curColumn;

    protected final Logger logger;

    public AbstractFish(long secondsToExecute, Lake lake, Logger logger) {
        AbstractFish.timeEnd = timeStart + secondsToExecute * 1000;
        this.lake = lake;
        this.logger = logger;
    }

    // This method should be overridden
    public abstract void run();

    public static long getTimeEnd() {
        return timeEnd;
    }

    public Lake getLake() {
        return lake;
    }

    public int getCurRow() {
        return curRow;
    }

    public int getCurColumn() {
        return curColumn;
    }

    public void setCurRow(int curRow) {
        this.curRow = curRow;
    }

    public void setCurColumn(int curColumn) {
        this.curColumn = curColumn;
    }
}
