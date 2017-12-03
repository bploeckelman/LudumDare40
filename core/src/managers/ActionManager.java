package managers;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld40.utils.Assets;

/**
 * Created by Brian on 12/3/2017.
 */
public abstract class ActionManager implements IManager {

    protected Vector3 hudVector = new Vector3();
    protected Vector3 worldVector = new Vector3();
    protected OrthographicCamera hudCamera;
    protected OrthographicCamera worldCamera;

    protected ActionManager(OrthographicCamera hudCamera, OrthographicCamera worldCamera) {
        this.hudCamera = hudCamera;
        this.worldCamera = worldCamera;
    }

    protected void drawText(SpriteBatch batch, String text) {
        batch.setShader(Assets.fontShader);
        Assets.layout.setText(Assets.font, text);
        Assets.font.draw(batch, text,
                0, hudCamera.viewportHeight - Assets.layout.height,
                hudCamera.viewportWidth,
                Align.center, true);
        batch.setShader(null);
    }

    protected Vector3 unprojectHud(float screenX, float screenY) {
        hudVector.set(screenX, screenY, 0);
        hudCamera.unproject(hudVector);
        return hudVector;
    }

    protected Vector3 unprojectWorld(float screenX, float screenY) {
        worldVector.set(screenX, screenY, 0);
        worldCamera.unproject(worldVector);
        return worldVector;
    }
}
