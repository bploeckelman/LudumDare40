package lando.systems.ld40.screens;

import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld40.utils.Config;

/**
 * Created by Brian on 7/25/2017
 */
public abstract class BaseScreen extends InputAdapter {

    public MutableFloat alpha;
    public OrthographicCamera camera;
    public OrthographicCamera hudCamera;

    public BaseScreen () {
        super();
        float aspect = Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
        camera = new OrthographicCamera(Config.gameWidth, Config.gameWidth / aspect);
        camera.translate(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

        hudCamera = new OrthographicCamera(Config.gameWidth, Config.gameWidth / aspect);
        hudCamera.translate(hudCamera.viewportWidth / 2, hudCamera.viewportHeight / 2, 0);
        hudCamera.update();
        alpha = new MutableFloat(0f);

    }

    public abstract void update(float dt);
    public abstract void render(SpriteBatch batch);

}
