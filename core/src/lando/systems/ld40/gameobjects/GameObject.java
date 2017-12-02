package lando.systems.ld40.gameobjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld40.utils.Assets;


public class GameObject {

    public Rectangle bounds = new Rectangle();
    public Vector2 position = new Vector2();
        
	public float animStateTime = 0;
    
    protected float bounds_offset_x;
    protected float bounds_offset_y;

    public Texture texture;

    public GameObject() {
        setTexture(Assets.testTexture);
    }

    protected void setTexture(Texture texture) {
        this.texture = texture;
        setSize(texture.getWidth(), texture.getHeight());
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

        if (texture != null) {
            batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }
}
