package lando.systems.ld40.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;


public class GameObject {

    public Rectangle bounds;

    public GameObject(){
        bounds = new Rectangle(0,0,64, 64);
    }
    
    public void update(float dt){

    }

    public void render (SpriteBatch batch){

    }
}
