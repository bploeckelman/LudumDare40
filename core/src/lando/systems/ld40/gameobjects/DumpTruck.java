package lando.systems.ld40.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import lando.systems.ld40.buildings.Building;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.world.World;

import java.util.ArrayList;

/**
 * Created by Brian on 12/3/2017.
 */
public class DumpTruck extends GameObject {

    public static class TruckType {
        public int speed = 2;
        public int capacity = 50;
        public Array<TextureAtlas.AtlasRegion> truckTextures;

        public TruckType(int speed, int capacity, Array<TextureAtlas.AtlasRegion> truckTextures) {
            this.speed = speed;
            this.capacity = capacity;
            this.truckTextures = truckTextures;
        }
    }

    public static final TruckType One = new TruckType(1, 10, Assets.truck1AtlasRegions);
    public static final TruckType Two = new TruckType(1, 20, Assets.truck2AtlasRegions);
    public static final TruckType Three = new TruckType(1, 40, Assets.truck3AtlasRegions);
    public static final TruckType Four = new TruckType(2, 10, Assets.truck1AtlasRegions);
    public static final TruckType Five = new TruckType(2, 20, Assets.truck2AtlasRegions);
    public static final TruckType Six = new TruckType(2, 40, Assets.truck3AtlasRegions);
    public static final TruckType Seven = new TruckType(3, 10, Assets.truck1AtlasRegions);
    public static final TruckType Eight = new TruckType(3, 20, Assets.truck2AtlasRegions);
    public static final TruckType Nine = new TruckType(3, 40, Assets.truck3AtlasRegions);

    // -----------------------------------------------------------------------------------------------------------------

    private static final float MOVE_TIME = 0.7f; // time from start to dest
    private static final float STOP_WAIT_TIME = .2f; // trucks pause on building this long
    private static final float TILE_OFFSET_Y = 10f;
    private static final float TRUCK_SIZE_SCALE = 1.6f;
    public final UpgradeType type = UpgradeType.TRUCK;

    public int speed;
    public Array<TextureAtlas.AtlasRegion> truckTextures;
    public int capacity;
    public TruckType truckType;
    public float currentTrash;
    private World world;

    private final float computedTileOffsetX;


    // -----------------------------------------------------------------------------------------------------------------


    public DumpTruck(TruckType type) {
        truckTextures = type.truckTextures;
        setTexture(truckTextures.get(0));
        speed = type.speed;
        capacity = type.capacity;
        truckType = type;
        currentTrash = 0;

        // Update the size
        setSize(texture.getRegionWidth() * TRUCK_SIZE_SCALE, texture.getRegionHeight() * TRUCK_SIZE_SCALE);

        computedTileOffsetX = (World.tile_pixels_wide * 0.5f) - (bounds.width / 2);
    }

    private boolean     thisActionPhaseStarted;
    private boolean     thisActionPhaseComplete; // Everything is done, the truck is sitting back home on HQ
    private boolean     thisActionPhaseShowAnimation;
    private boolean     thisActionTruckReturningToHQ;
    private Building    thisActionPhaseTargetBuilding;
    private float       thisActionPhaseTrashDeposited;
    private boolean     thisActionPhaseTargetBuildingProcessed;
    private float       thisActionPhaseTrashPickedUp;
    private int         thisActionPhaseNextStopIndex;
    private IntArray    thisActionPhaseRoute;
    private Vector2     thisActionPhaseStartPos = new Vector2(0,0);
    private Vector2     thisActionPhaseTargetPos = new Vector2(0,0);
    private Vector2     thisActionPhaseTargetPosDiff = new Vector2(0,0);
    private float       thisActionPhaseCurrentStopDT;
    public void resetForActionPhase() {

        // move the truck to home
        Building hq = World.buildings.get(World.hqIndex);
        thisActionPhaseStartPos.x = hq.position.x + computedTileOffsetX;
        thisActionPhaseStartPos.y = hq.position.y + TILE_OFFSET_Y;
        position.x = thisActionPhaseStartPos.x;
        position.y = thisActionPhaseStartPos.y;
        // todo: currently, trash does NOT reset to 0.

        thisActionPhaseStarted = false;
        thisActionPhaseComplete = false;
        thisActionPhaseNextStopIndex = 0;
        thisActionPhaseRoute = World.GetWorld().routes.routes.get(this);
        thisActionPhaseTargetBuilding = null;
        thisActionPhaseTrashDeposited = 0;
        thisActionPhaseTrashPickedUp = 0;
        thisActionPhaseShowAnimation = false;
        thisActionTruckReturningToHQ = false;
        thisActionPhaseTargetBuildingProcessed = false;
        thisActionPhaseCurrentStopDT = 0;

        if (thisActionPhaseRoute == null) {
            throw new RuntimeException("Failed to find route for dumptruck");
        }
    }

    public boolean actionPhaseIsComplete() {
        // NOTE, don't report complete until all value animations complete
        return thisActionPhaseComplete && !valueAnimationsInProgress();
    }

    public void actionPhaseRun(boolean animate) {
        if (thisActionPhaseStarted) {
            throw new RuntimeException("You can only run the truck once per action phase");
        }
        thisActionPhaseStarted = true;
        // Animate flag
        thisActionPhaseShowAnimation = animate;
        actionPhaseGoToAndProcessNextStop();
    }

    private void actionPhaseGoToAndProcessNextStop() {
        if (thisActionPhaseComplete) { return; }
        if (thisActionPhaseTargetBuilding != null &&
                thisActionPhaseTargetBuilding.type == Building.Type.GARBAGE_HQ) {
            // There is no next stop... we're done.
            thisActionPhaseComplete = true;
            return;
        }
        // Resets
        thisActionPhaseCurrentStopDT = 0;
        thisActionPhaseTargetBuildingProcessed = false;
        // Update the start position?
        if (thisActionPhaseTargetBuilding != null) {
            thisActionPhaseStartPos.x = thisActionPhaseTargetBuilding.position.x + computedTileOffsetX;
            thisActionPhaseStartPos.y = thisActionPhaseTargetBuilding.position.y + TILE_OFFSET_Y;
        }
        // Choose the next stop!
        if (thisActionPhaseNextStopIndex < thisActionPhaseRoute.size) {
            // Go to the next stop.
            thisActionPhaseTargetBuilding = World.buildings.get(thisActionPhaseRoute.get(thisActionPhaseNextStopIndex));
            thisActionPhaseTargetPos.x = thisActionPhaseTargetBuilding.position.x + computedTileOffsetX;
            thisActionPhaseTargetPos.y = thisActionPhaseTargetBuilding.position.y + TILE_OFFSET_Y;
        } else {
            // Go home to the HQ
            thisActionPhaseTargetBuilding = World.buildings.get(World.hqIndex);
            thisActionPhaseTargetPos.x = thisActionPhaseTargetBuilding.position.x + computedTileOffsetX;
            thisActionPhaseTargetPos.y = thisActionPhaseTargetBuilding.position.y + TILE_OFFSET_Y;
            thisActionTruckReturningToHQ = true;
        }
        thisActionPhaseTargetPosDiff.x = thisActionPhaseTargetPos.x - thisActionPhaseStartPos.x;
        thisActionPhaseTargetPosDiff.y = thisActionPhaseTargetPos.y - thisActionPhaseStartPos.y;
        thisActionPhaseNextStopIndex++;
    }

    private void pickUpTrash(Building building) {
        float newTrash = building.removeTrash(capacity - currentTrash, thisActionPhaseShowAnimation);
        thisActionPhaseTrashPickedUp += newTrash;
        currentTrash += newTrash;
        if (thisActionPhaseShowAnimation) {
            addValueAnimation(new ValueAnimation(newTrash, ValueAnimationIcon.TRASH, new ArrayList<ValueAnimationIcon>()));
        }
        updateTexture();
    }

    private void depositTrash(Building building) {
        float trashRemainder = building.depositTrash(currentTrash, thisActionPhaseShowAnimation);
        float trashDeposited = currentTrash - trashRemainder;
        thisActionPhaseTrashDeposited += trashDeposited;
        currentTrash = trashRemainder;
        if (thisActionPhaseShowAnimation) {
            addValueAnimation(new ValueAnimation(-trashDeposited, ValueAnimationIcon.TRASH, new ArrayList<ValueAnimationIcon>()));
        }
        updateTexture();
    }

    private void updateTexture() {
        if (currentTrash == 0) {
            // Truck empty
            texture = truckTextures.get(0);
        } else if (currentTrash < capacity) {
            // Truck has trash
            texture = truckTextures.get(1);
        } else {
            // Truck is full!
            texture = truckTextures.get(2);
        }
    }

    /**
     * Abort any and all animations in progress... and interrupt if you will.
     */
    public void clearAnimations() {
        // TODO:
    }

    public void setType(TruckType type){
        truckTextures = type.truckTextures;
        setTexture(truckTextures.get(0));
        speed = type.speed;
        capacity = type.capacity;
        truckType = type;
    }



    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void update(float dt) {
        thisActionPhaseCurrentStopDT += dt;
        updateValueAnimations(dt);
        updateHelperUpdateTruck();
    }

    private void updateHelperUpdateTruck() {

        if (!thisActionPhaseStarted || thisActionPhaseComplete) {
            return;
        }

        boolean moveTimeComplete = thisActionPhaseCurrentStopDT >= MOVE_TIME;
        boolean waitTimeComplete = thisActionPhaseCurrentStopDT >= MOVE_TIME + STOP_WAIT_TIME;

        // Move the truck
        if (thisActionPhaseShowAnimation) {
            if (moveTimeComplete) {
                position.x = thisActionPhaseTargetPos.x;
                position.y = thisActionPhaseTargetPos.y;
            } else {
                float percent = Math.min(thisActionPhaseCurrentStopDT / MOVE_TIME, 1);
                position.x = thisActionPhaseStartPos.x + thisActionPhaseTargetPosDiff.x * percent;
                position.y = thisActionPhaseStartPos.y + thisActionPhaseTargetPosDiff.y * percent;
            }
        }

        // Process the building?
        if (!thisActionPhaseTargetBuildingProcessed) {
            if (thisActionPhaseShowAnimation) {
                if (moveTimeComplete) {
                    actionPhaseProcessBuilding();
                }
            } else {
                actionPhaseProcessBuilding();
            }
        }

        // Go to the next stop?
        if (!thisActionPhaseShowAnimation) {
            actionPhaseGoToAndProcessNextStop();
            // Animations disabled... go again if needed.
            if (!thisActionPhaseComplete) {
                updateHelperUpdateTruck();
            }
        } else {
            // Have we waited long enough?
            if (waitTimeComplete) {
                actionPhaseGoToAndProcessNextStop();
            }
        }
    }

    private void actionPhaseProcessBuilding() {
        if (thisActionPhaseTargetBuildingProcessed) {
            return;
        }
        // Pick up trash?
        if (thisActionPhaseTargetBuilding.type != Building.Type.GARBAGE_HQ &&
                thisActionPhaseTargetBuilding.type != Building.Type.DUMP) {
            pickUpTrash(thisActionPhaseTargetBuilding);
        }
        // Drop off trash (only at the dump)
        if (thisActionPhaseTargetBuilding.type == Building.Type.DUMP) {
            depositTrash(thisActionPhaseTargetBuilding);
        }
        // Flip the flag
        thisActionPhaseTargetBuildingProcessed = true;
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        renderValueAnimations(batch);
    }

    // Value Animations ------------------------------------------------------------------------------------------------

    private ArrayList<ValueAnimation> valueAnimations = new ArrayList<ValueAnimation>();
    private void addValueAnimation(ValueAnimation valueAnimation) {
        valueAnimations.add(valueAnimation);
    }
    private void renderValueAnimations(SpriteBatch batch) {
        for (ValueAnimation valueAnimation : valueAnimations) {
            valueAnimation.render(batch, bounds.x + 10, bounds.y + bounds.height * 0.9f);
        }
    }
    private void updateValueAnimations(float dt) {
        ArrayList<ValueAnimation> valueAnimationsToRemove = new ArrayList<ValueAnimation>();
        for (ValueAnimation valueAnimation : valueAnimations) {
            valueAnimation.update(dt);
            // Prune?
            if (valueAnimation.isComplete) {
                valueAnimationsToRemove.add(valueAnimation);
            }
        }
        if (valueAnimationsToRemove.size() > 0) {
            valueAnimations.removeAll(valueAnimationsToRemove);
        }

    }
    private boolean valueAnimationsInProgress() {
        return valueAnimations.size() > 0;
    }

}
