package lando.systems.ld40.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld40.utils.Assets;

public class Cloud {
    Vector2 pos;
    boolean headingRight;
    float speed;
    public boolean offScreen;
    TextureRegion texture;

    public Cloud(boolean onScreen){
        texture = Assets.cloudTextures.get(MathUtils.random(Assets.cloudTextures.size-1));
        headingRight = MathUtils.randomBoolean();
        speed = MathUtils.random(20f,50f);
        offScreen = false;
        if (onScreen){
            pos = new Vector2(MathUtils.random(800), MathUtils.random(150) + 400);
        } else {
            pos = new Vector2(headingRight? -texture.getRegionWidth() : 800, MathUtils.random(150) + 400);
        }
    }

    public void update(float dt){
        if (headingRight){
            pos.x += speed * dt;
        } else {
            pos.x -= speed * dt;
        }

        if (pos.x < -texture.getRegionWidth() || pos.x > 800) offScreen = true;
    }

    public void render(SpriteBatch batch){
        batch.draw(texture, pos.x, pos.y);
    }
}
