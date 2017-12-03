package lando.systems.ld40.managers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Brian on 12/3/2017.
 */
public interface IManager {

    void activate();
    void deactivate();

    void render(SpriteBatch batch);
    void update(float dt);

    boolean touchUp(float screenX, float screenY);
    boolean isModal();
}
