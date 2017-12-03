package managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld40.gameobjects.GameObject;
import lando.systems.ld40.gameobjects.Inventory;
import lando.systems.ld40.world.World;

/**
 * Created by Brian on 12/3/2017.
 */
public class BuildManager extends ActionManager {

    public enum BuildState { NONE, START, PICK_TILE, PICK_ITEM, DONE }

    private BuildState state =  BuildState.START;
    public GameObject selectedObject;

    public BuildManager(OrthographicCamera actionCamera, OrthographicCamera worldCamera) {
        super(actionCamera, worldCamera);
    }

    public void activate() {
        state = BuildState.PICK_TILE;
    }


    @Override
    public void updateManager(float dt) {
        switch(state) {
            case PICK_TILE:
                // ...
                break;
            case PICK_ITEM:
                if (Gdx.input.justTouched()) {
                    window.hide();
                }
                break;
            case DONE:
                // ...
                break;
        }

    }

    @Override
    public void renderManager(SpriteBatch batch) {
        switch (state) {
            case START:
                drawText(batch, "Building...");
                break;
            case PICK_TILE:
                drawText(batch, "Click a tile to build on...");
                break;
            case PICK_ITEM:
                break;
            case DONE:
                // nothing to see here
                break;
        }

    }

    @Override
    public void touchUp(float screenX, float screenY) {

        Vector3 touchPoisition = unprojectWorld(screenX, screenY);

        switch (state) {
            case PICK_TILE:
                selectedObject = World.GetWorld().getSelectedObject(touchPoisition.x, touchPoisition.y);
                window = new BuildActionModalWindow(hudCamera, this);
                window.show();
                state = BuildState.PICK_ITEM;
                break;
            case PICK_ITEM:
                // check inventory
                break;
        }
    }
}
