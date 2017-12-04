package lando.systems.ld40.managers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld40.ui.ModalWindow;
import lando.systems.ld40.utils.Assets;

/**
 * Created by Brian on 12/3/2017.
 */
public abstract class ActionManager implements IManager {

    protected Vector3 hudVector = new Vector3();
    protected Vector3 worldVector = new Vector3();
    protected OrthographicCamera hudCamera;
    protected OrthographicCamera worldCamera;

    protected ModalWindow window;
    protected boolean isCompleted;

    protected ActionManager(OrthographicCamera hudCamera, OrthographicCamera worldCamera) {
        this.hudCamera = hudCamera;
        this.worldCamera = worldCamera;
    }

    protected void drawText(SpriteBatch batch, String text) {
        drawText(batch, text, 0.5f);
    }

    protected void drawText(SpriteBatch batch, String text, float scale) {
        batch.setShader(Assets.fontShader);
        Assets.font.getData().setScale(scale);
        Assets.fontShader.setUniformf("u_scale", scale);
        Assets.layout.setText(Assets.font, text);
        Assets.font.draw(batch, text,
                0, hudCamera.viewportHeight - Assets.layout.height,
                hudCamera.viewportWidth,
                Align.center, true);
        Assets.font.setColor(Color.WHITE);
        Assets.font.getData().setScale(1f);
        Assets.fontShader.setUniformf("u_scale", 1f);
        batch.setShader(null);
    }

    protected Vector3 unprojectHud(float screenX, float screenY) {
        hudVector.set(screenX, screenY, 0);
        hudCamera.unproject(hudVector);
        return hudVector;
    }

    protected Vector3 unprojectWorld(float screenX, float screenY) {
        worldVector.set(screenX, screenY, 0);
        worldCamera.unproject(worldVector);
        return worldVector;
    }

    public void activate() { }

    public void deactivate() {
        complete();
    }

    public void complete() {
        isCompleted = true;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (isCompleted) return;

        if (window != null) {
            window.render(batch);
        }

        renderManager(batch);
    }

    public abstract void renderManager(SpriteBatch batch);

    @Override
    public void update(float dt) {
        if (isCompleted) return;

        if (window != null) {
            window.update(dt);
            // modal has been closed, no longer active, clean up
            if (!window.isActive) {
                onModalClose();
                window = null;
            }
        }

        updateManager(dt);
    }

    protected void onModalClose() {

    }

    protected void updateManager(float dt) {

    }


    public boolean touchUp(float screenX, float screenY) {
        Vector3 position = unprojectHud(screenX, screenY);
        if (isModal()) {
            if (window.contains(position)) {
                window.handleTouch(position.x, position.y);
            }
            return true;
        }

        return handleTouch(screenX, screenY);
    }

    public boolean handleTouch(float screenX, float screenY) {
        return false;
    }

    @Override
    public boolean isModal() {
        return (window != null && window.isActive);
    }
}