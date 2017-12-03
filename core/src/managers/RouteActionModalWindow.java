package managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld40.gameobjects.DumpTruck;
import lando.systems.ld40.ui.ModalWindow;
import lando.systems.ld40.world.World;

/**
 * Created by Brian on 12/3/2017.
 */
public class RouteActionModalWindow extends ModalWindow {

    private RouteManager routeManager;

    public RouteActionModalWindow(OrthographicCamera camera, RouteManager routeManager) {
        super(camera);
        this.routeManager = routeManager;
    }

    @Override
    protected void renderWindowContents(SpriteBatch batch) {
        if (routeManager.state == RouteManager.RouteState.PICK_ROUTE.PICK_ROUTE) {

            float left = modalRect.x;
            float top = modalRect.y + modalRect.height;

            for (DumpTruck dumpTrump : World.GetWorld().routes.trucks) {
                batch.draw(dumpTrump.type.texture, left, top);

            }
        }

    }
}
