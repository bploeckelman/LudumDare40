package lando.systems.ld40.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld40.LudumDare40;
import lando.systems.ld40.buildings.Building;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.Config;
import lando.systems.ld40.utils.SoundManager;
import lando.systems.ld40.world.World;

/**
 * Created by Brian on 12/2/2017.
 */
class ActionPhaseScreen extends BaseScreen {

    private World world;

    private enum Phase {
        READY,  // Waiting
        ANIMATING_ACTIONS, // Show everything happening
        ANIMATING_REWARDS, // Pop up the UI and count up the points
        REWARDS_FINAL, // Show all the points counted up
    }

    private Phase currentPhase;
    private String debugPhaseLabel;

    // -----------------------------------------------------------------------------------------------------------------

    public ActionPhaseScreen() {
        world = World.GetWorld();
        SoundManager.playSound(SoundManager.SoundOptions.garbageTruck);
//        Gdx.input.setInputProcessor(this);
        camera.zoom = 2.5f;
        camera.position.set(World.pixels_wide /2f, World.pixels_high/2f, 0);
        setPhase(Phase.READY);
    }

    private void nextPhase() {
        switch (currentPhase) {
            case READY:
                setPhase(Phase.ANIMATING_ACTIONS);
                break;
            case ANIMATING_ACTIONS:
                setPhase(Phase.ANIMATING_REWARDS);
                break;
            case ANIMATING_REWARDS:
                setPhase(Phase.REWARDS_FINAL);
                break;
            case REWARDS_FINAL:
                LudumDare40.game.setScreen(new ResolutionPhaseScreen(), Assets.crosshatchShader);
                break;
        }
    }

    private void setPhase(Phase phase) {
        currentPhase = phase;
        switch (phase) {
            case READY:
                debugPhaseLabel = "wait for click";
                break;
            case ANIMATING_ACTIONS:
                // Order of actions:
                // Generate all trash
                debugPhaseLabel = "trash";
                buildingsGenerateTrash();
                // Run the tracks, moving trash around
                // Building upkeep (e.g. incineration)
                // Generate value
                break;
            case ANIMATING_REWARDS:
                debugPhaseLabel = "reward";
                break;
            case REWARDS_FINAL:
                debugPhaseLabel = "final";
                break;
        }
    }

    private void buildingsGenerateTrash() {
        for (Building building : World.buildings) {
            building.generateTrash();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

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
//        batch.setColor(Color.LIGHT_GRAY);
//        batch.draw(Assets.whitePixel, 10, 10, camera.viewportWidth - 20, 50);
//        batch.setColor(Color.WHITE);
        Assets.drawString(batch, "Action Phase ("+debugPhaseLabel+")", 20f, 45f, Color.GOLD, 0.5f, Assets.font);
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        nextPhase();
        return true;
    }
}
