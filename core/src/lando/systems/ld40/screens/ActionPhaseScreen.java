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
        ANIMATING_ACTIONS_GENERATE_TRASH, // Show everything happening
        ANIMATING_REWARDS, // Pop up the UI and count up the points
        REWARDS_FINAL; // Show all the points counted up

        public static Phase getNextPhase(Phase phase) {
            switch (phase) {
                case READY:                         return Phase.ANIMATING_ACTIONS_GENERATE_TRASH;
                case ANIMATING_ACTIONS_GENERATE_TRASH:     return Phase.ANIMATING_REWARDS;
                case ANIMATING_REWARDS:             return Phase.REWARDS_FINAL;
                case REWARDS_FINAL:                 return Phase.REWARDS_FINAL;
                default:
                    throw new RuntimeException("Unknown Phase");
            }
        }
    }

    private Phase currentPhase;
    private float currentPhaseDT;
    private boolean currentPhaseSkipAnimation = false;
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
        if (currentPhase == Phase.REWARDS_FINAL) {
            LudumDare40.game.setScreen(new ResolutionPhaseScreen(), Assets.crosshatchShader);
            return;
        }
        switch (currentPhase) {
            // Animation phases can be skipped, but that happens in the UpdateObjects method. Set a flag.
            case ANIMATING_ACTIONS_GENERATE_TRASH:
                currentPhaseSkipAnimation = true;
                break;
            // Otherwise, sure, go to the next step
            default:
                setPhase(Phase.getNextPhase(currentPhase));
        }
    }


    private void setPhase(Phase phase) {
        currentPhase = phase;
        currentPhaseDT = 0;
        switch (phase) {
            case READY:
                debugPhaseLabel = "wait for click";
                break;
            case ANIMATING_ACTIONS_GENERATE_TRASH:
                // Order of actions:
                // Generate all trash
                debugPhaseLabel = "trash";
//                buildingsGenerateTrash();
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


    private static final float AA_GENERATE_TRASH_BUILDING_DELAY = 0.1f;
    private int aaGenerateTrashBuildingsComplete = 0;
    private int aaGenerateTrashLastBuildingIndex;
    private Building aaGenerateTrashLastBuilding;
    private boolean aaGenerateTrashBuildingsAreProcessed = false;

    private void updateObjects(float dt) {

        currentPhaseDT += dt;

        /**
         * NOTE: check the phases here in phase order so it is possible to flow through if the skip animation flag is set
         */

        if (currentPhase == Phase.ANIMATING_ACTIONS_GENERATE_TRASH) {

            if (!aaGenerateTrashBuildingsAreProcessed) {
                // We need to do work, generate trash and the like
                int buildingsToCompleteTarget = 1 + (int) Math.floor(currentPhaseDT / AA_GENERATE_TRASH_BUILDING_DELAY);
                if (aaGenerateTrashBuildingsComplete < buildingsToCompleteTarget) {
                    int startingIndex = aaGenerateTrashBuildingsComplete == 0 ? 0 : aaGenerateTrashLastBuildingIndex + 1;
                    for (int i = startingIndex; i < World.buildings.size; i++) {
                        if (World.buildings.get(i).generateTrash(!currentPhaseSkipAnimation)) {
                            aaGenerateTrashLastBuilding = World.buildings.get(i);
                            aaGenerateTrashLastBuildingIndex = i;
                            aaGenerateTrashBuildingsComplete++;
                            // Check to see if we should stop
                            if (!currentPhaseSkipAnimation &&
                                    aaGenerateTrashBuildingsComplete >= buildingsToCompleteTarget) {
                                break;
                            }
                        }
                        // Was this the last one?
                        if (i >= World.buildings.size - 1) {
                            aaGenerateTrashBuildingsAreProcessed = true;
                        }
                    }
                }
            }
            if (aaGenerateTrashBuildingsAreProcessed) {
                // Is it time to move on to the next phase?
                if (currentPhaseSkipAnimation ||
                        aaGenerateTrashLastBuilding == null ||
                        !aaGenerateTrashLastBuilding.isAnimating()) {
                    // Time to go to the next step!
                    setPhase(Phase.getNextPhase(currentPhase));
                }
            }

        }

        if (currentPhase == Phase.ANIMATING_REWARDS) {

            // This is a "halt" point... e.g. if we're skipping animations we'll restart them here.
            currentPhaseSkipAnimation = false;

        }
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
