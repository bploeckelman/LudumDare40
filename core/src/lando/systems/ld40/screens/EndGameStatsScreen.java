package lando.systems.ld40.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld40.LudumDare40;
import lando.systems.ld40.ui.Button;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.Config;
import lando.systems.ld40.world.Statistics;

public class EndGameStatsScreen extends BaseScreen {
    Statistics stats;
    Button replayButton;
    Button nextButton;
    Button showMoneyButton;
    Button showBuildingsButton;
    Button showAddonsButton;
    Button showGarbageGenerated;
    Button showGarbageHauled;
    Button showGarbageInLandfills;

    public EndGameStatsScreen(){
        stats = Statistics.getStatistics();
        // I am a bad person.  Here are some magic numbers
        nextButton = new Button(Assets.nextButtonTexture, new Rectangle(690, 60, 50, 50), hudCamera);
        replayButton = new Button(Assets.replayButton, new Rectangle(60, 60, 50, 50), hudCamera);
        showMoneyButton = new Button(Assets.whiteNinePatch, new Rectangle(550, 440, 20, 20), hudCamera);
        showBuildingsButton = new Button(Assets.whiteNinePatch, new Rectangle(550, 400, 20, 20), hudCamera);
        showAddonsButton = new Button(Assets.whiteNinePatch, new Rectangle(550, 360, 20, 20), hudCamera);
        showGarbageGenerated = new Button(Assets.whiteNinePatch, new Rectangle(550, 320, 20, 20), hudCamera);
        showGarbageHauled = new Button(Assets.whiteNinePatch, new Rectangle(550, 280, 20, 20), hudCamera);
        showGarbageInLandfills = new Button(Assets.whiteNinePatch, new Rectangle(550, 240, 20, 20), hudCamera);
    }

    @Override
    public void update(float dt) {
        if (allowInput) {
            stats.update(dt);
        }


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
        nextButton.render(batch);

        batch.setColor(stats.showMoney? Statistics.COLOR_MONEY : Config.COLOR_BACKGROUND);
        showMoneyButton.render(batch);
        batch.setColor(stats.showBuildings? Statistics.COLOR_BUILDINGS : Config.COLOR_BACKGROUND);
        showBuildingsButton.render(batch);
        batch.setColor(stats.showAddons? Statistics.COLOR_ADDONS : Config.COLOR_BACKGROUND);
        showAddonsButton.render(batch);
        batch.setColor(stats.showGarbageGenerated? Statistics.COLOR_GARBAGE_GENERATED : Config.COLOR_BACKGROUND);
        showGarbageGenerated.render(batch);
        batch.setColor(stats.showGarbageHauled? Statistics.COLOR_GARBAGE_HAULED : Config.COLOR_BACKGROUND);
        showGarbageHauled.render(batch);
        batch.setColor(stats.showGarbageInLandFills? Statistics.COLOR_GARBAGE_IN_LANDFILLS : Config.COLOR_BACKGROUND);
        showGarbageInLandfills.render(batch);


        batch.setColor(Color.WHITE);
        Assets.drawString(batch, "Statistics", stats.modalBounds.x, stats.modalBounds.y + stats.modalBounds.height - 10, Config.COLOR_TEXT, 1f, Assets.font, stats.modalBounds.width, Align.center);

        Assets.drawString(batch, "Money", showMoneyButton.bounds.x + 25, showMoneyButton.bounds.y + 17, Config.COLOR_TEXT, .3f, Assets.font);
        Assets.drawString(batch, "Buildings", showBuildingsButton.bounds.x + 25, showBuildingsButton.bounds.y + 17, Config.COLOR_TEXT, .3f, Assets.font);
        Assets.drawString(batch, "Addons", showAddonsButton.bounds.x + 25, showAddonsButton.bounds.y + 17, Config.COLOR_TEXT, .3f, Assets.font);
        Assets.drawString(batch, "Garbage Generated", showGarbageGenerated.bounds.x + 25, showGarbageGenerated.bounds.y + 17, Config.COLOR_TEXT, .3f, Assets.font, 240, Align.left);
        Assets.drawString(batch, "Garbage Hauled", showGarbageHauled.bounds.x + 25, showGarbageHauled.bounds.y + 17, Config.COLOR_TEXT, .3f, Assets.font, 240, Align.left);
        Assets.drawString(batch, "Garbage in Landfills", showGarbageInLandfills.bounds.x + 25, showGarbageInLandfills.bounds.y + 17, Config.COLOR_TEXT, .3f, Assets.font, 150, Align.left);



        stats.drawTooltip(batch, hudCamera);
        batch.end();
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        if (replayButton.checkForTouch(screenX, screenY)){
            stats.animationTimer = 0;
        }
        if (nextButton.checkForTouch(screenX, screenY)){
            LudumDare40.game.setScreen(new EndScreen(), Assets.heartShader, 3f);
        }
        if (showMoneyButton.checkForTouch(screenX, screenY)){
            stats.showMoney = !stats.showMoney;
        }
        if (showBuildingsButton.checkForTouch(screenX, screenY)){
            stats.showBuildings = !stats.showBuildings;
        }
        if (showAddonsButton.checkForTouch(screenX, screenY)){
            stats.showAddons = !stats.showAddons;
        }
        if (showGarbageGenerated.checkForTouch(screenX, screenY)){
            stats.showGarbageGenerated = !stats.showGarbageGenerated;
        }
        if (showGarbageHauled.checkForTouch(screenX, screenY)){
            stats.showGarbageHauled = !stats.showGarbageHauled;
        }
        if (showGarbageInLandfills.checkForTouch(screenX, screenY)){
            stats.showGarbageInLandFills = !stats.showGarbageInLandFills;
        }


        return false;
    }
}
