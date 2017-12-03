package managers;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Brian on 12/3/2017.
 */
public interface IManager {

    void activate();
    void deactivate();

    void render(SpriteBatch batch);
    void update(float dt);

    void touchUp(float screenX, float screenY);
}
