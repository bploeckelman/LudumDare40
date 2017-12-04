package lando.systems.ld40.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    private static final float ELEMENT_PADDING = 8;
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
    private final float computedIconWidth;
    private final float computedIconHeight;
    private final float iconOffsetX;
    private final float iconOffsetY;

    private final TextureRegion iconTexture;

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
        iconTexture = getIconTexture(icon);
        this.modifierIcons = modifierIcons;

        this.isComplete = false;
        this.currentTime = 0;
        this.currentY = 0;

        this.textColor = new Color(0x170d20ff);   // todo: change this color based on icon type?
        this.backgroundColor = new Color(BACKGROUND_COLOR);

        // Compute measurements for layout once here
        // Layout ->  [ICON] [VALUE] ([MOD_ICONS...])
        Assets.font.getData().setScale(TEXT_SCALE);
        Assets.fontShader.setUniformf("u_scale", TEXT_SCALE);
        Assets.layout.setText(Assets.font, valueString);
        float textHeight = Assets.layout.height;
        float textWidth = Assets.layout.width;
        this.computedBackgroundHeight = textHeight + BACKGROUND_PADDING * 2;

        computedIconHeight = textHeight;
        computedIconWidth = (computedIconHeight / iconTexture.getRegionHeight()) * iconTexture.getRegionWidth();
        iconOffsetX = BACKGROUND_PADDING;
        iconOffsetY = BACKGROUND_PADDING;

        textOffsetX = iconOffsetX + computedIconWidth + ELEMENT_PADDING;
        textOffsetY = BACKGROUND_PADDING + textHeight; // Text is *top* left corner

        this.computedBackgroundWidth = textOffsetX + textWidth + BACKGROUND_PADDING;
    }

    public TextureRegion getIconTexture(ValueAnimationIcon icon) {
        switch (icon) {
            case COMPRESSOR:    return Assets.compactorTexture;
            case GREEN_CERT:    return Assets.leafTexture;
            case INCINERATOR:   return Assets.incineratorTexture;
            case MONEY:         return Assets.moneyTexture;
            case OVER_CAPACITY: return Assets.trashButton;
            case RECYCLE:       return Assets.recycleTexture;
            case TIER:          return Assets.tier1Texture;
            case TRASH:         return Assets.trashBag;
            default:
                throw new RuntimeException("unknown icon type");
        }
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

        // Icon(s)
        batch.setColor(1,1,1,alpha);
        batch.draw(iconTexture, x + iconOffsetX, y + iconOffsetY + dy, computedIconWidth, computedIconHeight);
        batch.setColor(c);
    }

}
