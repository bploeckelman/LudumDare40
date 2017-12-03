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
    private BuildActionModalWindow modalWindow;
    private boolean isCompleted;

    public BuildManager(OrthographicCamera actionCamera, OrthographicCamera worldCamera) {
        super(actionCamera, worldCamera);
    }

    public void activate() {
        state = BuildState.PICK_TILE;
    }

    public void deactivate() {
        state = BuildState.NONE;
    }

    public void complete() {
        isCompleted = true;
    }

    @Override
    public void update(float dt) {
        if (isCompleted) return;

        switch(state) {
            case PICK_TILE:
                // ...
                break;
            case PICK_ITEM:
                modalWindow.update(dt);
                if (Gdx.input.justTouched()) {
                    modalWindow.hide();
                }
                break;
            case DONE:
                // ...
                break;
        }

    }

    @Override
    public void render(SpriteBatch batch) {
        if (isCompleted) return;

        switch (state) {
            case START:
                drawText(batch, "Building...");
                break;
            case PICK_TILE:
                drawText(batch, "Click a tile to build on...");
                break;
            case PICK_ITEM:
                modalWindow.render(batch);
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
                modalWindow = new BuildActionModalWindow(hudCamera, this);
                modalWindow.show();
                state = BuildState.PICK_ITEM;
                break;
            case PICK_ITEM:
                // check inventory
                break;
        }
    }
}
