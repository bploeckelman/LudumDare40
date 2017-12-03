package lando.systems.ld40.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld40.LudumDare40;
import lando.systems.ld40.ui.Button;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.Config;
import lando.systems.ld40.world.World;


/**
 * Created by Brian on 12/2/2017.
 */
public class ResolutionPhaseScreen extends BaseScreen {
    private World world;

    Button bBuildings;
    Button bAddons;
    Button bResearch;
    Button bContinue;
    Button purchaseUpgradeButton;

    Rectangle rectHeadBut1;
    Rectangle rectHeadBut2;
    Rectangle rectHeadBut3;

    Rectangle rectButBox;
    Rectangle rectInfoBox;

    Rectangle rectButContinueBox;

    Vector3 touchPos;

    public class UpgradeItem {
        public int type;
        public String description;
        public TextureRegion buttonTexture;
        public TextureRegion upgradeTexture;
        public Button button;
        public int currentLevel;
        public int maxLevel;
    }

    public enum ItemGroups {
        None,
        Building,
        Addon,
        Research
    }

    public ItemGroups selectedGroup;

    Array<UpgradeButton> buildingsButtons;

    UpgradeButton currentUpgrade;

    public ResolutionPhaseScreen() {
        buildingsButtons = new Array<UpgradeButton>();
        world = World.GetWorld();
        Gdx.input.setInputProcessor(this);
        camera.zoom = 2.5f;
        camera.position.x = 500;
        camera.position.y = 500;

        touchPos = new Vector3();

        float fScreenWidth = hudCamera.viewportWidth;
        float fScreenHeight = hudCamera.viewportHeight;

        rectHeadBut1 = new Rectangle(fScreenWidth / 40, fScreenHeight * 0.75f, fScreenWidth * 0.3f, fScreenHeight / 12);
        bBuildings = new Button(Assets.defaultNinePatch, rectHeadBut1, hudCamera, "Buildings", null);
        bBuildings.textColor = Color.BLACK;
        rectHeadBut2 = new Rectangle((fScreenWidth * 2 / 40) + rectHeadBut1.width, fScreenHeight * 0.75f, fScreenWidth * 0.3f, fScreenHeight / 12);
        bAddons = new Button(Assets.defaultNinePatch, rectHeadBut2, hudCamera, "Add-Ons", null);
        bAddons.textColor = Color.BLACK;
        rectHeadBut3 = new Rectangle((fScreenWidth * 3 / 40) + rectHeadBut1.width * 2, fScreenHeight * 0.75f, fScreenWidth * 0.3f, fScreenHeight / 12);
        bResearch = new Button(Assets.defaultNinePatch, rectHeadBut3, hudCamera, "Research", null);
        bResearch.textColor = Color.BLACK;

        rectButBox = new Rectangle(20, fScreenHeight * 0.15f, fScreenWidth * 0.55f, fScreenHeight * 0.55f);
        rectInfoBox = new Rectangle(rectButBox.x + rectButBox.width + fScreenWidth * 0.05f, fScreenHeight * 0.15f, fScreenWidth * 0.35f, 330);

        purchaseUpgradeButton = new Button(Assets.defaultNinePatch, new Rectangle(rectInfoBox.x + 10, rectInfoBox.y + 10, rectInfoBox.width - 20, 50),
                hudCamera, "Upgrade", null);

        initBuildingsUpgrades();

        rectButContinueBox = new Rectangle(fScreenWidth * 0.75f, fScreenHeight / 30, fScreenWidth * 0.225f, fScreenHeight / 12);
        bContinue = new Button(Assets.defaultNinePatch, rectButContinueBox, hudCamera, "Next Day", null);
        bContinue.textColor = Color.BLACK;

        selectedGroup = ItemGroups.None;
    }

    void initBuildingsUpgrades()
    {
        UpgradeButton bUpgrade1 = new UpgradeButton();
        bUpgrade1.name = "Low Residential";
        bUpgrade1.description = "LOW RESIDENTIAL STATS";
        bUpgrade1.picture = Assets.atlas.findRegion("res-low");
        bUpgrade1.cost = 10;

        UpgradeButton bUpgrade2 = new UpgradeButton();
        bUpgrade2.name = "Low Commercial";
        bUpgrade2.description = "LOW COMMERCIAL STATS";
        bUpgrade2.picture = Assets.atlas.findRegion("com-low");
        bUpgrade2.cost = 20;

        buildingsButtons.add(bUpgrade1);
        buildingsButtons.add(bUpgrade2);
    }

    @Override
    public void update(float dt) {
        updateWorld(dt);
        updateObjects(dt);
        updateCamera(dt);
    }

    private void updateWorld(float dt) {
        world.update(dt);
    }

    private void updateObjects(float dt) {
        if (Gdx.input.justTouched()) {
            hudCamera.unproject(touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0f));

            int touchX = (int) touchPos.x;
            int touchY = (int) hudCamera.viewportHeight - (int) touchPos.y;

            if (bBuildings.checkForTouch(touchX, touchY)) {
                selectedGroup = ItemGroups.Building;
            }
            else if (bAddons.checkForTouch(touchX, touchY)) {
                selectedGroup = ItemGroups.Addon;
            }
            else if (bResearch.checkForTouch(touchX, touchY)) {
                selectedGroup = ItemGroups.Research;
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.A))
        {
            currentUpgrade = buildingsButtons.get(0);
        }
        else if(Gdx.input.isKeyJustPressed(Input.Keys.S))
        {
            currentUpgrade = buildingsButtons.get(1);
        }
    }

    private void updateCamera(float dt) {
        camera.update();
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            renderHud(batch);

            // Draw screen background
            //batch.draw(Assets.whitePixel, 0, 0, hudCamera.viewportWidth, hudCamera.viewportHeight);

            // Draw section backgrounds
            //batch.setColor(64f / 255f, 64f / 255f, 64f / 255f, 0.9f);

            // Draw header buttons
            batch.setColor(selectedGroup == ItemGroups.Building ? Color.WHITE : Color.LIGHT_GRAY);
            batch.draw(Assets.whitePixel, rectHeadBut1.x, rectHeadBut1.y, rectHeadBut1.width, rectHeadBut1.height);
            bBuildings.render(batch);
            batch.setColor(selectedGroup == ItemGroups.Addon ? Color.WHITE : Color.LIGHT_GRAY);
            batch.draw(Assets.whitePixel, rectHeadBut2.x, rectHeadBut2.y, rectHeadBut2.width, rectHeadBut2.height);
            bAddons.render(batch);
            batch.setColor(selectedGroup == ItemGroups.Research ? Color.WHITE : Color.LIGHT_GRAY);
            batch.draw(Assets.whitePixel, rectHeadBut3.x, rectHeadBut3.y, rectHeadBut3.width, rectHeadBut3.height);
            bResearch.render(batch);
            batch.setColor(Color.WHITE);

            // Draw header text


            // Draw upgrade item buttons
            batch.draw(Assets.whitePixel, rectButBox.x, rectButBox.y, rectButBox.width, rectButBox.height);
            batch.draw(Assets.whitePixel, rectInfoBox.x, rectInfoBox.y, rectInfoBox.width, rectInfoBox.height);

            // Draw info section details


            // Draw continue button
            batch.draw(Assets.whitePixel, rectButContinueBox.x, rectButContinueBox.y, rectButContinueBox.width, rectButContinueBox.height);

            //Render upgrade information
            if(currentUpgrade != null)
            {
                Assets.drawString(batch, currentUpgrade.name, rectInfoBox.x, rectInfoBox.y + rectInfoBox.height - 20,
                        Color.BLACK, 0.5f, Assets.font, rectInfoBox.width, Align.center);
                batch.draw(currentUpgrade.picture, rectInfoBox.x + 80, rectInfoBox.y + rectInfoBox.height - 180);
                Assets.drawString(batch, currentUpgrade.description, rectInfoBox.x, rectInfoBox.y + rectInfoBox.height - 200,
                        Color.BLACK, 0.25f, Assets.font, rectInfoBox.width, Align.center);
                Assets.drawString(batch, "Cost: " + currentUpgrade.cost, rectInfoBox.x, rectInfoBox.y + 100,
                        Color.BLACK, 0.25f, Assets.font, rectInfoBox.width, Align.center);
                purchaseUpgradeButton.render(batch);
            }
            bContinue.render(batch);


            // Screen transition overlay

        }
        batch.end();
    }

    private void renderWorld(SpriteBatch batch) {
        batch.setColor(Color.WHITE);
        world.render(batch);
    }

    private void renderObjects(SpriteBatch batch) {
        // todo
    }

    private void renderHud(SpriteBatch batch) {
        batch.setColor(Color.LIGHT_GRAY);
        batch.draw(Assets.whitePixel, 10, 10, camera.viewportWidth - 20, 50);
        batch.setColor(Color.WHITE);
        Assets.drawString(batch, "Resolution Phase", 20f, 45f, Color.GOLD, 0.5f, Assets.font);
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {

        return true;
    }

    class UpgradeButton
    {
        public String description;
        public String name;
        public TextureRegion picture;
        public Button button;
        public int cost;
        public int quantity;
    }
}

