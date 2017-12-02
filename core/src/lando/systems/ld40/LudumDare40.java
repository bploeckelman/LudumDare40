package lando.systems.ld40;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import lando.systems.ld40.screens.BaseScreen;
import lando.systems.ld40.screens.TitleScreen;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.SoundManager;

/**
 * Created by Brian 11/28/2017
 */
public class LudumDare40 extends ApplicationAdapter {

    public static LudumDare40 game;

    private BaseScreen screen;

    @Override
    public void create () {
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
//        Gdx.app.log("Render Calls", "" + Assets.batch.renderCalls);
    }

    @Override
    public void dispose () {
        Assets.dispose();
    }

    public void setScreen(BaseScreen newScreen){
        screen = newScreen;
    }

}
