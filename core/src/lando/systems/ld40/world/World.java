package lando.systems.ld40.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld40.gameobjects.GameObject;

public class World {

    private static World world;

    public static World GetWorld(){
        if (world == null){
            world = new World();
        }
        return world;
    }

    Array<GameObject> buildings;


    public World(){
        buildings = new Array<GameObject>();
        buildings.add(new GameObject());
    }


    public void update(float dt){
        for (GameObject building : buildings){
            building.update(dt);
        }
    }

    public void render(SpriteBatch batch){
        for (GameObject building : buildings){
            building.render(batch);
        }
    }
}
