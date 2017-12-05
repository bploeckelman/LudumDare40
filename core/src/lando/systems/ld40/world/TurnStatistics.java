package lando.systems.ld40.world;

public class TurnStatistics {

    public int turnNumber;
    /**
     * The total amount of money the player currently has.
     */
    public int money;
    public int buildings;
    public int addons;
    public float garbageGenerated;
    public float garbageHauled;
    public float garbageInLandFills;


    public TurnStatistics(int money) {
        this.money = money;
        this.buildings = 0;
        this.addons = 0;
        this.garbageGenerated = 0;
        this.garbageHauled = 0;
        this.garbageInLandFills = 0;
        this.turnNumber = 1;
    }
}
