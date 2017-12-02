package lando.systems.ld40.ui;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld40.utils.Config;

/**
 * Created by dsgraham on 7/31/17.
 */
public class TutorialInfo {

    public String text;
    public Vector2 pos;
    public Rectangle highlightBounds;
    public int wrapWidth;

    public TutorialInfo(String text, Rectangle bounds){
        this(text, new Vector2(Config.gameWidth / 2f, Config.gameHeight / 2f), 400, bounds);
    }

    public TutorialInfo(String text, Vector2 centerPos, int wrapWidth, Rectangle bounds) {
        this.text = text;// + "\n\nClick to Continue\nEscape to Cancel Tutorial";
        this.pos = centerPos;
        this.highlightBounds = bounds;
        this.wrapWidth = wrapWidth;
    }

}
