package lando.systems.ld40.ui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.accessors.RectangleAccessor;

public abstract class ModalWindow {

    private final BitmapFont font;
    protected final float margin_top = 10f;
    protected final float margin_left = 10f;

    public float accum;
    public boolean isActive;
    public OrthographicCamera camera;

    protected boolean showText;
    protected Rectangle modalRect;
    protected Rectangle modalTarget;
    protected float touchDelay;
//    protected Button exitButton;

    protected Vector3 touchPos = new Vector3();

    public ModalWindow(OrthographicCamera camera) {
        this.font = Assets.font;
        this.accum = 0f;
        this.isActive = false;
        this.showText = false;
        this.camera = camera;

        float modal_width  = camera.viewportWidth - 2f * margin_left;
        float modal_height = (4f / 5f) * camera.viewportHeight;
        this.modalTarget = new Rectangle(
                camera.viewportWidth  / 2f - modal_width  / 2f,
                camera.viewportHeight / 2f - modal_height / 2f,
                modal_width, modal_height
        );
        this.modalRect = new Rectangle(modalTarget);
        this.touchDelay = 0f;
    }

    public boolean contains(Vector3 position) {
        return modalRect.contains(position.x, position.y);
    }

    public void show() {
        if (isActive) return;
        isActive = true;
        touchDelay = 0.5f;
        modalRect.set(
                camera.viewportWidth / 2f,
                camera.viewportHeight / 2f,
                0f, 0f
        );
        Tween.to(modalRect, RectangleAccessor.XYWH, 0.2f)
                .target(modalTarget.x, modalTarget.y, modalTarget.width, modalTarget.height)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        showText = true;
                    }
                })
                .start(Assets.tween);
    }

    public void hide() {
        if (!isActive) return;
        showText = false;

        float modal_target_x = camera.viewportWidth / 2f;
        float modal_target_y = camera.viewportHeight / 2f;
        Tween.to(modalRect, RectangleAccessor.XYWH, 0.2f)
                .target(modal_target_x, modal_target_y, 0f, 0f)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        isActive = false;
                    }
                })
                .start(Assets.tween);
    }

    public void update(float dt) {
        float screenX = 0f;
        float screenY = 0f;
        if (Gdx.input.justTouched()) {
            screenX = Gdx.input.getX();
            screenY = Gdx.input.getY();
        }
        touchPos.set(screenX, screenY, 0f);
        camera.unproject(touchPos);

        // NOTE: If clicks inside the modal need to be handled, do it here...
        // if (foo.contains(touchPos.x, touchPos.y)) {}

        accum += dt;
        touchDelay -= dt;
        if (Gdx.input.justTouched() && touchDelay <= 0) {
            hide();
        }
    }

    public void render(SpriteBatch batch) {
        // Draw background
        batch.setColor(0f, 0f, 0f, 0.95f);
        batch.draw(Assets.whitePixel, modalRect.x, modalRect.y, modalRect.width, modalRect.height);

        // Draw outline
        batch.setColor(Color.WHITE);
        Assets.defaultNinePatch.draw(batch, modalRect.x, modalRect.y, modalRect.width, modalRect.height);

        renderWindowContents(batch);
    }

    protected abstract void renderWindowContents(SpriteBatch batch);

    public void touchUp(float windowX, float windowY) {

    }

}
