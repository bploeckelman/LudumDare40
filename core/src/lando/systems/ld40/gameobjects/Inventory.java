package lando.systems.ld40.gameobjects;

import com.badlogic.gdx.utils.ObjectIntMap;

public class Inventory {

    private ObjectIntMap<UpgradeType> upgradeTypeCount;

    public Inventory() {
        this.upgradeTypeCount = new ObjectIntMap<UpgradeType>();
        for (UpgradeType upgradeType : UpgradeType.values()) {
            this.upgradeTypeCount.put(upgradeType, 0);
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

    public ObjectIntMap<UpgradeType> getUpgradeTypeCount() {
        return upgradeTypeCount;
    }

}
