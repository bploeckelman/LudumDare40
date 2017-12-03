package lando.systems.ld40.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
import lando.systems.ld40.world.Statistics;
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
        Building,
        Addon,
        Research
    }

    public ItemGroups selectedGroup;

    Array<UpgradeButton> buildingsButtons;
    Array<UpgradeButton> addOnButtons;
    Array<UpgradeButton> researchButtons;

    UpgradeButton currentUpgrade;

    public ResolutionPhaseScreen() {
        world = World.GetWorld();
//        Gdx.input.setInputProcessor(this);
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
                hudCamera, "Purchase", null);
        purchaseUpgradeButton.textColor = Color.BLACK;

        rectButContinueBox = new Rectangle(fScreenWidth * 0.75f, fScreenHeight / 30, fScreenWidth * 0.225f, fScreenHeight / 12);
        bContinue = new Button(Assets.defaultNinePatch, rectButContinueBox, hudCamera, "Next Day", null);
        bContinue.textColor = Color.BLACK;

        selectedGroup = ItemGroups.Building;

        //Initialize upgrade buttons (and populate the arrays)
        researchButtons = new Array<UpgradeButton>();
        addOnButtons = new Array<UpgradeButton>();
        buildingsButtons = new Array<UpgradeButton>();

        initBuildingsUpgrades();
        initAddOnButtons();
        initResearchUpgrades();
    }

    void initAddOnButtons()
    {
        UpgradeButton aUpgrade1 = new UpgradeButton();
        aUpgrade1.name = "Tier Token";
        aUpgrade1.description = "Token used to make buildings go up a tier";
        aUpgrade1.picture = Assets.atlas.findRegion("tile-128");
        aUpgrade1.cost = 100;
        aUpgrade1.group = ItemGroups.Addon;

        UpgradeButton aUpgrade2 = new UpgradeButton();
        aUpgrade2.name = "Dumpster";
        aUpgrade2.description = "Where trash goes. Place on any I, C, R";
        aUpgrade2.picture = Assets.atlas.findRegion("newdumpster");
        aUpgrade2.cost = 500;
        aUpgrade2.group = ItemGroups.Addon;

        UpgradeButton aUpgrade3 = new UpgradeButton();
        aUpgrade3.name = "Incinerator";
        aUpgrade3.description = "Used to burn trash. Place on any landfill";
        aUpgrade3.picture = Assets.atlas.findRegion("incinerator");
        aUpgrade3.cost = 1000;
        aUpgrade3.group = ItemGroups.Addon;

        UpgradeButton aUpgrade4 = new UpgradeButton();
        aUpgrade4.name = "Green Token";
        aUpgrade4.description = "Green certified! Reduces trash output. Place on any I, C, R";
        aUpgrade4.picture = Assets.atlas.findRegion("leaf-green");
        aUpgrade4.cost = 200;
        aUpgrade4.group = ItemGroups.Addon;

        UpgradeButton aUpgrade5 = new UpgradeButton();
        aUpgrade5.name = "Recycling Plant";
        aUpgrade5.description = "Recycle your trash. Place on any landfill";
        aUpgrade5.picture = Assets.atlas.findRegion("recycle");
        aUpgrade5.cost = 5000;
        aUpgrade5.group = ItemGroups.Addon;

        UpgradeButton aUpgrade6 = new UpgradeButton();
        aUpgrade6.name = "Compactor";
        aUpgrade6.description = "Compact your trash. Place on any landfill";
        aUpgrade6.picture = Assets.atlas.findRegion("compactor");
        aUpgrade6.cost = 10000;
        aUpgrade6.group = ItemGroups.Addon;

        addOnButtons.add(aUpgrade1);
        addOnButtons.add(aUpgrade2);
        addOnButtons.add(aUpgrade3);
        addOnButtons.add(aUpgrade4);
        addOnButtons.add(aUpgrade5);
        addOnButtons.add(aUpgrade6);

        createButtons(ItemGroups.Addon);
    }

    void initResearchUpgrades()
    {
        UpgradeButton rUpgrade1 = new UpgradeButton();
        rUpgrade1.name = "Incineration";
        rUpgrade1.description = "This research allows for purchase of incinerator";
        rUpgrade1.picture = Assets.atlas.findRegion("incinerator");
        rUpgrade1.cost = 20000000;
        rUpgrade1.group = ItemGroups.Research;

        UpgradeButton rUpgrade2 = new UpgradeButton();
        rUpgrade2.name = "Compaction";
        rUpgrade2.description = "This research allows for purchase of compactor";
        rUpgrade2.picture = Assets.atlas.findRegion("compactor");
        rUpgrade2.cost = 50000000;
        rUpgrade2.group = ItemGroups.Research;

        UpgradeButton rUpgrade3 = new UpgradeButton();
        rUpgrade3.name = "Recyclation";
        rUpgrade3.description = "This research allows for purchase of recycling plate";
        rUpgrade3.picture = Assets.atlas.findRegion("recycle");
        rUpgrade3.cost = 100000000;
        rUpgrade3.group = ItemGroups.Research;

        UpgradeButton rUpgrade4 = new UpgradeButton();
        rUpgrade4.name = "Truck Speed";
        rUpgrade4.description = "Get a faster truck (vroom, vroom)";
        rUpgrade4.picture = Assets.atlas.findRegion("tile-128");
        rUpgrade4.cost = 100;
        rUpgrade4.group = ItemGroups.Research;

        UpgradeButton rUpgrade5 = new UpgradeButton();
        rUpgrade5.name = "Truck Capacity";
        rUpgrade5.description = "Get a truck that can pick more shit up";
        rUpgrade5.picture = Assets.atlas.findRegion("tile-128");
        rUpgrade5.cost = 500;
        rUpgrade5.group = ItemGroups.Research;

        researchButtons.add(rUpgrade1);
        researchButtons.add(rUpgrade2);
        researchButtons.add(rUpgrade3);
        researchButtons.add(rUpgrade4);
        researchButtons.add(rUpgrade5);

        createButtons(ItemGroups.Research);
    }

    void initBuildingsUpgrades()
    {
        UpgradeButton bUpgrade1 = new UpgradeButton();
        bUpgrade1.name = "Low Residential";
        bUpgrade1.description = "LOW RESIDENTIAL STATS";
        bUpgrade1.picture = Assets.atlas.findRegion("res-low");
        bUpgrade1.cost = 10;
        bUpgrade1.group = ItemGroups.Building;

        UpgradeButton bUpgrade2 = new UpgradeButton();
        bUpgrade2.name = "Low Commercial";
        bUpgrade2.description = "LOW COMMERCIAL STATS";
        bUpgrade2.picture = Assets.atlas.findRegion("com-low");
        bUpgrade2.cost = 10;
        bUpgrade2.group = ItemGroups.Building;

        UpgradeButton bUpgrade3 = new UpgradeButton();
        bUpgrade3.name = "Low Industrial";
        bUpgrade3.description = "LOW INDUSTRIAL STATS";
        bUpgrade3.picture = Assets.atlas.findRegion("ind-low");
        bUpgrade3.cost = 10;
        bUpgrade3.group = ItemGroups.Building;

        UpgradeButton bUpgrade4 = new UpgradeButton();
        bUpgrade4.name = "Medium Residential";
        bUpgrade4.description = "MEDIUM RESIDENTIAL STATS";
        bUpgrade4.picture = Assets.atlas.findRegion("res-med");
        bUpgrade4.cost = 20;
        bUpgrade4.group = ItemGroups.Building;

        UpgradeButton bUpgrade5 = new UpgradeButton();
        bUpgrade5.name = "Medium Commercial";
        bUpgrade5.description = "MEDIUM COMMERCIAL STATS";
        bUpgrade5.picture = Assets.atlas.findRegion("com-med");
        bUpgrade5.cost = 20;
        bUpgrade5.group = ItemGroups.Building;

        UpgradeButton bUpgrade6 = new UpgradeButton();
        bUpgrade6.name = "Medium Industrial";
        bUpgrade6.description = "MEDIUM INDUSTRIAL STATS";
        bUpgrade6.picture = Assets.atlas.findRegion("ind-med");
        bUpgrade6.cost = 20;
        bUpgrade6.group = ItemGroups.Building;

        UpgradeButton bUpgrade7 = new UpgradeButton();
        bUpgrade7.name = "High Residential";
        bUpgrade7.description = "HIGH RESIDENTIAL STATS";
        bUpgrade7.picture = Assets.atlas.findRegion("res-high");
        bUpgrade7.cost = 30;
        bUpgrade7.group = ItemGroups.Building;

        UpgradeButton bUpgrade8 = new UpgradeButton();
        bUpgrade8.name = "High Commercial";
        bUpgrade8.description = "HIGH COMMERCIAL STATS";
        bUpgrade8.picture = Assets.atlas.findRegion("com-high");
        bUpgrade8.cost = 30;
        bUpgrade8.group = ItemGroups.Building;

        UpgradeButton bUpgrade9 = new UpgradeButton();
        bUpgrade9.name = "High Industrial";
        bUpgrade9.description = "HIGH INDUSTRIAL STATS";
        bUpgrade9.picture = Assets.atlas.findRegion("ind-high");
        bUpgrade9.cost = 30;
        bUpgrade9.group = ItemGroups.Building;

        buildingsButtons.add(bUpgrade1);
        buildingsButtons.add(bUpgrade2);
        buildingsButtons.add(bUpgrade3);
        buildingsButtons.add(bUpgrade4);
        buildingsButtons.add(bUpgrade5);
        buildingsButtons.add(bUpgrade6);
        buildingsButtons.add(bUpgrade7);
        buildingsButtons.add(bUpgrade8);
        buildingsButtons.add(bUpgrade9);

        //create buttons
        createButtons(ItemGroups.Building);
    }

    void createButtons(ItemGroups group) {
        Array<UpgradeButton> currArr;
        if (group == ItemGroups.Building)
        {
            currArr = buildingsButtons;
        }
        else if (group == ItemGroups.Addon)
        {
            currArr = addOnButtons;
        }
        else
        {
            currArr = researchButtons;
        }

        final float margLeft = 20f;
        final float horizontalSpacing = 50f;
        final float verticalSpacing = 40f;
        final float buttonSize = 64f;
        final float margTop = 30f;
        int bArrPos = 0;
        for(float i = rectButBox.y + rectButBox.height - buttonSize - margTop; i > 0f; i -= (buttonSize + verticalSpacing))
        {
            for(float j = rectButBox.x + margLeft; j < rectButBox.x + rectButBox.width; j += (buttonSize + horizontalSpacing * 2))
            {
                if(!(bArrPos >= currArr.size ))
                {
                    Rectangle buttonBounds = new Rectangle(j, i, buttonSize, buttonSize);
                    currArr.get(bArrPos).button = new Button(Assets.defaultNinePatch, buttonBounds, hudCamera);
                    bArrPos++;
                }
            }
        }
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
        if (Gdx.input.justTouched() && allowInput) {
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
            else if (bContinue.checkForTouch(touchX, touchY)){
                world.nextTurn();
                LudumDare40.game.setScreen(new PlanPhaseScreen());
            }

            //Loop through the array pertaining to the selected item group
            if(selectedGroup == ItemGroups.Building)
            {
                for(UpgradeButton button : buildingsButtons)
                {
                    if(button.button.checkForTouch(touchX, touchY))
                    {
                        currentUpgrade = button;
                    }
                }
            }
            else if(selectedGroup == ItemGroups.Addon)
            {
                for(UpgradeButton button : addOnButtons)
                {
                    if(button.button.checkForTouch(touchX, touchY))
                    {
                        currentUpgrade = button;
                    }
                }
            }
            else if(selectedGroup == ItemGroups.Research)
            {
                for(UpgradeButton button : researchButtons)
                {
                    if(button.button.checkForTouch(touchX, touchY))
                    {
                        currentUpgrade = button;
                    }
                }
            }
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

            // Render blank inserts
            batch.setColor(Color.CORAL);
            batch.draw(Assets.whitePixel, rectButBox.x, rectButBox.y, rectButBox.width, rectButBox.height);
            batch.draw(Assets.whitePixel, rectInfoBox.x, rectInfoBox.y, rectInfoBox.width, rectInfoBox.height);
            batch.setColor(Color.WHITE);

            // Render buttons for selected item group
            if(selectedGroup == ItemGroups.Building)
            {
                renderBuildingButtons(batch);
            }
            else if(selectedGroup == ItemGroups.Addon)
            {
                renderAddOnButtons(batch);
            }
            else if(selectedGroup == ItemGroups.Research)
            {
                renderResearchButtons(batch);
            }

            // Draw continue button
            batch.draw(Assets.whitePixel, rectButContinueBox.x, rectButContinueBox.y, rectButContinueBox.width, rectButContinueBox.height);

            //Render upgrade information
            if(currentUpgrade != null)
            {
                Assets.drawString(batch, currentUpgrade.name, rectInfoBox.x, rectInfoBox.y + rectInfoBox.height - 20,
                        Color.BLACK, 0.5f, Assets.font, rectInfoBox.width, Align.center);
                batch.draw(currentUpgrade.picture, rectInfoBox.x + 80, rectInfoBox.y + rectInfoBox.height - 180, 128, 128);
                Assets.drawString(batch, currentUpgrade.description, rectInfoBox.x, rectInfoBox.y + rectInfoBox.height - 200,
                        Color.BLACK, 0.25f, Assets.font, rectInfoBox.width, Align.center);
                Assets.drawString(batch, "Cost: " + currentUpgrade.cost, rectInfoBox.x, rectInfoBox.y + 80,
                        Color.BLACK, 0.25f, Assets.font, rectInfoBox.width, Align.center);

                if(currentUpgrade.group == ItemGroups.Research)
                {
                    purchaseUpgradeButton.text = "Research";
                }
                else
                {
                    purchaseUpgradeButton.text = "Purchase";
                }
                batch.setColor(Color.LIGHT_GRAY);
                batch.draw(Assets.whitePixel, purchaseUpgradeButton.bounds.x, purchaseUpgradeButton.bounds.y,
                        purchaseUpgradeButton.bounds.width, purchaseUpgradeButton.bounds.height);
                purchaseUpgradeButton.render(batch);
            }
            bContinue.render(batch);
        }
        batch.end();
    }

    private void renderBuildingButtons(SpriteBatch batch)
    {
        for(UpgradeButton button : buildingsButtons)
        {
            button.button.render(batch);
            batch.draw(button.picture, button.button.bounds.x + 3, button.button.bounds.y + 3,
                    button.button.bounds.width - 6, button.button.bounds.height - 6);
        }
    }

    private void renderAddOnButtons(SpriteBatch batch)
    {
        for(UpgradeButton button : addOnButtons)
        {
            button.button.render(batch);
            batch.draw(button.picture, button.button.bounds.x + 3, button.button.bounds.y + 3,
                    button.button.bounds.width - 6, button.button.bounds.height - 6);
        }
    }

    private void renderResearchButtons(SpriteBatch batch)
    {
        for(UpgradeButton button : researchButtons)
        {
            button.button.render(batch);
            batch.draw(button.picture, button.button.bounds.x + 3, button.button.bounds.y + 3,
                    button.button.bounds.width - 6, button.button.bounds.height - 6);
        }
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
        public ItemGroups group;
    }
}

