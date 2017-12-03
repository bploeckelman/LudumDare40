package lando.systems.ld40.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld40.LudumDare40;
import lando.systems.ld40.gameobjects.GameObject;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.Config;
import lando.systems.ld40.utils.accessors.Vector3Accessor;
import lando.systems.ld40.world.World;
import managers.BuildManager;
import managers.IManager;
import managers.RouteManager;

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
    public MutableFloat targetZoom = new MutableFloat(1f);
    public Vector3 cameraTargetPos;

    private Rectangle nextButtonBounds;
    private Rectangle buildButtonBounds;
    private Rectangle routesButtonBounds;
    private Vector3 projectionVector = new Vector3();

    private IManager actionManager;

    public PlanPhaseScreen() {
        this.game = LudumDare40.game;
        cameraTouchStart = new Vector3();
        touchStart = new Vector3();
        cameraTargetPos = new Vector3(camera.position);
        world = World.GetWorld();

        float margin = 10f;
        float size = 80f;
        nextButtonBounds = new Rectangle(hudCamera.viewportWidth - margin - size, hudCamera.viewportHeight - margin - size, size, size);
        buildButtonBounds = new Rectangle(margin, hudCamera.viewportHeight - margin - size, size, size);
        routesButtonBounds = new Rectangle(margin, hudCamera.viewportHeight - 2f * margin - 2f * size, size, size);
    }

    @Override
    public void update(float dt) {
        if ( Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new TitleScreen());
        }

        //More test code
        if (Gdx.input.isKeyJustPressed(Input.Keys.W))
        {
            game.setScreen(new ResolutionPhaseScreen());
        }

        updateWorld(dt);
        updateObjects(dt);
        updateAction(dt);
        updateCamera();
    }

    private void updateWorld(float dt) {
        world.update(dt);
    }

    private void updateObjects(float dt) {
        // todo
    }

    private void updateAction(float dt) {
        if (actionManager != null) {
            actionManager.update();
        }
    }

    private void updateCamera() {
        camera.zoom = MathUtils.lerp(camera.zoom, targetZoom.floatValue(), ZOOM_LERP);
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
        batch.setColor(Color.WHITE);
        Assets.layout.setText(Assets.font, "Plan Phase", Color.GOLD, hudCamera.viewportWidth, Align.center, true);
        Assets.drawString(batch, "Plan Phase", 20f, 45f, Color.GOLD, 0.5f, Assets.font);

        // Build button
        batch.setColor(Color.SKY);
        batch.draw(Assets.whitePixel, buildButtonBounds.x, buildButtonBounds.y, buildButtonBounds.width, buildButtonBounds.height);

        // Route button
        batch.setColor(Color.ORANGE);
        batch.draw(Assets.whitePixel, routesButtonBounds.x, routesButtonBounds.y, routesButtonBounds.width, routesButtonBounds.height);

        // Next button
        batch.setColor(Color.FOREST);
        batch.draw(Assets.whitePixel, nextButtonBounds.x, nextButtonBounds.y, nextButtonBounds.width, nextButtonBounds.height);

        batch.setColor(Color.WHITE);

        if (actionManager != null) {
            actionManager.render(batch);
        }
        batch.setColor(Color.WHITE);
    }

    private void zoomOut(final IManager manager) {
        // Zoom out
        float camTargetX = World.pixels_wide / 2f;
        float camTargetY = World.pixels_high / 2f;
        float camTargetZoom = Math.max(
                World.pixels_wide * 1.2f / hudCamera.viewportWidth,
                World.pixels_high * 1.2f / hudCamera.viewportHeight
        );

        Timeline.createSequence()
                .push(Timeline.createParallel()
                        .push(Tween.to(cameraTargetPos, Vector3Accessor.XY, 0.5f).target(camTargetX, camTargetY).ease(Quad.INOUT))
                        .push(Tween.to(targetZoom, -1, 0.5f).target(camTargetZoom).ease(Quad.INOUT)))
                .push(Tween.call(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        manager.activate();
                    }
                }))
                .start(Assets.tween);
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
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

        // if one of the buttons, disable button presses, zoom out and create correct action maanger

        projectionVector.set(screenX, screenY, 0);
        hudCamera.unproject(projectionVector);
        if (nextButtonBounds.contains(projectionVector.x, projectionVector.y)) {
            game.setScreen(new ActionPhaseScreen());
            return true;
        } else if (buildButtonBounds.contains(projectionVector.x, projectionVector.y)) {
            setManager(new BuildManager(hudCamera, camera));
            return true;
        } else if (routesButtonBounds.contains(projectionVector.x, projectionVector.y)) {
            setManager(new RouteManager(hudCamera, camera));
            return true;
        }

        if (actionManager != null) {
            actionManager.touchUp(screenX, screenY);
        }

        return true;
    }

    private void setManager(IManager manager) {
        if (actionManager != null) {
            actionManager.deactivate();
        }

        actionManager = manager;
        zoomOut(actionManager);
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
        targetZoom.setValue(targetZoom.floatValue() + change * targetZoom.floatValue() * zoomScale);
        targetZoom.setValue(MathUtils.clamp(targetZoom.floatValue(), minZoom, maxZoom));
        return true;
    }

}
