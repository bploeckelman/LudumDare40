package lando.systems.ld40.gameobjects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld40.buildings.Building;
import lando.systems.ld40.utils.Assets;

public enum TileType {

    DUMP               (Assets.dumpTexture,              "Dump",       "A place for trash to live"),
    COMMERCIAL_LOW     (Assets.commercialLowTexture,     "Store Sm.",  "Low density commercial building"),
    COMMERCIAL_MEDIUM  (Assets.commercialMediumTexture,  "Store Med.", "Medium density commercial building"),
    COMMERCIAL_HIGH    (Assets.commercialHighTexture,    "Store Lg.",  "High density commercial building"),
    INDUSTRIAL_LOW     (Assets.industrialLowTexture,     "Work Sm.",   "Low density industrial building"),
    INDUSTRIAL_MEDIUM  (Assets.industrialMediumTexture,  "Work Med.",  "Medium density industrial building"),
    INDUSTRIAL_HIGH    (Assets.industrialHighTexture,    "Work Lg.",   "High density industrial building"),
    RESIDENTIAL_LOW    (Assets.residentialLowTexture,    "Home Sm.",   "Low density residential building"),
    RESIDENTIAL_MEDIUM (Assets.residentialMediumTexture, "Home Med.",  "Medium density residential building"),
    RESIDENTIAL_HIGH   (Assets.residentialHighTexture,   "Home Lg.",   "High density residential building");

    public TextureRegion texture;
    public String shortName;
    public String description;

    TileType(TextureRegion texture, String shortName, String description) {
        this.texture = texture;
        this.shortName = shortName;
        this.description = description;
    }

    // T_T
    public Building.Type toBuildingType() {
        switch (this) {
            case DUMP               : return Building.Type.DUMP;
            case COMMERCIAL_LOW     : return Building.Type.COMMERCIAL_LOW;
            case COMMERCIAL_MEDIUM  : return Building.Type.COMMERCIAL_MEDIUM;
            case COMMERCIAL_HIGH    : return Building.Type.COMMERCIAL_HIGH;
            case INDUSTRIAL_LOW     : return Building.Type.INDUSTRIAL_LOW;
            case INDUSTRIAL_MEDIUM  : return Building.Type.INDUSTRIAL_MEDIUM;
            case INDUSTRIAL_HIGH    : return Building.Type.INDUSTRIAL_HIGH;
            case RESIDENTIAL_LOW    : return Building.Type.RESIDENTIAL_LOW;
            case RESIDENTIAL_MEDIUM : return Building.Type.RESIDENTIAL_MEDIUM;
            case RESIDENTIAL_HIGH   : return Building.Type.RESIDENTIAL_HIGH;
            default: return Building.Type.EMPTY;
        }
    }

}
