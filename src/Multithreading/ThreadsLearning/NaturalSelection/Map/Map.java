package Multithreading.ThreadsLearning.NaturalSelection.Map;

import java.util.Random;
import java.util.function.Consumer;

public class Map {
    private final MapUnit[][] map;
    private final int rows;
    private final int columns;

    public Map(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.map = new MapUnit[rows][columns];

        // initialize map
        initializeMap();

        // fills map with food at random places
        fillMapWithFood();
    }

    private void initializeMap() {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                map[i][j] = new MapUnit();
    }

    /**
     * Performs an operation on each map unit.
     */
    private void forEachMatrix(Consumer<MapUnit> consumer) {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                consumer.accept(map[i][j]);
    }

    /**
     *  Returns random value that is < {@param upperBound};
     */
    public int getRandomValue(Random random, int upperBound) {
        return random.nextInt(upperBound);
    }

    /**
     *  This method returns a value in the lower and upper bounds range, INCLUDING both bounds.
     */
    public int getRandomValue(Random random, int lowerBound, int upperBound) {
        return lowerBound + random.nextInt(upperBound - lowerBound + 1);
    }

    /* Getters */
    public int getNumOfRows() {
        return rows;
    }

    public int getNumOfColumns() {
        return columns;
    }

    /**
     *  Removes food in each map unit.
     */
    public void clearMapFood() {
        forEachMatrix(MapUnit::eatFood);
    }

    /**
     *  Fills map with food at random positions.
     */
    public void fillMapWithFood() {
        var random = new Random();

        // 22% will contain food. Use additional 2% (instead of 20%)
        // to fill some more units in case random function can return
        // similar row and column.
        int unitsToFill = (int)(rows * columns * 0.22);

        for (int i = 0; i < unitsToFill; i++) {
            int rowToFill = getRandomValue(random, rows),
                columnToFill = getRandomValue(random, columns);
            map[rowToFill][columnToFill].putFood();
        }
    }

    /**
     * @return MapUnit which correspond to coordinates {@param row}
     * and {@param column}.
     * Can produce IndexOutOfBoundsException if {@param row} >= rows or
     * {@param column} >= columns.
     */
    public MapUnit getMapUnitWithCoordinates(int row, int column) {
        if (row >= rows || column >= columns)
            throw new IndexOutOfBoundsException("Row or column value is out" +
                    " of bounds of matrix size.");
        return map[row][column];
    }

}
