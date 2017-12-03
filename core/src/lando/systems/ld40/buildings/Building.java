package lando.systems.ld40.buildings;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld40.gameobjects.Tile;
import lando.systems.ld40.utils.Assets;

import java.util.HashMap;

public class Building extends Tile {

    public static float CUTOUT_Y_OFFSET = 8;
    public static float CUTOUT_X_OFFSET = 8;

    private Type type;

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
    public float trashCapacity;
    public float currentTrashLevel;

    public Resource resource;

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
                    float trashCapacity,
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
        this.trashCapacity = trashCapacity;
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
        float trashCapacity = 0;
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
                trashCapacity = 10;
                resource = Resource.MONEY;
                break;

            case COMMERCIAL_MEDIUM:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 2;
                valueGeneratedPerRound = 3;
                trashCapacity = 13;
                resource = Resource.MONEY;
                break;

            case COMMERCIAL_HIGH:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 3;
                valueGeneratedPerRound = 5;
                trashCapacity = 16;
                resource = Resource.MONEY;
                break;

            case DUMP:
                supportsCompactor = true;
                supportsIncinerator = true;
                supportsRecycle = true;
                canRaze = false;
                trashCapacity = 100;
                break;

            case INDUSTRIAL_LOW:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 1;
                valueGeneratedPerRound = 1;
                trashCapacity = 10;
                resource = Resource.MONEY;
                break;

            case INDUSTRIAL_MEDIUM:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 2;
                valueGeneratedPerRound = 3;
                trashCapacity = 13;
                resource = Resource.MONEY;
                break;

            case INDUSTRIAL_HIGH:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 3;
                valueGeneratedPerRound = 5;
                trashCapacity = 16;
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
                trashCapacity = 10;
                resource = Resource.MONEY;
                break;

            case RESIDENTIAL_MEDIUM:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 2;
                valueGeneratedPerRound = 3;
                trashCapacity = 13;
                resource = Resource.MONEY;
                break;

            case RESIDENTIAL_HIGH:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 3;
                valueGeneratedPerRound = 5;
                trashCapacity = 16;
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
                trashCapacity,
                currentTrashLevel,
                resource);
    }

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
