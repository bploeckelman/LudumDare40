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
import lando.systems.ld40.gameobjects.TileType;
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
                                    if (lastSelectedUpgrade == UpgradeType.DEMOLITION) {
                                        World.GetWorld().replaceTile(building, Building.Type.EMPTY);
                                    } else {
                                        building.applyUpgrade(lastSelectedUpgrade);
                                    }
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
                World.GetWorld().inventory.useTileItem(lastSelectedBuilding);

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

        // Check for addon / tile type inventory button click
        if (isAddon) {
            for (int i = 0; i < inventoryButtons.size; ++i) {
                Button button = inventoryButtons.get(i);
                if (button.checkForTouchNoUnproject(x, y)) {
                    lastSelectedUpgrade = (UpgradeType) inventoryButtons.get(i).meta;
                    if (building.allowsUpgrade(lastSelectedUpgrade)) {
                        buildButton.enable(true);
                    } else {
                        lastSelectedUpgrade = null;
                    }
                }
            }
        } else {
            for (Button button : buildingButtons) {
                if (button.checkForTouchNoUnproject(x, y)) {
                    lastSelectedBuilding = (TileType) button.meta;
                    buildButton.enable(true);
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
        batch.setColor(Color.WHITE);


        final Building building = ((Building) buildAction.selectedObject);
//        if (building.type == Building.Type.EMPTY) {
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

            for (int i = 0; i < buildingButtons.size; i += 2) {
                // Layout and draw button 1 in this row
                Button buildingButton1 = buildingButtons.get(i);
                buildingButton1.bounds.set(
                        inventoryRect.x + button_margin_left,
                        inventoryRect.y + inventoryRect.height - margin_top - (((i / 2) + 1) * button_size) - ((i / 2) * button_spacing_y),
                        button_size, button_size);
                buildingButton1.textScale = 0.38f;
                buildingButton1.setText(buildingButton1.text, button_size + 2f * margin_left); // re-layout text
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
                buildingButton2.textScale = 0.38f;
                buildingButton2.setText(buildingButton2.text, button_size + 2f * margin_left); // re-layout text
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
                        // TODO: take 'counts' into account
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

                // no truck
                if (i+1 >= inventoryButtons.size) continue;

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

        cancelButton.renderTooltip(batch, camera);
        buildButton.renderTooltip(batch, camera);

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
