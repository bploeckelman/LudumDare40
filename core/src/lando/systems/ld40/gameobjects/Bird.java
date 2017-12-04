package lando.systems.ld40.gameobjects;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld40.utils.Assets;

public class Bird {

    Vector2 pos;
    TextureRegion texture;
    MutableFloat scaleX;
    MutableFloat alpha;
    float rotation;
    boolean rotateLeft;
    int floatUp;
    float floatDelay;

    public Bird(){
        if (MathUtils.randomBoolean()) {
            texture = Assets.bird1;
        } else {
            texture = Assets.bird2;
        }
        alpha = new MutableFloat(0);
        floatUp = MathUtils.randomBoolean() ? 1 : -1;
        floatDelay = MathUtils.random(-1f, 1f);
        rotation = MathUtils.random(-10f, 10f);
        rotateLeft = MathUtils.randomBoolean();
        pos = new Vector2(MathUtils.random(700) + 50, 120 + MathUtils.random(140));
        scaleX = new MutableFloat(0);
        float delay =  2f + MathUtils.random(2f);
        Tween.to(scaleX, -1, 1f)
                .target(1 + MathUtils.random(.4f))
                .delay(delay)
                .start(Assets.tween);

        Tween.to(alpha, -1, 1f)
                .target(1)
                .delay(delay)
                .start(Assets.tween);
    }

    public void update(float dt){
        if (rotateLeft){
            rotation += 15 * dt;
            if (rotation > 10) rotateLeft = false;
        } else {
            rotation -= 15 * dt;
            if (rotation < -10) rotateLeft = true;
        }

        floatDelay -= dt;
        if (floatDelay < 0){
            floatDelay = MathUtils.random(1f) + 1f;
            floatUp = -floatUp;
        }

        pos.add(MathUtils.sinDeg(rotation) * floatUp * dt * 5f, MathUtils.cosDeg(rotation) * floatUp * dt * 10f);
    }

    public void render(SpriteBatch batch){
        batch.setColor(1f,1f,1f,alpha.floatValue());
        batch.draw(texture, pos.x, pos.y, 20, 11, 42, 16, scaleX.floatValue(), scaleX.floatValue(), rotation);
        batch.setColor(Color.WHITE);
    }
}
