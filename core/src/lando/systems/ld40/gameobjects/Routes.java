package lando.systems.ld40.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by Brian on 12/3/2017.
 */
public class Routes {
    public Array<DumpTruck> trucks = new Array<DumpTruck>();
    public ObjectMap<DumpTruck, IntArray> routes = new ObjectMap<DumpTruck, IntArray>();

    public Array<Color> routeColors = new Array<Color>();

    public Routes() {
        setColors();
        addRoute(DumpTruck.One);
        // temp
        addRoute(DumpTruck.Five);
        addRoute(DumpTruck.Nine);
    }

    private void setColors() {
        addColor(23, 13, 32);
        addColor(104,41,62);
        addColor(129,96,49);
        addColor(1,163,195);
        addColor(71,71,87);
        addColor(169,68,0);
    }

    public Color getColor(int index) {
        return routeColors.get(index % routeColors.size);
    }

    public Color getColor(DumpTruck truck) {
        int index = trucks.indexOf(truck, true);
        return getColor(index);
    }

    private void addColor(int r, int g, int b) {
        routeColors.add(new Color(r/255f, g/255f, b/255f, 0.8f));
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
