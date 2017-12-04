package lando.systems.ld40.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld40.gameobjects.DumpTruck;
import lando.systems.ld40.gameobjects.Routes;
import lando.systems.ld40.ui.Button;
import lando.systems.ld40.ui.ModalWindow;
import lando.systems.ld40.world.World;

/**
 * Created by Brian on 12/3/2017.
 */
public class RouteActionModalWindow extends ModalWindow {

    private RouteManager routeManager;

    private Routes routes;

    private Array<Button> trucks = new Array<Button>();

    public RouteActionModalWindow(OrthographicCamera camera, RouteManager routeManager) {
        super(camera);
        this.routeManager = routeManager;

        routes = World.GetWorld().routes;

        setupTrucks();
    }

    private void setupTrucks() {

        int rows = (int) Math.ceil(Math.sqrt(routes.trucks.size));
        int cols = (int) (Math.ceil(routes.trucks.size / (float)rows));

        // assumes trucks are same size
        TextureRegion truck = routes.trucks.get(0).type.texture;
        float truckWidth = truck.getRegionWidth();

        float height = 100;
        if (modalRect.height / rows < height) {
            height = modalRect.height / rows;
        }
        float width = truckWidth / truck.getRegionHeight() * height;

        float left = modalRect.x + (modalRect.width - (width*cols)) / 2;
        float top = modalRect.y + modalRect.height - (modalRect.height - (height*rows)) / 2;

        float x = left;
        float y = top;

        int index = 0;
        for (DumpTruck dumpTruck : routes.trucks) {
            y -= height;
            Rectangle bounds = new Rectangle(x, y, width, height);
            trucks.add(new Button(dumpTruck, bounds, camera));

            if (++index == rows) {
                index = 0;
                y = top;
                x += width;
            }
        }
    }

    @Override
    protected void renderWindowContents(SpriteBatch batch) {
        if (!showText) return;

        if (routeManager.state == RouteManager.RouteState.PICK_ROUTE.PICK_ROUTE) {
            for (Button truck : trucks) {
                truck.render(batch);
            }
        }
    }

    @Override
    public void update(float dt) {
        for (Button truck : trucks) {
            truck.update(dt);
        }
    }

    @Override
    public void handleTouch(float windowX, float windowY) {
        for (Button truck : trucks) {
            if (truck.bounds.contains(windowX, windowY)) {
                routeManager.selectTruck(truck.gameObject);
                hide();
            }
        }
    }
}
