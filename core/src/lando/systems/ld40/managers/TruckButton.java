package lando.systems.ld40.managers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.IntArray;
import lando.systems.ld40.gameobjects.DumpTruck;
import lando.systems.ld40.gameobjects.Routes;
import lando.systems.ld40.ui.Button;
import lando.systems.ld40.utils.Assets;

/**
 * Created by Brian on 12/3/2017.
 */
public class TruckButton extends Button {

    public DumpTruck truck;

    public TruckButton(DumpTruck truck, Rectangle bounds, OrthographicCamera camera) {
        super(truck.texture, bounds, camera);
        this.truck = truck;
    }

    public void render(SpriteBatch batch, Routes routes) {
        render(batch);
        IntArray route = routes.routes.get(truck);
        if (route.size > 0) {
            Color color = routes.getColor(truck);
            Color prevColor = batch.getColor();
            batch.setColor(Color.WHITE);
            batch.draw(Assets.whitePixel, bounds.x - 1, bounds.y - 20, bounds.width + 2, 10);
            batch.setColor(color);
            batch.draw(Assets.whitePixel, bounds.x, bounds.y - 19, bounds.width, 8);
            batch.setColor(prevColor);
        }
    }
}
