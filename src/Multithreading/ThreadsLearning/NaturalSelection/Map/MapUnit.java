package Multithreading.ThreadsLearning.NaturalSelection.Map;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class that represents a single map unit of a total map.
 * It contains information only about if the current unit has food or not.
 */
public class MapUnit {
    // a flat that contains info is food at current unit is present or not;
    private final AtomicBoolean hasFood = new AtomicBoolean(false);

    /**
     *  Returns true if a current map unit has contained
     *  food and it was successfully eaten.
     */
    public boolean eatFood() {
        return hasFood.getAndSet(false);
    }

    /**
     *  This method puts a food at current map unit;
     *  A field "hasFood" will be set to true after
     *  this method is invoked.
     */
    public void putFood() {
        // don't use CAS because this value will be set
        // by only one thread at start of each cycle
        hasFood.set(true);
    }
}
