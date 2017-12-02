package lando.systems.ld40.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import lando.systems.ld40.utils.Assets;

public class Building extends GameObject {

    public Color _color;
    public Building(Color color) {
        _color = color;
    }
    
    @Override
    public void render(SpriteBatch batch) {
        batch.setColor(_color);
        batch.draw(Assets.whitePixel, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.setColor(Color.WHITE);
    }
}
