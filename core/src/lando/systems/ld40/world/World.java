package lando.systems.ld40.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import lando.systems.ld40.buildings.Building;
import lando.systems.ld40.gameobjects.DumpTruck;
import lando.systems.ld40.gameobjects.GameObject;
import lando.systems.ld40.gameobjects.Inventory;
import lando.systems.ld40.gameobjects.Routes;
import lando.systems.ld40.utils.Assets;

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
    }

    public void update(float dt){
        for (GameObject tile : buildings){
            tile.update(dt);
        }
    }

    public void render(SpriteBatch batch){
        for (GameObject tile : buildings){
            tile.render(batch);
        }
    }

    public void renderRoutes(SpriteBatch batch, OrthographicCamera camera) {
        batch.end();
        Assets.shapes.begin(ShapeRenderer.ShapeType.Filled);
        Assets.shapes.setProjectionMatrix(camera.combined);


        Vector2 hqPoint = getPoint(hqIndex);
        Vector2 point = hqPoint;

        Color[] colors = new Color[] {
                new Color(104/255f,41/255f,62/255f, 1),
                new Color(217/255f,126/255f,0, 1),
                new Color(188/255f,139/255f,87/255f,1),
                new Color(90/255f,178/255f,23/255f, 1),
                new Color(71/255f,71/255f,87/255f, 1),
                new Color(1/255f,163/255f,195/255f, 1)
        };

        float thickness = 25;

        for (int d = 0; d < routes.trucks.size; d++) {
            Assets.shapes.setColor(colors[d]);
            IntArray route = routes.routes.get(routes.trucks.get(d));
            for (int i = 0; i < route.size; i++) {
                Vector2 newPoint = getPoint(route.get(i));
                Assets.shapes.rectLine(point, newPoint, thickness);
                point = newPoint;
            }
            Assets.shapes.rectLine(point, hqPoint, thickness);
        }

        Assets.shapes.end();
        batch.begin();
    }

    private Vector2 getPoint(int index) {
        Rectangle bounds = buildings.get(index).bounds;
        return new Vector2(bounds.x + bounds.width/2, bounds.y + bounds.height/2);
    }


    public GameObject getSelectedObject(float x, float y) {
        int index = getSelectedObjectIndex(x, y);
        System.out.println("tile: " + index);
        return (index == -1) ? null : buildings.get(index);
    }

    public int getSelectedObjectIndex(float x, float y) {
        System.out.println("x: " + x + " y: " + y);
        if (x < 0 || y < 0 || x >= pixels_wide || y >= pixels_high) return -1;

        int xOffset = (int)(tiles_wide * x / pixels_wide);
        int yOffset = (int)(tiles_high * y / pixels_high);

        return yOffset * tiles_wide + xOffset;
    }

    public void nextTurn(){
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
