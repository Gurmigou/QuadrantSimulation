package Multithreading.ThreadsLearning.NaturalSelection.Map;

import java.util.Random;
import java.util.function.Consumer;

/**
 *  This is a map where the simulation executes.
 */
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

    /**
     *  Initializes a map creating new {@code MapUnit} objects
     */
    private void initializeMap() {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                map[i][j] = new MapUnit();
    }

    /**
     *  @return a random value that is < {@param upperBound};
     */
    public int getRandomValue(Random random, int upperBound) {
        return random.nextInt(upperBound);
    }

    /**
     *  @return a value in the lower and upper bounds range, INCLUDING both bounds.
     */
    public int getRandomValue(Random random, int lowerBound, int upperBound) {
        return lowerBound + random.nextInt(upperBound - lowerBound + 1);
    }

    /**
     *  Performs an operation on each map unit.
     */
    private void forEachMatrix(Consumer<MapUnit> consumer) {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                consumer.accept(map[i][j]);
    }

    /**
     *  Removes food in each map unit.
     */
    public void clearMapFood() {
        forEachMatrix(MapUnit::eatFood);
    }

    /**
     *  Fills a map with food at random positions.
     */
    public void fillMapWithFood() {
        final var random = new Random();
        // ~10% will contain food. Use an additional 1% to fill
        // some more units in case a random function can return
        // a similar row and column.
        int unitsToFill = (int)(rows * columns * 0.11);

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

    /* Getters */
    public int getNumOfRows() {
        return rows;
    }

    public int getNumOfColumns() {
        return columns;
    }
}
