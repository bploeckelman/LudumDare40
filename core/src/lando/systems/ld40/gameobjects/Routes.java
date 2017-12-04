package lando.systems.ld40.gameobjects;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by Brian on 12/3/2017.
 */
public class Routes {
    public Array<DumpTruck> trucks = new Array<DumpTruck>();
    public ObjectMap<DumpTruck, IntArray> routes = new ObjectMap<DumpTruck, IntArray>();

    public Routes() {
        addRoute(DumpTruck.One);
        // temp
        addRoute(DumpTruck.Five);
        addRoute(DumpTruck.Nine);
    }

    public void addRoute(DumpTruck.TruckType type) {
        DumpTruck truck = new DumpTruck(type);
        trucks.add(truck);
        routes.put(truck, new IntArray(type.speed));
    }

    public void setRoute(DumpTruck truck, IntArray route) {
        routes.put(truck, route);
    }

    public void removeRoute(DumpTruck dumpTruck) {
        trucks.removeValue(dumpTruck, true);
        routes.remove(dumpTruck);
    }
}
