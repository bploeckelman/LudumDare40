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
    }

    public void addRoute(DumpTruck.TruckType type) {
        DumpTruck truck = new DumpTruck(type);
        trucks.add(truck);
        routes.put(truck, new IntArray(type.speed));
    }

    public void addIndex(DumpTruck dumpTruck, int index) {
        IntArray route = routes.get(dumpTruck);
        int existingIndex = route.indexOf(index);
        if (existingIndex != -1) {
            route.removeIndex(existingIndex);
        }
        route.add(index);
    }

    public void removeRoute(DumpTruck dumpTruck) {
        trucks.removeValue(dumpTruck, true);
        routes.remove(dumpTruck);
    }
}
