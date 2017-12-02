package lando.systems.ld40.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld40.utils.Assets;


public class GameObject {

    public Rectangle bounds;

    public GameObject(){
        bounds = new Rectangle(120,120,64, 64);
    }
    
    public void update(float dt){

    }

    public void render (SpriteBatch batch){
        batch.draw(Assets.testTexture, bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
