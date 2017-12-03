package lando.systems.ld40.gameobjects;

/**
 * Created by Brian on 12/3/2017.
 */
public class DumpTruck {
    public static class TruckType {
        public int speed = 2;
        public int capacity = 50;

        public TruckType(int speed, int capacity) {
            this.speed = speed;
            this.capacity = capacity;
        }
    }

    public static final TruckType One = new TruckType(2, 50);
    public static final TruckType Two = new TruckType(2, 100);
    public static final TruckType Three = new TruckType(2, 200);
    public static final TruckType Four = new TruckType(3, 50);
    public static final TruckType Five = new TruckType(3, 100);
    public static final TruckType Six = new TruckType(3, 200);
    public static final TruckType Seven = new TruckType(4, 50);
    public static final TruckType Eight = new TruckType(4, 100);
    public static final TruckType Nine = new TruckType(4, 200);

    public final UpgradeType type = UpgradeType.TRUCK;

    public int speed;
    public int capacity;

    public static DumpTruck getTruck(TruckType stats) {

        DumpTruck truck = new DumpTruck();
        truck.speed = stats.speed;
        truck.capacity = stats.capacity;
        return truck;
    }
}
