package lando.systems.ld40.gameobjects;

import lando.systems.ld40.utils.Assets;

/**
 * Created by Brian on 12/3/2017.
 */
public class DumpTruck extends GameObject {
    public static class TruckType {
        public int speed = 2;
        public int capacity = 50;
        public String image;

        public TruckType(int speed, int capacity, String image) {
            this.speed = speed;
            this.capacity = capacity;
            this.image = image;
        }
    }

    public static final TruckType One = new TruckType(1, 50, "truck1");
    public static final TruckType Two = new TruckType(1, 100, "truck2");
    public static final TruckType Three = new TruckType(1, 200, "truck3");
    public static final TruckType Four = new TruckType(2, 50, "truck1");
    public static final TruckType Five = new TruckType(2, 100, "truck2");
    public static final TruckType Six = new TruckType(2, 200, "truck3");
    public static final TruckType Seven = new TruckType(3, 50, "truck1");
    public static final TruckType Eight = new TruckType(3, 100, "truck2");
    public static final TruckType Nine = new TruckType(3, 200, "truck3");

    public final UpgradeType type = UpgradeType.TRUCK;

    public int speed;
    public int capacity;

    public DumpTruck(TruckType type) {
        setTexture(Assets.atlas.findRegion(type.image));
        speed = type.speed;
        capacity = type.capacity;
    }
}
