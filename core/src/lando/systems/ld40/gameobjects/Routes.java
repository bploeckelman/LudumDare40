package lando.systems.ld40.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.world.World;

/**
 * Created by Brian on 12/3/2017.
 */
public class Routes {
    public Array<DumpTruck> trucks = new Array<DumpTruck>();
    public ObjectMap<DumpTruck, IntArray> routes = new ObjectMap<DumpTruck, IntArray>();
    public ObjectMap<DumpTruck, CatmullRomSpline<Vector2>> routesSpline = new ObjectMap<DumpTruck, CatmullRomSpline<Vector2>>();
    public Array<Color> routeColors = new Array<Color>();

    public Routes() {
        setColors();
        addRoute(DumpTruck.One);
        // temp
        addRoute(DumpTruck.One);
        addRoute(DumpTruck.One);

    }

    private void setColors() {
        addColor(217, 126, 0);
        addColor(1,163,195);
        addColor(235,255,218);
        addColor(90,178,23);
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
        routeColors.add(new Color(r/255f, g/255f, b/255f, 1f));
    }

    public void addRoute(DumpTruck.TruckType type) {
        DumpTruck truck = new DumpTruck(type);
        trucks.add(truck);
        routes.put(truck, new IntArray(type.speed));
    }

    public void setRoute(DumpTruck truck, IntArray route) {
        routes.put(truck, route);
        buildSpline(truck, route);
    }

    public void buildSpline(DumpTruck truck, IntArray route){
        if (route.size == 0) return;
        float thickness = 25;
        int index = trucks.indexOf(truck, true);
        World world = World.GetWorld();
        Vector2[] cp = new Vector2[route.size + 4];
        Vector2 point1  = world.getPoint(world.hqIndex, index, thickness);
        cp[0] = point1;
        cp[1] = point1;

        for (int i = 0; i < route.size; i++) {
            point1 = world.getPoint(route.get(i), index, thickness);
            cp[i + 2] = point1;
        }

        point1 = world.getPoint(world.hqIndex, index, thickness);

        cp[route.size + 2] = point1;
        cp[route.size + 3] = point1;

        CatmullRomSpline<Vector2> spline = new CatmullRomSpline<Vector2>(cp, false);
        routesSpline.put(truck, spline);
    }

    public void removeRoute(DumpTruck dumpTruck) {
        trucks.removeValue(dumpTruck, true);
        routes.remove(dumpTruck);
    }

    public void verifyRoutesAfterBuildingChange(int index){
        for (DumpTruck truck: trucks){
            if (routes.get(truck).contains(index)){
                routes.get(truck).clear();
                buildSpline(truck, routes.get(truck));
            }
        }
    }
}
