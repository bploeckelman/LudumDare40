package lando.systems.ld40.world;

public class TurnStatistics {

    public int turnNumber;
    /**
     * The total amount of money the player currently has.
     */
    public int money;
    public int buildings;

    public TurnStatistics(int money) {
        this.money = money;
    }
}
