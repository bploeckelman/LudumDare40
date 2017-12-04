package lando.systems.ld40.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld40.LudumDare40;
import lando.systems.ld40.buildings.Building;
import lando.systems.ld40.gameobjects.DumpTruck;
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
        ANIMATING_ACTIONS_GENERATE_TRASH,
        ANIMATING_ACTIONS_RUN_TRUCKS,
        ANIMATING_ACTIONS_BUILDING_UPKEEP,
        ANIMATING_REWARDS, // Pop up the UI and count up the points
        REWARDS_FINAL; // Show all the points counted up

        public static Phase getNextPhase(Phase phase) {
            switch (phase) {
                case READY:                             return Phase.ANIMATING_ACTIONS_GENERATE_TRASH;
                case ANIMATING_ACTIONS_GENERATE_TRASH:  return Phase.ANIMATING_ACTIONS_RUN_TRUCKS;
                case ANIMATING_ACTIONS_RUN_TRUCKS:      return Phase.ANIMATING_ACTIONS_BUILDING_UPKEEP;
                case ANIMATING_ACTIONS_BUILDING_UPKEEP: return Phase.ANIMATING_REWARDS;
                case ANIMATING_REWARDS:                 return Phase.REWARDS_FINAL;
                case REWARDS_FINAL:                     return Phase.REWARDS_FINAL;
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
        float camTargetZoom = Math.max(
                World.pixels_wide * 1.4f / hudCamera.viewportWidth,
                World.pixels_high * 1.4f / hudCamera.viewportHeight
        );
        camera.zoom = camTargetZoom;
        camera.position.set(World.pixels_wide /2f, World.pixels_high/2f, 0);
        // Reset all trucks for action phase
        for (DumpTruck dumpTruck : world.routes.trucks) {
            dumpTruck.resetForActionPhase();
        }
        // Set the phase!
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


    private static final float  AA_GENERATE_TRASH_BUILDING_DELAY = 0.1f;
    private int                 aaGenerateTrashBuildingsComplete = 0;
    private int                 aaGenerateTrashLastBuildingIndex;
    private Building            aaGenerateTrashLastBuilding;
    private boolean             aaGenerateTrashBuildingsAreProcessed = false;

    private Building            aaRunTrucksLastBuildingModified; // use this to track animations
    private boolean             aaRunTrucksTrucksAreRunning = false;
    private boolean             aaRunTrucksAllTrucksAreDone = false;


    private static final float  AA_BUILDING_UPKEEP_BUILDING_DELAY = 0.1f;
    private int                 aaBuildingUpkeepBuildingsComplete = 0;
    private int                 aaBuildingUpkeepLastBuildingIndex;
    private Building            aaBuildingUpkeepLastBuilding;
    private boolean             aaBuildingUpkeepBuildingsAreProcessed = false;

    private void updateObjects(float dt) {

        currentPhaseDT += dt;

        // Update the trucks
        for (DumpTruck dumpTruck : world.routes.trucks) {
            dumpTruck.update(dt);
        }

        /**
         * NOTE: check the phases here in phase order so it is possible to flow through if the skip animation flag is set
         */

        if (currentPhase == Phase.READY) { debugPhaseLabel = "ready..."; }
        if (currentPhase == Phase.ANIMATING_ACTIONS_GENERATE_TRASH) {
            debugPhaseLabel = "trash";
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
        if (currentPhase == Phase.ANIMATING_ACTIONS_RUN_TRUCKS) {
            debugPhaseLabel = "trucking...";
            if (!aaRunTrucksTrucksAreRunning) {
                for (DumpTruck dumpTruck : world.routes.trucks) {
                    dumpTruck.actionPhaseRun(!currentPhaseSkipAnimation);
                }
                aaRunTrucksTrucksAreRunning = true;
            }
            // Check to see if the trucks are done.
            boolean trucksAreDone = true;
            for (DumpTruck dumpTruck : world.routes.trucks) {
                if (!dumpTruck.actionPhaseIsComplete()) {
                    trucksAreDone = false;
                    break;
                }
            }
            if (trucksAreDone) {
                setPhase(Phase.getNextPhase(currentPhase));
            }
        }
        if (currentPhase == Phase.ANIMATING_ACTIONS_BUILDING_UPKEEP) {
            debugPhaseLabel = "upkeep";

            if (!aaBuildingUpkeepBuildingsAreProcessed) {
                // We need to do work, generate trash and the like
                int buildingsToCompleteTarget = 1 + (int) Math.floor(currentPhaseDT / AA_BUILDING_UPKEEP_BUILDING_DELAY);
                if (aaBuildingUpkeepBuildingsComplete < buildingsToCompleteTarget) {
                    int startingIndex = aaBuildingUpkeepBuildingsComplete == 0 ? 0 : aaBuildingUpkeepLastBuildingIndex + 1;
                    for (int i = startingIndex; i < World.buildings.size; i++) {
                        if (World.buildings.get(i).runUpkeep(!currentPhaseSkipAnimation)) {
                            aaBuildingUpkeepLastBuilding = World.buildings.get(i);
                            aaBuildingUpkeepLastBuildingIndex = i;
                            aaBuildingUpkeepBuildingsComplete++;
                            // Check to see if we should stop
                            if (!currentPhaseSkipAnimation &&
                                    aaBuildingUpkeepBuildingsComplete >= buildingsToCompleteTarget) {
                                break;
                            }
                        }
                        // Was this the last one?
                        if (i >= World.buildings.size - 1) {
                            aaBuildingUpkeepBuildingsAreProcessed = true;
                        }
                    }
                }
            }
            if (aaBuildingUpkeepBuildingsAreProcessed) {
                // Is it time to move on to the next phase?
                if (currentPhaseSkipAnimation ||
                        aaBuildingUpkeepLastBuilding == null ||
                        !aaBuildingUpkeepLastBuilding.isAnimating()) {
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
        // Render them trucks
        for (DumpTruck dumpTruck : world.routes.trucks) {
            dumpTruck.render(batch);
        }
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
