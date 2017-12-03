package lando.systems.ld40;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import lando.systems.ld40.screens.BaseScreen;
import lando.systems.ld40.screens.TitleScreen;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.Config;

/**
 * Created by Brian 11/28/2017
 */
public class LudumDare40 extends ApplicationAdapter {

    public static LudumDare40 game;

    private BaseScreen screen;
    private BaseScreen nextScreen;
    private MutableFloat transitionPercent;
    private FrameBuffer transitionFBO;
    TextureRegion transitionTexture;

    @Override
    public void create () {
        transitionPercent = new MutableFloat(0);
        transitionFBO = FrameBuffer.createFrameBuffer(Pixmap.Format.RGBA8888, Config.gameWidth, Config.gameHeight, false);
        transitionTexture = new TextureRegion(transitionFBO.getColorBufferTexture());
        transitionTexture.flip(false, true);
        Assets.load();
        float progress = 0f;
        do {
            progress = Assets.update();
        } while (progress != 1f);
        game = this;

        setScreen(new TitleScreen());
    }

    @Override
    public void render () {
        float dt = Math.min(Gdx.graphics.getDeltaTime(), 1f / 30f);
        Assets.tween.update(dt);
        screen.update(dt);
        screen.render(Assets.batch);
        if (nextScreen != null) {
            nextScreen.update(dt);
            transitionFBO.begin();
            nextScreen.render(Assets.batch);
            transitionFBO.end();
            Assets.batch.setShader(Assets.blindsShader);
            Assets.batch.begin();
            Assets.blindsShader.setUniformf("u_percent", transitionPercent.floatValue());
            Assets.batch.draw(transitionTexture, 0, 0, Config.gameWidth, Config.gameHeight);
            Assets.batch.end();
            Assets.batch.setShader(null);
        }
//        Gdx.app.log("Render Calls", "" + Assets.batch.renderCalls);
    }

    @Override
    public void dispose () {
        Assets.dispose();
    }

    public void setScreen(final BaseScreen newScreen){
        if (nextScreen != null) return;
        if (screen == null) { // First time i hope
            screen = newScreen;
            Gdx.input.setInputProcessor(screen);
        } else { // transition
            Gdx.input.setInputProcessor(null);
            screen.allowInput = false;
            nextScreen = newScreen;
            transitionPercent.setValue(0);
            Timeline.createSequence()
                    .pushPause(.5f)
                    .push(Tween.to(transitionPercent, 1, 2f)
                        .target(1f))
                    .push(Tween.call(new TweenCallback() {
                        @Override
                        public void onEvent(int i, BaseTween<?> baseTween) {
                            screen = nextScreen;
                            nextScreen = null;
                            screen.allowInput = true;
                            Gdx.input.setInputProcessor(screen);
                        }
                    }))
                    .start(Assets.tween);
        }
    }

}
