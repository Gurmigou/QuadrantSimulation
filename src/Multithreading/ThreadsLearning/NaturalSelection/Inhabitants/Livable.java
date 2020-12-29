package Multithreading.ThreadsLearning.NaturalSelection.Inhabitants;

/**
 * An interface that provides a some method to perform a simulation.
 */
public interface Livable {
    /**
     *  The implementor of this interface should
     *  implement the ability to move.
     */
    void move();

    /**
     * The implementor of this interface should
     * increase a counter of eaten food-units when
     * this method is invoked.
     */
    void eat();

    /**
     * This method displays how many food-units the creature has already eaten.
     * @return a number of eaten food-units;
     */
    int numHasEatenCurrentRound();


    /**
     * This method should be used to create a new creature if a return type is "true".
     * @return "true" if a current creature can reproduce a new one.
     *         Otherwise, false;
     */
    boolean canReproduce();

    /**
     * The creature should stop participating in the simulation if it hasn't eaten
     * any food-units during a simulation round.
     * @return true if a creature should die. Otherwise, false.
     */
    boolean die();

    /**
     * The creature should perform a reproduce action.
     * Moreover, a counter of eaten food should be set to zero.
     * @return a Thread which represents a new creature.
     */
    Quadrant reproduce();
}
