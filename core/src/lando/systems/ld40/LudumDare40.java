package lando.systems.ld40;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.compression.lzma.Base;
import lando.systems.ld40.screens.BaseScreen;
import lando.systems.ld40.screens.EndGameStatsScreen;
import lando.systems.ld40.screens.TitleScreen;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.Config;
import lando.systems.ld40.world.World;
import lando.systems.ld40.utils.SoundManager;


/**
 * Created by Brian 11/28/2017
 */
public class LudumDare40 extends ApplicationAdapter {

    public static LudumDare40 game;

    private BaseScreen screen;
    private BaseScreen nextScreen;
    private MutableFloat transitionPercent;
    private FrameBuffer transitionFBO;
    private FrameBuffer originalFBO;
    Texture originalTexture;
    Texture transitionTexture;
    ShaderProgram transitionShader;

    @Override
    public void create () {
        transitionPercent = new MutableFloat(0);
        transitionFBO = FrameBuffer.createFrameBuffer(Pixmap.Format.RGBA8888, Config.gameWidth, Config.gameHeight, false);
        transitionTexture = transitionFBO.getColorBufferTexture();

        originalFBO = FrameBuffer.createFrameBuffer(Pixmap.Format.RGBA8888, Config.gameWidth, Config.gameHeight, false);
        originalTexture = originalFBO.getColorBufferTexture();

        Assets.load();
        SoundManager.load(true);
        float progress = 0f;
        do {
            progress = Assets.update();
        } while (progress != 1f);
        game = this;

//        for (int i = 0; i < 50;  i++) {
//            World.GetWorld().nextTurn();
//        }
//        setScreen(new EndGameStatsScreen());
//        screen.allowInput = true;
        setScreen(new TitleScreen());
    }

    @Override
    public void render () {
        float dt = Math.min(Gdx.graphics.getDeltaTime(), 1f / 30f);
        Assets.tween.update(dt);
        screen.update(dt);

        if (nextScreen != null) {
            nextScreen.update(dt);

            transitionFBO.begin();
            nextScreen.render(Assets.batch);
            transitionFBO.end();

            originalFBO.begin();
            screen.render(Assets.batch);
            originalFBO.end();

            Assets.batch.setShader(transitionShader);
            Assets.batch.begin();
            originalTexture.bind(1);
            transitionShader.setUniformi("u_texture1", 1);
            transitionTexture.bind(0);
            transitionShader.setUniformf("u_percent", transitionPercent.floatValue());
            Assets.batch.setColor(Color.WHITE);
            Assets.batch.draw(transitionTexture, 0, 0, Config.gameWidth, Config.gameHeight);
            Assets.batch.end();
            Assets.batch.setShader(null);
        } else {
            screen.render(Assets.batch);
        }
//        Gdx.app.log("Render Calls", "" + Assets.batch.renderCalls);
    }

    @Override
    public void dispose () {
        Assets.dispose();
    }

    public void setScreen(final BaseScreen newScreen){
        setScreen(newScreen, null, 1f);
    }

    public void setScreen(final BaseScreen newScreen, ShaderProgram transitionType, float tranistionSpeed){
        if (nextScreen != null) return;
        if (screen == null) { // First time i hope
            screen = newScreen;
            Gdx.input.setInputProcessor(screen);
        } else { // transition
            Gdx.input.setInputProcessor(null);
            if (transitionType == null) {
                transitionShader = Assets.randomTransitions.get(MathUtils.random(Assets.randomTransitions.size-1));
            } else {
                transitionShader = transitionType;
            }
            screen.allowInput = false;
            transitionPercent.setValue(0);
            Timeline.createSequence()
                    .pushPause(.1f)
                    .push(Tween.call(new TweenCallback() {
                        @Override
                        public void onEvent(int i, BaseTween<?> baseTween) {
                            nextScreen = newScreen;
                        }
                    }))
                    .push(Tween.to(transitionPercent, 1, tranistionSpeed)
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
