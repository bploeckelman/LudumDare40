package lando.systems.ld40.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld40.LudumDare40;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.Config;

/**
 * Created by Brian on 11/28/2017
 */
public class TitleScreen extends BaseScreen {

    private final LudumDare40 game;

    public TitleScreen() {
        this.game = LudumDare40.game;
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        if (Gdx.input.justTouched()) {
            game.setScreen(new GameScreen());
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(Config.bgColor.r, Config.bgColor.g, Config.bgColor.b, Config.bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            batch.setColor(Color.DARK_GRAY);
            batch.draw(Assets.whitePixel, 0, 0, hudCamera.viewportWidth, hudCamera.viewportHeight);
            batch.setColor(Color.WHITE);
            Assets.drawString(batch, "Title", 10f, hudCamera.viewportHeight - 20f, Color.GOLD, 0.5f, Assets.font);
        }
        batch.end();
    }

}
