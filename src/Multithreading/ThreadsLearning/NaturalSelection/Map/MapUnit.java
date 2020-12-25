package Multithreading.ThreadsLearning.NaturalSelection.Map;

import java.util.concurrent.atomic.AtomicBoolean;

public class MapUnit {
    private final AtomicBoolean hasFood = new AtomicBoolean(false);

    public void removeFood() {
        hasFood.getAndSet(false);
    }

    public void putFood() {

        // don't use CAS because this value will be set
        // by only one thread at start of each cycle
        hasFood.set(true);
    }
}
