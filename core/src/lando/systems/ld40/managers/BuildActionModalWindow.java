package lando.systems.ld40.managers;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Circ;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld40.buildings.Building;
import lando.systems.ld40.gameobjects.UpgradeType;
import lando.systems.ld40.ui.Button;
import lando.systems.ld40.ui.ModalWindow;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.accessors.RectangleAccessor;
import lando.systems.ld40.world.World;

import java.util.Comparator;

public class BuildActionModalWindow extends ModalWindow {

    private static final Color highlight_color = new Color();

    private BuildManager buildAction;
    private Rectangle inventoryRect;
    private Array<Button> inventoryButtons;
    private UpgradeType lastSelectedUpgrade;
    private TextureRegion lastSelectedUpgradeTexture;
    private boolean isTweening;
    private Rectangle buildTweenRect;
    private Rectangle selectedTileRect;
    private Button buildButton;
    private Button cancelButton;

    public BuildActionModalWindow(OrthographicCamera camera, BuildManager buildAction) {
        super(camera);
        this.buildAction = buildAction;
        this.inventoryRect = new Rectangle();
        this.inventoryButtons = new Array<Button>(UpgradeType.values().length);
        for (UpgradeType upgradeType : UpgradeType.values()) {
            Button button = new Button(upgradeType.texture, new Rectangle(),
                    camera, upgradeType.shortName, upgradeType.description);
            button.meta = upgradeType;
            inventoryButtons.add(button);
        }
        this.buildButton = new Button(Assets.buttonBackgroundTexture, new Rectangle(),
                camera, "Build", "Build this addon on this tile");
        this.cancelButton = new Button(Assets.buttonBackgroundTexture, new Rectangle(),
                camera, "Cancel", "Cancel building on this tile");
        this.buildButton.textColor = Color.WHITE;
        this.buildButton.textScale = 0.4f;
        this.buildButton.enable(false);
        this.cancelButton.textColor = Color.WHITE;
        this.cancelButton.textScale = 0.4f;
        this.lastSelectedUpgrade = null;
        this.buildTweenRect = new Rectangle();
        this.selectedTileRect = null;
        this.isTweening = false;
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        if (showText && selectedTileRect == null) {
            float tile_size = modalRect.width / 2f - 2f * margin_left;
            selectedTileRect = new Rectangle(
                    modalRect.x + margin_left,
                    modalRect.y + modalRect.height / 2f - tile_size / 2f,
                    tile_size, tile_size);
        }

        for (int i = 0; i < inventoryButtons.size; ++i) {
            Button button = inventoryButtons.get(i);
            button.update(dt);
        }
        cancelButton.update(dt);
        buildButton.update(dt);
    }

    @Override
    public void handleTouch(float windowX, float windowY) {
        if (isTweening) return;

        int x = (int) windowX;
        int y = (int) windowY;
        final Building building = (Building) buildAction.selectedObject;

        if (cancelButton.checkForTouchNoUnproject(x, y)) {
            selectedTileRect = null;
            hide();
        }

        if (buildButton.checkForTouchNoUnproject(x, y)) {
            if (lastSelectedUpgrade != null) {
                World.GetWorld().inventory.useUpgradeItem(lastSelectedUpgrade);

                for (Button button : inventoryButtons) {
                    if (button.meta == lastSelectedUpgrade) {
                        buildTweenRect.set(button.bounds);
                        lastSelectedUpgradeTexture = button.region;
                        isTweening = true;
                        break;
                    }
                }
                Timeline.createSequence()
                        .push(Tween.to(buildTweenRect, RectangleAccessor.XYWH, 1f)
                                   .target(selectedTileRect.x, selectedTileRect.y, selectedTileRect.width, selectedTileRect.height))
                        .pushPause(0.2f)
                        .push(Tween.to(buildTweenRect, RectangleAccessor.XYWH, 0.5f)
                                .target(selectedTileRect.x + selectedTileRect.width / 2f, selectedTileRect.y + selectedTileRect.height / 2f, 0f, 0f))
                        .push(Tween.call(new TweenCallback() {
                            @Override
                            public void onEvent(int i, BaseTween<?> baseTween) {
                                building.applyUpgrade(lastSelectedUpgrade);
                            }
                        }))
                        .pushPause(0.5f)
                        .push(Tween.call(new TweenCallback() {
                            @Override
                            public void onEvent(int i, BaseTween<?> baseTween) {
                                lastSelectedUpgradeTexture = null;
                                selectedTileRect = null;
                                isTweening = false;
                                hide();
                            }
                        }))
                        .start(Assets.tween);
            }
        }

        for (int i = 0; i < inventoryButtons.size; ++i) {
            Button button = inventoryButtons.get(i);
            if (button.checkForTouchNoUnproject(x, y))  {
                lastSelectedUpgrade = (UpgradeType) inventoryButtons.get(i).meta;
                if (building.allowsUpgrade(lastSelectedUpgrade)) {
                    buildButton.enable(true);
                    // TODO: do anything else here?
                } else {
                    lastSelectedUpgrade = null;
                }
            }
        }
    }

    @Override
    protected void renderWindowContents(SpriteBatch batch) {
        if (buildAction == null || !showText) return;
        if (buildAction.selectedObject == null) return;

        // Draw 'selected' building / tile
        buildAction.selectedObject.render(batch, selectedTileRect.x, selectedTileRect.y, selectedTileRect.width, selectedTileRect.height);

        // Draw inventory border
        inventoryRect.set(
                modalRect.x + modalRect.width - margin_left - selectedTileRect.width,
                modalRect.y + modalRect.height / 2f - selectedTileRect.height / 2f,
                selectedTileRect.width, selectedTileRect.height);
        Assets.defaultNinePatch.draw(batch, inventoryRect.x, inventoryRect.y, inventoryRect.width, inventoryRect.height);

        // Draw modal buttons
        float modal_button_w = 200f;
        float modal_button_h = 40f;
        float modal_button_gap = 40f;
        batch.setColor(Color.RED);
        cancelButton.bounds.set(modalRect.x + modalRect.width / 2f + modal_button_gap / 2f, modalRect.y + margin_top, modal_button_w, modal_button_h);
        cancelButton.setText("Cancel"); // re-layout text
        buildButton.bounds.set(modalRect.x + modalRect.width / 2f - modal_button_gap / 2f - modal_button_w, modalRect.y + margin_top, modal_button_w, modal_button_h);
        buildButton.setText("Build"); // re-layout text
        cancelButton.render(batch);
        buildButton.render(batch);
        cancelButton.renderTooltip(batch, camera);
        buildButton.renderTooltip(batch, camera);
        batch.setColor(Color.WHITE);


        final Building building = ((Building) buildAction.selectedObject);
        if (building.type == Building.Type.EMPTY) {

            // TODO: sort, layout, and show available building options from inventory

        } else {
            // Sort, layout, and show available building options from inventory
            inventoryButtons.sort(new Comparator<Button>() {
                @Override
                public int compare(Button button1, Button button2) {
                    UpgradeType button1UpgradeType = (UpgradeType) button1.meta;
                    UpgradeType button2UpgradeType = (UpgradeType) button2.meta;

                    if (button1.meta == button2.meta) {
                        return 0;
                    } else if (building.allowsUpgrade(button1UpgradeType)
                           && !building.allowsUpgrade(button2UpgradeType)) {
//                           &&  World.GetWorld().inventory.getCurrentCountForUpgrade(button1UpgradeType) > 0) {
                        return -1;
                    } else return 1;
                }
            });

            // Layout and Draw inventory item buttons
            int num_upgrades = UpgradeType.values().length;
            int num_rows = num_upgrades / 2;

            float button_margin_left = 10f;
            float button_margin_top = 10f;
            float button_spacing_x = (inventoryRect.width - 2f * button_margin_left) / 4f;
            float button_spacing_y = 20f;

            float button_width  = (inventoryRect.width  - 2f * button_margin_left - button_spacing_x) / 2f;
            float button_height = (inventoryRect.height - 2f * button_margin_top - (num_rows - 1) * button_spacing_y) / num_rows;
            float button_size = Math.min(button_width, button_height);

            for (int i = 0; i < inventoryButtons.size; i += 2) {
                // Layout and draw button 1 in this row
                Button inventoryButton1 = inventoryButtons.get(i);
                inventoryButton1.bounds.set(
                        inventoryRect.x + button_margin_left,
                        inventoryRect.y + inventoryRect.height - margin_top - (((i / 2) + 1) * button_size) - ((i / 2) * button_spacing_y),
                        button_size, button_size);
                boolean isEnabled1 = building.allowsUpgrade((UpgradeType) inventoryButton1.meta);
                inventoryButton1.enable(isEnabled1);
                inventoryButton1.textScale = 0.38f;
                inventoryButton1.setText(inventoryButton1.text, button_size + 2f * margin_left); // re-layout text
                inventoryButton1.render(batch);

                // Draw selected highlight
                if (lastSelectedUpgrade == inventoryButton1.meta) {
                    batch.setColor(235f / 255f, 208f / 255f, 0f, 1f);
                    Assets.defaultNinePatch.draw(batch, inventoryButton1.bounds.x, inventoryButton1.bounds.y, inventoryButton1.bounds.width, inventoryButton1.bounds.height);
                    batch.setColor(1f, 1f, 1f, 1f);
                }

                // Layout and draw button 2 in this row
                Button inventoryButton2 = inventoryButtons.get(i+1);
                inventoryButton2.bounds.set(
                        inventoryRect.x + inventoryRect.width / 2f + button_margin_left,
                        inventoryRect.y + inventoryRect.height - margin_top - (((i / 2) + 1) * button_size) - ((i / 2) * button_spacing_y),
                        button_size, button_size);
                boolean isEnabled2 = building.allowsUpgrade((UpgradeType) inventoryButton2.meta);
                inventoryButton2.enable(isEnabled2);
                inventoryButton2.textScale = 0.38f;
                inventoryButton2.setText(inventoryButton2.text, button_size + 2f * margin_left); // re-layout text
                inventoryButton2.textColor = Color.WHITE;
                inventoryButton2.render(batch);

                // Draw selected highlight
                if (lastSelectedUpgrade == inventoryButton2.meta) {
                    batch.setColor(235f / 255f, 208f / 255f, 0f, 1f);
                    Assets.defaultNinePatch.draw(batch, inventoryButton2.bounds.x, inventoryButton2.bounds.y, inventoryButton2.bounds.width, inventoryButton2.bounds.height);
                    batch.setColor(1f, 1f, 1f, 1f);
                }
            }

            for (Button button : inventoryButtons) {
                button.renderTooltip(batch, camera);
            }

            if (lastSelectedUpgradeTexture != null) {
                batch.draw(lastSelectedUpgradeTexture, buildTweenRect.x, buildTweenRect.y, buildTweenRect.width, buildTweenRect.height);
            }
        }

        // NOTE: 0,0 is top left instead of bottom for text
        batch.setShader(Assets.fontShader);
        {
            final float title_text_scale = 0.5f;
            final float target_width = modalRect.width;
            Assets.font.getData().setScale(title_text_scale);
            Assets.fontShader.setUniformf("u_scale", title_text_scale);
            Assets.layout.setText(Assets.font, "Pick an item to build on this tile...",
                    Color.GOLD, target_width, Align.center, true);
            Assets.font.draw(batch, Assets.layout,
                    modalRect.x + margin_left,
                    modalRect.y + modalRect.height - margin_top);
        }
        batch.setShader(null);
    }

}
