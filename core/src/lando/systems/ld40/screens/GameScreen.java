package lando.systems.ld40.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld40.ui.TutorialManager;

public class GameScreen extends BaseScreen {
    static boolean firstTimeRun = false;
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
        batch.begin();
        if(tutorialManager != null)
        {
            tutorialManager.render(batch);
        }
        batch.end();
    }
}
