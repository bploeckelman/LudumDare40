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
import lando.systems.ld40.gameobjects.GameObject;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.Config;

import static com.badlogic.gdx.Gdx.input;

public class Button {

    private static final float TOOLTIP_TEXT_OFFSET_Y = 3f; // Catches characters with ligatures below the baseline
    private static final float TOOLTIP_TEXT_PADDING_X = 8f;
    private static final float TOOLTIP_TEXT_PADDING_Y = 8f;
    private static final float TOOLTIP_TEXT_SCALE = 0.4f;
    private static final float TOOLTIP_SHOW_DELAY = 0.3f;
    private static final float TOOLTIP_CURSOR_OFFSET_X = 8f;
    private static final float TOOLTIP_MAX_WIDTH = 350;
//    private static final float TOOLTIP_CURSOR_OFFSET_Y = 10f;
    private static final Color DISABLED_COLOR = new Color(120f / 255f, 120f / 255f, 118f / 255f, 1f);
    private static final float DISABLED_ALPHA = 0.8f;

    private float tooltipBackgroundHeight;
    private float tooltipBackgroundWidth;
    private float tooltipTextOffsetY;

    public final TextureRegion region;
    public final TextureRegion regionPressed;
    private final NinePatch ninePatch;
    public final Rectangle bounds;
    public String text = null;
    public String tooltip = null;
    public Object meta = null;

    protected OrthographicCamera camera;
    private Vector2 touchPosScreen = new Vector2();
    public float textScale = 0.3f;
    private float textOffsetY = 3f;
    public Color textColor = Color.WHITE;
    private float textX;
    private float textY;
    private float timeHovered = 0;
    private boolean showTooltip = false;
    public boolean enabled = true;
    Vector3 tempVec3 = new Vector3();

    public boolean selected;
    public boolean isHover;
    public boolean noHover;
    public ButtonGroup buttonGroup;

    public GameObject gameObject;

    // Constructors ----------------------------------------------------------------------------------------------------
    public Button(GameObject gameObject, Rectangle bounds, OrthographicCamera camera) {
        this(gameObject.texture, bounds, camera);
        this.gameObject = gameObject;
    }

    public Button(String atlasRegion, OrthographicCamera camera, float x, float y, String tooltip) {
        this(atlasRegion, camera, x, y, null, tooltip);
    }

    public Button(String atlasRegion, String atlasRegionPressed, OrthographicCamera camera, float x, float y, String tooltip) {
        this(atlasRegion, atlasRegionPressed, camera, x, y, null, tooltip);
    }

    public Button(String atlasRegion, OrthographicCamera camera, float x, float y, String text, String tooltip) {
        this.region = Assets.atlas.findRegion(atlasRegion);
        this.regionPressed = null;
        this.bounds = new Rectangle(x, y, region.getRegionWidth(), region.getRegionHeight());
        this.camera = camera;
        this.setText(text);
        this.setTooltip(tooltip);
        this.ninePatch = null;
    }

    public Button(String atlasRegion, String atlasRegionPressed, OrthographicCamera camera, float x, float y, String text, String tooltip) {
        this.region = Assets.atlas.findRegion(atlasRegion);
        this.regionPressed = Assets.atlas.findRegion(atlasRegionPressed);
        this.bounds = new Rectangle(x, y, region.getRegionWidth(), region.getRegionHeight());
        this.camera = camera;
        this.setText(text);
        this.setTooltip(tooltip);
        this.ninePatch = null;
    }

    public Button(TextureRegion region, Rectangle bounds, OrthographicCamera camera, String text, String tooltip) {
        this.bounds = new Rectangle(bounds);
        this.camera = camera;
        this.region = region;
        this.regionPressed = null;
        this.setText(text);
        this.setTooltip(tooltip);
        this.ninePatch = null;
    }

    public Button(TextureRegion region, Rectangle bounds, OrthographicCamera camera) {
        this.bounds = new Rectangle(bounds);
        this.camera = camera;
        this.region = region;
        this.regionPressed = null;
        this.ninePatch = null;
    }

    public Button(NinePatch ninePatch, Rectangle bounds, OrthographicCamera camera, String text, String tooltip) {
        this.ninePatch = ninePatch;
        this.region = null;
        this.regionPressed = null;
        this.bounds = new Rectangle(bounds);
        this.camera = camera;
        this.setText(text);
        this.setTooltip(tooltip);
    }

    public Button(NinePatch ninePatch, Rectangle bounds, OrthographicCamera camera) {
        this.bounds = new Rectangle(bounds);
        this.camera = camera;
        this.region = null;
        this.regionPressed = null;
        this.ninePatch = ninePatch;
    }

    public void enable(boolean enable) {
        this.enabled = enable;
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
            Assets.drawString(batch, text, textX, textY, enabled ? textColor : Color.DARK_GRAY, textScale, Assets.font);
        }

        if (selected) {
            if (regionPressed != null) {
                batch.draw(regionPressed, bounds.x, bounds.y, bounds.width, bounds.height);
            } else {
//                Assets.defaultNinePatch.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
                batch.setColor(217f / 255f, 126f / 255f, 0f, 0f);
                batch.draw(Assets.buttonHighlightTexture, bounds.x, bounds.y, bounds.width, bounds.height);
                batch.setColor(1f, 1f, 1f, 1f);
            }
        }

        if (!enabled) {
            highlight(batch, DISABLED_COLOR, DISABLED_ALPHA);
        } else {
            if (isHover && !noHover) {
                batch.setColor(217f / 255f, 126f / 255f, 0f, 0f);
                batch.draw(Assets.buttonHighlightTexture, bounds.x, bounds.y, bounds.width, bounds.height);
                batch.setColor(1f, 1f, 1f, 1f);
//                Assets.defaultNinePatch.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
            }

//            renderTooltip(batch, camera);
        }
    }

    private void highlight(SpriteBatch batch, Color color, float alpha) {
        Color batchColor = batch.getColor();
        batch.setColor(color.r, color.g, color.b, alpha);
        batch.draw(Assets.whitePixel, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.setColor(batchColor);
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
        batch.setColor(Color.WHITE);
        Assets.tooltipNinePatch.draw(batch, backgroundX, backgroundY, tooltipBackgroundWidth, tooltipBackgroundHeight);
        Assets.drawString(batch,
                tooltip,
                stringTX,
                stringTY,
                Color.WHITE,
                TOOLTIP_TEXT_SCALE,
                Assets.font, TOOLTIP_MAX_WIDTH, Align.center);
    }

    public void update(float dt) {
        isHover = enabled && checkForTouch(input.getX(), input.getY());
        if (isHover) {
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
        return  bounds.contains(touchPosScreen.x, touchPosScreen.y);
    }

    public boolean checkForTouchNoUnproject(int x, int y) {
        return bounds.contains(x, y);
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

    public void setText(String text, float xOffset) {
        this.setText(text);
        textX += xOffset;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
        if (tooltip != null) {
            Assets.font.getData().setScale(TOOLTIP_TEXT_SCALE);
            Assets.layout.setText(Assets.font, tooltip, Color.WHITE, TOOLTIP_MAX_WIDTH, Align.center, true);
            tooltipBackgroundHeight = Assets.layout.height + (TOOLTIP_TEXT_PADDING_Y * 2);
            tooltipBackgroundWidth = TOOLTIP_MAX_WIDTH + (TOOLTIP_TEXT_PADDING_X * 2);
            tooltipTextOffsetY = (Assets.layout.height + TOOLTIP_TEXT_PADDING_Y + TOOLTIP_TEXT_OFFSET_Y);
            Assets.font.getData().setScale(1f);
        }
    }

    public void updateTextProperties(float textScale, Color textColor, float textOffsetY) {
        this.textScale = textScale;
        this.textColor = textColor;
        this.textOffsetY = textOffsetY;
    }

    public void select() {
        if (buttonGroup != null) {
            showTooltip = false;
            buttonGroup.select(this);
        }
    }
}
