package Multithreading.ThreadsLearning.FishAndShark;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LakeUnit {
    private final List<Fish> fishList = new CopyOnWriteArrayList<>();
    private Shark shark;

    public void putFish(Shark shark) {
        if (!fishList.isEmpty()) {
            synchronized (this) {
                shark.eatFish(fishList);
                this.fishList.clear();
            }
        }
        this.shark = shark;
    }

    public void removeShark() {
        this.shark = null;
    }

    public void putFish(Fish fish) {
        if (shark != null)
            shark.eatFish(fish);
        else
            this.fishList.add(fish);
    }
    // todo
    public void removeFish(Fish fish) {
        fishList.remove(fish);
    }
}
