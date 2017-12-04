package lando.systems.ld40.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntArray;
import lando.systems.ld40.buildings.Building;
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
    private IntArray newRoute;

    int remainingSelections = 0;

    public RouteManager(OrthographicCamera hudCamera, OrthographicCamera worldCamera) {

        super(hudCamera, worldCamera);
        world = World.GetWorld();
        routes = world.routes;
    }

    @Override
    public void activate() {
        setState(RouteState.PICK_ROUTE);
        //window = new RouteActionModalWindow(hudCamera, this);
        //window.show();
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
                //drawText(batch, "Select pickup location");
                drawText(batch, "select " + remainingSelections);
                renderSelectSourcesHud(batch);
                break;
            case PICK_DEST:
                drawText(batch, "Pick a drop location");
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
        switch (state) {
            case PICK_SOURCES:
                remainingSelections = selectedTruck.speed - newRoute.size;
                if (remainingSelections == 0) {
                    setState(RouteState.PICK_DEST);
                }
                break;
            case PICK_DEST:
                break;
            case DONE:
                // nothing to see here
                break;
        }
    }

    public void selectTruck(GameObject truck) {
        if (truck instanceof DumpTruck) {
            selectedTruck = (DumpTruck)truck;
            newRoute = new IntArray();
            setState(RouteState.PICK_SOURCES);
        }
    }

    @Override
    public boolean handleTouch(float screenX, float screenY) {
        boolean handled = false;
        Vector3 touchPosition = unprojectWorld(screenX, screenY);

        switch (state) {
            case PICK_SOURCES:
                handled = addToRoute(touchPosition);
                break;
            case PICK_DEST:
                handled = addToRoute(touchPosition);
                if (handled) {
                    routes.setRoute(selectedTruck, newRoute);
                    setState(RouteState.DONE);
                }
                break;
        }

        return handled;
    }

    private boolean addToRoute(Vector3 position) {
        int index = world.getSelectedObjectIndex(position.x, position.y);
        if (index == -1) return false;

        Building selected = world.buildings.get(index);
        if (selected.filtered) return false;

        int existingIndex = newRoute.indexOf(index);
        if (existingIndex != -1) {
            newRoute.removeIndex(existingIndex);
        }
        newRoute.add(index);
        return true;
    }
}
