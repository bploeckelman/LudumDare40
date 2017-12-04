package lando.systems.ld40.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld40.ui.Button;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.Config;
import lando.systems.ld40.world.Statistics;

public class EndGameStatsScreen extends BaseScreen {
    Statistics stats;
    Button replayButton;
    Button showMoneyButton;
    Button showBuildingsButton;

    public EndGameStatsScreen(){
        stats = Statistics.getStatistics();
        // I am a bad person.  Here are some magic numbers
        replayButton = new Button(Assets.whiteNinePatch, new Rectangle(60, 60, 50, 50), hudCamera);
        showMoneyButton = new Button(Assets.whiteNinePatch, new Rectangle(600, 400, 20, 20), hudCamera);
        showBuildingsButton = new Button(Assets.whiteNinePatch, new Rectangle(600, 360, 20, 20), hudCamera);
    }

    @Override
    public void update(float dt) {
        stats.update(dt);


    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(Config.bgColor.r, Config.bgColor.g, Config.bgColor.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        stats.render(batch, hudCamera);

        batch.setColor(Color.WHITE);
        replayButton.render(batch);

        batch.setColor(stats.showMoney? Statistics.COLOR_MONEY : Color.DARK_GRAY);
        showMoneyButton.render(batch);
        batch.setColor(stats.showBuildings? Statistics.COLOR_BUILDINGS : Color.DARK_GRAY);
        showBuildingsButton.render(batch);

        batch.setColor(Color.WHITE);
        Assets.drawString(batch, "Statistics", stats.modalBounds.x, stats.modalBounds.y + stats.modalBounds.height - 10, Color.WHITE, 1f, Assets.font, stats.modalBounds.width, Align.center);
        Assets.drawString(batch, "Money", showMoneyButton.bounds.x + 25, showMoneyButton.bounds.y + 17, Color.WHITE, .3f, Assets.font);
        Assets.drawString(batch, "Buildings", showBuildingsButton.bounds.x + 25, showBuildingsButton.bounds.y + 17, Color.WHITE, .3f, Assets.font);

        stats.drawTooltip(batch, hudCamera);
        batch.end();
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        if (replayButton.checkForTouch(screenX, screenY)){
            stats.animationTimer = 0;
        }
        if (showMoneyButton.checkForTouch(screenX, screenY)){
            stats.showMoney = !stats.showMoney;
        }
        if (showBuildingsButton.checkForTouch(screenX, screenY)){
            stats.showBuildings = !stats.showBuildings;
        }

        return false;
    }
}
