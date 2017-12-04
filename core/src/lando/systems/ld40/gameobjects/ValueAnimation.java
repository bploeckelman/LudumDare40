package lando.systems.ld40.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld40.utils.Assets;

import java.util.ArrayList;

// things I want to animate:
// add/remove trash
//      compress added trash
//      recycle added trash
// incinerate trash
// add valueString
// ADD TRASH (+/- NUMBER ICON (ICONS))
// REMOVE TRASH (+/- NUMBER ICON (ICONS))

public class ValueAnimation {
    private static final int BACKGROUND_COLOR = 0xebffdaff;
    private static final float BACKGROUND_PADDING = 10f;
    private static final float ANIMATION_END_DY = 100; // pixels
    private static final float TEXT_SCALE = 0.7f;
    private static final float DURATION = 1.6f; // seconds
    private static final int SIG_DIGITS = 2;

    private String valueString;
    private ValueAnimationIcon icon;
    private ArrayList<ValueAnimationIcon> modifierIcons;
    private Color textColor;
    private Color backgroundColor;

    public boolean isComplete;
    float currentTime;
    float currentY;

    private final float computedBackgroundWidth;
    private final float computedBackgroundHeight;
    private final float textOffsetX; // offset from the bottom left corner of the background
    private final float textOffsetY; // offset from the bottom left corner of the background

    public ValueAnimation(float value, ValueAnimationIcon icon, ArrayList<ValueAnimationIcon> modifierIcons) {
        this.valueString = String.valueOf(value);
        if (this.valueString.contains(".")) {
            int index = this.valueString.indexOf(".");
            int substringEnd = SIG_DIGITS == 0 ? index : Math.min(index + 1 + SIG_DIGITS, valueString.length());
            this.valueString = this.valueString.substring(0, substringEnd);
            if (value >= 0) {
                this.valueString = "+" + this.valueString;
            }
        }
        this.icon = icon;
        this.modifierIcons = modifierIcons;

        this.isComplete = false;
        this.currentTime = 0;
        this.currentY = 0;

        this.textColor = new Color(0x170d20ff);   // todo: change this color based on icon type?
        this.backgroundColor = new Color(BACKGROUND_COLOR);

        // Compute measurements for layout once here
        Assets.font.getData().setScale(TEXT_SCALE);
        Assets.fontShader.setUniformf("u_scale", TEXT_SCALE);
        Assets.layout.setText(Assets.font, valueString);
        this.computedBackgroundWidth = Assets.layout.width + BACKGROUND_PADDING * 2; // TODO add icon(s)
        this.computedBackgroundHeight = Assets.layout.height + BACKGROUND_PADDING * 2;
        textOffsetX = BACKGROUND_PADDING;
        textOffsetY = BACKGROUND_PADDING + Assets.layout.height; // Text is *top* left corner
    }

    public void update(float dt) {
        this.currentTime += dt;
        if (currentTime > DURATION) {
            this.isComplete = true;
        }
    }

    /**
     * Render.  Provide origin x/y to allow this to move with anything it is 'attached' to
     * X/Y will be the bottom left corner of the animation
     *
     * @param batch The SpriteBatch
     * @param x     The x origin of this animation
     * @param y     The y origin of this animation
     */
    public void render(SpriteBatch batch, float x, float y) {
        if (isComplete) {
            return;
        }
        float percent = Math.min(currentTime / DURATION, 1);
        float dy = ANIMATION_END_DY * percent;
        float alpha = 1 - percent;
        // Background
        Color c = batch.getColor();
        backgroundColor.a = alpha;
        batch.setColor(backgroundColor);
        batch.draw(Assets.whitePixel, x, y + dy, computedBackgroundWidth, computedBackgroundHeight);
        batch.setColor(c);
        // Text
        textColor.a = alpha;
        Assets.drawString(batch, valueString, x + textOffsetX, y + textOffsetY + dy, textColor, TEXT_SCALE, Assets.font);
        // TODO: draw icons.
    }

}
