package lando.systems.ld40.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class GameObject {

    public Rectangle bounds;
    public Vector2 position;
        
	public float animStateTime = 0;
    
    protected float bounds_offset_x;
    protected float bounds_offset_y;

    public TextureRegion keyframe;

    public GameObject() {
        bounds = new Rectangle(0,0,64, 64);
    }
    
    protected void setKeyFrame(TextureRegion textureRegion) {
        keyframe = textureRegion;
        setSize(keyframe.getRegionWidth(), keyframe.getRegionHeight());
    }

    public void setX(float x) {
        position.x = x;
        bounds.x = position.x - bounds_offset_x;
    }

    public void setY(float y) {
        position.y = y;
        bounds.y = position.y - bounds_offset_y;
    }

    public void setLocation(float x, float y) {
        setX(x);
        setY(y);
    }

    public void setSize(float width, float height) {
        bounds.width = width;
        bounds.height = height;

        bounds_offset_x = width / 2;
        bounds_offset_y = height /2;
    }
    
    public void setBoundsLocation(float x, float y) {
        setLocation(x + bounds_offset_x, y + bounds_offset_y);
    }

    public void update(float dt) {
        animStateTime += dt;
    }

    public void render(SpriteBatch batch) {
        bounds.x = position.x - bounds_offset_x;
        bounds.y = position.y - bounds_offset_y;

        if (keyframe != null) {
            batch.draw(keyframe, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }
}
