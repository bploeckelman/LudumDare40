package lando.systems.ld40.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld40.buildings.Building;
import lando.systems.ld40.gameobjects.GameObject;
import lando.systems.ld40.gameobjects.Tile;

public class World {

    private static World world;

    public static final int tiles_wide = 12;
    public static final int tiles_high = 10;
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

    Array<GameObject> tiles;
    public Rectangle bounds;


    public World(){
        int type = 0;
        tiles = new Array<GameObject>();
        for (int y = 0; y < tiles_high; ++y) {
            for (int x = 0; x < tiles_wide; ++x) {
                type = MathUtils.random(9);
                Tile newTile = Building.getBuilding(Building.Type.values()[type]);
                newTile.setLocation(x * tile_pixels_wide, y * tile_pixels_high);
                tiles.add(newTile);

                if (++type > 9) {
                    type = 0;
                }                
            }
        }
        bounds = new Rectangle(-tile_pixels_wide/2f, -tile_pixels_high/2f,
                pixels_wide, pixels_high);
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
}
