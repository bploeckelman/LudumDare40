package lando.systems.ld40.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.IntArray;
import lando.systems.ld40.gameobjects.DumpTruck;
import lando.systems.ld40.gameobjects.GameObject;
import lando.systems.ld40.gameobjects.Routes;
import lando.systems.ld40.world.World;

/**
 * Created by Brian on 12/3/2017.
 */
public class RouteManager extends ActionManager {

    public enum RouteState { NONE, START, PICK_ROUTE, PICK_SOURCES, PICK_DEST, DONE }
    public RouteState state = RouteState.START;
    public DumpTruck selectedTruck;

    private World world;
    private Routes routes;
    private IntArray currentRoute;

    public RouteManager(OrthographicCamera hudCamera, OrthographicCamera worldCamera) {

        super(hudCamera, worldCamera);
        world = World.GetWorld();
        routes = world.routes;
    }

    @Override
    public void activate() {
        setState(RouteState.PICK_ROUTE);
        window = new RouteActionModalWindow(hudCamera, this);
        window.show();
    }

    private void setState(RouteState state) {
        this.state = state;
        switch (state) {
            case PICK_SOURCES:
                world.setFilter(World.FilterType.Source);
                break;
            case PICK_DEST:
                world.setFilter(World.FilterType.Desitination);
                break;
            default:
                world.setFilter(World.FilterType.None);
                break;
        }
    }

    @Override
    public void renderManager(SpriteBatch batch) {
        switch (state) {
            case START:
                break;
            case PICK_SOURCES:
                renderSelectSourcesHud(batch);
                break;
            case PICK_DEST:
                break;
            case DONE:
                // nothing to see here
                break;
        }
    }

    private void renderSelectSourcesHud(SpriteBatch batch) {

    }

    @Override
    public void updateManager(float dt) {

    }

    public void selectTruck(GameObject truck) {
        if (truck instanceof DumpTruck) {
            selectedTruck = (DumpTruck)truck;
            currentRoute = routes.routes.get(selectedTruck);
            state = RouteState.PICK_SOURCES;
        }
    }
}
