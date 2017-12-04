package lando.systems.ld40.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld40.gameobjects.DumpTruck;
import lando.systems.ld40.ui.Button;

/**
 * Created by Brian on 12/3/2017.
 */
public class TruckButton extends Button {

    public DumpTruck truck;

    public TruckButton(DumpTruck truck, Rectangle bounds, OrthographicCamera camera) {
        super(truck.texture, bounds, camera);
        this.truck = truck;
    }
}
