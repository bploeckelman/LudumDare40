package lando.systems.ld40.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld40.ui.TutorialManager;
import lando.systems.ld40.utils.Config;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class GameScreen extends BaseScreen {
    public static boolean firstTimeRun = false;
    public TutorialManager tutorialManager;

    public GameScreen(){
        init();
    }

    public void init()
    {
        if(firstTimeRun)
        {
            tutorialManager = new TutorialManager(this);
        }
    }

    @Override
    public void update(float dt) {
        if(tutorialManager != null && tutorialManager.isDisplayed())
        {
            firstTimeRun = false;
            tutorialManager.update(dt);
            return;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(Config.bgColor.r, Config.bgColor.g, Config.bgColor.b, Config.bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        if(tutorialManager != null)
        {
            tutorialManager.render(batch);
        }
        batch.end();
    }
}
