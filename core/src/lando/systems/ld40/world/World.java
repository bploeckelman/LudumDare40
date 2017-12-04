package lando.systems.ld40.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import lando.systems.ld40.buildings.Building;
import lando.systems.ld40.gameobjects.GameObject;
import lando.systems.ld40.gameobjects.Inventory;
import lando.systems.ld40.gameobjects.Routes;
import lando.systems.ld40.spline.Precision;
import lando.systems.ld40.spline.SinglePointSplineIterator;
import lando.systems.ld40.spline.SplinePoint;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.Config;

public class World {

    public enum FilterType { None, Source, Desitination }

    private static World world;

    public static final int tiles_wide = 13;
    public static final int tiles_high = 11;
    public static final int hq_x = 6;
    public static final int hq_y = 5;
    public static int hqIndex;
    public static final float tile_pixels_wide = 128;
    public static final float tile_pixels_high = 128;
    public static final float pixels_wide = tiles_wide * tile_pixels_wide;
    public static final float pixels_high = tiles_high * tile_pixels_high;

    private FilterType filter;

    public static World GetWorld(){
        if (world == null){
            world = new World();
        }
        return world;
    }

    public static Array<Building> buildings;
    public Rectangle bounds;
    public int turnNumber;
    public Inventory inventory;
    public Routes routes;

    private int renderIndex = 0;
    private float time = 0;

    public World() {
        turnNumber = 0;
        bounds = new Rectangle(0, 0, pixels_wide, pixels_high);
        inventory = new Inventory();
        routes = new Routes();

        buildings = new Array<Building>();
        for (int y = 0; y < tiles_high; ++y) {
            for (int x = 0; x < tiles_wide; ++x) {
                Building.Type type = Building.Type.EMPTY;
                if (x == hq_x && y == hq_y) {
                    type = Building.Type.GARBAGE_HQ;
                    hqIndex = buildings.size;
                }

                Building newBuilding = Building.getBuilding(type);
                newBuilding.setLocation(x * tile_pixels_wide, y * tile_pixels_high);
                buildings.add(newBuilding);
            }
        }
        // Reversing the array so that building render order goes from top down
        buildings.reverse();

        setRandom(Building.Type.DUMP);
        setRandom(Building.Type.COMMERCIAL_LOW);
        setRandom(Building.Type.INDUSTRIAL_MEDIUM);
        setRandom(Building.Type.RESIDENTIAL_HIGH);
        setRandom(Building.Type.COMMERCIAL_MEDIUM);
    }

    private void setRandom(Building.Type buildingType) {
        int index;
        do {
            index = MathUtils.random.nextInt(buildings.size - 1);
        } while(!buildings.get(index).canBuild);

        setTile(index, buildingType);
    }

    public void setTile(int index, Building.Type buildingType) {
        Building building = buildings.get(index);
        Building newBuilding = Building.getBuilding(buildingType);
        newBuilding.setLocation(building.position.x, building.position.y);
        buildings.set(index, newBuilding);
        routes.verifyRoutesAfterBuildingChange(index);
    }

    public void replaceTile(Building originalTile, Building.Type newBuildingType) {
        int index = buildings.indexOf(originalTile, true);
        setTile(index, newBuildingType);
    }

    public void update(float dt){
        time += dt;
        if (time > 500) {
            time -= 500;
            if (++renderIndex == routes.trucks.size) {
                renderIndex = 0;
            }
        }
        for (GameObject tile : buildings){
            tile.update(dt);
        }
    }

    public void render(SpriteBatch batch) {
        for (GameObject tile : buildings){
            tile.render(batch);
        }
    }

    public void renderRoutes(SpriteBatch batch, OrthographicCamera camera, IntArray newRoute, int routeIndex) {

        batch.setProjectionMatrix(camera.combined);
        
        if (newRoute != null) {
            renderRoute(newRoute, routeIndex, batch);
        } else {
            for (int i = 0; i < routes.trucks.size; i++) {
                IntArray route = routes.routes.get(routes.trucks.get(i));
                renderRoute(route, i, batch);
            }
        }

    }

    private void renderRoute(IntArray route, int index, SpriteBatch batch) {
        batch.setColor(routes.getColor(index));

        Vector2 point1;

        float thickness = 25;

        if (route.size == 0) return;

        for (int i = 0; i < route.size; i++) {
            point1 = getPoint(route.get(i), index, thickness);
            float stopSize = 50;
            batch.draw(Assets.whiteCircle, point1.x - stopSize/2f, point1.y - stopSize/2f, stopSize, stopSize);

        }

        CatmullRomSpline<Vector2> spline = routes.routesSpline.get(routes.trucks.get(index));
        Precision p = new Precision(60);
        SinglePointSplineIterator iterator = new SinglePointSplineIterator(spline, p);
        SplinePoint splinePoint;

        while ((splinePoint = iterator.getNext()) != null) {
            float dotSize = 30;
              batch.draw(Assets.whiteCircle, splinePoint.point.x - dotSize/2f, splinePoint.point.y - dotSize/2f, dotSize, dotSize);
        }

    }

    public Vector2 getPoint(int bIndex, int rIndex, float thickness) {
        Rectangle bounds = buildings.get(bIndex).bounds;
        Vector2 point = new Vector2(bounds.x + bounds.width/2, bounds.y + bounds.height/2);

        int corner = rIndex % 5;
        switch (corner) {
            case 1:
                point.x = bounds.x + bounds.width - thickness;
                point.y = bounds.y + bounds.height - thickness;
                break;
            case 2:
                point.x = bounds.x + thickness;
                point.y = bounds.y + thickness;
                break;
            case 3:
                point.x = bounds.x + thickness;
                point.y = bounds.y + bounds.height - thickness;
                break;
            case 4:
                point.x = bounds.x + bounds.width - thickness;
                point.y = bounds.y + thickness;
                break;
        }

        return point;
    }


    public GameObject getSelectedObject(float x, float y) {
        int index = getSelectedObjectIndex(x, y);
        System.out.println("tile: " + index);
        return (index == -1) ? null : buildings.get(index);
    }

    public int getSelectedObjectIndex(float x, float y) {
        System.out.println("x: " + x + " y: " + y);
        if (x < 0 || y < 0 || x >= pixels_wide || y >= pixels_high) return -1;

        // Have to reverse these offsets because the buildings array is reversed for painters algorithm reasons
        int xOffset = tiles_wide - 1 - (int)(tiles_wide * x / pixels_wide);
        int yOffset = tiles_high - 1 - (int)(tiles_high * y / pixels_high);

        return yOffset * tiles_wide + xOffset;
    }

    public void nextTurn(){
        Statistics.getStatistics().getCurrentTurnStatistics().buildings = totalBuildings();
        Statistics.getStatistics().onTurnComplete(turnNumber);
        turnNumber++;
        // Reset all buildings!
        for (Building building : buildings) {
            building.resetForNewTurn();
        }
    }

    private int totalBuildings(){
        int count = 0;
        for (Building tile : buildings){
            if (tile.type != Building.Type.EMPTY) count++;
        }
        return count;
    }

    private Array<Building.Type> sources = new Array<Building.Type>(new Building.Type[] {
            Building.Type.COMMERCIAL_HIGH,
            Building.Type.COMMERCIAL_LOW,
            Building.Type.COMMERCIAL_MEDIUM,
            Building.Type.INDUSTRIAL_HIGH,
            Building.Type.INDUSTRIAL_LOW,
            Building.Type.INDUSTRIAL_MEDIUM,
            Building.Type.RESIDENTIAL_HIGH,
            Building.Type.RESIDENTIAL_LOW,
            Building.Type.RESIDENTIAL_MEDIUM });

    public void setFilter(FilterType filter) {
        this.filter = filter;
        for (Building building : buildings) {
            switch(filter) {
                case Source:
                    building.filtered = !sources.contains(building.type, true);
                    break;
                case Desitination:
                    building.filtered = building.type != Building.Type.DUMP;
                    break;
                default:
                    building.filtered = false;
                    break;
            }
        }
    }
}
