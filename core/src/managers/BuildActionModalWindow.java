package managers;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld40.buildings.Building;
import lando.systems.ld40.gameobjects.Inventory;
import lando.systems.ld40.gameobjects.UpgradeType;
import lando.systems.ld40.ui.Button;
import lando.systems.ld40.ui.ModalWindow;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.accessors.RectangleAccessor;
import lando.systems.ld40.world.World;

import java.util.Comparator;

public class BuildActionModalWindow extends ModalWindow {

    private BuildManager buildAction;
    private Inventory inventory;
    private Rectangle inventoryRect;
    private Array<Button> inventoryButtons;

    public BuildActionModalWindow(OrthographicCamera camera, BuildManager buildAction) {
        super(camera);
        this.buildAction = buildAction;
        this.inventoryRect = new Rectangle();
        this.inventory = World.GetWorld().inventory;
        this.inventoryButtons = new Array<Button>(UpgradeType.values().length);
        for (UpgradeType upgradeType : UpgradeType.values()) {
            Button button = new Button(upgradeType.texture, new Rectangle(),
                    camera, upgradeType.shortName, upgradeType.description);
            button.meta = upgradeType;
            inventoryButtons.add(button);
        }
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        for (Button button : inventoryButtons) {
            button.update(dt);
        }
    }

    @Override
    public void hide() {
        if (!isActive) return;
        showText = false;

        float modal_target_x = camera.viewportWidth / 2f;
        float modal_target_y = camera.viewportHeight / 2f;
        Tween.to(modalRect, RectangleAccessor.XYWH, 0.2f)
                .target(modal_target_x, modal_target_y, 0f, 0f)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        buildAction.complete();
                        isActive = false;
                    }
                })
                .start(Assets.tween);
    }

    @Override
    protected void renderWindowContents(SpriteBatch batch) {
        if (buildAction == null || !showText) return;

        if (buildAction.selectedObject == null) return;
        // Draw 'selected' building / tile
        float tile_size = modalRect.width / 2f - 2f * margin_left;
        buildAction.selectedObject.render(batch,
                modalRect.x + margin_left,
                modalRect.y + modalRect.height / 2f - tile_size / 2f,
                tile_size, tile_size);

        // Draw inventory border
        inventoryRect.set(
                modalRect.x + modalRect.width - margin_left - tile_size,
                modalRect.y + modalRect.height / 2f - tile_size / 2f,
                tile_size, tile_size);
        Assets.defaultNinePatch.draw(batch, inventoryRect.x, inventoryRect.y, inventoryRect.width, inventoryRect.height);

        final Building building = ((Building) buildAction.selectedObject);
        if (building.type == Building.Type.EMPTY) {

            // TODO: sort, layout, and show available building options from inventory

        } else {
            // Sort, layout, and show available building options from inventory
            inventoryButtons.sort(new Comparator<Button>() {
                @Override
                public int compare(Button button1, Button button2) {
                    // If the buttons are the same, they are the same
                    if (button1.meta == button2.meta) {
                        return 0;
                    } else if (building.allowsUpgrade((UpgradeType) button1.meta)
                           && !building.allowsUpgrade((UpgradeType) button2.meta)) {
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
