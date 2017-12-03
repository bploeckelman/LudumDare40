package lando.systems.ld40.gameobjects;

import com.badlogic.gdx.utils.Array;

/**
 * Created by Brian on 12/3/2017.
 */
public class Routes {
    public Array<DumpTruck> trucks = new Array<DumpTruck>();

    public Routes() {
        trucks.add(DumpTruck.getTruck(DumpTruck.One));
    }

    public void addTruck(DumpTruck.TruckType type) {
        trucks.add(DumpTruck.getTruck(type));
    }
}
