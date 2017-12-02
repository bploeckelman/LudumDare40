package lando.systems.ld40.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.world.World;

public class Tile extends GameObject {

    private Color tempColor = new Color(50 / 255f, 115 / 255f, 69 / 255f, 1f);

    public Tile() {
        texture = Assets.atlas.findRegion("tile-128");
        setSize(World.tile_pixels_wide, World.tile_pixels_high);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.setColor(tempColor);
        super.render(batch);
        batch.setColor(Color.WHITE);
    }

}
