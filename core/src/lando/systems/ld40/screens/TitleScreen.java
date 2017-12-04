package lando.systems.ld40.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Elastic;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld40.LudumDare40;
import lando.systems.ld40.gameobjects.Bird;
import lando.systems.ld40.gameobjects.Cloud;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.Config;
import lando.systems.ld40.utils.accessors.Vector2Accessor;
//import lando.systems.ld40.utils.SoundManager;

/**
 * Created by Brian on 11/28/2017
 */
public class TitleScreen extends BaseScreen {

    private final LudumDare40 game;

    private Vector2 titlePos;
    Array<Bird> birds;
    Array<Cloud> clouds;

    public TitleScreen() {
        this.game = LudumDare40.game;
        allowInput = false;
        titlePos = new Vector2(0, 500);
        birds = new Array<Bird>();
        for (int i = 0; i < 25; i++){
            birds.add(new Bird());
        }

        clouds =  new Array<Cloud>();
        for (int i = 0; i < 5; i ++){
            clouds.add(new Cloud(true));
        }

        Tween.to(titlePos, Vector2Accessor.Y, 3f)
                .target(0)
                .ease(Elastic.OUT)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        allowInput = true;
                    }
                })
                .start(Assets.tween);
    }

    @Override
    public void update(float dt) {
        for (Bird bird : birds){
            bird.update(dt);
        }
        for (int i = clouds.size -1; i >= 0; i--){
            clouds.get(i).update(dt);
            if (clouds.get(i).offScreen){
                clouds.removeIndex(i);
                clouds.add(new Cloud(false));
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        // TODO: REMOVE DEV SHORTCUT
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            game.setScreen(new ActionPhaseScreen());
        }
        // TODO: REMOVE DEV SHORTCUT
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            game.setScreen(new ResolutionPhaseScreen());
        }
        if (Gdx.input.justTouched() & allowInput) {
            game.setScreen(new PlanPhaseScreen(true), Assets.doorwayShader, 2.5f);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(Config.bgColor.r, Config.bgColor.g, Config.bgColor.b, Config.bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            batch.draw(Assets.titleScreenBackground, 0, 0, hudCamera.viewportWidth, hudCamera.viewportHeight);
            batch.draw(Assets.titleName, titlePos.x, titlePos.y, hudCamera.viewportWidth, hudCamera.viewportHeight);
            for (Bird bird : birds){
                bird.render(batch);
            }

            for (Cloud cloud: clouds){
                cloud.render(batch);
            }
//            batch.setColor(Color.DARK_GRAY);
//            batch.draw(Assets.whitePixel, 0, 0, hudCamera.viewportWidth, hudCamera.viewportHeight);
//            batch.setColor(Color.WHITE);
//            Assets.drawString(batch, "Garbage Town", 10f, hudCamera.viewportHeight - 20f, Color.GOLD, 1.5f, Assets.font);
//            Assets.drawString(batch, "Population: You", 10f, hudCamera.viewportHeight - 20f - 100f, Color.WHITE, 0.5f, Assets.font);
        }
        batch.end();
    }

}
