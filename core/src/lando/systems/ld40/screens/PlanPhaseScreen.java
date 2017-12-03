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
import lando.systems.ld40.ui.BuildActionModalWindow;
import lando.systems.ld40.ui.ModalWindow;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.Config;
import lando.systems.ld40.utils.accessors.Vector3Accessor;
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
    public MutableFloat targetZoom = new MutableFloat(1f);
    public Vector3 cameraTargetPos;

    private Rectangle nextButtonBounds;
    private Rectangle buildButtonBounds;
    private Rectangle routesButtonBounds;
    private Vector3 projectionVector = new Vector3();

    public enum BuildState { START, PICK_TILE, PICK_ITEM, DONE }
    public class BuildAction {
        public BuildState state;
        public GameObject selectedObject;
        public BuildActionModalWindow modalWindow;
        // TODO: access inventory how?
        public BuildAction() {
            state = BuildState.START;
            selectedObject = null;
            modalWindow = new BuildActionModalWindow(hudCamera, this);
        }
    }
    private BuildAction buildAction;

    private enum RouteState { START, PICK_SOURCES, PICK_DEST, DONE }
    private class RouteAction {
        RouteState state;
        GameObject selectedTruck;
        // TODO: store route, in truck?
        public RouteAction() {
            state = RouteState.START;
            selectedTruck = null;
        }
    }
    private RouteAction routeAction;


    public PlanPhaseScreen() {
        this.game = LudumDare40.game;
        cameraTouchStart = new Vector3();
        touchStart = new Vector3();
        cameraTargetPos = new Vector3(camera.position);
        world = World.GetWorld();
//        Gdx.input.setInputProcessor(this);

        float margin = 10f;
        float size = 80f;
        nextButtonBounds = new Rectangle(hudCamera.viewportWidth - margin - size, hudCamera.viewportHeight - margin - size, size, size);
        buildButtonBounds = new Rectangle(margin, hudCamera.viewportHeight - margin - size, size, size);
        routesButtonBounds = new Rectangle(margin, hudCamera.viewportHeight - 2f * margin - 2f * size, size, size);
//        buildTileHudBounds = new Rectangle()

        buildAction = null;
        routeAction = null;
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
        if (buildAction != null) {
            switch (buildAction.state) {
                case START: {
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
                                    buildAction.state = BuildState.PICK_TILE;
                                }
                            }))
                            .start(Assets.tween);
                }
                break;
                case PICK_TILE: {
//                    if (buildAction.selectedObject != null) {
//                        buildAction.modalWindow.show();
//                        buildAction.state = BuildState.PICK_ITEM;
//                    }
                }
                break;
                case PICK_ITEM: {
                    // TODO: ...
                    if (Gdx.input.justTouched()) {
                        buildAction.modalWindow.hide();
                    }
                }
                break;
                case DONE: {
                    buildAction = null;
                }
                break;
            }
        } else if (routeAction != null) {
            // ...
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
//        batch.setColor(Color.LIGHT_GRAY);
//        batch.draw(Assets.whitePixel, 10, 10, camera.viewportWidth - 20, 50);

        batch.setColor(Color.WHITE);
        Assets.layout.setText(Assets.font, "Plan Phase", Color.GOLD, hudCamera.viewportWidth, Align.center, true);
        Assets.drawString(batch, "Plan Phase", 20f, 45f, Color.GOLD, 0.5f, Assets.font);

        renderBuildActionHud(batch);
        renderRouteActionHud(batch);
        renderNextActionHud(batch);
    }

    private void renderBuildActionHud(SpriteBatch batch) {
        // Build button
        batch.setColor(Color.SKY);
        batch.draw(Assets.whitePixel, buildButtonBounds.x, buildButtonBounds.y, buildButtonBounds.width, buildButtonBounds.height);
        batch.setColor(Color.WHITE);
        if (buildAction == null) return;

        switch (buildAction.state) {
            case START:
                drawText(batch, "Building...");
                break;
            case PICK_TILE:
                drawText(batch, "Click a tile to build on...");
                break;
            case PICK_ITEM:
                buildAction.modalWindow.render(batch);
                break;
            case DONE:
                // nothing to see here
                break;
        }
    }

    private void renderRouteActionHud(SpriteBatch batch) {
        // Routes button
        batch.setColor(Color.ORANGE);
        batch.draw(Assets.whitePixel, routesButtonBounds.x, routesButtonBounds.y, routesButtonBounds.width, routesButtonBounds.height);
        batch.setColor(Color.WHITE);
        if (routeAction == null) return;

        switch (routeAction.state) {
            case START:
                drawText(batch, "Select Route...");
                break;
            case PICK_SOURCES:
                drawText(batch, "Select Source...");
                break;
            case PICK_DEST:
                drawText(batch, "Click a destination...");
                break;
            case DONE: {
                // nothing to see here
            }
            break;
        }
    }

    private void drawText(SpriteBatch batch, String text) {
        batch.setShader(Assets.fontShader);
        Assets.layout.setText(Assets.font, text);
        Assets.font.draw(batch, text,
                0, hudCamera.viewportHeight - Assets.layout.height,
                hudCamera.viewportWidth,
                Align.center, true);
        batch.setShader(null);
    }

    private void renderNextActionHud(SpriteBatch batch) {
        // Next button
        batch.setColor(Color.FOREST);
        batch.draw(Assets.whitePixel, nextButtonBounds.x, nextButtonBounds.y, nextButtonBounds.width, nextButtonBounds.height);
        batch.setColor(Color.WHITE);

        // ...
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

        projectionVector.set(screenX, screenY, 0);
        hudCamera.unproject(projectionVector);
        if (nextButtonBounds.contains(projectionVector.x, projectionVector.y)) {
            game.setScreen(new ActionPhaseScreen());
            return true;
        } else if (buildButtonBounds.contains(projectionVector.x, projectionVector.y)) {
            buildAction = new BuildAction();
            routeAction = null;
            return true;
        } else if (routesButtonBounds.contains(projectionVector.x, projectionVector.y)) {
            routeAction = new RouteAction();
            buildAction = null;
            return true;
        }

        if (buildAction != null) {
            projectionVector.set(screenX, screenY, 0);
            camera.unproject(projectionVector);

            if (buildAction.state == BuildState.PICK_TILE) {
                GameObject selectedObject = world.getSelectedObject(projectionVector.x, projectionVector.y);
                if (selectedObject != null) {
                    buildAction.selectedObject = selectedObject;
                    buildAction.modalWindow.show();
                    buildAction.state = BuildState.PICK_ITEM;
                }
            } else if (buildAction.state == BuildState.PICK_ITEM) {
                // check for item pick in inventory view
            }
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
        targetZoom.setValue(targetZoom.floatValue() + change * targetZoom.floatValue() * zoomScale);
        targetZoom.setValue(MathUtils.clamp(targetZoom.floatValue(), minZoom, maxZoom));
        return true;
    }

}
