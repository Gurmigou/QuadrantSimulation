package Multithreading.ThreadsLearning.NaturalSelection.Map;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class that represents a single map-unit of a total map.
 * It contains information only about if the current unit has food or not.
 */
public class MapUnit {
    // an atomic boolean flag that contains info if the food is present at current unit
    private final AtomicBoolean hasFood = new AtomicBoolean(false);

    /**
     *  @return true if a current map unit contains food.
     */
    public boolean eatFood() {
        return hasFood.getAndSet(false);
    }

    /**
     *  This method puts a food at current map unit.
     *  After calling this method a field "hasFood"
     *  will be set to true.
     */
    public void putFood() {
        // don't use CAS because this value will be set
        // by only one thread at the start of each round.
        hasFood.set(true);
    }
}
