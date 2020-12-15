package Multithreading.ThreadsLearning.FishAndShark;

import java.util.logging.Logger;

public class Fish extends AbstractFish {
    private boolean stopThread;

    public Fish(long secondsToExecute, Lake lake, Logger logger) {
        super(secondsToExecute, lake, logger);
    }

    public void stopThread() {
        this.stopThread = true;
    }

    @Override
    public void run() {
        long curTime = System.currentTimeMillis();
        Lake lake = getLake();
        while (curTime <= getTimeEnd() && !stopThread) {

            int curRow = getCurRow();
            int curColumn = getCurColumn();

            // remove fish
            lake.getLakeUnit(curRow, curColumn).removeFish(this);
//            logger.info( "Fish has left row: " + curRow +
//                    " column: " + curColumn + ".");

            if (stopThread) break;

            // create new coordinates
            Lake.Pair nc = lake.getNewRandomCoordinates();

            int newRow = nc.row();
            int newColumn = nc.column();
//            // get current LakeUnit
//            if (newRow == curRow && newColumn == curColumn)
//                continue;

            setCurRow(newRow);
            setCurColumn(newColumn);

            lake.getLakeUnit(newRow, newColumn).putFish(this);
//            logger.info("Fish has joined row: " + newRow +
//                    " column: " + newColumn + ".");

            curTime = System.currentTimeMillis();

            try {
                Thread.sleep(8);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
