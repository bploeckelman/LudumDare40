package lando.systems.ld40.buildings;

import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld40.gameobjects.Tile;
import lando.systems.ld40.gameobjects.UpgradeType;
import lando.systems.ld40.gameobjects.ValueAnimation;
import lando.systems.ld40.gameobjects.ValueAnimationIcon;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.SoundManager;
import lando.systems.ld40.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class Building extends Tile {

    private static final float DUMPSTER_CAPACITY_BONUS = 3;
    /**
     * Of trash deposited, reduce by this percent. 0 through 1f;
     */
    private static final float RECYCLE_PERCENT = 0.2f;
    /**
     * When reducing trash via recycling, generate this much revenue for each 1 trash.
     */
    private static final float RECYCLE_VALUE_GENERATION = 0.5f;
    /**
     *
     */
    private static final float COMPACTOR_REDUCTION_PERCENT = 0.4f;
    /**
     * The amount of trash that can be incinerated per turn
     */
    private static final float INCINERATION_VALUE = 2f;
    /**
     * Green certs will reduce the garbage generated per turn by a fixed percent.
     */
    private static final float GREEN_CERT_TRASH_GENERATION_REDUCTION_PERCENT = 0.2f;
    /**
     * If the building supports tiers, each level will increase both valueString and garbage generation by a fixed percent.
     */
    private static final float TIER_LEVEL_GENERATION_BOOST_PERCENT = 0.25f;

    public static float CUTOUT_Y_OFFSET = 8;
    public static float CUTOUT_X_OFFSET = 8;

    public Type type;

    public boolean supportsCompactor;
    public boolean hasCompactor;
    public boolean supportsDumpster;
    public boolean hasDumpster;
    public boolean supportsRecycle;
    public boolean hasRecycle;
    public boolean supportsGreenCert;
    public boolean hasGreenCert;
    public boolean supportsIncinerator;
    public boolean hasIncinerator;
    public boolean supportsTiers;
    public boolean canBuild;
    public boolean canRaze;
    public Tier currentTier;

    public float trashGeneratedPerRound;
    public float valueGeneratedPerRound;
    public float baseTrashCapacity;
    public float currentTrashLevel;

    public Resource resource;

    private boolean thisTurnTrashHasBeenGenerated;
    private boolean thisTurnUpkeepHasBeenRun;
    private boolean thisTurnValueHasBeenGenerated;
    public float thisTurnAdditionalTrashGeneratedByTier;
    public float thisTurnAdditionalValueGeneratedByTier;
    public float thisTurnGarbageCompacted;
    public float thisTurnGarbageGenerated;
    public float thisTurnGarbageIncinerated;
    public float thisTurnGarbageReceived;
    public float thisTurnGarbageRecycled;
    public float thisTurnGreenCertTrashReduction;
    public float thisTurnValueGenerated;
    public float thisTurnValueGeneratedByRecycling;

    /**
     * This tracks contiguous turns over capacity
     */
    private int turnsOverCapacity = 0;
    private boolean isMarkedForRemoval = false;
    private Color trashColor = new Color();

    private final float trash_button_default_size = 1f;
    private MutableFloat trashButtonSizeScale = new MutableFloat(trash_button_default_size);
    private float accum = 0f;

    // if this gets grayed out in world
    public boolean filtered = false;


    // -----------------------------------------------------------------------------------------------------------------

    private enum Tier {
        ONE,
        TWO,
        THREE;

        public Tier next() {
            if      (this == ONE)   return TWO;
            else if (this == TWO)   return THREE;
            else if (this == THREE) return THREE;
            else                    return ONE;
        }
        public Tier prev() {
            if      (this == ONE)   return ONE;
            else if (this == TWO)   return ONE;
            else if (this == THREE) return TWO;
            else                    return THREE;
        }
    }

    public enum Type {
        COMMERCIAL_LOW,
        COMMERCIAL_MEDIUM,
        COMMERCIAL_HIGH,
        INDUSTRIAL_HIGH,
        INDUSTRIAL_LOW,
        INDUSTRIAL_MEDIUM,
        RESIDENTIAL_HIGH,
        RESIDENTIAL_LOW,
        RESIDENTIAL_MEDIUM,
        GARBAGE_HQ,
        DUMP,
        EMPTY
    }

    public enum Resource {
        MONEY
    }


    // -----------------------------------------------------------------------------------------------------------------

    public static HashMap<Type, String> buildingTypeTextureLookup = new HashMap<Type, String>();
    static {
        buildingTypeTextureLookup.put(Type.DUMP, "dump");
        buildingTypeTextureLookup.put(Type.COMMERCIAL_LOW, "com-low");
        buildingTypeTextureLookup.put(Type.COMMERCIAL_MEDIUM, "com-med");
        buildingTypeTextureLookup.put(Type.COMMERCIAL_HIGH, "com-high");
        buildingTypeTextureLookup.put(Type.INDUSTRIAL_LOW, "ind-low");
        buildingTypeTextureLookup.put(Type.INDUSTRIAL_MEDIUM, "ind-med");
        buildingTypeTextureLookup.put(Type.INDUSTRIAL_HIGH, "ind-high");
        // TODO: TEXTURE
        buildingTypeTextureLookup.put(Type.RESIDENTIAL_LOW, "res-low");
        buildingTypeTextureLookup.put(Type.RESIDENTIAL_MEDIUM, "res-med");
        buildingTypeTextureLookup.put(Type.RESIDENTIAL_HIGH, "res-high");
        buildingTypeTextureLookup.put(Type.GARBAGE_HQ, "hq");
        buildingTypeTextureLookup.put(Type.EMPTY, "grass");
    }


    // -----------------------------------------------------------------------------------------------------------------

    private Building(Type type,
                     boolean supportsCompactor, boolean hasCompactor,
                     boolean supportsDumpster, boolean hasDumpster,
                     boolean supportsRecycle, boolean hasRecycle,
                     boolean supportsGreenCert, boolean hasGreenCert,
                     boolean supportsIncinerator, boolean hasIncinerator,
                     boolean supportsTiers, Tier currentTier,
                     boolean canBuild, boolean canRaze,
                     float trashGeneratedPerRound, float valueGeneratedPerRound,
                     float baseTrashCapacity, float currentTrashLevel,
                     Resource resource) {

        super("grass1");

        this.type = type;
        String textureName = buildingTypeTextureLookup.get(type);

        if (textureName == null) {
            throw new RuntimeException();
        }
        if (type == Type.EMPTY){
            setTexture(Assets.grassTiles.get(MathUtils.random(Assets.grassTiles.size-1)));
        } else {
            setTexture(Assets.atlas.findRegion(textureName));
        }

        this.currentTier = currentTier;
        this.currentTrashLevel = currentTrashLevel;
        this.hasCompactor = hasCompactor;
        this.hasDumpster = hasDumpster;
        this.hasRecycle = hasRecycle;
        this.hasGreenCert = hasGreenCert;
        this.hasIncinerator = hasIncinerator;
        this.canBuild = canBuild;
        this.canRaze = canRaze;
        this.supportsCompactor = supportsCompactor;
        this.supportsDumpster = supportsDumpster;
        this.supportsRecycle = supportsRecycle;
        this.supportsGreenCert = supportsGreenCert;
        this.supportsIncinerator = supportsIncinerator;
        this.supportsTiers = supportsTiers;
        this.baseTrashCapacity = baseTrashCapacity;
        this.trashGeneratedPerRound = trashGeneratedPerRound;
        this.valueGeneratedPerRound = valueGeneratedPerRound;
        this.resource = resource;

        this.valueAnimations = new ArrayList<ValueAnimation>();

        resetForNewTurn();
    }

    public static Building getBuilding(Type buildingType) {

        // Defaults
        boolean hasCompactor = false;
        boolean hasDumpster = false;
        boolean hasRecycle = false;
        boolean hasGreenCert = false;
        boolean hasIncinerator = false;
        boolean supportsCompactor = false;
        boolean supportsDumpster = false;
        boolean supportsRecycle = false;
        boolean supportsGreenCert = false;
        boolean supportsIncinerator = false;
        boolean supportsTiers = false;
        boolean canBuild = false;
        boolean canRaze = true;
        Tier currentTier = null;
        float trashGeneratedPerRound = 0;
        float valueGeneratedPerRound = 0;
        float baseTrashCapacity = 0;
        float currentTrashLevel = 0;
        Resource resource = null;

        // Customize the types!

        switch (buildingType) {

            case COMMERCIAL_LOW:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 2;
                valueGeneratedPerRound = 30;
                baseTrashCapacity = 6;
                resource = Resource.MONEY;
                break;

            case COMMERCIAL_MEDIUM:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 2;
                valueGeneratedPerRound = 40;
                baseTrashCapacity = 8;
                resource = Resource.MONEY;
                break;

            case COMMERCIAL_HIGH:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 3;
                valueGeneratedPerRound = 60;
                baseTrashCapacity = 12;
                resource = Resource.MONEY;
                break;

            case DUMP:
                supportsCompactor = true;
                supportsIncinerator = true;
                supportsRecycle = true;
                canRaze = false;
                baseTrashCapacity = 100;
                break;

            case INDUSTRIAL_LOW:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 2;
                valueGeneratedPerRound = 40;
                baseTrashCapacity = 10;
                resource = Resource.MONEY;
                break;

            case INDUSTRIAL_MEDIUM:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 3;
                valueGeneratedPerRound = 80;
                baseTrashCapacity = 13;
                resource = Resource.MONEY;
                break;

            case INDUSTRIAL_HIGH:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 3;
                valueGeneratedPerRound = 200;
                baseTrashCapacity = 16;
                resource = Resource.MONEY;
                break;

//            case RECYCLING_CENTER:
//                supportsTiers = true;
//                currentTier = Tier.ONE;
//                resource = Resource.MONEY;
//                break;

            case RESIDENTIAL_LOW:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 1;
                valueGeneratedPerRound = 20;
                baseTrashCapacity = 10;
                resource = Resource.MONEY;
                break;

            case RESIDENTIAL_MEDIUM:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 2;
                valueGeneratedPerRound = 40;
                baseTrashCapacity = 13;
                resource = Resource.MONEY;
                break;

            case RESIDENTIAL_HIGH:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 3;
                valueGeneratedPerRound = 100;
                baseTrashCapacity = 16;
                resource = Resource.MONEY;
                break;

            case EMPTY:
                canBuild = true;
                break;

            case GARBAGE_HQ:
                canRaze = false;
                break;
        }


        return new Building(buildingType,
                supportsCompactor, hasCompactor,
                supportsDumpster, hasDumpster,
                supportsRecycle, hasRecycle,
                supportsGreenCert, hasGreenCert,
                supportsIncinerator, hasIncinerator,
                supportsTiers, currentTier,
                canBuild, canRaze,
                trashGeneratedPerRound, valueGeneratedPerRound,
                baseTrashCapacity, currentTrashLevel,
                resource);
    }

    /**
     * To be called at the start of the turn for each building.
     */
    public void resetForNewTurn() {
        thisTurnAdditionalTrashGeneratedByTier = 0;
        thisTurnAdditionalValueGeneratedByTier = 0;
        thisTurnGarbageCompacted = 0;
        thisTurnGarbageGenerated = 0;
        thisTurnGarbageIncinerated = 0;
        thisTurnGarbageReceived = 0;
        thisTurnGarbageRecycled = 0;
        thisTurnGreenCertTrashReduction = 0;
        thisTurnValueGenerated = 0;
        thisTurnValueGeneratedByRecycling = 0;
        thisTurnTrashHasBeenGenerated = false;
        thisTurnValueHasBeenGenerated = false;
        thisTurnUpkeepHasBeenRun = false;

        valueAnimations.clear();
    }

    /**
     * Add trash to this Building
     * @param trashAmount How much trash you're depositing.
     * @return The amount of trash that won't fit in the building
     * TODO: because of the remainder calculation, you can never overfill a building on deposit.
     */
    public float depositTrash(float trashAmount, boolean animate) {
        if (thisTurnUpkeepHasBeenRun) { throw new RuntimeException("Cannot deposit trash af"); }
        ArrayList<ValueAnimationIcon> modifierIcons = new ArrayList<ValueAnimationIcon>();
        if (hasRecycle) {
            float recycledAmount = trashAmount * RECYCLE_PERCENT;
            thisTurnGarbageRecycled += recycledAmount;
            float recycleValue = recycledAmount * RECYCLE_VALUE_GENERATION;
            thisTurnValueGeneratedByRecycling += recycleValue;
            thisTurnValueGenerated += recycleValue;
            // Reduce the trash
            trashAmount -= recycledAmount;
            modifierIcons.add(ValueAnimationIcon.RECYCLE);
        }
        if (hasCompactor) {
            float compactedAmount = trashAmount * COMPACTOR_REDUCTION_PERCENT;
            thisTurnGarbageCompacted += compactedAmount;
            // Reduce the trash
            trashAmount -= compactedAmount;
            modifierIcons.add(ValueAnimationIcon.COMPRESSOR);
        }
        thisTurnGarbageReceived += trashAmount;
        currentTrashLevel += trashAmount;
        SoundManager.playSound(SoundManager.SoundOptions.dumpTrash);
        float remainder = Math.max(currentTrashLevel - getCurrentTrashCapacity(), 0);
        float trashAdded = trashAmount - remainder;
        if (animate) {
            addValueAnimation(new ValueAnimation(trashAdded, ValueAnimationIcon.TRASH, modifierIcons));
        }
        if (remainder > 0) {
            currentTrashLevel = getCurrentTrashCapacity();
            return remainder;
        } else {
            return 0f;
        }
    }

    /**
     * Deducts trash from this building.
     * @param trashRequested How much trash would you like to take?
     * @return The amount of trash the building gives you.
     */
    public float removeTrash(float trashRequested, boolean animate) {
        float trashToRemove = Math.min(trashRequested, currentTrashLevel);
        currentTrashLevel -= trashToRemove;
        if (animate) {
            SoundManager.playSound(SoundManager.SoundOptions.pickupTrash);
            addValueAnimation(new ValueAnimation(-trashToRemove, ValueAnimationIcon.TRASH, new ArrayList<ValueAnimationIcon>()));
        }
        return trashToRemove;
    }

    /**
     * Generate the trash for the building.
     * Track it in the thisTurn variables
     * Add the trash to the building.
     * @return True if this building generates trash (e.g. kicks off animation), false otherwise.
     */
    public boolean generateTrash(boolean animate) {
        if (thisTurnTrashHasBeenGenerated) { throw new RuntimeException("You've already generated trash this turn"); }
        // Flag
        thisTurnTrashHasBeenGenerated = true;
        // Does this tile generate trash?
        if (trashGeneratedPerRound == 0) {
            // Nothing to do here...
            return false;
        }
        ArrayList<ValueAnimationIcon> modifierIcons = new ArrayList<ValueAnimationIcon>();
        float newTrash = trashGeneratedPerRound;
        float additonalTrashFromTiers = getAdditionalValueByTiers(newTrash);
        if (additonalTrashFromTiers > 0) {
            thisTurnAdditionalTrashGeneratedByTier += additonalTrashFromTiers;
            newTrash += additonalTrashFromTiers;
            modifierIcons.add(ValueAnimationIcon.TIER);
        }
        // Green cert reduction?
        if (hasGreenCert) {
            float greenCertTrashReduction = newTrash * GREEN_CERT_TRASH_GENERATION_REDUCTION_PERCENT;
            thisTurnGreenCertTrashReduction += greenCertTrashReduction;
            newTrash -= greenCertTrashReduction;
            modifierIcons.add(ValueAnimationIcon.GREEN_CERT);
        }
        thisTurnGarbageGenerated += newTrash;
        // Add the trash to the building.
        currentTrashLevel += newTrash;
        // Animate?
        if (animate) {
            addValueAnimation(new ValueAnimation(newTrash, ValueAnimationIcon.TRASH, modifierIcons));
        }
        return true;
    }

    /**
     *
     * @param animate
     * @return True if the building was modified in an animate-able/updatable fashion
     */
    public boolean runUpkeep(boolean animate) {
        if (thisTurnUpkeepHasBeenRun) { throw new RuntimeException("You've already run upkeep this turn"); }
        if (!thisTurnTrashHasBeenGenerated) { throw new RuntimeException("Must generate your trash before running upkeep!"); }
        boolean wasModified = false;
        ArrayList<ValueAnimationIcon> trashModifierIcons = new ArrayList<ValueAnimationIcon>();
        // Incinerate!
        if (hasIncinerator) {
            float garbageIncinerated = Math.min(currentTrashLevel, INCINERATION_VALUE);
            if (garbageIncinerated > 0) {
                wasModified = true;
                thisTurnGarbageIncinerated += garbageIncinerated;
                currentTrashLevel -= garbageIncinerated;
                trashModifierIcons.add(ValueAnimationIcon.INCINERATOR);
            }
        }
        // Capacity check!
        if (currentTrashLevel > getCurrentTrashCapacity()) {
            turnsOverCapacity += 1;
            wasModified = true; // over cap (again)
        } else {
            if (turnsOverCapacity > 0) {
                wasModified = true; // no longer over cap.
            }
            turnsOverCapacity = 0;
        }
        thisTurnUpkeepHasBeenRun = true;
        if (animate) {
            if (thisTurnGarbageIncinerated > 0) {
                addValueAnimation(new ValueAnimation(-thisTurnGarbageIncinerated, ValueAnimationIcon.TRASH, trashModifierIcons));
            }
        }
        return wasModified;
    }

    /**
     *
     * @param animate
     * @return True if this building generates a reward
     */
    public boolean generateValue(boolean animate) {
        if (thisTurnValueHasBeenGenerated) { throw new RuntimeException("You've already run valueString generation this turn"); }
        if (!thisTurnUpkeepHasBeenRun) { throw new RuntimeException("Must run upkeep before generating valueString!"); }
        thisTurnValueHasBeenGenerated = true;
        if (valueGeneratedPerRound == 0) {
            // Nothing to do here
            return false;
        }
        ArrayList<ValueAnimationIcon> modifierIcons = new ArrayList<ValueAnimationIcon>();
        float newValue = valueGeneratedPerRound;
        float additonalValueFromTiers = getAdditionalValueByTiers(newValue);
        if (additonalValueFromTiers > 0) {
            thisTurnAdditionalValueGeneratedByTier += additonalValueFromTiers;
            newValue += additonalValueFromTiers;
            modifierIcons.add(ValueAnimationIcon.TIER);
        }
        if (turnsOverCapacity > 0) {
            newValue = 0;
            modifierIcons.add(ValueAnimationIcon.OVER_CAPACITY);
            // We'll still animate this so they see that they're missing out.
        }
        thisTurnValueGenerated += newValue;
        if (animate) {
            addValueAnimation(new ValueAnimation(newValue, ValueAnimationIcon.MONEY, modifierIcons));
        }
        return true;
    }

    /**
     * For both Trash and Value
     * @param baseValue
     * @return
     */
    private float getAdditionalValueByTiers(float baseValue) {
        if (supportsTiers) {
            float additionalValue;
            switch (currentTier) {
                case ONE:
                    additionalValue = 0;
                    break;
                case TWO:
                    additionalValue = baseValue * (TIER_LEVEL_GENERATION_BOOST_PERCENT);
                    break;
                case THREE:
                    additionalValue = baseValue * (TIER_LEVEL_GENERATION_BOOST_PERCENT * 2);
                    break;
                default:
                    throw new RuntimeException("Unrecognized Tier");
            }
            return additionalValue;
        } else {
            return 0;
        }
    }

    public float getCurrentTrashCapacity() {
        float trashCapacity = baseTrashCapacity;
        if (hasDumpster) {
            trashCapacity += DUMPSTER_CAPACITY_BONUS;
        }
        return trashCapacity;
    }

    public float getFinalTrash() {
        float finalTrash = (this.trashGeneratedPerRound + getAdditionalValueByTiers(this.trashGeneratedPerRound));
        if (hasGreenCert) {
            finalTrash -= finalTrash * GREEN_CERT_TRASH_GENERATION_REDUCTION_PERCENT;
        }
        return finalTrash;
    }

    public float getFinalValue() {
        float finalValue = (this.trashGeneratedPerRound + getAdditionalValueByTiers(this.trashGeneratedPerRound));
        return finalValue;
    }

    public boolean allowsUpgrade(UpgradeType upgradeType) {
        switch (upgradeType) {
            case DEMOLITION:   return canRaze;
            case COMPACTOR:    return supportsCompactor   && !hasCompactor;
            case DUMPSTER:     return supportsDumpster    && !hasDumpster;
            case GREEN_TOKEN:  return supportsGreenCert   && !hasGreenCert;
            case INCINERATOR:  return supportsIncinerator && !hasIncinerator;
            case RECLAMATION:  return supportsRecycle     && !hasRecycle;
            case TIER_UPGRADE: return supportsTiers       && currentTier != Tier.THREE;
//            case TRUCK:        return false;
            default:           return false;
        }
    }

    public void applyUpgrade(UpgradeType upgradeType) {
        if (!allowsUpgrade(upgradeType)) return;

        switch (upgradeType) {
            case COMPACTOR:    if (supportsCompactor)   hasCompactor   = true; break;
            case DUMPSTER:     if (supportsDumpster)    hasDumpster    = true; break;
            case GREEN_TOKEN:  if (supportsGreenCert)   hasGreenCert   = true; break;
            case INCINERATOR:  if (supportsIncinerator) hasIncinerator = true; break;
            case RECLAMATION:  if (supportsRecycle)     hasRecycle     = true; break;
            case TIER_UPGRADE: if (supportsTiers)       currentTier    = currentTier.next(); break;
        }
    }

    public void removeUpgrade(UpgradeType upgradeType) {
        if (!allowsUpgrade(upgradeType)) return;

        switch (upgradeType) {
            case COMPACTOR:    hasCompactor   = false; break;
            case DUMPSTER:     hasDumpster    = false; break;
            case GREEN_TOKEN:  hasGreenCert   = false; break;
            case INCINERATOR:  hasIncinerator = false; break;
            case RECLAMATION:  hasRecycle     = false; break;
            case TIER_UPGRADE: currentTier    = currentTier.prev(); break;
        }
    }

    public void demolish() {
        type = Type.EMPTY;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void render(SpriteBatch batch){
        super.render(batch);
        if (supportsCompactor){
            TextureRegion compactor = hasCompactor ? Assets.compactorTexture : Assets.compactorCutoutTexture;
            batch.draw(compactor, bounds.x + CUTOUT_X_OFFSET, bounds.y + CUTOUT_Y_OFFSET);
        }
        if (supportsIncinerator){
            TextureRegion incinerator = hasIncinerator ? Assets.incineratorTexture : Assets.incineratorCutoutTexture;
            batch.draw(incinerator, bounds.x + (bounds.width - incinerator.getRegionWidth())/2, bounds.y + CUTOUT_Y_OFFSET);
        }
        if (supportsRecycle){
            TextureRegion recycle = hasRecycle ? Assets.recycleTexture : Assets.recycleCutoutTexture;
            batch.draw(recycle, bounds.x + bounds.width - (recycle.getRegionWidth() + CUTOUT_X_OFFSET), bounds.y + CUTOUT_Y_OFFSET);
        }
        if (supportsDumpster){
            TextureRegion dumpster = hasDumpster ? Assets.dumpsterTexture : Assets.dumpsterCutoutTexture;
            batch.draw(dumpster, bounds.x + CUTOUT_X_OFFSET, bounds.y + CUTOUT_Y_OFFSET);
        }
        if (supportsGreenCert){
            TextureRegion greenCert = hasGreenCert ? Assets.leafTexture : Assets.leafCutoutTexture;
            batch.draw(greenCert, bounds.x + (CUTOUT_Y_OFFSET / 2f), bounds.y +  bounds.height - (greenCert.getRegionHeight() + (CUTOUT_Y_OFFSET / 2f)));
        }
        if (supportsTiers) {
            switch (currentTier) {

                case ONE:
                    TextureRegion tier1 = Assets.tier1Texture;
                    float tier1TextureWidth = tier1.getRegionWidth() * 2;
                    float tier1TextureHeight = tier1.getRegionHeight() * 2;
                    batch.draw(tier1, bounds.x + (bounds.width - tier1TextureWidth)/2, bounds.y + (bounds.height - tier1TextureHeight)/2, tier1TextureWidth, tier1TextureHeight);
                    break;

                case TWO:
                    TextureRegion tier2 = Assets.tier2Texture;
                    float tier2TextureWidth = tier2.getRegionWidth() * 2;
                    float tier2TextureHeight = tier2.getRegionHeight() * 2;
                    batch.draw(tier2, bounds.x + (bounds.width - tier2TextureWidth)/2, bounds.y + (bounds.height - tier2TextureHeight)/2, tier2TextureWidth, tier2TextureHeight);
                    break;

                case THREE:
                    TextureRegion tier3 = Assets.tier3Texture;
                    float tier3TextureWidth = tier3.getRegionWidth() * 2;
                    float tier3TextureHeight = tier3.getRegionHeight() * 2;
                    batch.draw(tier3, bounds.x + (bounds.width - tier3TextureWidth)/2, bounds.y + (bounds.height - tier3TextureHeight)/2, tier3TextureWidth, tier3TextureHeight);
                    break;

            }
        }

        if (currentTrashLevel > 0) {
            TextureRegion trashIcon = (currentTrashLevel >= getCurrentTrashCapacity()) ? Assets.trashButtonFull : Assets.trashButton;
            float trashButtonCurrentSize = trashButtonSizeScale.floatValue();
            batch.draw(trashIcon,
                    bounds.x + bounds.width - 44f,
                    bounds.y + bounds.height - 44f,
                    trashIcon.getRegionWidth() / 2f, trashIcon.getRegionHeight() / 2f,
                    trashIcon.getRegionWidth(), trashIcon.getRegionHeight(),
                    trashButtonCurrentSize, trashButtonCurrentSize,
                    0f);

            float n = 1.0f - MathUtils.clamp(currentTrashLevel / getCurrentTrashCapacity(), 0, 1f);
            trashColor = Utils.hsvToRgb(((n * 120f) - 20) / 365f, 1.0f, 1.0f, trashColor);
            Assets.drawString(batch,
                    (int)currentTrashLevel + "",
                    bounds.x + bounds.width - 44f,
                    bounds.y + bounds.height ,
                    trashColor, .5f, Assets.font, Assets.trashButton.getRegionWidth(), Align.center);
        }

        if (filtered) {
            batch.draw(Assets.tileCover, bounds.x, bounds.y, bounds.width, bounds.height);
        }
//        renderValueAnimations(batch);
    }

    @Override
    public void render(SpriteBatch batch, float x, float y, float w, float h) {
        super.render(batch, x, y, w, h);
        float wScale = w / texture.getRegionWidth();
        float hScale = h / texture.getRegionHeight();
        TextureRegion addonTexture;
        if (supportsCompactor){
            addonTexture = hasCompactor ? Assets.compactorTexture : Assets.compactorCutoutTexture;
            batch.draw(addonTexture,
                    x + CUTOUT_X_OFFSET * wScale,
                    y + CUTOUT_Y_OFFSET * hScale,
                    wScale  * addonTexture.getRegionWidth(),
                    hScale * addonTexture.getRegionHeight());
        }
        if (supportsIncinerator){
            addonTexture = hasIncinerator ? Assets.incineratorTexture : Assets.incineratorCutoutTexture;
            batch.draw(addonTexture,
                    x + (w - wScale * addonTexture.getRegionWidth()) / 2,
                    y + CUTOUT_Y_OFFSET * hScale,
                    wScale  * addonTexture.getRegionWidth(),
                    hScale * addonTexture.getRegionHeight());
        }
        if (supportsRecycle){
            addonTexture = hasRecycle ? Assets.recycleTexture : Assets.recycleCutoutTexture;
            batch.draw(addonTexture,
                    x + w - wScale * (addonTexture.getRegionWidth() + CUTOUT_X_OFFSET),
                    y + CUTOUT_Y_OFFSET * hScale,
                    wScale  * addonTexture.getRegionWidth(),
                    hScale * addonTexture.getRegionHeight());
        }
        if (supportsDumpster){
            addonTexture = hasDumpster ? Assets.dumpsterTexture : Assets.dumpsterCutoutTexture;
            batch.draw(addonTexture,
                    x + CUTOUT_X_OFFSET * wScale,
                    y + CUTOUT_Y_OFFSET * hScale,
                    wScale  * addonTexture.getRegionWidth(),
                    hScale * addonTexture.getRegionHeight());
        }
        if (supportsGreenCert){
            addonTexture = hasGreenCert ? Assets.leafTexture : Assets.leafCutoutTexture;
            batch.draw(addonTexture,
                    x + (CUTOUT_X_OFFSET / 2f) * wScale,
                    y + h - hScale * (addonTexture.getRegionWidth() + (CUTOUT_Y_OFFSET / 2f)),
                    wScale  * addonTexture.getRegionWidth(),
                    hScale * addonTexture.getRegionHeight());
        }
        if (supportsTiers) {
            addonTexture = null;

            switch (currentTier) {
                case ONE:
                    addonTexture = Assets.tier1Texture;
                    break;

                case TWO:
                    addonTexture = Assets.tier2Texture;
                    break;

                case THREE:
                    addonTexture = Assets.tier3Texture;
                    break;

            }

            batch.draw(addonTexture,
                    x + (w - wScale * addonTexture.getRegionWidth()) / 2,
                    y + (h - hScale * addonTexture.getRegionHeight()) / 2,
                    wScale  * addonTexture.getRegionWidth(),
                    hScale * addonTexture.getRegionHeight());
        }


    }

    @Override
    public void update(float dt) {
        super.update(dt);
        updateValueAnimations(dt);
        if (currentTrashLevel < getCurrentTrashCapacity()) {
            trashButtonSizeScale.setValue(1f);
            accum = 0f;
        } else {
            accum += 4f * dt;
            trashButtonSizeScale.setValue(trashButtonSizeScale.floatValue() * ((float) Math.sin(accum) * 0.2f) + 1f);
        }
    }

    // Value Animations ------------------------------------------------------------------------------------------------

    private ArrayList<ValueAnimation> valueAnimations;
    private void addValueAnimation(ValueAnimation valueAnimation) {
        valueAnimations.add(valueAnimation);
    }
    public void renderValueAnimations(SpriteBatch batch) {
        for (ValueAnimation valueAnimation : valueAnimations) {
            valueAnimation.render(batch, bounds.x + 20, bounds.y + bounds.height * 0.9f);
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
    public boolean isAnimating() {
        return valueAnimations.size() > 0;
    }


}
