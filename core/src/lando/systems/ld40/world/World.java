package lando.systems.ld40.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld40.buildings.Building;
import lando.systems.ld40.gameobjects.GameObject;
import lando.systems.ld40.gameobjects.Inventory;
import lando.systems.ld40.gameobjects.Routes;

public class World {

    private static World world;

    public static final int tiles_wide = 13;
    public static final int tiles_high = 11;
    public static final int hq_x = 6;
    public static final int hq_y = 5;
    public static final float tile_pixels_wide = 128;
    public static final float tile_pixels_high = 128;
    public static final float pixels_wide = tiles_wide * tile_pixels_wide;
    public static final float pixels_high = tiles_high * tile_pixels_high;

    public static World GetWorld(){
        if (world == null){
            world = new World();
        }
        return world;
    }

    Array<Building> tiles;
    public Rectangle bounds;
    public int turnNumber;
    public Inventory inventory;
    public Routes routes;

    public World() {
        turnNumber = 0;
        bounds = new Rectangle(0, 0, pixels_wide, pixels_high);
        inventory = new Inventory();
        routes = new Routes();

        tiles = new Array<Building>();
        for (int y = 0; y < tiles_high; ++y) {
            for (int x = 0; x < tiles_wide; ++x) {
                Building.Type type = Building.Type.EMPTY;
                if (x == hq_x && y == hq_y) {
                    type = Building.Type.GARBAGE_HQ;
                }

                Building newBuilding = Building.getBuilding(type);
                newBuilding.setLocation(x * tile_pixels_wide, y * tile_pixels_high);
                tiles.add(newBuilding);
            }
        }

        setRandom(Building.Type.DUMP);
        setRandom(Building.Type.COMMERCIAL_LOW);
        setRandom(Building.Type.COMMERCIAL_LOW);
    }

    private void setRandom(Building.Type buildingType) {
        int index;
        do {
            index = MathUtils.random.nextInt(tiles.size - 1);
        } while(!tiles.get(index).canBuild);

        setTile(index, buildingType);
    }

    public void setTile(int index, Building.Type buildingType) {
        Building building = tiles.get(index);
        Building newBuilding = Building.getBuilding(buildingType);
        newBuilding.setLocation(building.position.x, building.position.y);
        tiles.set(index, newBuilding);
    }

    public void update(float dt){
        for (GameObject tile : tiles){
            tile.update(dt);
        }
    }

    public void render(SpriteBatch batch){
        for (GameObject tile : tiles){
            tile.render(batch);
        }
    }

    public GameObject getSelectedObject(float x, float y) {
        int index = getSelectedObjectIndex(x, y);
        System.out.println("tile: " + index);
        return (index == -1) ? null : tiles.get(index);
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
    }

    private int totalBuildings(){
        int count = 0;
        for (Building tile : tiles){
            if (tile.type != Building.Type.EMPTY) count++;
        }
        return count;
    }
}
