package lando.systems.ld40.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld40.LudumDare40;
import lando.systems.ld40.buildings.Building;
import lando.systems.ld40.gameobjects.DumpTruck;
import lando.systems.ld40.managers.TurnStatisticsModalWindow;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.Config;
import lando.systems.ld40.utils.SoundManager;
import lando.systems.ld40.world.Statistics;
import lando.systems.ld40.world.TurnStatistics;
import lando.systems.ld40.world.World;

/**
 * Created by Brian on 12/2/2017.
 */
class ActionPhaseScreen extends BaseScreen {

    private World world;
    private TurnStatisticsModalWindow turnStatisticsModalWindow;
    private boolean firstTime;

    private enum Phase {
        READY,  // Waiting
        ANIMATING_ACTIONS_GENERATE_TRASH,
        ANIMATING_ACTIONS_RUN_TRUCKS,
        ANIMATING_ACTIONS_BUILDING_UPKEEP,
        ANIMATING_ACTIONS_GENERATE_VALUE,
        ANIMATING_REWARDS, // Pop up the UI and count up the points
        REWARDS_FINAL; // Show all the points counted up

        public static Phase getNextPhase(Phase phase) {
            switch (phase) {
                case READY:                             return Phase.ANIMATING_ACTIONS_GENERATE_TRASH;
                case ANIMATING_ACTIONS_GENERATE_TRASH:  return Phase.ANIMATING_ACTIONS_RUN_TRUCKS;
                case ANIMATING_ACTIONS_RUN_TRUCKS:      return Phase.ANIMATING_ACTIONS_BUILDING_UPKEEP;
                case ANIMATING_ACTIONS_BUILDING_UPKEEP: return Phase.ANIMATING_ACTIONS_GENERATE_VALUE;
                case ANIMATING_ACTIONS_GENERATE_VALUE:  return Phase.REWARDS_FINAL;
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
        firstTime = true;
        world = World.GetWorld();
        SoundManager.playSound(SoundManager.SoundOptions.startActionPhase);
//        Gdx.input.setInputProcessor(this);
        float camTargetZoom = Math.max(
                World.pixels_wide * 1.1f / hudCamera.viewportWidth,
                World.pixels_high * 1.1f / hudCamera.viewportHeight
        );
        camera.zoom = camTargetZoom;
        camera.position.set(World.pixels_wide /2f, World.pixels_high/2f, 0);
        turnStatisticsModalWindow = new TurnStatisticsModalWindow(hudCamera);
        // Reset all trucks for action phase
        for (DumpTruck dumpTruck : world.routes.trucks) {
            dumpTruck.resetForActionPhase();
        }
        // Set the phase!
        setPhase(Phase.READY);
    }

    private void nextPhase() {
        if (currentPhase == Phase.REWARDS_FINAL) {
            LudumDare40.game.setScreen(new ResolutionPhaseScreen(), Assets.crosshatchShader, 1f);
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
                turnStatisticsModalWindow.show();

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
        if (currentPhase == Phase.READY && allowInput){
            nextPhase();
        }
    }

    private void updateWorld(float dt) {
        world.update(dt);
    }


    private static final float  AA_GENERATE_TRASH_BUILDING_DELAY = 0.01f;
    private int                 aaGenerateTrashBuildingsComplete = 0;
    private int                 aaGenerateTrashLastBuildingIndex;
    private Building            aaGenerateTrashLastBuilding;
    private boolean             aaGenerateTrashBuildingsAreProcessed = false;

    private Building            aaRunTrucksLastBuildingModified; // use this to track animations
    private boolean             aaRunTrucksTrucksAreRunning = false;
    private boolean             aaRunTrucksAllTrucksAreDone = false;

    private static final float  AA_BUILDING_UPKEEP_BUILDING_DELAY = 0.01f;
    private int                 aaBuildingUpkeepBuildingsComplete = 0;
    private int                 aaBuildingUpkeepLastBuildingIndex;
    private Building            aaBuildingUpkeepLastBuilding;
    private boolean             aaBuildingUpkeepBuildingsAreProcessed = false;

    private static final float  AA_GENERATE_VALUE_BUILDING_DELAY = 0.01f;
    private int                 aaGenerateValueBuildingsComplete = 0;
    private int                 aaGenerateValueLastBuildingIndex;
    private Building            aaGenerateValueLastBuilding;
    private boolean             aaGenerateValueBuildingsAreProcessed = false;

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
        if (currentPhase == Phase.ANIMATING_ACTIONS_GENERATE_VALUE) {
            debugPhaseLabel = "generate value";

            if (!aaGenerateValueBuildingsAreProcessed) {
                // We need to do work, generate trash and the like
                int buildingsToCompleteTarget = 1 + (int) Math.floor(currentPhaseDT / AA_GENERATE_VALUE_BUILDING_DELAY);
                if (aaGenerateValueBuildingsComplete < buildingsToCompleteTarget) {
                    int startingIndex = aaGenerateValueBuildingsComplete == 0 ? 0 : aaGenerateValueLastBuildingIndex + 1;
                    for (int i = startingIndex; i < World.buildings.size; i++) {
                        if (World.buildings.get(i).generateValue(!currentPhaseSkipAnimation)) {
                            aaGenerateValueLastBuilding = World.buildings.get(i);
                            aaGenerateValueLastBuildingIndex = i;
                            aaGenerateValueBuildingsComplete++;
                            // Check to see if we should stop
                            if (!currentPhaseSkipAnimation &&
                                    aaGenerateValueBuildingsComplete >= buildingsToCompleteTarget) {
                                break;
                            }
                        }
                        // Was this the last one?
                        if (i >= World.buildings.size - 1) {
                            aaGenerateValueBuildingsAreProcessed = true;
                        }
                    }
                }
            }
            if (aaGenerateValueBuildingsAreProcessed) {
                // Is it time to move on to the next phase?
                if (currentPhaseSkipAnimation ||
                        aaGenerateValueLastBuilding == null ||
                        !aaGenerateValueLastBuilding.isAnimating()) {
                    // Time to go to the next step!
                    setPhase(Phase.getNextPhase(currentPhase));
                }
            }

        }

        if (currentPhase == Phase.ANIMATING_REWARDS || currentPhase == Phase.REWARDS_FINAL) {
            // This is a "halt" point... e.g. if we're skipping animations we'll restart them here.
            currentPhaseSkipAnimation = false;
            float totalGarbageGenerated = 0;
            float totalGarbageHauled = 0;
            float totalGarbageInLandfill = 0;
            float totalGarbageRecycled = 0;
            int totalMoneyGained = 0;
            int numberOfAddons = 0;
            for (int i = 0; i < World.buildings.size; i++) {
                totalGarbageGenerated += World.buildings.get(i).thisTurnGarbageGenerated;
                totalGarbageHauled += World.buildings.get(i).thisTurnGarbageReceived;
                totalGarbageRecycled += World.buildings.get(i).thisTurnGarbageRecycled;
                totalMoneyGained += World.buildings.get(i).thisTurnValueGenerated + World.buildings.get(i).thisTurnValueGeneratedByRecycling;
                if (World.buildings.get(i).hasIncinerator) {
                    numberOfAddons++;
                }
                if (World.buildings.get(i).hasCompactor) {
                    numberOfAddons++;
                }
                if (World.buildings.get(i).hasDumpster) {
                    numberOfAddons++;
                }
                if (World.buildings.get(i).hasGreenCert) {
                    numberOfAddons++;
                }
                if (World.buildings.get(i).hasRecycle) {
                    numberOfAddons++;
                }
                if (World.buildings.get(i).type == Building.Type.DUMP) {
                    totalGarbageInLandfill += World.buildings.get(i).currentTrashLevel;
                }

            }
            if (firstTime) {
                firstTime = false;
                TurnStatistics currentTurnStats = Statistics.getStatistics().getCurrentTurnStatistics();
                currentTurnStats.money += totalMoneyGained;
                currentTurnStats.garbageHauled = totalGarbageHauled;
                currentTurnStats.garbageGenerated = totalGarbageGenerated;
                currentTurnStats.addons = numberOfAddons;
                currentTurnStats.garbageInLandFills = totalGarbageInLandfill;
            }

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
            //renderObjects(batch);
            for (Building building : world.buildings) {
                building.renderValueAnimations(batch);
            }
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
        if (currentPhase == Phase.ANIMATING_REWARDS || currentPhase == Phase.REWARDS_FINAL) {
            turnStatisticsModalWindow.render(batch);
        }
//        Assets.drawString(batch, "Action Phase ("+debugPhaseLabel+")", 20f, 45f, Color.GOLD, 0.5f, Assets.font);
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        nextPhase();
        return true;
    }
}
