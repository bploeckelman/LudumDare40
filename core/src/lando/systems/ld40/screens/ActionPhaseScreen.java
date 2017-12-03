package lando.systems.ld40.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld40.LudumDare40;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.Config;
import lando.systems.ld40.world.World;

/**
 * Created by Brian on 12/2/2017.
 */
public class ActionPhaseScreen extends BaseScreen {

    private World world;

    public ActionPhaseScreen() {
        world = World.GetWorld();
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void update(float dt) {
        updateWorld(dt);
        updateObjects(dt);
        updateCamera(dt);
    }

    private void updateWorld(float dt) {
        world.update(dt);
    }

    private void updateObjects(float dt) {
        // todo
    }

    private void updateCamera(float dt) {
        //camera.zoom = MathUtils.clamp(camera.zoom, minZoom, maxZoom);
        float minY = world.bounds.y + camera.viewportHeight/2 * camera.zoom;
        float maxY = world.bounds.height - camera.viewportHeight/2 * camera.zoom;

        float minX = world.bounds.x + camera.viewportWidth/2 * camera.zoom;
        float maxX = world.bounds.x + world.bounds.width - camera.viewportWidth/2 * camera.zoom;

        if (camera.viewportHeight * camera.zoom > world.bounds.height){
            camera.position.y = world.bounds.height/2;
        } else {
            camera.position.y = MathUtils.clamp(camera.position.y, minY, maxY);
        }


        if (camera.viewportWidth * camera.zoom > world.bounds.width){
            camera.position.x = world.bounds.x + world.bounds.width/2;
        } else {
            camera.position.x = MathUtils.clamp(camera.position.x, minX, maxX);
        }

        camera.update();
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(Config.bgColor.r, Config.bgColor.g, Config.bgColor.b, Config.bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw world
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        {
            renderWorld(batch);
            renderObjects(batch);
        }
        batch.end();

        // Draw hud
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            renderHud(batch);
        }
        batch.end();
    }

    private void renderWorld(SpriteBatch batch) {
        batch.setColor(Color.WHITE);
        world.render(batch);
    }

    private void renderObjects(SpriteBatch batch) {
        // todo
    }

    private void renderHud(SpriteBatch batch) {
        batch.setColor(Color.LIGHT_GRAY);
        batch.draw(Assets.whitePixel, 10, 10, camera.viewportWidth - 20, 50);
        batch.setColor(Color.WHITE);
        Assets.drawString(batch, "Action Phase", 20f, 45f, Color.GOLD, 0.5f, Assets.font);
    }
}
