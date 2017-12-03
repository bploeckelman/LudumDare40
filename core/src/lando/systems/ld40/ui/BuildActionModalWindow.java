package lando.systems.ld40.ui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld40.screens.PlanPhaseScreen;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.accessors.RectangleAccessor;

public class BuildActionModalWindow extends ModalWindow {

    private PlanPhaseScreen.BuildAction buildAction;

    public BuildActionModalWindow(OrthographicCamera camera, PlanPhaseScreen.BuildAction buildAction) {
        super(camera);
        this.buildAction = buildAction;
    }

    @Override
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
                        buildAction.state = PlanPhaseScreen.BuildState.DONE;
                        isActive = false;
                    }
                })
                .start(Assets.tween);
    }

    @Override
    protected void renderWindowContents(SpriteBatch batch) {
        if (buildAction == null || !showText) return;

        float tile_size = modalRect.width / 2f - 2f * margin_left;
        buildAction.selectedObject.render(batch,
                modalRect.x + margin_left,
                modalRect.y + modalRect.height / 2f - tile_size / 2f,
                tile_size, tile_size);

        // NOTE: 0,0 is top left instead of bottom for text
        batch.setShader(Assets.fontShader);
        {
            final float title_text_scale = 0.4f;
            final float target_width = modalRect.width;
            Assets.font.getData().setScale(title_text_scale);
            Assets.fontShader.setUniformf("u_scale", title_text_scale);
            Assets.layout.setText(Assets.font, "Pick an item to build on this tile...",
                    Color.GOLD, target_width, Align.center, true);
            Assets.font.draw(batch, Assets.layout,
                    modalRect.x + margin_left,
                    modalRect.y + modalRect.height - Assets.layout.height - margin_top);
        }
        batch.setShader(null);
    }

}
