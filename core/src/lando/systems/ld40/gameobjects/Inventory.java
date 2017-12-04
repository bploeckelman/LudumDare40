package lando.systems.ld40.gameobjects;

import com.badlogic.gdx.utils.ObjectIntMap;

public class Inventory {

    private ObjectIntMap<UpgradeType> upgradeTypeCount;
    private ObjectIntMap<TileType> tileTypeCount;
    private ObjectIntMap<TruckType> truckTypeCount;

    public Inventory() {
        this.upgradeTypeCount = new ObjectIntMap<UpgradeType>();
        for (UpgradeType upgradeType : UpgradeType.values()) {
            this.upgradeTypeCount.put(upgradeType, 0);
        }

        tileTypeCount = new ObjectIntMap<TileType>();
        for(TileType tileType : TileType.values())
        {
            tileTypeCount.put(tileType, 0);
        }

        truckTypeCount = new ObjectIntMap<TruckType>();
        for(TruckType truckType : TruckType.values())
        {
            truckTypeCount.put(truckType, 0);
        }
    }

    public int getCurrentCountForUpgrade(UpgradeType type) {
        return upgradeTypeCount.get(type, 0);
    }

    public void addUpgradeItem(UpgradeType type) {
        upgradeTypeCount.getAndIncrement(type, 0, 1);
    }

    public void addUpgradeItems(UpgradeType type, int numItems) {
        upgradeTypeCount.getAndIncrement(type, 0, numItems);
    }

    public void useUpgradeItem(UpgradeType type) {
        upgradeTypeCount.getAndIncrement(type, 0, -1);
    }


    public int getCurrentCountForTile(TileType type) {
        return tileTypeCount.get(type, 0);
    }

    public void addTileItem(TileType type) {
        tileTypeCount.getAndIncrement(type, 0, 1);
    }

    public void useTileItem(TileType type) {
        tileTypeCount.getAndIncrement(type, 0, -1);
    }


    public void addTruckItem(TruckType type) {
        truckTypeCount.getAndIncrement(type, 0, 1);
    }

    public int getCurrentCountForTruck(TruckType type) {
        return truckTypeCount.get(type, 0);
    }


    public ObjectIntMap<UpgradeType> getUpgradeTypeCount() {
        return upgradeTypeCount;
    }

}
