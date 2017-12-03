package lando.systems.ld40.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld40.LudumDare40;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.Config;
import lando.systems.ld40.world.World;

/**
 * Created by Brian on 7/25/2017
 */
public class PlanPhaseScreen extends BaseScreen {

    private static final boolean DEBUG = false;

    private final LudumDare40 game;
    private World world;


    public Vector3 cameraTouchStart;
    public Vector3 touchStart;
    public static float zoomScale = 0.1f;
    public static float maxZoom = 4f;
    public static float minZoom = 0.5f;
    public static float DRAG_DELTA = 10f;
    public static float ZOOM_LERP = .1f;
    public static float PAN_LERP = .2f;
    public boolean cancelTouchUp = false;
    public float targetZoom = 1;
    public Vector3 cameraTargetPos;

    private Rectangle nextButtonBounds;
    private Vector3 projectionVector = new Vector3();


    public PlanPhaseScreen() {
        this.game = LudumDare40.game;
        cameraTouchStart = new Vector3();
        touchStart = new Vector3();
        cameraTargetPos = new Vector3(camera.position);
        world = World.GetWorld();
        Gdx.input.setInputProcessor(this);

        nextButtonBounds = new Rectangle(camera.viewportWidth - 100, camera.viewportHeight - 40, 80, 20);
    }

    @Override
    public void update(float dt) {
        if ( Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new TitleScreen());
        }

        updateWorld(dt);
        updateObjects(dt);
        updateCamera();
    }

    private void updateWorld(float dt) {
        world.update(dt);
    }

    private void updateObjects(float dt) {
        // todo
    }

    private void updateCamera() {
        camera.zoom = MathUtils.lerp(camera.zoom, targetZoom, ZOOM_LERP);
        camera.zoom = MathUtils.clamp(camera.zoom, minZoom, maxZoom);

        camera.position.x = MathUtils.lerp(camera.position.x, cameraTargetPos.x, PAN_LERP);
        camera.position.y = MathUtils.lerp(camera.position.y, cameraTargetPos.y, PAN_LERP);
        camera.position.y = MathUtils.clamp(camera.position.y, world.bounds.y, world.bounds.y + world.bounds.height);
        camera.position.x = MathUtils.clamp(camera.position.x, world.bounds.x, world.bounds.x + world.bounds.width);
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
            if (DEBUG) {
                Assets.drawString(batch, "DEBUG TEXT: " + Gdx.graphics.getDeltaTime(), 10f, 20f, Color.WHITE, 0.3f, Assets.font);
            }
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
//        batch.setColor(Color.LIGHT_GRAY);
//        batch.draw(Assets.whitePixel, 10, 10, camera.viewportWidth - 20, 50);
//        batch.setColor(Color.WHITE);
//        Assets.drawString(batch, "Plan Phase", 20f, 45f, Color.GOLD, 0.5f, Assets.font);
    }


    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        projectionVector.x = screenX;
        projectionVector.y = screenY;
        Vector3 hudClick = hudCamera.unproject(projectionVector);
        if (nextButtonBounds.contains(hudClick.x, hudClick.y)) {
            game.setScreen(new ActionPhaseScreen());
            return true;
        }


        cameraTouchStart.set(camera.position);
        touchStart.set(screenX, screenY, 0);
        return true;
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        if (cancelTouchUp) {
            cancelTouchUp = false;
            return false;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        cameraTargetPos.x = cameraTouchStart.x + (touchStart.x - screenX) * camera.zoom;
        cameraTargetPos.y = cameraTouchStart.y + (screenY - touchStart.y) * camera.zoom;
        if (cameraTouchStart.dst(cameraTargetPos) > DRAG_DELTA) {
            cancelTouchUp = true;
        }
        return true;
    }

    Vector3 tp = new Vector3();
    @Override
    public boolean scrolled (int change) {

//        camera.unproject(tp.set(Gdx.input.getX(), Gdx.input.getY(), 0 ));
//        float px = tp.x;
//        float py = tp.y;
//        camera.zoom += change * camera.zoom * zoomScale;
//        updateCamera();
//
//        camera.unproject(tp.set(Gdx.input.getX(), Gdx.input.getY(), 0));
//        camera.position.add(px - tp.x, py- tp.y, 0);
//        camera.update();
        targetZoom += change * targetZoom * zoomScale;
        targetZoom = MathUtils.clamp(targetZoom, minZoom, maxZoom);
        return true;
    }

}
