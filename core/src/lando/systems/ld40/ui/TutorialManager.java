package lando.systems.ld40.ui;

import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld40.screens.*;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.Config;

/**
 * Created by dsgraham on 7/31/17.
 */
public class TutorialManager {

    GameScreen gameScreen;
    Array<TutorialInfo> screens;
    MutableFloat sceneAlpha;
    boolean acceptInput;

    public TutorialManager(GameScreen gameScreen) {
        acceptInput = true;
        sceneAlpha = new MutableFloat(1);
        screens = new Array<TutorialInfo>();

//        [#FFFF00xALPHAx] Gold[]
        Rectangle rect = new Rectangle();
        GameScreen g = gameScreen;
        TutorialInfo info;
        

        screens.add(new TutorialInfo(
                "Controls for Lieutenant Planet",
                new Rectangle(0, 0, 0, 0)));

        // --------------------------------------------------------------------

        screens.add(new TutorialInfo(
                "Placeholder for more tutorial information",
                new Rectangle(0, 0, 0, 0)));


        // --------------------------------------------------------------------
    }

    public boolean isDisplayed() {
        return screens.size > 0;
    }

    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            screens.clear();
            return;
        }

        if (acceptInput && Gdx.input.justTouched()) {
            acceptInput = false;
            if (screens.size > 1) {
                screens.removeIndex(0);
                acceptInput = true;
            } else {
                screens.removeIndex(0);
            }
        }
    }

    public Rectangle expandRectangle(Rectangle rect) {
        return new Rectangle(rect.x - 10, rect.y - 10, rect.width + 20, rect.height + 20);
    }

    public void render(SpriteBatch batch) {
        if (screens.size <= 0) return;
        Assets.font.getData().setScale(.7f);
        TutorialInfo info = screens.get(0);
        drawHighlight(batch, info);

        String coloredReplace = info.text;
        Color color = new Color(1, 1, 1, sceneAlpha.floatValue());
        int intAlpha = (int) (sceneAlpha.floatValue() * 255);
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toHexString(intAlpha));
        if (sb.length() < 2) sb.insert(0, '0'); // pad with leading zero if needed
        String hex = sb.toString();

        Assets.layout.setText(Assets.font, coloredReplace.replace("xALPHAx", hex), color, info.wrapWidth, Align.center, true);
        float txtH = Assets.layout.height;
        float boxWidth = (info.wrapWidth + 20);
        Rectangle bounds = new Rectangle(info.pos.x - boxWidth / 2 - 10, info.pos.y - txtH / 2 - 10, boxWidth, txtH + 20);

        batch.setColor(62f / 255, 42f / 255, 0, sceneAlpha.floatValue());
        batch.draw(Assets.whitePixel, bounds.x, bounds.y, bounds.width, bounds.height);

        batch.setColor(new Color(1, 1, 1, sceneAlpha.floatValue()));
        Assets.defaultNinePatch.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);

        Assets.font.draw(batch, Assets.layout, bounds.x + 10, bounds.y + bounds.height - 10);
    }

    private void drawHighlight(SpriteBatch batch, TutorialInfo info) {
        Rectangle light = info.highlightBounds;
        batch.setColor(0, 0, 0, .75f * sceneAlpha.floatValue());
        batch.draw(Assets.whitePixel, 0, 0, light.x, light.y);
        batch.draw(Assets.whitePixel, light.x, 0, light.width, light.y);
        batch.draw(Assets.whitePixel, light.x + light.width, 0, Config.gameWidth, light.y);

        batch.draw(Assets.whitePixel, 0, light.y, light.x, light.height);
        batch.draw(Assets.whitePixel, light.x + light.width, light.y, Config.gameWidth, light.height);

        batch.draw(Assets.whitePixel, 0, light.y + light.height, light.x, Config.gameHeight);
        batch.draw(Assets.whitePixel, light.x, light.y + light.height, light.width, Config.gameHeight);
        batch.draw(Assets.whitePixel, light.x + light.width, light.y + light.height, Config.gameWidth, Config.gameHeight);
        batch.setColor(Color.WHITE);
    }
}
