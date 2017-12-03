package managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld40.gameobjects.GameObject;

/**
 * Created by Brian on 12/3/2017.
 */
public class RouteManager extends ActionManager {

    private enum RouteState { NONE, START, PICK_SOURCES, PICK_DEST, DONE }
    private RouteState state = RouteState.START;
    public GameObject selectedTruck;

    public RouteManager(OrthographicCamera hudCamera, OrthographicCamera worldCamera) {
        super(hudCamera, worldCamera);
    }

    @Override
    public void activate() {
        state = RouteState.PICK_SOURCES;
    }

    @Override
    public void deactivate() {
        state = RouteState.NONE;
    }

    @Override
    public void render(SpriteBatch batch) {
        switch (state) {
            case START:
                drawText(batch, "Select Route...");
                break;
            case PICK_SOURCES:
                drawText(batch, "Select Source...");
                break;
            case PICK_DEST:
                drawText(batch, "Click a destination...");
                break;
            case DONE: {
                // nothing to see here
            }
            break;
        }
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void touchUp(float screenX, float screenY) {

    }
}
