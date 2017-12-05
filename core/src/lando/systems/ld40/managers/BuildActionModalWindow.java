package lando.systems.ld40.managers;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld40.buildings.Building;
import lando.systems.ld40.gameobjects.Inventory;
import lando.systems.ld40.gameobjects.TileType;
import lando.systems.ld40.gameobjects.UpgradeType;
import lando.systems.ld40.ui.Button;
import lando.systems.ld40.ui.ModalWindow;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.SoundManager;
import lando.systems.ld40.utils.accessors.RectangleAccessor;
import lando.systems.ld40.world.World;

import java.util.Comparator;

public class BuildActionModalWindow extends ModalWindow {

//    private static final Color text_msg_color = new Color(235f / 255f, 208f / 255f, 0f, 1f);
//    private static final Color text_msg_color = new Color(177f / 255f, 185f / 255f, 166f / 255f, 1f);
    private static final Color text_msg_color = new Color(235f / 255f, 255f / 255f, 218f / 255f, 1f);
    private static final Color text_msg_bg_color = new Color(71f / 255f, 71f / 255f, 87f / 255f, 1f);
    private static final Color text_color = new Color(217f / 255f, 126f / 255f, 0f, 1f);
    private static final float text_scale = 0.3f;

    private BuildManager buildAction;
    private Rectangle inventoryRect;
    private Array<Button> inventoryButtons;
    private Array<Button> buildingButtons;
    private UpgradeType lastSelectedUpgrade;
    private TextureRegion lastSelectedUpgradeTexture;
    private TileType lastSelectedBuilding;
    private TextureRegion lastSelectedBuildingTexture;
    private boolean isTweening;
    private boolean isAddon;
    private Rectangle buildTweenRect;
    private Rectangle selectedTileRect;
    private Button buildButton;
    private Button cancelButton;
    private Inventory inventory;

    private boolean isNothingToBuild;
    private String nothingToBuildMsg1;
    private String nothingToBuildMsg2;
    private Rectangle nothingToBuildRect;

    public BuildActionModalWindow(OrthographicCamera camera, BuildManager buildAction) {
        super(camera);
        this.buildAction = buildAction;
        this.isAddon = ((Building) buildAction.selectedObject).type != Building.Type.EMPTY;
        this.inventoryRect = new Rectangle();
        this.inventoryButtons = new Array<Button>(UpgradeType.values().length - 1); // no truck
        for (UpgradeType upgradeType : UpgradeType.values()) {
            if (upgradeType == UpgradeType.TRUCK) continue;
            Button button = new Button(upgradeType.texture, new Rectangle(),
                    camera, upgradeType.shortName, upgradeType.description);
            button.meta = upgradeType;
            inventoryButtons.add(button);
        }
        this.buildingButtons = new Array<Button>(TileType.values().length);
        for (TileType tileType : TileType.values()) {
            Button button = new Button(tileType.texture, new Rectangle(),
                    camera, tileType.shortName, tileType.description);
            button.meta = tileType;
            buildingButtons.add(button);
        }
        this.buildButton = new Button(Assets.whiteNinePatch, new Rectangle(),
                camera, "Build", "Build this addon on this tile");
        this.cancelButton = new Button(Assets.whiteNinePatch, new Rectangle(),
                camera, "Cancel", "Cancel building on this tile");
        this.buildButton.textColor = Color.WHITE;
        this.buildButton.textScale = 0.4f;
        this.buildButton.enable(false);
        this.buildButton.noHover = true;
        this.cancelButton.textColor = Color.WHITE;
        this.cancelButton.textScale = 0.4f;
        this.cancelButton.noHover = true;
        this.lastSelectedUpgrade = null;
        this.buildTweenRect = new Rectangle();
        this.selectedTileRect = null;
        this.isTweening = false;
        this.lastSelectedBuilding = null;
        this.lastSelectedBuildingTexture = null;
        this.inventory = World.GetWorld().inventory;
        this.nothingToBuildMsg1 = "You have nothing that can be built on this tile.";
        this.nothingToBuildMsg2 = "Buy some things in the store after this round.";
        this.isNothingToBuild = false;
        this.nothingToBuildRect = new Rectangle();

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

        if (isAddon) {
            for (Button button : inventoryButtons) {
                button.update(dt);
            }
        } else {
            for (Button button : buildingButtons) {
                button.update(dt);
            }
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
            if (isAddon) {
                if (lastSelectedUpgrade != null) {
                    inventory.useUpgradeItem(lastSelectedUpgrade);

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
                                    if (lastSelectedUpgrade == UpgradeType.DEMOLITION) {
                                        World.GetWorld().replaceTile(building, Building.Type.EMPTY);
                                    } else {
                                        building.applyUpgrade(lastSelectedUpgrade);
                                    }
                                    SoundManager.playSound(SoundManager.SoundOptions.buildAddon);
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

            } else { // isBuilding
                inventory.useTileItem(lastSelectedBuilding);

                for (Button button : buildingButtons) {
                    if (button.meta == lastSelectedBuilding) {
                        buildTweenRect.set(button.bounds);
                        lastSelectedBuildingTexture = button.region;
                        isTweening = true;
                        break;
                    }
                }
                Timeline.createSequence()
                        .push(Tween.to(buildTweenRect, RectangleAccessor.XYWH, 1f)
                                .target(selectedTileRect.x, selectedTileRect.y, selectedTileRect.width, selectedTileRect.height))
                        .pushPause(0.5f)
                        .push(Tween.call(new TweenCallback() {
                            @Override
                            public void onEvent(int i, BaseTween<?> baseTween) {
                                SoundManager.playSound(SoundManager.SoundOptions.buildAddon);
                                World.GetWorld().replaceTile(building, lastSelectedBuilding.toBuildingType());
                                lastSelectedBuildingTexture = null;
                                selectedTileRect = null;
                                isTweening = false;
                                hide();
                            }
                        }))
                        .start(Assets.tween);
            }
        }

        if (isNothingToBuild) {
            buildButton.enable(false);
        }

        // Check for addon / tile type inventory button click
        if (isAddon) {
            if (!isNothingToBuild) {
                for (int i = 0; i < inventoryButtons.size; ++i) {
                    Button button = inventoryButtons.get(i);
                    if (button.checkForTouchNoUnproject(x, y)) {
                        SoundManager.playSound(SoundManager.SoundOptions.clickButton);
                        lastSelectedUpgrade = (UpgradeType) inventoryButtons.get(i).meta;
                        if (building.allowsUpgrade(lastSelectedUpgrade)) {
                            buildButton.enable(true);
                        } else {
                            lastSelectedUpgrade = null;
                        }
                    }
                }
            }
        } else {
            if (!isNothingToBuild) {
                for (Button button : buildingButtons) {
                    if (button.checkForTouchNoUnproject(x, y)) {
                        SoundManager.playSound(SoundManager.SoundOptions.clickButton);
                        lastSelectedBuilding = (TileType) button.meta;
                        buildButton.enable(true);
                    }
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
        batch.setColor(217f / 255f, 126f / 255f, 0f, 1f);
        cancelButton.bounds.set(modalRect.x + modalRect.width / 2f + modal_button_gap / 2f, modalRect.y + margin_top, modal_button_w, modal_button_h);
        cancelButton.setText("Cancel"); // re-layout text
        buildButton.bounds.set(modalRect.x + modalRect.width / 2f - modal_button_gap / 2f - modal_button_w, modalRect.y + margin_top, modal_button_w, modal_button_h);
        buildButton.setText("Build"); // re-layout text
        cancelButton.render(batch);
        buildButton.render(batch);
        batch.setColor(Color.WHITE);


        final Building building = ((Building) buildAction.selectedObject);
        if (!isAddon) {
            // TODO: sort available building options from inventory

            // Layout and Draw building tile buttons
            int num_upgrades = TileType.values().length;
            int num_rows = num_upgrades / 2;

            float button_margin_left = 10f;
            float button_margin_top = 10f;
            float button_spacing_x = (inventoryRect.width - 2f * button_margin_left) / 4f;
            float button_spacing_y = 20f;

            float button_width  = (inventoryRect.width  - 2f * button_margin_left - button_spacing_x) / 2f;
            float button_height = (inventoryRect.height - 2f * button_margin_top - (num_rows - 1) * button_spacing_y) / num_rows;
            float button_size = Math.min(button_width, button_height);

            isNothingToBuild = true;

            for (int i = 0; i < buildingButtons.size; i += 2) {
                // Layout and draw button 1 in this row
                Button buildingButton1 = buildingButtons.get(i);
                buildingButton1.bounds.set(
                        inventoryRect.x + button_margin_left,
                        inventoryRect.y + inventoryRect.height - margin_top - (((i / 2) + 1) * button_size) - ((i / 2) * button_spacing_y),
                        button_size, button_size);
                int building1Count = inventory.getCurrentCountForTile((TileType) buildingButton1.meta);
                if (building1Count > 0) isNothingToBuild = false;
                buildingButton1.enable(building1Count != 0);
                buildingButton1.textScale = text_scale;
                buildingButton1.setText(((TileType) buildingButton1.meta).shortName + "\n  x" + building1Count, button_size + 3f * margin_left + 2f); // re-layout text
                buildingButton1.render(batch);

                // Draw selected highlight
                if (lastSelectedBuilding == buildingButton1.meta) {
                    batch.setColor(235f / 255f, 208f / 255f, 0f, 1f);
                    Assets.defaultNinePatch.draw(batch, buildingButton1.bounds.x, buildingButton1.bounds.y, buildingButton1.bounds.width, buildingButton1.bounds.height);
                    batch.setColor(1f, 1f, 1f, 1f);
                }

                // Layout and draw button 2 in this row
                Button buildingButton2 = buildingButtons.get(i+1);
                buildingButton2.bounds.set(
                        inventoryRect.x + inventoryRect.width / 2f + button_margin_left,
                        inventoryRect.y + inventoryRect.height - margin_top - (((i / 2) + 1) * button_size) - ((i / 2) * button_spacing_y),
                        button_size, button_size);
                int building2Count = inventory.getCurrentCountForTile((TileType) buildingButton2.meta);
                if (building2Count > 0) isNothingToBuild = false;
                buildingButton2.enable(building2Count != 0);
                buildingButton2.textScale = text_scale;
                buildingButton2.setText(((TileType) buildingButton2.meta).shortName + "\n  x" + building2Count, button_size + 3f * margin_left + 2f); // re-layout text
                buildingButton2.textColor = Color.WHITE;
                buildingButton2.render(batch);

                // Draw selected highlight
                if (lastSelectedBuilding == buildingButton2.meta) {
                    batch.setColor(235f / 255f, 208f / 255f, 0f, 1f);
                    Assets.defaultNinePatch.draw(batch, buildingButton2.bounds.x, buildingButton2.bounds.y, buildingButton2.bounds.width, buildingButton2.bounds.height);
                    batch.setColor(1f, 1f, 1f, 1f);
                }
            }

            for (Button button : buildingButtons) {
                button.renderTooltip(batch, camera);
            }

            if (lastSelectedBuildingTexture != null) {
                batch.draw(lastSelectedBuildingTexture, buildTweenRect.x, buildTweenRect.y, buildTweenRect.width, buildTweenRect.height);
            }
        } else { // isAddon
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

            isNothingToBuild = true;

            for (int i = 0; i < inventoryButtons.size; i += 2) {
                // Layout and draw button 1 in this row
                Button inventoryButton1 = inventoryButtons.get(i);
                inventoryButton1.bounds.set(
                        inventoryRect.x + button_margin_left,
                        inventoryRect.y + inventoryRect.height - margin_top - (((i / 2) + 1) * button_size) - ((i / 2) * button_spacing_y),
                        button_size, button_size);
                int upgrade1Count = inventory.getCurrentCountForUpgrade((UpgradeType) inventoryButton1.meta);
                boolean isEnabled1 = building.allowsUpgrade((UpgradeType) inventoryButton1.meta) && upgrade1Count > 0;
                if (isEnabled1) isNothingToBuild = false;
                inventoryButton1.enable(isEnabled1);
                inventoryButton1.textScale = text_scale;
                inventoryButton1.setText(((UpgradeType) inventoryButton1.meta).shortName + "\n  x" + upgrade1Count, button_size + margin_left + 2f); // re-layout text
                inventoryButton1.render(batch);

                // Draw selected highlight
                if (lastSelectedUpgrade == inventoryButton1.meta) {
                    batch.setColor(235f / 255f, 208f / 255f, 0f, 1f);
                    Assets.defaultNinePatch.draw(batch, inventoryButton1.bounds.x, inventoryButton1.bounds.y, inventoryButton1.bounds.width, inventoryButton1.bounds.height);
                    batch.setColor(1f, 1f, 1f, 1f);
                }

                // no truck
                if (i+1 >= inventoryButtons.size) continue;

                // Layout and draw button 2 in this row
                Button inventoryButton2 = inventoryButtons.get(i+1);
                inventoryButton2.bounds.set(
                        inventoryRect.x + inventoryRect.width / 2f + button_margin_left,
                        inventoryRect.y + inventoryRect.height - margin_top - (((i / 2) + 1) * button_size) - ((i / 2) * button_spacing_y),
                        button_size, button_size);
                int upgrade2Count = inventory.getCurrentCountForUpgrade((UpgradeType) inventoryButton2.meta);
                boolean isEnabled2 = building.allowsUpgrade((UpgradeType) inventoryButton2.meta) && upgrade2Count > 0;
                inventoryButton2.enable(isEnabled2);
                if (isEnabled2) isNothingToBuild = false;
                inventoryButton2.textScale = text_scale;
                inventoryButton2.setText(((UpgradeType) inventoryButton2.meta).shortName + "\n  x" + upgrade2Count, button_size + margin_left + 2f); // re-layout text
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

        cancelButton.renderTooltip(batch, camera);
        buildButton.renderTooltip(batch, camera);

        if (isNothingToBuild && !isTweening) {
            final float rect_w = (2f / 3f) * modalRect.width;
            final float rect_h = (1f / 3f) * modalRect.height;
            nothingToBuildRect.set(
                    modalRect.x + modalRect.width  / 2f - rect_w / 2f,
                    modalRect.y + modalRect.height / 2f - rect_h / 2f,
                    rect_w, rect_h);

            batch.setColor(text_msg_bg_color);
            Assets.whiteNinePatch.draw(batch, nothingToBuildRect.x, nothingToBuildRect.y, nothingToBuildRect.width, nothingToBuildRect.height);
            batch.setColor(Color.WHITE);

            batch.setShader(Assets.fontShader);
            {
                final float nothing_text_target_width = nothingToBuildRect.width - 2f * margin_left;
                final float nothing_text_scale = 0.5f;
                Assets.font.getData().setScale(nothing_text_scale);
                Assets.fontShader.setUniformf("u_scale", nothing_text_scale);

                Assets.layout.setText(Assets.font, nothingToBuildMsg1, text_msg_color, nothing_text_target_width, Align.center, true);
                final float line1Y = nothingToBuildRect.y + nothingToBuildRect.height - 2f * margin_top;
                Assets.font.draw(batch, Assets.layout, nothingToBuildRect.x + margin_left, line1Y);

                final float line2Y = line1Y - Assets.layout.height - 2f * margin_top;
                Assets.layout.setText(Assets.font, nothingToBuildMsg2, text_msg_color, nothing_text_target_width, Align.center, true);
                Assets.font.draw(batch, Assets.layout, nothingToBuildRect.x + margin_left, line2Y);

                Assets.font.setColor(Color.WHITE);
                Assets.font.getData().setScale(1f);
                Assets.fontShader.setUniformf("u_scale", 1f);
            }
            batch.setShader(null);
        }

        // NOTE: 0,0 is top left instead of bottom for text
        batch.setShader(Assets.fontShader);
        {
            final float title_text_scale = 0.5f;
            final float target_width = modalRect.width;
            Assets.font.getData().setScale(title_text_scale);
            Assets.fontShader.setUniformf("u_scale", title_text_scale);
            Assets.layout.setText(Assets.font, "Pick an item to build on this tile...",
                    text_color, target_width, Align.center, true);
            Assets.font.draw(batch, Assets.layout,
                    modalRect.x + margin_left,
                    modalRect.y + modalRect.height - margin_top);
            Assets.font.setColor(Color.WHITE);
            Assets.font.getData().setScale(1f);
            Assets.fontShader.setUniformf("u_scale", 1f);
        }
        batch.setShader(null);
    }

}
