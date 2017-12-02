package lando.systems.ld40.buildings;

import lando.systems.ld40.gameobjects.Tile;

public class Building extends Tile {
    public enum Type {
        None, ResidentialLow, ResidentalMedium, ResidentailHigh, IndustrialLow, IndustrialMedium, IndustrialHigh, ComercialLow, ComercialMedium, ComercialHigh
    }

    public static Tile getBuilding(Type type) {
        String buildingName;
        switch (type) {
            case ResidentialLow:
                buildingName = "res-low";
                break;
            case ResidentalMedium:
                buildingName = "res-med";
                break;
            case ResidentailHigh:
                buildingName = "res-high";
                break;
            case IndustrialLow:
                buildingName = "ind-low";
                break;
            case IndustrialMedium:
                buildingName = "ind-med";
                break;
            case IndustrialHigh:
                buildingName = "ind-high";
                break;
            case ComercialLow:
                buildingName = "com-low";
                break;
            case ComercialMedium:
                buildingName = "com-med";
                break;
            case ComercialHigh:
                buildingName = "com-high";
                break;
            default:
                return new Tile("grass");

        }

        System.out.println(buildingName);

        return new Building(buildingName);
    }

    public Building(String buildingName) {
        super(buildingName);
    }
}
