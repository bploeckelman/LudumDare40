package lando.systems.ld40.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld40.gameobjects.GameObject;
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
        }
    }

    @Override
    public void onModalClose() {
        if (state == BuildState.PICK_ITEM) {
            // transition back to pick tile
            state = BuildState.PICK_TILE;
        }
    }


    @Override
    public boolean handleTouch(float screenX, float screenY) {
        boolean handled = false;
        Vector3 touchPosition = unprojectWorld(screenX, screenY);

        switch (state) {
            case PICK_TILE:
                selectedObject = World.GetWorld().getSelectedObject(touchPosition.x, touchPosition.y);
                if (selectedObject != null) {
                    window = new BuildActionModalWindow(hudCamera, this);
                    window.show();
                    state = BuildState.PICK_ITEM;
                    handled = true;
                }
                break;
        }

        return handled;
    }
}
