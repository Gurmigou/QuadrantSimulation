package Multithreading.ThreadsLearning.FishAndShark;

import java.util.List;
import java.util.logging.Logger;

public class Shark extends AbstractFish {
    private static int next = 1;
    private final int current;
    private int fishEaten;

    public Shark(long secondsToExecute, Lake lake, Logger logger) {
        super(secondsToExecute, lake, logger);
        this.current = next++;
    }

    @Override
    public void run() {
        long curTime = System.currentTimeMillis();
        // current Lake
        Lake lake = getLake();
        while (curTime <= getTimeEnd()) {
            int curRow = getCurRow();
            int curColumn = getCurColumn();

            // remove shark
            lake.getLakeUnit(curRow, curColumn).removeShark();
//            logger.info("Shark #" + current + " has left row: " + curRow +
//                    " column: " + curColumn + ". Eaten = " + fishEaten + ".");

            // create new coordinates
            Lake.Pair nc = lake.getNewRandomCoordinates();

            int newRow = nc.row();
            int newColumn = nc.column();

            setCurRow(newRow);
            setCurColumn(newColumn);

            lake.getLakeUnit(newRow, newColumn).putFish(this);
//            logger.info("Shark #" + current + " has joined row: " + newRow +
//                    " column: " + newColumn + ". Eaten = " + fishEaten + ".");

            curTime = System.currentTimeMillis();

            try {
                Thread.sleep(8);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void eatFish(List<Fish> fishList) {
        fishList.forEach(Fish::stopThread);
        fishEaten += fishList.size();
    }

    public void eatFish(Fish fish) {
        fish.stopThread();
        fishEaten++;
    }

    public int getFishEaten() {
        return fishEaten;
    }
}
