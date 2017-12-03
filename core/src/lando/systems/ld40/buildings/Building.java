package lando.systems.ld40.buildings;

import lando.systems.ld40.gameobjects.Tile;
import lando.systems.ld40.utils.Assets;

import java.util.HashMap;

public class Building extends Tile {

//    public static final

    private Type type;

    private boolean supportsCompactor;
    private boolean hasCompactor;
    private boolean supportsDumpster;
    private boolean hasDumpster;
    private boolean supportsGreenCert;
    private boolean hasGreenCert;
    private boolean supportsIncinerator;
    private boolean hasIncinerator;
    private boolean supportsTiers;
    private Tier currentTier;

    private float trashGeneratedPerRound;
    private float valueGeneratedPerRound;
    private float trashCapacity;
    private float currentTrashLevel;
    private Resource resource;

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
    }


    // -----------------------------------------------------------------------------------------------------------------

    private Building(Type type,
                     boolean supportsCompactor, boolean hasCompactor,
                     boolean supportsDumpster, boolean hasDumpster,
                     boolean supportsGreenCert, boolean hasGreenCert,
                     boolean supportsIncinerator, boolean hasIncinerator,
                     boolean supportsTiers, Tier currentTier,
                     float trashGeneratedPerRound,
                     float valueGeneratedPerRound,
                     float trashCapacity,
                     float currentTrashLevel,
                     Resource resource
    ) {

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
        this.hasGreenCert = hasGreenCert;
        this.hasIncinerator = hasIncinerator;
        this.supportsCompactor = supportsCompactor;
        this.supportsDumpster = supportsDumpster;
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
        boolean hasGreenCert = false;
        boolean hasIncinerator = false;
        boolean supportsCompactor = false;
        boolean supportsDumpster = false;
        boolean supportsGreenCert = false;
        boolean supportsIncinerator = false;
        boolean supportsTiers = false;
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

        }

        return new Building(buildingType,
                supportsCompactor, hasCompactor,
                supportsDumpster, hasDumpster,
                supportsGreenCert, hasGreenCert,
                supportsIncinerator, hasIncinerator,
                supportsTiers, currentTier,
                trashGeneratedPerRound,
                valueGeneratedPerRound,
                trashCapacity,
                currentTrashLevel,
                resource);
    }

}
