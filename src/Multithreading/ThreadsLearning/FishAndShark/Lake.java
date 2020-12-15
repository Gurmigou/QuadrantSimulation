package Multithreading.ThreadsLearning.FishAndShark;

import java.util.*;

public class Lake {
    private final LakeUnit[][] lake;
    private final int rows;
    private final int columns;

    private static final Random random = new Random();

    public synchronized Pair getNewRandomCoordinates() {
        return new Pair(random.nextInt(rows), random.nextInt(columns));
    }

    public Lake(int rows, int columns) {
        this.lake = new LakeUnit[rows][columns];
        this.rows = rows;
        this.columns = columns;
        initLake(); // initialize lake with LakeUnits;
    }

    public static record Pair(int row, int column) {
        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || this.getClass() != obj.getClass()) return false;
            Pair castedObj = (Pair) obj;
            return castedObj.row == this.row &&
                   castedObj.column == this.column;
        }

        @Override
        public int hashCode() {
            if (row == 0)
                return Objects.hash(column * 180);
            else if (column == 0)
                return Objects.hash(row * 215);
            else
                return Objects.hash((row % column) * row * column);
        }
    }

    private void initLake() {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                lake[i][j] = new LakeUnit();
    }

    public void fillLakeWithFish(List<Fish> fishList) {
        var randomLocal = new Random();
        Set<Pair> fishCoordinates = new HashSet<>();
        for (Fish fish : fishList) {
            int newRow, newColumn;
            Pair newCoordinatesPair;
            do {
                newRow = randomLocal.nextInt(rows);
                newColumn = randomLocal.nextInt(columns);
                newCoordinatesPair = new Pair(newRow, newColumn);
            } while (fishCoordinates.contains(newCoordinatesPair));

            fishCoordinates.add(newCoordinatesPair);
            lake[newRow][newColumn].putFish(fish);
            fish.setCurRow(newRow);
            fish.setCurColumn(newColumn);
        }
    }

    public void fillLakeWithSharks(List<Shark> sharkList) {
        var randomLocal = new Random();
        Set<Pair> sharkCoordinates = new HashSet<>();
        for (Shark shark : sharkList) {
            int newRow, newColumn;
            Pair newCoordinatesPair;
            do {
                newRow = randomLocal.nextInt(rows);
                newColumn = randomLocal.nextInt(columns);
                newCoordinatesPair = new Pair(newRow, newColumn);
            } while (sharkCoordinates.contains(newCoordinatesPair));

            sharkCoordinates.add(newCoordinatesPair);
            lake[newRow][newColumn].putFish(shark);
            shark.setCurRow(newRow);
            shark.setCurColumn(newColumn);
        }
    }

    public LakeUnit getLakeUnit(int row, int column) {
        return lake[row][column];
    }
}
