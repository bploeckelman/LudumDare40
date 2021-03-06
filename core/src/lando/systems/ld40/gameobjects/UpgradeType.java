package lando.systems.ld40.gameobjects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld40.utils.Assets;

public enum UpgradeType {

    DEMOLITION   (Assets.demolishButton,      "Destroy",  "Demolish a building"),
    COMPACTOR    (Assets.compactorTexture,    "Compact" , "Reduce amount of trash delivered by trucks"),
    DUMPSTER     (Assets.dumpsterTexture,     "Dumpster", "Increase trash capacity of building"),
    GREEN_TOKEN  (Assets.leafTexture,         "Green",    "Reduce amount of trash generated by building"),
    INCINERATOR  (Assets.incineratorTexture,  "Burner",   "Burn trash in a landfill over time"),
    RECLAMATION  (Assets.recycleTexture,      "Recycle",  "Recycles trash in a landfill over time"),
    TIER_UPGRADE (Assets.upgradeTexture,      "Upgrade",  "Improves a building"),
    TRUCK        (Assets.atlas.findRegion("garbagetruck"), "Truck", "Add a new garbage truck");

    public TextureRegion texture;
    public String shortName;
    public String description;

    UpgradeType(TextureRegion texture, String shortName, String description) {
        this.texture = texture;
        this.shortName = shortName;
        this.description = description;
    }

}
