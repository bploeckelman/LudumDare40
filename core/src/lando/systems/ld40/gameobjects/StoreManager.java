package lando.systems.ld40.gameobjects;

import java.util.ArrayList;

public class StoreManager {
    
    enum Tile {
        COMMERCIAL_LOW_DENSITY,
        COMMERCIAL_MEDIUM_DENSITY,
        COMMERCIAL_HIGH_DENSITY,
        INDUSTRIAL_LOW_DENSITY,
        INDUSTRIAL_MEDIUM_DENSITY,
        INDUSTRIAL_HIGH_DENSITY,
        RECYCLING_CENTER,
        RESIDENTIAL_LOW_DENSITY,
        RESIDENTIAL_MEDIUM_DENSITY,
        RESIDENTIAL_HIGH_DENSITY,
    }

    enum Upgrade {
        COMPACTOR,
        DEMOLITION,
        DUMPSTER,
        GREEN_TOKEN,
        INCINERATOR,
        RECLAMATION,
        TIER_UPGRADE,
        TRUCK,
    }
    
    enum Research {
        COMPACTION,
        INCINERATION,
        RECYCLING,
        TRUCK_CAPACITY_1,
        TRUCK_CAPACITY_2,
        TRUCK_CAPACITY_3,
        TRUCK_STOPS_1,
        TRUCK_STOPS_2,
        TRUCK_STOPS_3,
    }

    enum ResearchStatus {
        RESEARCHED,
        RESEARCHABLE,
        LOCKED,
    }
    
    enum Status {
        UNLOCKED,
        LOCKED
    }
    
    private ArrayList<Research> completedResearch = new ArrayList<Research>();
    private ArrayList<Research> unlockedResearch = new ArrayList<Research>();
    private ArrayList<Tile> unlockedTiles = new ArrayList<Tile>();
    private ArrayList<Upgrade> unlockedUpgrades = new ArrayList<Upgrade>();

    
    public StoreManager() {

        // "Free" unlocks.  See {self::updateLocks}
        unlockResearch(Research.COMPACTION);
        unlockResearch(Research.INCINERATION);
        unlockResearch(Research.RECYCLING);
        unlockResearch(Research.TRUCK_CAPACITY_1);
        unlockResearch(Research.TRUCK_STOPS_1);
        unlockTile(Tile.COMMERCIAL_LOW_DENSITY);
        unlockTile(Tile.INDUSTRIAL_LOW_DENSITY);
        unlockTile(Tile.RESIDENTIAL_LOW_DENSITY);
        unlockUpgrade(Upgrade.DEMOLITION);
        unlockUpgrade(Upgrade.DUMPSTER);
        unlockUpgrade(Upgrade.GREEN_TOKEN);
        unlockUpgrade(Upgrade.RECLAMATION);
        unlockUpgrade(Upgrade.TIER_UPGRADE);
        unlockUpgrade(Upgrade.TRUCK);

    }
    
    public void completeResearch(Research research) {
        if (!completedResearch.contains(research)) {
            completedResearch.add(research);
            updateLocks();
        }
    }
    private void unlockResearch(Research research) {
        if (!unlockedResearch.contains(research)) {
            unlockedResearch.add(research);
            updateLocks();
        }
    }
    private void unlockTile(Tile tile) {
        if (!unlockedTiles.contains(tile)) {
            unlockedTiles.add(tile);
            updateLocks();
        }
    }
    private void unlockUpgrade(Upgrade upgrade) {
        if (!unlockedUpgrades.contains(upgrade)) {
            unlockedUpgrades.add(upgrade);
            updateLocks();
        }
    }
    
    public Status getTileStatus(Tile tile) {
        return unlockedTiles.contains(tile) ? Status.UNLOCKED : Status.LOCKED;
    }

    public ResearchStatus getResearchStatus(Research research) {
        if (completedResearch.contains(research)) {
            return ResearchStatus.RESEARCHED;
        } else {
            return unlockedResearch.contains(research) ? ResearchStatus.RESEARCHABLE : ResearchStatus.LOCKED;
        }
    }

    public Status getUpgradeStatus(Upgrade upgrade) {
        return unlockedUpgrades.contains(upgrade) ? Status.UNLOCKED : Status.LOCKED;
    }

    /**
     * To be called when a status has changed which may result in unlocking another tile/upgrade/research
     * This method should contain all of the 'rules' of unlocking, e.g. must research recycling for a med commercial tile.
     * "Free" unlocks, those available at the start of the game should be set in the constructor.
     */
    private void updateLocks() {

        // TILES -------------------------------------------------------------------------------------------------------

        // Residential
        if (getTileStatus(Tile.RESIDENTIAL_LOW_DENSITY) == Status.UNLOCKED &&
                getResearchStatus(Research.COMPACTION) == ResearchStatus.RESEARCHED) {
            unlockTile(Tile.RESIDENTIAL_MEDIUM_DENSITY);
        }
        if (getTileStatus(Tile.RESIDENTIAL_MEDIUM_DENSITY) == Status.UNLOCKED &&
                getResearchStatus(Research.RECYCLING) == ResearchStatus.RESEARCHED) {
            unlockTile(Tile.RESIDENTIAL_HIGH_DENSITY);
        }

        // Commercial
        if (getTileStatus(Tile.COMMERCIAL_LOW_DENSITY) == Status.UNLOCKED &&
                getResearchStatus(Research.RECYCLING) == ResearchStatus.RESEARCHED) {
            unlockTile(Tile.COMMERCIAL_MEDIUM_DENSITY);
        }
        if (getTileStatus(Tile.COMMERCIAL_MEDIUM_DENSITY) == Status.UNLOCKED &&
                getResearchStatus(Research.INCINERATION) == ResearchStatus.RESEARCHED) {
            unlockTile(Tile.COMMERCIAL_HIGH_DENSITY);
        }

        // Industrial
        if (getTileStatus(Tile.INDUSTRIAL_LOW_DENSITY) == Status.UNLOCKED &&
                getResearchStatus(Research.INCINERATION) == ResearchStatus.RESEARCHED) {
            unlockTile(Tile.INDUSTRIAL_MEDIUM_DENSITY);
        }
        if (getTileStatus(Tile.INDUSTRIAL_MEDIUM_DENSITY) == Status.UNLOCKED &&
                getResearchStatus(Research.COMPACTION) == ResearchStatus.RESEARCHED) {
            unlockTile(Tile.INDUSTRIAL_HIGH_DENSITY);
        }

        // Recycling Center
        if (getResearchStatus(Research.RECYCLING) == ResearchStatus.RESEARCHED) {
            unlockTile(Tile.RECYCLING_CENTER);
        }

        // UPGRADES ----------------------------------------------------------------------------------------------------

        if (getResearchStatus(Research.COMPACTION) == ResearchStatus.RESEARCHED) {
            unlockUpgrade(Upgrade.COMPACTOR);
        }
        if (getResearchStatus(Research.INCINERATION) == ResearchStatus.RESEARCHED) {
            unlockUpgrade(Upgrade.INCINERATOR);
        }

        // Research ----------------------------------------------------------------------------------------------------

        if (getResearchStatus(Research.TRUCK_CAPACITY_1) == ResearchStatus.RESEARCHED) {
            unlockResearch(Research.TRUCK_CAPACITY_2);
        }
        if (getResearchStatus(Research.TRUCK_CAPACITY_2) == ResearchStatus.RESEARCHED) {
            unlockResearch(Research.TRUCK_CAPACITY_3);
        }
        if (getResearchStatus(Research.TRUCK_STOPS_1) == ResearchStatus.RESEARCHED) {
            unlockResearch(Research.TRUCK_STOPS_2);
        }
        if (getResearchStatus(Research.TRUCK_STOPS_2) == ResearchStatus.RESEARCHED) {
            unlockResearch(Research.TRUCK_STOPS_3);
        }

    }
    
}
