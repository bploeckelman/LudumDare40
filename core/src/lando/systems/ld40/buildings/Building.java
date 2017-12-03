package lando.systems.ld40.buildings;

import lando.systems.ld40.gameobjects.GameObject;
import lando.systems.ld40.utils.Assets;

import java.util.HashMap;

public class Building extends GameObject {

//    public static final

    private Type type;

    public boolean supportsCompactor;
    public boolean hasCompactor;
    public boolean supportsDumpster;
    public boolean hasDumpster;
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

    private int turnsOverCapacity = 0;
    private boolean isMarkedForRemoval = false;

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

    private static HashMap<Type, String> buildingTypeTextureLookup = new HashMap<Type, String>();
    static {
        buildingTypeTextureLookup.put(Type.COMMERCIAL_HIGH, "com-high");
        buildingTypeTextureLookup.put(Type.COMMERCIAL_LOW, "com-low");
        buildingTypeTextureLookup.put(Type.COMMERCIAL_MEDIUM, "com-med");
        // TODO: TEXTURE
        buildingTypeTextureLookup.put(Type.DUMP, "white-pixel");
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

    private Building(Type type,
                    boolean supportsCompactor, boolean hasCompactor,
                    boolean supportsDumpster, boolean hasDumpster,
                    boolean supportsGreenCert, boolean hasGreenCert,
                    boolean supportsIncinerator, boolean hasIncinerator,
                    boolean supportsTiers, boolean canBuild,
                    boolean canRaze,
                    Tier currentTier,
                    float trashGeneratedPerRound,
                    float valueGeneratedPerRound,
                    float trashCapacity,
                    float currentTrashLevel) {

        this.type = type;
        String textureName = buildingTypeTextureLookup.get(type);
        if (textureName == null) {
            throw new RuntimeException();
        }
        System.out.println(textureName);
        setTexture(Assets.atlas.findRegion(textureName));

        this.currentTier = currentTier;
        this.currentTrashLevel = currentTrashLevel;
        this.hasCompactor = hasCompactor;
        this.hasDumpster = hasDumpster;
        this.hasGreenCert = hasGreenCert;
        this.hasIncinerator = hasIncinerator;
        this.canBuild = canBuild;
        this.canRaze = canRaze;
        this.supportsCompactor = supportsCompactor;
        this.supportsDumpster = supportsDumpster;
        this.supportsGreenCert = supportsGreenCert;
        this.supportsIncinerator = supportsIncinerator;
        this.supportsTiers = supportsTiers;
        this.trashCapacity = trashCapacity;
        this.trashGeneratedPerRound = trashGeneratedPerRound;
        this.valueGeneratedPerRound = valueGeneratedPerRound;
    }

    public static Building getBuilding(Type buildingType) {

        // Defaults
        boolean supportsCompactor = false;
        boolean hasCompactor = false;
        boolean supportsDumpster = false;
        boolean hasDumpster = false;
        boolean supportsGreenCert = false;
        boolean hasGreenCert = false;
        boolean supportsIncinerator = false;
        boolean hasIncinerator = false;
        boolean supportsTiers = false;
        boolean canBuild = false;
        boolean canRaze = true;
        Tier currentTier = null;
        float trashGeneratedPerRound = 0;
        float valueGeneratedPerRound = 0;
        float trashCapacity = 0;
        float currentTrashLevel = 0;

        // Customize the types!

        switch (buildingType) {

            case RESIDENTIAL_LOW:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 1;
                valueGeneratedPerRound = 1;
                break;

            case RESIDENTIAL_MEDIUM:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 2;
                valueGeneratedPerRound = 3;
                break;

            case RESIDENTIAL_HIGH:
                supportsDumpster = true;
                supportsGreenCert = true;
                supportsTiers = true;
                currentTier = Tier.ONE;
                trashGeneratedPerRound = 3;
                valueGeneratedPerRound = 5;
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
                supportsGreenCert, hasGreenCert,
                supportsIncinerator, hasIncinerator,
                canBuild, canRaze,
                supportsTiers, currentTier,
                trashGeneratedPerRound,
                valueGeneratedPerRound,
                trashCapacity,
                currentTrashLevel);
    }

}
