package lando.systems.ld40.buildings;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld40.gameobjects.Tile;
import lando.systems.ld40.gameobjects.UpgradeType;
import lando.systems.ld40.utils.Assets;

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
     * If the building supports tiers, each level will increase both value and garbage generation by a fixed percent.
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

    public boolean thisTurnActionsAreProcessed;
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


    // -----------------------------------------------------------------------------------------------------------------

    private enum Tier {
        ONE,
        TWO,
        THREE
    }

    public enum Type {
        COMMERCIAL_HIGH,
        COMMERCIAL_LOW,
        COMMERCIAL_MEDIUM,
        DUMP,
        INDUSTRIAL_HIGH,
        INDUSTRIAL_LOW,
        INDUSTRIAL_MEDIUM,
        RECYCLING_CENTER,
        RESIDENTIAL_HIGH,
        RESIDENTIAL_LOW,
        RESIDENTIAL_MEDIUM,
        GARBAGE_HQ,
        EMPTY
    }

    public enum Resource {
        MONEY
    }


    // -----------------------------------------------------------------------------------------------------------------

    private static HashMap<Type, String> buildingTypeTextureLookup = new HashMap<Type, String>();
    static {
        buildingTypeTextureLookup.put(Type.COMMERCIAL_HIGH, "com-high");
        buildingTypeTextureLookup.put(Type.COMMERCIAL_LOW, "com-low");
        buildingTypeTextureLookup.put(Type.COMMERCIAL_MEDIUM, "com-med");
        buildingTypeTextureLookup.put(Type.DUMP, "dump");
        buildingTypeTextureLookup.put(Type.INDUSTRIAL_HIGH, "ind-high");
        buildingTypeTextureLookup.put(Type.INDUSTRIAL_LOW, "ind-low");
        buildingTypeTextureLookup.put(Type.INDUSTRIAL_MEDIUM, "ind-med");
        // TODO: TEXTURE
        buildingTypeTextureLookup.put(Type.RECYCLING_CENTER, "white-pixel");
        buildingTypeTextureLookup.put(Type.RESIDENTIAL_HIGH, "res-high");
        buildingTypeTextureLookup.put(Type.RESIDENTIAL_LOW, "res-low");
        buildingTypeTextureLookup.put(Type.RESIDENTIAL_MEDIUM, "res-med");
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
                    boolean supportsTiers, boolean canBuild,
                    boolean canRaze,
                    Tier currentTier,
                    float trashGeneratedPerRound,
                    float valueGeneratedPerRound,
                    float baseTrashCapacity,
                    float currentTrashLevel,
                    Resource resource) {

        super("grass");

        this.type = type;
        String textureName = buildingTypeTextureLookup.get(type);
        if (textureName == null) {
            throw new RuntimeException();
        }
        setTexture(Assets.atlas.findRegion(textureName));

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
                trashGeneratedPerRound = 1;
                valueGeneratedPerRound = 1;
                baseTrashCapacity = 10;
                resource = Resource.MONEY;
                break;

            case COMMERCIAL_MEDIUM:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 2;
                valueGeneratedPerRound = 3;
                baseTrashCapacity = 13;
                resource = Resource.MONEY;
                break;

            case COMMERCIAL_HIGH:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 3;
                valueGeneratedPerRound = 5;
                baseTrashCapacity = 16;
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
                trashGeneratedPerRound = 1;
                valueGeneratedPerRound = 1;
                baseTrashCapacity = 10;
                resource = Resource.MONEY;
                break;

            case INDUSTRIAL_MEDIUM:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 2;
                valueGeneratedPerRound = 3;
                baseTrashCapacity = 13;
                resource = Resource.MONEY;
                break;

            case INDUSTRIAL_HIGH:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 3;
                valueGeneratedPerRound = 5;
                baseTrashCapacity = 16;
                resource = Resource.MONEY;
                break;

            case RECYCLING_CENTER:
                supportsTiers = true;
                currentTier = Tier.ONE;
                resource = Resource.MONEY;
                break;

            case RESIDENTIAL_LOW:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 1;
                valueGeneratedPerRound = 1;
                baseTrashCapacity = 10;
                resource = Resource.MONEY;
                break;

            case RESIDENTIAL_MEDIUM:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 2;
                valueGeneratedPerRound = 3;
                baseTrashCapacity = 13;
                resource = Resource.MONEY;
                break;

            case RESIDENTIAL_HIGH:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 3;
                valueGeneratedPerRound = 5;
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
                canBuild, canRaze,
                supportsTiers, currentTier,
                trashGeneratedPerRound,
                valueGeneratedPerRound,
                baseTrashCapacity,
                currentTrashLevel,
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
        thisTurnActionsAreProcessed = false;
    }

    /**
     * Add trash to this Building
     * @param trashAmount How much trash you're depositing.
     */
    public void depositTrash(float trashAmount) {
        if (thisTurnActionsAreProcessed) {
            throw new RuntimeException("Cannot add trash to a building that's already been processed this turn.  Did you forget to reset it?");
        }
        if (hasRecycle) {
            float recycledAmount = trashAmount * RECYCLE_PERCENT;
            thisTurnGarbageRecycled += recycledAmount;
            float recycleValue = recycledAmount * RECYCLE_VALUE_GENERATION;
            thisTurnValueGeneratedByRecycling += recycleValue;
            thisTurnValueGenerated += recycleValue;
            // Reduce the trash
            trashAmount -= recycledAmount;
        }
        if (hasCompactor) {
            float compactedAmount = trashAmount * COMPACTOR_REDUCTION_PERCENT;
            thisTurnGarbageCompacted += compactedAmount;
            // Reduce the trash
            trashAmount -= compactedAmount;
        }
        thisTurnGarbageReceived += trashAmount;
        currentTrashLevel += trashAmount;
    }

    /**
     * Deducts trash from this building.
     * @param trashRequested How much trash would you like to take?
     * @return The amount of trash the building gives you.
     */
    public float removeTrash(float trashRequested) {
        float trashToRemove = Math.min(trashRequested, currentTrashLevel);
        currentTrashLevel -= trashToRemove;
        return trashToRemove;
    }

    /**
     * This is where the building will go to work, generating trash, perhaps getting rid of it, etc.
     */
    public void processActions() {
        if (thisTurnActionsAreProcessed) {
            throw new RuntimeException("Building already processed this turn!");
        }
        generateTrash();
        generateValue();
        // Incinerate!
        if (hasIncinerator) {
            float garbageIncinerated = Math.min(currentTrashLevel, INCINERATION_VALUE);
            thisTurnGarbageIncinerated += garbageIncinerated;
            currentTrashLevel -= garbageIncinerated;
        }
        // Capacity check!
        if (currentTrashLevel > getCurrentTrashCapacity()) {
            turnsOverCapacity += 1;
        } else {
            turnsOverCapacity = 0;
        }
        // At this point the building is done for the turn.
        thisTurnActionsAreProcessed = true;
    }

    /**
     * Generate the trash for the building.
     * Track it in the thisTurn variables
     * Add the trash to the building.
     */
    private void generateTrash() {
        float newTrash = trashGeneratedPerRound;
        float additonalTrashFromTiers = getAdditionalValueByTiers(newTrash);
        if (additonalTrashFromTiers > 0) {
            thisTurnAdditionalTrashGeneratedByTier += additonalTrashFromTiers;
            newTrash += additonalTrashFromTiers;
        }
        // Green cert reduction?
        if (hasGreenCert) {
            float greenCertTrashReduction = newTrash * GREEN_CERT_TRASH_GENERATION_REDUCTION_PERCENT;
            thisTurnGreenCertTrashReduction += greenCertTrashReduction;
            newTrash -= greenCertTrashReduction;
        }
        thisTurnGarbageGenerated += newTrash;
        // Add the trash to the building.
        currentTrashLevel += newTrash;
    }

    private void generateValue() {
        float newValue = valueGeneratedPerRound;
        float additonalValueFromTiers = getAdditionalValueByTiers(newValue);
        if (additonalValueFromTiers > 0) {
            thisTurnAdditionalValueGeneratedByTier += additonalValueFromTiers;
            newValue += additonalValueFromTiers;
        }
        thisTurnValueGenerated += newValue;
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
                    additionalValue = baseValue * (TIER_LEVEL_GENERATION_BOOST_PERCENT * 1);
                    break;
                case TWO:
                    additionalValue = baseValue * (TIER_LEVEL_GENERATION_BOOST_PERCENT * 2);
                    break;
                case THREE:
                    additionalValue = baseValue * (TIER_LEVEL_GENERATION_BOOST_PERCENT * 3);
                    break;
                default:
                    throw new RuntimeException("Unrecognized Tier");
            }
            return additionalValue;
        } else {
            return 0;
        }
    }

    private float getCurrentTrashCapacity() {
        float trashCapacity = baseTrashCapacity;
        if (hasDumpster) {
            trashCapacity += DUMPSTER_CAPACITY_BONUS;
        }
        return trashCapacity;
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
            case TRUCK:        return false;
            default:           return false;
        }
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
            batch.draw(greenCert, bounds.x + bounds.width - (greenCert.getRegionWidth() + CUTOUT_X_OFFSET), bounds.y +  bounds.height - (greenCert.getRegionHeight() + CUTOUT_Y_OFFSET));
        }
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
                    x + w - wScale * (addonTexture.getRegionWidth()  + CUTOUT_X_OFFSET),
                    y + h - hScale * (addonTexture.getRegionHeight() + CUTOUT_Y_OFFSET),
                    wScale  * addonTexture.getRegionWidth(),
                    hScale * addonTexture.getRegionHeight());

        }
    }


}
