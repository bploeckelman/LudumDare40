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
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld40.LudumDare40;
import lando.systems.ld40.buildings.Building;
import lando.systems.ld40.gameobjects.TileType;
import lando.systems.ld40.gameobjects.UpgradeType;
import lando.systems.ld40.managers.BuildManager;
import lando.systems.ld40.managers.IManager;
import lando.systems.ld40.managers.RouteManager;
import lando.systems.ld40.ui.Button;
import lando.systems.ld40.ui.ButtonGroup;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.Config;
import lando.systems.ld40.utils.accessors.Vector3Accessor;
import lando.systems.ld40.world.Statistics;
import lando.systems.ld40.world.World;

import static com.badlogic.gdx.Gdx.input;
import static lando.systems.ld40.managers.BuildManager.BuildState.PICK_TILE;

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
    public MutableFloat targetZoom = new MutableFloat(2f);
    public Vector3 cameraTargetPos;

    private Button nextButton;
    private Button buildButton;
    private Button routeButton;

    private static final float TOOLTIP_TEXT_PADDING_X = 8f;
    private static final float TOOLTIP_TEXT_SCALE = 0.35f;
    private static final float TOOLTIP_SHOW_DELAY = .5f;
    private static final float TOOLTIP_CURSOR_OFFSET_X = 8f;

    //these tooltip values are assigned in checkForTouch, as the size depends on individual building
    private float tooltipBackgroundHeight;
    private float tooltipBackgroundWidth;
    private float tooltipTextOffsetY;

    public String tooltip = null;
    private boolean showTooltip = false;
    private Vector3 tempVec3 = new Vector3();
    private Vector2 touchPosScreen = new Vector2();
    private float timeHovered = 0;
    private Building previousMouseOveredTile = null;
    private Building currentMouseOveredTile = null;

    private IManager actionManager;

    public PlanPhaseScreen(boolean firstLaunch) {
        this.game = LudumDare40.game;
        world = World.GetWorld();

        // TODO: remove me... testing
        world.inventory.addTileItem(TileType.DUMP);
        world.inventory.addUpgradeItem(UpgradeType.TIER_UPGRADE);
        world.inventory.addUpgradeItem(UpgradeType.DUMPSTER);
        world.inventory.addUpgradeItem(UpgradeType.INCINERATOR);
        // TODO: remove me... testing

        cameraTouchStart = new Vector3();
        touchStart = new Vector3();
        camera.position.set(World.pixels_wide /2f, World.pixels_high/2f, 0);
        cameraTargetPos = new Vector3(camera.position);
        camera.zoom = 2f;
        float camTargetZoom = Math.max(
                World.pixels_wide * 1.1f / hudCamera.viewportWidth,
                World.pixels_high * 1.1f / hudCamera.viewportHeight
        );
        targetZoom.setValue(camTargetZoom);
        if (firstLaunch){

            minZoom = .1f;
            camera.zoom = .2f;
            targetZoom.setValue(.2f);
            Tween.to(targetZoom, -1, 2f)
                    .target(camTargetZoom)
                    .delay(2f)
                    .setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int i, BaseTween<?> baseTween) {
                            minZoom = .5f;
                        }
                    })
                    .start(Assets.tween);
        }

        float margin = 10f;
        float size = 80f;


        nextButton = new Button("next_button", hudCamera, hudCamera.viewportWidth - margin - size,
                hudCamera.viewportHeight - margin - size, "this goes next, duh");

        buildButton = new Button("button-build-in", "button-build-out", hudCamera, margin, hudCamera.viewportHeight - margin - size, "Build something");
//        routeButton = new Button("button-route-in", "button-route-out", hudCamera, margin + size, hudCamera.viewportHeight - margin - size, "Route something'");
        routeButton = new Button("button-route-in", "button-route-out", hudCamera, margin, hudCamera.viewportHeight - margin - 2f * size, "Route something'");

        ButtonGroup bg = new ButtonGroup();
        bg.add(buildButton);
        bg.add(routeButton);
    }

    @Override
    public void update(float dt) {
        if ( Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new TitleScreen());
        }

        if (actionManager == null || !actionManager.isModal()) {
            updateCamera();
            updateWorld(dt);
            updateHud(dt);
            updateTileTooltip(dt);
        }
        updateAction(dt);

        // Highlight tile under mouse
        world.disableHighlightTile();
        if (actionManager != null && actionManager.isTileHighlightState()) {
            tempVec3.set(input.getX(), input.getY(), 0);
            camera.unproject(tempVec3);
            world.highlightTileAt(tempVec3.x, tempVec3.y);
        }
    }

    private void updateWorld(float dt) {
        world.update(dt);
    }

    private void updateHud(float dt) {
        nextButton.update(dt);
        buildButton.update(dt);
        routeButton.update(dt);
    }

    private void updateAction(float dt) {
        if (actionManager != null) {
            actionManager.update(dt);
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

    private void updateTileTooltip(float dt) {
        //Tooltip when tile is mouseovered
        boolean isTouching = checkForTouch(input.getX(), input.getY());
        if (isTouching && (timeHovered == 0 || previousMouseOveredTile == currentMouseOveredTile)) {
            timeHovered += dt;
            previousMouseOveredTile = currentMouseOveredTile;

        } else {
            timeHovered = 0;
        }
        showTooltip = timeHovered >= TOOLTIP_SHOW_DELAY;

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
            renderTooltip(batch,hudCamera);
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
//        batch.setColor(Color.WHITE);
//        Assets.layout.setText(Assets.font, "Plan Phase", Color.GOLD, hudCamera.viewportWidth, Align.center, true);
//        Assets.drawString(batch, "Plan Phase", 20f, 45f, Color.GOLD, 0.5f, Assets.font);
//        batch.setColor(Color.WHITE);

        buildButton.render(batch);
        routeButton.render(batch);
        nextButton.render(batch);

        if (actionManager != null) {
            actionManager.render(batch);
        }

        Assets.font.setColor(Config.COLOR_TEXT);
        Assets.drawString(batch, "Turn\n" + (world.turnNumber+1) + "/" + Config.gameTurns, nextButton.bounds.x, nextButton.bounds.y - 10, Config.COLOR_TEXT, .3f, Assets.font, nextButton.bounds.width, Align.center);
        batch.setColor(Color.WHITE);
    }

    public void renderTooltip(SpriteBatch batch, OrthographicCamera hudCamera){
        tempVec3.set(input.getX(), input.getY(), 0);
        hudCamera.unproject(tempVec3);
        float tX = tempVec3.x;
        float tY = tempVec3.y;
        float backgroundX;
        float backgroundY;
        float stringTX ;
        float stringTY;

        if (tooltip == null || tooltip.equals("") || !showTooltip) return;
        if (actionManager != null && actionManager.isModal()) return;

        backgroundX = 10;
        // Screen spacee
        if (tX < Config.gameWidth / 2) {
            backgroundX = hudCamera.viewportWidth - (10 + tooltipBackgroundWidth);
        }
        stringTX = backgroundX + TOOLTIP_TEXT_PADDING_X;

        backgroundY = 10;
        stringTY = backgroundY + tooltipTextOffsetY;

        // DRAW
        batch.setColor(Color.WHITE);
        Assets.tooltipNinePatch.draw(batch, backgroundX, backgroundY, tooltipBackgroundWidth, tooltipBackgroundHeight);
        Assets.drawString(batch,
                tooltip,
                stringTX,
                stringTY,
                Config.COLOR_TEXT,
                TOOLTIP_TEXT_SCALE,
                Assets.font);
    }

    public boolean checkForTouch(int screenX, int screenY) {
        if (!allowInput) return false;
        Vector3 touchPosUnproject = camera.unproject(tempVec3.set(screenX, screenY, 0));
        touchPosScreen.set(touchPosUnproject.x, touchPosUnproject.y);

        for (Building tile : World.buildings) {
            int additionalLine = 0;
            tooltipBackgroundHeight = 150f;
            tooltipBackgroundWidth = 270f;
            tooltipTextOffsetY = 130f;
            if (tile.bounds.contains(touchPosScreen.x, touchPosScreen.y) && !nextButton.checkForTouch(screenX, screenY) && !buildButton.checkForTouch(screenX, screenY) && !routeButton.checkForTouch(screenX, screenY)) {
                tooltip = "Type: " + tile.type + "\nCurrent Trash: " + tile.currentTrashLevel;
                if (tile.currentTier != null) {
                    tooltip += "\nTier: " + tile.currentTier;
                    additionalLine++;
                }
                tooltip += "\nTrash Capacity: " + tile.getCurrentTrashCapacity();
                if (tile.trashGeneratedPerRound != 0) tooltip += "\nTrash per Round: " + tile.getFinalTrash();
                if (tile.valueGeneratedPerRound != 0) tooltip += "\nMoney Generated: " + tile.getFinalValue();
                currentMouseOveredTile = tile;
                tooltipBackgroundHeight += additionalLine * 30f;
                tooltipTextOffsetY += additionalLine * 20f;
                return true;
            }
        }
        return false;
    }

    private void zoomOut(IManager manager) {
        // Zoom out
        float camTargetX = World.pixels_wide / 2f;
        float camTargetY = World.pixels_high / 2f;
        float camTargetZoom = Math.max(
                World.pixels_wide * 1.3f / hudCamera.viewportWidth,
                World.pixels_high * 1.2f / hudCamera.viewportHeight
        );
        zoomOut(manager, camTargetX, camTargetY, camTargetZoom);
    }

    private void zoomOut(final IManager manager, float x, float y, float zoom) {
        enableHud(false);

        Timeline.createSequence()
                .push(Timeline.createParallel()
                        .push(Tween.to(cameraTargetPos, Vector3Accessor.XY, 0.5f).target(x, y).ease(Quad.INOUT))
                        .push(Tween.to(targetZoom, -1, 0.5f).target(zoom).ease(Quad.INOUT)))
                .push(Tween.call(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        activateActions(manager);
                    }
                }))
                .start(Assets.tween);
    }

    private void activateActions(IManager manager) {
        manager.activate();
        enableHud(true);
    }

    private void enableHud(boolean enabled) {
        buildButton.enable(enabled);
        routeButton.enable(enabled);
        nextButton.enable(enabled);
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

        if (nextButton.checkForTouch(screenX, screenY)) {
            world.setFilter(World.FilterType.None);
            game.setScreen(new ActionPhaseScreen());
            return true;
        } else if (buildButton.checkForTouch(screenX, screenY)) {
            buildButton.select();
            world.setFilter(World.FilterType.None);
            IManager manager = new BuildManager(hudCamera, camera);
            setManager(manager);
            zoomOut(manager);
            return true;
        } else if (routeButton.checkForTouch(screenX, screenY)) {
            routeButton.select();
            IManager manager = new RouteManager(hudCamera, camera);
            setManager(manager);
            float camTargetZoom = Math.max(
                    World.pixels_wide * 1.7f / hudCamera.viewportWidth,
                    World.pixels_high * 1.7f / hudCamera.viewportHeight
            );
            zoomOut(manager, World.pixels_wide / 2f, World.pixels_high * .2f, camTargetZoom);
            return true;
        }

        if (actionManager != null && actionManager.touchUp(screenX, screenY)) {
            return false;
        }

        return true;
    }

    private void setManager(IManager manager) {
        if (actionManager != null) {
            actionManager.deactivate();
        }

        actionManager = manager;
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
