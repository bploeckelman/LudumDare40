package lando.systems.ld40.gameobjects;

import java.util.ArrayList;
import lando.systems.ld40.utils.SoundManager;

public class StoreManager {
    
    public enum ResearchStatus {
        RESEARCHED,
        RESEARCHABLE,
        LOCKED,
    }
    
    public enum Status {
        UNLOCKED,
        LOCKED
    }
    
    private ArrayList<ResearchType> completedResearchTypes = new ArrayList<ResearchType>();
    private ArrayList<ResearchType> unlockedResearchTypes = new ArrayList<ResearchType>();
    private ArrayList<TileType> unlockedTileTypes = new ArrayList<TileType>();
    private ArrayList<UpgradeType> unlockedUpgradeTypes = new ArrayList<UpgradeType>();

    
    public StoreManager() {

        // "Free" unlocks.  See {self::updateLocks}
        unlockResearch(ResearchType.COMPACTION);
        unlockResearch(ResearchType.INCINERATION);
        unlockResearch(ResearchType.RECYCLING);
        unlockResearch(ResearchType.TRUCK_CAPACITY_1);
        unlockResearch(ResearchType.TRUCK_STOPS_1);
        unlockTile(TileType.COMMERCIAL_LOW);
        unlockTile(TileType.INDUSTRIAL_LOW);
        unlockTile(TileType.RESIDENTIAL_LOW);
        unlockUpgrade(UpgradeType.DEMOLITION);
        unlockUpgrade(UpgradeType.DUMPSTER);
        unlockUpgrade(UpgradeType.GREEN_TOKEN);
        //unlockUpgrade(UpgradeType.RECLAMATION);
        unlockUpgrade(UpgradeType.TIER_UPGRADE);
        unlockUpgrade(UpgradeType.TRUCK);

    }
    
    public void completeResearch(ResearchType researchType) {
        if (!completedResearchTypes.contains(researchType)) {
            completedResearchTypes.add(researchType);
            updateLocks();
        }
    }
    private void unlockResearch(ResearchType researchType) {
        if (!unlockedResearchTypes.contains(researchType)) {
            unlockedResearchTypes.add(researchType);
            updateLocks();
        }
    }
    public void unlockTile(TileType tileType) {
        if (!unlockedTileTypes.contains(tileType)) {
            unlockedTileTypes.add(tileType);
            updateLocks();
        }
    }
    public void unlockUpgrade(UpgradeType upgradeType) {
        if (!unlockedUpgradeTypes.contains(upgradeType)) {
            unlockedUpgradeTypes.add(upgradeType);
            updateLocks();
        }
    }
    
    public Status getTileStatus(TileType tileType) {
        return unlockedTileTypes.contains(tileType) ? Status.UNLOCKED : Status.LOCKED;
    }

    public ResearchStatus getResearchStatus(ResearchType researchType) {
        if (completedResearchTypes.contains(researchType)) {
            return ResearchStatus.RESEARCHED;
        } else {
            return unlockedResearchTypes.contains(researchType) ? ResearchStatus.RESEARCHABLE : ResearchStatus.LOCKED;
        }
    }

    public Status getUpgradeStatus(UpgradeType upgradeType) {
        return unlockedUpgradeTypes.contains(upgradeType) ? Status.UNLOCKED : Status.LOCKED;
    }

    /**
     * To be called when a status has changed which may result in unlocking another tile/upgrade/research
     * This method should contain all of the 'rules' of unlocking, e.g. must research recycling for a med commercial tile.
     * "Free" unlocks, those available at the start of the game should be set in the constructor.
     */
    private void updateLocks() {

        // TILES -------------------------------------------------------------------------------------------------------

        // Residential
        if (getTileStatus(TileType.RESIDENTIAL_LOW) == Status.UNLOCKED &&
                getResearchStatus(ResearchType.COMPACTION) == ResearchStatus.RESEARCHED) {
            unlockTile(TileType.RESIDENTIAL_MEDIUM);
        }
        if (getTileStatus(TileType.RESIDENTIAL_MEDIUM) == Status.UNLOCKED &&
                getResearchStatus(ResearchType.RECYCLING) == ResearchStatus.RESEARCHED) {
            unlockTile(TileType.RESIDENTIAL_HIGH);
        }

        // Commercial
        if (getTileStatus(TileType.COMMERCIAL_LOW) == Status.UNLOCKED &&
                getResearchStatus(ResearchType.RECYCLING) == ResearchStatus.RESEARCHED) {
            unlockTile(TileType.COMMERCIAL_MEDIUM);
        }
        if (getTileStatus(TileType.COMMERCIAL_MEDIUM) == Status.UNLOCKED &&
                getResearchStatus(ResearchType.INCINERATION) == ResearchStatus.RESEARCHED) {
            unlockTile(TileType.COMMERCIAL_HIGH);
        }

        // Industrial
        if (getTileStatus(TileType.INDUSTRIAL_LOW) == Status.UNLOCKED &&
                getResearchStatus(ResearchType.INCINERATION) == ResearchStatus.RESEARCHED) {
            unlockTile(TileType.INDUSTRIAL_MEDIUM);
        }
        if (getTileStatus(TileType.INDUSTRIAL_MEDIUM) == Status.UNLOCKED &&
                getResearchStatus(ResearchType.COMPACTION) == ResearchStatus.RESEARCHED) {
            unlockTile(TileType.INDUSTRIAL_HIGH);
        }

        // Recycling Center
        if (getResearchStatus(ResearchType.RECYCLING) == ResearchStatus.RESEARCHED) {
            unlockUpgrade(UpgradeType.RECLAMATION);
        }

        // UPGRADES ----------------------------------------------------------------------------------------------------

        if (getResearchStatus(ResearchType.COMPACTION) == ResearchStatus.RESEARCHED) {
            unlockUpgrade(UpgradeType.COMPACTOR);
        }
        if (getResearchStatus(ResearchType.INCINERATION) == ResearchStatus.RESEARCHED) {
            unlockUpgrade(UpgradeType.INCINERATOR);
        }

        // Research ----------------------------------------------------------------------------------------------------

        if (getResearchStatus(ResearchType.TRUCK_CAPACITY_1) == ResearchStatus.RESEARCHED) {
            unlockResearch(ResearchType.TRUCK_CAPACITY_2);
        }
        if (getResearchStatus(ResearchType.TRUCK_CAPACITY_2) == ResearchStatus.RESEARCHED) {
            unlockResearch(ResearchType.TRUCK_CAPACITY_3);
        }
        if (getResearchStatus(ResearchType.TRUCK_STOPS_1) == ResearchStatus.RESEARCHED) {
            unlockResearch(ResearchType.TRUCK_STOPS_2);
        }
        if (getResearchStatus(ResearchType.TRUCK_STOPS_2) == ResearchStatus.RESEARCHED) {
            unlockResearch(ResearchType.TRUCK_STOPS_3);
        }

    }

    public DumpTruck.TruckType getMaxTruck(){
        int speed = 0;
        int capacity = 0;
        if (getResearchStatus(ResearchType.TRUCK_STOPS_2) == ResearchStatus.RESEARCHED){
            speed = 2;
        } else if (getResearchStatus(ResearchType.TRUCK_STOPS_1) == ResearchStatus.RESEARCHED){
            speed = 1;
        }
        if (getResearchStatus(ResearchType.TRUCK_CAPACITY_2) == ResearchStatus.RESEARCHED){
            capacity = 2;
        } else if (getResearchStatus(ResearchType.TRUCK_CAPACITY_1) == ResearchStatus.RESEARCHED){
            capacity = 1;
        }

        if (speed == 0 && capacity == 0) return DumpTruck.One;
        if (speed == 0 && capacity == 1) return DumpTruck.Two;
        if (speed == 0 && capacity == 2) return DumpTruck.Three;

        if (speed == 1 && capacity == 0) return DumpTruck.Four;
        if (speed == 1 && capacity == 1) return DumpTruck.Five;
        if (speed == 1 && capacity == 2) return DumpTruck.Six;

        if (speed == 2 && capacity == 0) return DumpTruck.Seven;
        if (speed == 2 && capacity == 1) return DumpTruck.Eight;
        if (speed == 2 && capacity == 2) return DumpTruck.Nine;

        return DumpTruck.One;
    }

    public int getTruckCost(DumpTruck.TruckType type){
        return (type.speed * 20 + type.capacity);
    }
    
}
