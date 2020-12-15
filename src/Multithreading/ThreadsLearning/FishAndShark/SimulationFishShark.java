package Multithreading.ThreadsLearning.FishAndShark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class SimulationFishShark {
    private static final int NUM_FISH  = 1200;
    private static final int NUM_SHARK = 15;
    private static final long SECONDS_RUN = 5;

//    public static final Logger logger = Logger.getLogger(SimulationFishShark.class.getName());

    public static void main(String[] args) throws InterruptedException {

        ExecutorService es = Executors.newFixedThreadPool(NUM_FISH + NUM_SHARK);
        Lake lake = new Lake(90, 90);

        List<Fish> fishList = new ArrayList<>();
        for (int i = 0; i < NUM_FISH; i++)
            fishList.add(new Fish(SECONDS_RUN, lake, Logger.getLogger("1")));

        List<Shark> sharkList = new ArrayList<>();
        for (int i = 0; i < NUM_SHARK; i++)
            sharkList.add(new Shark(SECONDS_RUN, lake, Logger.getLogger("2")));

        lake.fillLakeWithFish(fishList);
        lake.fillLakeWithSharks(sharkList);

        for (Shark shark : sharkList)
            es.submit(shark);

        for (Fish fish : fishList)
            es.submit(fish);

        es.shutdown();

        boolean ended = es.awaitTermination(SECONDS_RUN + 1, TimeUnit.SECONDS);
        if (!ended)
            throw new RuntimeException("Time " + SECONDS_RUN + " isn't enough to execute.");

        int numberOfEatenFish = 0;
        for (Shark shark : sharkList)
            numberOfEatenFish += shark.getFishEaten();

        System.out.println("Simulation ended. Number of fish eaten is " + numberOfEatenFish);
    }
}
