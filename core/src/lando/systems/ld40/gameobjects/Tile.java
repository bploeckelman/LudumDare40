package lando.systems.ld40.gameobjects;

import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.world.World;

public class Tile extends GameObject {

    public Tile(String textureName) {
        setTexture(Assets.atlas.findRegion(textureName));
        setSize(World.tile_pixels_wide, World.tile_pixels_high);
    }

}
