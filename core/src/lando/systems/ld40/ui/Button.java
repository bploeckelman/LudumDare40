package lando.systems.ld40.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.Config;

import static com.badlogic.gdx.Gdx.input;

public class Button {

    private static final float TOOLTIP_TEXT_OFFSET_Y = 3f; // Catches characters with ligatures below the baseline
    private static final float TOOLTIP_TEXT_PADDING_X = 8f;
    private static final float TOOLTIP_TEXT_PADDING_Y = 8f;
    private static final float TOOLTIP_TEXT_SCALE = 0.3f;
    private static final float TOOLTIP_SHOW_DELAY = 0.3f;
    private static final float TOOLTIP_CURSOR_OFFSET_X = 8f;
//    private static final float TOOLTIP_CURSOR_OFFSET_Y = 10f;

    private float tooltipBackgroundHeight;
    private float tooltipBackgroundWidth;
    private float tooltipTextOffsetY;

    public final TextureRegion region;
    private final NinePatch ninePatch;
    public final Rectangle bounds;
    public String text = null;
    public String tooltip = null;

    private OrthographicCamera camera;
    private Vector2 touchPosScreen = new Vector2();
    public float textScale = 0.3f;
    private float textOffsetY = 3f;
    public Color textColor = Color.WHITE;
    private float textX;
    private float textY;
    private float timeHovered = 0;
    private boolean showTooltip = false;
    Vector3 tempVec3 = new Vector3();


    // Constructors ----------------------------------------------------------------------------------------------------

    public Button(TextureRegion region, Rectangle bounds, OrthographicCamera camera, String text, String tooltip) {
        this.bounds = new Rectangle(bounds);
        this.camera = camera;
        this.region = region;
        this.setText(text);
        this.setTooltip(tooltip);
        this.ninePatch = null;
    }

    public Button(TextureRegion region, Rectangle bounds, OrthographicCamera camera) {
        this.bounds = new Rectangle(bounds);
        this.camera = camera;
        this.region = region;
        this.ninePatch = null;
    }

    public Button(NinePatch ninePatch, Rectangle bounds, OrthographicCamera camera, String text, String tooltip) {
        this.ninePatch = ninePatch;
        this.region = null;
        this.bounds = new Rectangle(bounds);
        this.camera = camera;
        this.setText(text);
        this.setTooltip(tooltip);
    }

    public Button(NinePatch ninePatch, Rectangle bounds, OrthographicCamera camera) {
        this.bounds = new Rectangle(bounds);
        this.camera = camera;
        this.region = null;
        this.ninePatch = ninePatch;
    }


    // Update & Render -------------------------------------------------------------------------------------------------

    public void render(SpriteBatch batch) {
        // Button texture
        if (region != null) {
            batch.draw(region, bounds.x, bounds.y, bounds.width, bounds.height);
        } else if (ninePatch != null) {
            ninePatch.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
        }

        // Button text
        if (text != null && !text.equals("")) {
            Assets.drawString(batch, text, textX, textY, textColor, textScale, Assets.font);
        }
    }


    public void renderTooltip(SpriteBatch batch, OrthographicCamera hudCamera){
        // Tooltip
        if (tooltip == null || tooltip.equals("") || !showTooltip) return;

        tempVec3.set(input.getX(), input.getY(), 0);
        hudCamera.unproject(tempVec3);
        float tX = tempVec3.x;
        float tY = tempVec3.y;
        float backgroundX;
        float backgroundY;
        float stringTX ;
        float stringTY;

        // Screen space
        if (tX < Config.gameWidth / 2) {
            // left half of the screen: align left edge of tooltip at cursor
            backgroundX = tX;
            if (tY > Config.gameHeight / 2) {
                // Tooltip will appear under the cursor (bottom-right).  Offset it.
                backgroundX += TOOLTIP_CURSOR_OFFSET_X;
            }
        } else {
            // Right side of screen: align right edge of tooltip at cursor
            backgroundX = tX - tooltipBackgroundWidth;
        }
        stringTX = backgroundX + TOOLTIP_TEXT_PADDING_X;
        if (tY <= Config.gameHeight / 2) {
            // bottom half of screen: align bottom edge of tooltip with cursor
            backgroundY = tY;
        } else {
            // top half of screen: align top edge of tooltip with cursor
            backgroundY = tY - tooltipBackgroundHeight;
        }
        stringTY = backgroundY + tooltipTextOffsetY;

        // DRAW
        Assets.defaultNinePatch.draw(batch, backgroundX, backgroundY, tooltipBackgroundWidth, tooltipBackgroundHeight);
        Assets.drawString(batch,
                tooltip,
                stringTX,
                stringTY,
                Color.WHITE,
                TOOLTIP_TEXT_SCALE,
                Assets.font);
    }

    public void update(float dt) {
        boolean isTouching = checkForTouch(input.getX(), input.getY());
        if (isTouching) {
            timeHovered += dt;
        } else {
            timeHovered = 0;
        }
        showTooltip = timeHovered >= TOOLTIP_SHOW_DELAY;
    }


    // -----------------------------------------------------------------------------------------------------------------


    public boolean checkForTouch(int screenX, int screenY) {
        Vector3 touchPosUnproject = camera.unproject(tempVec3.set(screenX, screenY, 0));
        touchPosScreen.set(touchPosUnproject.x, touchPosUnproject.y);
        return bounds.contains(touchPosScreen.x, touchPosScreen.y);
    }

    public void setText(String text) {
        this.text = text;
        if (text != null) {
            Assets.font.getData().setScale(textScale);
            Assets.layout.setText(Assets.font, text);
            Assets.font.getData().setScale(1f);
            float textWidth = Assets.layout.width;
            float textHeight = Assets.layout.height;
            textX = bounds.x + (bounds.width / 2) - (textWidth / 2);
            textY = bounds.y + (bounds.height / 2) + (textHeight / 2) + textOffsetY;
        }
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
        if (tooltip != null) {
            Assets.font.getData().setScale(TOOLTIP_TEXT_SCALE);
            Assets.layout.setText(Assets.font, tooltip);
            tooltipBackgroundHeight = Assets.layout.height + (TOOLTIP_TEXT_PADDING_Y * 2);
            tooltipBackgroundWidth = Assets.layout.width + (TOOLTIP_TEXT_PADDING_X * 2);
            tooltipTextOffsetY = (Assets.layout.height + TOOLTIP_TEXT_PADDING_Y + TOOLTIP_TEXT_OFFSET_Y);
            Assets.font.getData().setScale(1f);
        }
    }

    public void updateTextProperties(float textScale, Color textColor, float textOffsetY) {
        this.textScale = textScale;
        this.textColor = textColor;
        this.textOffsetY = textOffsetY;
    }

}
