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
import com.badlogic.gdx.utils.ObjectMap;
import lando.systems.ld40.LudumDare40;
import lando.systems.ld40.gameobjects.*;
import lando.systems.ld40.ui.Button;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.Config;
import lando.systems.ld40.world.Statistics;
import lando.systems.ld40.world.World;


/**
 * Created by Brian on 12/2/2017.
 */
public class ResolutionPhaseScreen extends BaseScreen {
    private static StoreManager storeManager = new StoreManager();

    private World world;

    Button bBuildings;
    Button bAddons;
    Button bResearch;
    Button bTrucks;
    Button bContinue;
    Button purchaseUpgradeButton;

    Rectangle rectHeadBut1;
    Rectangle rectHeadBut2;
    Rectangle rectHeadBut3;
    Rectangle rectHeadBut4;

    Rectangle rectButBox;
    Rectangle rectInfoBox;

    Rectangle rectButContinueBox;

    Vector3 touchPos;


    public enum ItemGroups {
        Building,
        Addon,
        Research,
        Trucks
    }

    public ItemGroups selectedGroup;

    Array<UpgradeButton> buildingsButtons;
    Array<UpgradeButton> addOnButtons;
    Array<UpgradeButton> researchButtons;
    Array<UpgradeButton> truckButtons;

    ObjectMap<String, ResearchType>  researchMap;
    ObjectMap<String, TileType> tileMap;
    ObjectMap<String, UpgradeType> upgradeMap;

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

        //Create top buttons
        rectHeadBut1 = new Rectangle(fScreenWidth / 30, fScreenHeight * 0.75f, fScreenWidth * 0.2125f, fScreenHeight / 12);
        bBuildings = new Button(Assets.whiteNinePatch, rectHeadBut1, hudCamera, "Buildings", null);
        bBuildings.textColor = Config.COLOR_BLACK;
        rectHeadBut2 = new Rectangle((fScreenWidth * 2 / 30) + rectHeadBut1.width, fScreenHeight * 0.75f, fScreenWidth * 0.2125f, fScreenHeight / 12);
        bAddons = new Button(Assets.whiteNinePatch, rectHeadBut2, hudCamera, "Add-Ons", null);
        bAddons.textColor = Config.COLOR_BLACK;
        rectHeadBut3 = new Rectangle((fScreenWidth * 3 / 30) + rectHeadBut1.width * 2, fScreenHeight * 0.75f, fScreenWidth * 0.2125f, fScreenHeight / 12);
        bResearch = new Button(Assets.whiteNinePatch, rectHeadBut3, hudCamera, "Research", null);
        bResearch.textColor = Config.COLOR_BLACK;
        rectHeadBut4 = new Rectangle((fScreenWidth * 4 / 30) + rectHeadBut1.width * 3, fScreenHeight * 0.75f, fScreenWidth * 0.2125f, fScreenHeight / 12);
        bTrucks = new Button(Assets.whiteNinePatch, rectHeadBut4, hudCamera, "Trucks", null);
        bTrucks.textColor = Config.COLOR_BLACK;

        rectButBox = new Rectangle(20, fScreenHeight * 0.15f, fScreenWidth * 0.55f, fScreenHeight * 0.55f);
        rectInfoBox = new Rectangle(rectButBox.x + rectButBox.width + fScreenWidth * 0.05f, fScreenHeight * 0.15f, fScreenWidth * 0.35f, 330);

        purchaseUpgradeButton = new Button(Assets.whiteNinePatch, new Rectangle(rectInfoBox.x + 10, rectInfoBox.y + 10, rectInfoBox.width - 20, 50),
                hudCamera, "Purchase", null);
        purchaseUpgradeButton.textColor = Config.COLOR_BLACK;

        rectButContinueBox = new Rectangle(fScreenWidth * 0.75f, fScreenHeight / 30, fScreenWidth * 0.225f, fScreenHeight / 12);
        bContinue = new Button(Assets.whiteNinePatch, rectButContinueBox, hudCamera, "Next Day", null);
        bContinue.textColor = Config.COLOR_BLACK;

        selectedGroup = ItemGroups.Building;

        //Initialize upgrade buttons (and populate the arrays)
        researchButtons = new Array<UpgradeButton>();
        addOnButtons = new Array<UpgradeButton>();
        buildingsButtons = new Array<UpgradeButton>();
        truckButtons = new Array<UpgradeButton>();

        upgradeMap = new ObjectMap<String, UpgradeType>();
        researchMap = new ObjectMap<String, ResearchType>();
        tileMap = new ObjectMap<String, TileType>();

        initBuildingsUpgrades();
        initAddOnButtons();
        initResearchUpgrades();
        initTruckButtons();
    }

    void initTruckButtons()
    {
        DumpTruck truck2 = null;
        DumpTruck truck3 = null;
        DumpTruck truck4 = null;
        DumpTruck truck5 = null;
        DumpTruck truck6 = null;
        DumpTruck truck1 = world.routes.trucks.get(0);
        if (world.routes.trucks.size > 1) truck2 = world.routes.trucks.get(1);
        if (world.routes.trucks.size > 2) truck3 = world.routes.trucks.get(2);
        if (world.routes.trucks.size > 3) truck4 = world.routes.trucks.get(3);
        if (world.routes.trucks.size > 4) truck5 = world.routes.trucks.get(4);
        if (world.routes.trucks.size > 5) truck6 = world.routes.trucks.get(5);

        UpgradeButton tUpgrade1 = new UpgradeButton();
        tUpgrade1.name = "Truck";
        tUpgrade1.description = "Your First Truck";
        tUpgrade1.picture = truck1.texture;
        tUpgrade1.group = ItemGroups.Trucks;
        tUpgrade1.cost = 100;
        tUpgrade1.truck = truck1;
        tUpgrade1.truckIndex = 0;

        UpgradeButton tUpgrade2 = new UpgradeButton();
        tUpgrade2.name = "Truck";
        tUpgrade2.description = "Add another route";
        tUpgrade2.picture = (truck2 != null) ? truck2.texture : Assets.atlas.findRegion("tile-128");
        tUpgrade2.group = ItemGroups.Trucks;
        tUpgrade2.cost = 200;
        tUpgrade2.truck = truck2;
        tUpgrade2.truckIndex = 1;

        UpgradeButton tUpgrade3 = new UpgradeButton();
        tUpgrade3.name = "Truck";
        tUpgrade3.description = "A third truck for your fleet";
        tUpgrade3.picture = (truck3 != null) ? truck3.texture : Assets.atlas.findRegion("tile-128");
        tUpgrade3.group = ItemGroups.Trucks;
        tUpgrade3.cost = 300;
        tUpgrade3.truck = truck3;
        tUpgrade3.truckIndex = 2;

        UpgradeButton tUpgrade4 = new UpgradeButton();
        tUpgrade4.name = "Truck";
        tUpgrade4.description = "These are getting expensive";
        tUpgrade4.picture = (truck4 != null) ? truck4.texture : Assets.atlas.findRegion("tile-128");
        tUpgrade4.group = ItemGroups.Trucks;
        tUpgrade4.cost = 150;
        tUpgrade4.truck = truck4;
        tUpgrade4.truckIndex = 3;

        UpgradeButton tUpgrade5 = new UpgradeButton();
        tUpgrade5.name = "Truck";
        tUpgrade5.description = "Five Routes!";
        tUpgrade5.picture = (truck5 != null) ? truck5.texture : Assets.atlas.findRegion("tile-128");
        tUpgrade5.group = ItemGroups.Trucks;
        tUpgrade5.cost = 250;
        tUpgrade5.truck = truck5;
        tUpgrade5.truckIndex = 4;


        UpgradeButton tUpgrade6 = new UpgradeButton();
        tUpgrade6.name = "Truck";
        tUpgrade6.description = "This will fill up your garage.";
        tUpgrade6.picture = (truck6 != null) ? truck6.texture : Assets.atlas.findRegion("tile-128");
        tUpgrade6.group = ItemGroups.Trucks;
        tUpgrade6.cost = 350;
        tUpgrade6.truck = truck6;
        tUpgrade6.truckIndex = 5;


        truckButtons.add(tUpgrade1);
        truckButtons.add(tUpgrade2);
        truckButtons.add(tUpgrade3);
        truckButtons.add(tUpgrade4);
        truckButtons.add(tUpgrade5);
        truckButtons.add(tUpgrade6);


        createButtons(ItemGroups.Trucks);
    }

    void initAddOnButtons()
    {
        UpgradeButton aUpgrade1 = new UpgradeButton();
        aUpgrade1.name = "Upgrade Token";
        aUpgrade1.description = "Token used to upgrade a building";
        aUpgrade1.picture = Assets.atlas.findRegion("upgrade");
        aUpgrade1.cost = 100;
        aUpgrade1.group = ItemGroups.Addon;

        UpgradeButton aUpgrade2 = new UpgradeButton();
        aUpgrade2.name = "Dumpster";
        aUpgrade2.description = "Placed on a building so it can hold more trash";
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
        aUpgrade4.description = "Green certified! Reduces trash output";
        aUpgrade4.picture = Assets.atlas.findRegion("leaf-green");
        aUpgrade4.cost = 1000;
        aUpgrade4.group = ItemGroups.Addon;

        UpgradeButton aUpgrade5 = new UpgradeButton();
        aUpgrade5.name = "Recycler";
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

        upgradeMap.put(aUpgrade1.name, UpgradeType.TIER_UPGRADE);
        upgradeMap.put(aUpgrade2.name, UpgradeType.DUMPSTER);
        upgradeMap.put(aUpgrade3.name, UpgradeType.INCINERATOR);
        upgradeMap.put(aUpgrade4.name, UpgradeType.GREEN_TOKEN);
        upgradeMap.put(aUpgrade5.name, UpgradeType.RECLAMATION);
        upgradeMap.put(aUpgrade6.name, UpgradeType.COMPACTOR);

        createButtons(ItemGroups.Addon);
    }

    void initResearchUpgrades()
    {
        UpgradeButton rUpgrade1 = new UpgradeButton();
        rUpgrade1.name = "Incineration";
        rUpgrade1.description = "This research allows for purchase of incinerators";
        rUpgrade1.picture = Assets.atlas.findRegion("incinerator");
        rUpgrade1.cost = 200;
        rUpgrade1.group = ItemGroups.Research;

        UpgradeButton rUpgrade2 = new UpgradeButton();
        rUpgrade2.name = "Compaction";
        rUpgrade2.description = "This research allows for purchase of compactors";
        rUpgrade2.picture = Assets.atlas.findRegion("compactor");
        rUpgrade2.cost = 500;
        rUpgrade2.group = ItemGroups.Research;

        UpgradeButton rUpgrade3 = new UpgradeButton();
        rUpgrade3.name = "Recyclation";
        rUpgrade3.description = "This research allows for purchase of recyclers";
        rUpgrade3.picture = Assets.atlas.findRegion("recycle");
        rUpgrade3.cost = 1000;
        rUpgrade3.group = ItemGroups.Research;

        UpgradeButton rUpgrade4 = new UpgradeButton();
        rUpgrade4.name = "Truck Speed 1";
        rUpgrade4.description = "Allows your trucks to make more than one stop";
        rUpgrade4.picture = Assets.atlas.findRegion("tile-128");
        rUpgrade4.cost = 100;
        rUpgrade4.group = ItemGroups.Research;

        UpgradeButton rUpgrade5 = new UpgradeButton();
        rUpgrade5.name = "Truck Speed 2";
        rUpgrade5.description = "Get a faster truck (vroom, vroom). Trucks can make 3 stops";
        rUpgrade5.picture = Assets.atlas.findRegion("tile-128");
        rUpgrade5.cost = 600;
        rUpgrade5.group = ItemGroups.Research;

        UpgradeButton rUpgrade7 = new UpgradeButton();
        rUpgrade7.name = "Truck Capacity 1";
        rUpgrade7.description = "Bigger trucks can carry more garbage before filling up";
        rUpgrade7.picture = Assets.atlas.findRegion("tile-128");
        rUpgrade7.cost = 500;
        rUpgrade7.group = ItemGroups.Research;

        UpgradeButton rUpgrade8 = new UpgradeButton();
        rUpgrade8.name = "Truck Capacity 2";
        rUpgrade8.description = "Even bigger trucks!!";
        rUpgrade8.picture = Assets.atlas.findRegion("tile-128");
        rUpgrade8.cost = 800;
        rUpgrade8.group = ItemGroups.Research;

        researchButtons.add(rUpgrade1);
        researchButtons.add(rUpgrade2);
        researchButtons.add(rUpgrade3);
        researchButtons.add(rUpgrade4);
        researchButtons.add(rUpgrade5);

        researchButtons.add(rUpgrade7);
        researchButtons.add(rUpgrade8);


        researchMap.put(rUpgrade1.name, ResearchType.INCINERATION);
        researchMap.put(rUpgrade2.name, ResearchType.COMPACTION);
        researchMap.put(rUpgrade3.name, ResearchType.RECYCLING);
        researchMap.put(rUpgrade4.name, ResearchType.TRUCK_STOPS_1);
        researchMap.put(rUpgrade5.name, ResearchType.TRUCK_STOPS_2);
        researchMap.put(rUpgrade7.name, ResearchType.TRUCK_CAPACITY_1);
        researchMap.put(rUpgrade8.name, ResearchType.TRUCK_CAPACITY_2);

        createButtons(ItemGroups.Research);
    }

    void initBuildingsUpgrades()
    {
        UpgradeButton bUpgrade1 = new UpgradeButton();
        bUpgrade1.name = "Low Residential";
        bUpgrade1.description = "Small taxes collected and little garbage";
        bUpgrade1.picture = Assets.atlas.findRegion("res-low");
        bUpgrade1.cost = 50;
        bUpgrade1.group = ItemGroups.Building;

        UpgradeButton bUpgrade2 = new UpgradeButton();
        bUpgrade2.name = "Low Commercial";
        bUpgrade2.description = "Moderate taxes and garbage created";
        bUpgrade2.picture = Assets.atlas.findRegion("com-low");
        bUpgrade2.cost = 70;
        bUpgrade2.group = ItemGroups.Building;

        UpgradeButton bUpgrade3 = new UpgradeButton();
        bUpgrade3.name = "Low Industrial";
        bUpgrade3.description = "Large taxes and a lot of garbage";
        bUpgrade3.picture = Assets.atlas.findRegion("ind-low");
        bUpgrade3.cost = 100;
        bUpgrade3.group = ItemGroups.Building;

        UpgradeButton bUpgrade4 = new UpgradeButton();
        bUpgrade4.name = "Medium Residential";
        bUpgrade4.description = "Larger houses create more taxes and garbage";
        bUpgrade4.picture = Assets.atlas.findRegion("res-med");
        bUpgrade4.cost = 100;
        bUpgrade4.group = ItemGroups.Building;

        UpgradeButton bUpgrade5 = new UpgradeButton();
        bUpgrade5.name = "Medium Commercial";
        bUpgrade5.description = "More shopping means more consumers";
        bUpgrade5.picture = Assets.atlas.findRegion("com-med");
        bUpgrade5.cost = 200;
        bUpgrade5.group = ItemGroups.Building;

        UpgradeButton bUpgrade6 = new UpgradeButton();
        bUpgrade6.name = "Medium Industrial";
        bUpgrade6.description = "Need to make the things that sell";
        bUpgrade6.picture = Assets.atlas.findRegion("ind-med");
        bUpgrade6.cost = 300;
        bUpgrade6.group = ItemGroups.Building;

        UpgradeButton bUpgrade7 = new UpgradeButton();
        bUpgrade7.name = "High Residential";
        bUpgrade7.description = "Moving up to the penthouse";
        bUpgrade7.picture = Assets.atlas.findRegion("res-high");
        bUpgrade7.cost = 150;
        bUpgrade7.group = ItemGroups.Building;

        UpgradeButton bUpgrade8 = new UpgradeButton();
        bUpgrade8.name = "High Commercial";
        bUpgrade8.description = "The most important executives work here";
        bUpgrade8.picture = Assets.atlas.findRegion("com-high");
        bUpgrade8.cost = 300;
        bUpgrade8.group = ItemGroups.Building;

        UpgradeButton bUpgrade9 = new UpgradeButton();
        bUpgrade9.name = "High Industrial";
        bUpgrade9.description = "Peak efficiency";
        bUpgrade9.picture = Assets.atlas.findRegion("ind-high");
        bUpgrade9.cost = 600;
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

        tileMap.put(bUpgrade1.name, TileType.RESIDENTIAL_LOW);
        tileMap.put(bUpgrade2.name, TileType.COMMERCIAL_LOW);
        tileMap.put(bUpgrade3.name, TileType.INDUSTRIAL_LOW);
        tileMap.put(bUpgrade4.name, TileType.RESIDENTIAL_MEDIUM);
        tileMap.put(bUpgrade5.name, TileType.COMMERCIAL_MEDIUM);
        tileMap.put(bUpgrade6.name, TileType.INDUSTRIAL_MEDIUM);
        tileMap.put(bUpgrade7.name, TileType.RESIDENTIAL_HIGH);
        tileMap.put(bUpgrade8.name, TileType.COMMERCIAL_HIGH);
        tileMap.put(bUpgrade9.name, TileType.INDUSTRIAL_HIGH);

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
        else if (group == ItemGroups.Research)
        {
            currArr = researchButtons;
        }
        else
        {
            currArr = truckButtons;
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

        if(selectedGroup == ItemGroups.Building)
        {
            for(UpgradeButton button : buildingsButtons)
            {
                button.button.update(dt);
                button.quantity = world.inventory.getCurrentCountForTile(tileMap.get(button.name));
            }
        }
        else if(selectedGroup == ItemGroups.Addon)
        {
            for(UpgradeButton button : addOnButtons)
            {
                button.button.update(dt);
                button.quantity = world.inventory.getCurrentCountForUpgrade(upgradeMap.get(button.name));
            }
        }
        else if(selectedGroup == ItemGroups.Research)
        {
            for(UpgradeButton button : researchButtons)
            {
                button.button.update(dt);
            }
        }
        else if(selectedGroup == ItemGroups.Trucks)
        {
            for(UpgradeButton button : truckButtons)
            {
                button.button.update(dt);
//                button.quantity = world.inventory.getCurrentCountForTruck(truckMap.get(button.name));
            }
        }

        if (Gdx.input.justTouched() && allowInput) {
            hudCamera.unproject(touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0f));

            int touchX = (int) touchPos.x;
            int touchY = (int) hudCamera.viewportHeight - (int) touchPos.y;

            if (bBuildings.checkForTouch(touchX, touchY)) {
                selectedGroup = ItemGroups.Building;
                currentUpgrade = null;
            }
            else if (bAddons.checkForTouch(touchX, touchY)) {
                selectedGroup = ItemGroups.Addon;
                currentUpgrade = null;
            }
            else if (bResearch.checkForTouch(touchX, touchY)) {
                selectedGroup = ItemGroups.Research;
                currentUpgrade = null;
            }
            else if (bTrucks.checkForTouch(touchX, touchY))
            {
                selectedGroup = ItemGroups.Trucks;
                currentUpgrade = null;
            }
            else if (bContinue.checkForTouch(touchX, touchY)){
                world.nextTurn();
                if (world.turnNumber >= Config.gameTurns){
                    LudumDare40.game.setScreen(new EndGameStatsScreen(), Assets.doorwayShader);
                } else {
                    LudumDare40.game.setScreen(new PlanPhaseScreen());
                }
            }
            else if(purchaseUpgradeButton.checkForTouch(touchX, touchY))
            {
                if(Statistics.getStatistics().getCurrentTurnStatistics().money >= currentUpgrade.cost && !purchaseUpgradeButton.text.contentEquals("Researched"))
                {
                    Statistics.getStatistics().getCurrentTurnStatistics().money -= currentUpgrade.cost;
                    if(currentUpgrade.group == ItemGroups.Research)
                    {
                        storeManager.completeResearch(researchMap.get(currentUpgrade.name));
                    }
                    else if(currentUpgrade.group == ItemGroups.Building)
                    {
                        world.inventory.addTileItem(tileMap.get(currentUpgrade.name));
                    }
                    else if(currentUpgrade.group == ItemGroups.Trucks)
                    {
//                        world.inventory.addTruckItem(truckMap.get(currentUpgrade.name));
                        if (currentUpgrade.truck == null){
                            world.routes.addRoute(storeManager.getMaxTruck());
                        }  else {
                            currentUpgrade.truck.setType(storeManager.getMaxTruck());
                        }
                        currentUpgrade.truck = world.routes.trucks.get(currentUpgrade.truckIndex);
                        currentUpgrade.picture = currentUpgrade.truck.texture;
                    }
                    else if(currentUpgrade.group == ItemGroups.Addon)
                    {
                        world.inventory.addUpgradeItem(upgradeMap.get(currentUpgrade.name));
                    }
                    currentUpgrade.quantity++;
                }
            }
            checkSubButtonTouch(touchX, touchY);
        }
    }

    private void checkSubButtonTouch(int x, int y)
    {
        //Loop through the array pertaining to the selected item group
        Array<UpgradeButton> currArr;

        switch (selectedGroup)
        {
            case Building: currArr = buildingsButtons; break;
            case Addon: currArr = addOnButtons; break;
            case Research: currArr = researchButtons; break;
            case Trucks: currArr = truckButtons; break;
            default: currArr = new Array<UpgradeButton>();
        }

        for(UpgradeButton button : currArr)
        {
            if(button.button.checkForTouch(x, y) && button.button.enabled)
            {
                currentUpgrade = button;
            }
        }
    }

    private void updateCamera(float dt) {
        camera.update();
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(Config.bgColor.r, Config.bgColor.g, Config.bgColor.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            renderHud(batch);

            // Draw header buttons
            batch.setColor(selectedGroup == ItemGroups.Building ? Config.COLOR_TEXT : Config.COLOR_SHADED);
            bBuildings.render(batch);
            batch.setColor(selectedGroup == ItemGroups.Addon ? Config.COLOR_TEXT : Config.COLOR_SHADED);
            bAddons.render(batch);
            batch.setColor(selectedGroup == ItemGroups.Research ? Config.COLOR_TEXT : Config.COLOR_SHADED);
            bResearch.render(batch);
            batch.setColor(selectedGroup == ItemGroups.Trucks ? Config.COLOR_TEXT : Config.COLOR_SHADED);
            bTrucks.render(batch);
            batch.setColor(Color.WHITE);

            // Render blank inserts
            batch.setColor(Config.COLOR_SHADED);
            Assets.whiteNinePatch.draw(batch, rectButBox.x, rectButBox.y, rectButBox.width, rectButBox.height);
            Assets.whiteNinePatch.draw(batch, rectInfoBox.x, rectInfoBox.y, rectInfoBox.width, rectInfoBox.height);
            batch.setColor(Color.WHITE);

            //Render sub-buttons
            renderSubButtons(batch);

            // Draw continue button
//            batch.draw(Assets.whitePixel, rectButContinueBox.x, rectButContinueBox.y, rectButContinueBox.width, rectButContinueBox.height);

            //Render upgrade information
            if(currentUpgrade != null)
            {
                renderSelectedInfo(batch);
            }
            batch.setColor(Config.COLOR_TEXT);
            bContinue.render(batch);
        }
        batch.end();
    }

    private void renderSubButtons(SpriteBatch batch)
    {
        Array<UpgradeButton> currArr;
        switch (selectedGroup)
        {
            case Building: currArr = buildingsButtons; break;
            case Addon: currArr = addOnButtons; break;
            case Research: currArr = researchButtons; break;
            case Trucks: currArr = truckButtons; break;
            default: currArr = new Array<UpgradeButton>();
        }

        boolean currButtonEnabled = true;
        for(UpgradeButton button : currArr)
        {
            switch (button.group)
            {
                case Addon: currButtonEnabled = checkAddonEnabled(button); break;
                case Trucks: currButtonEnabled = checkTruckEnabled(button); break;
                case Building: currButtonEnabled = checkBuildingEnabled(button); break;
                case Research: currButtonEnabled = checkResearchEnabled(button); break;
            }

            button.button.enable(currButtonEnabled);

            if(currButtonEnabled)
            {
                batch.setColor(Color.WHITE);
            }
            else
            {
                batch.setColor(1f, 1f, 1f, 0.5f);
            }

            batch.draw(button.picture, button.button.bounds.x + 3, button.button.bounds.y + 3,
                    button.button.bounds.width - 6, button.button.bounds.height - 6);
            button.button.render(batch);
        }
        batch.setColor(Color.WHITE);
    }

    boolean checkAddonEnabled(UpgradeButton button)
    {
        return storeManager.getUpgradeStatus(upgradeMap.get(button.name)) == StoreManager.Status.UNLOCKED;
    }

    boolean checkBuildingEnabled(UpgradeButton button)
    {
        return storeManager.getTileStatus(tileMap.get(button.name)) == StoreManager.Status.UNLOCKED;
    }

    boolean checkResearchEnabled(UpgradeButton button)
    {
        return storeManager.getResearchStatus(researchMap.get(button.name)) != StoreManager.ResearchStatus.LOCKED;
    }

    private void renderSelectedInfo(SpriteBatch batch)
    {
        Assets.drawString(batch, currentUpgrade.name, rectInfoBox.x, rectInfoBox.y + rectInfoBox.height - 20,
                Config.COLOR_GOLD, 0.5f, Assets.font, rectInfoBox.width, Align.center);
        batch.draw(currentUpgrade.picture, rectInfoBox.x + 80, rectInfoBox.y + rectInfoBox.height - 180, 128, 128);
        if(currentUpgrade.group != ItemGroups.Research && currentUpgrade.group != ItemGroups.Trucks)
        {
            Assets.drawString(batch, "x" + currentUpgrade.quantity, rectInfoBox.x + 200, rectInfoBox.y + rectInfoBox.height - 105,
                    Config.COLOR_TEXT, 0.4f, Assets.font, 80, Align.center);
        }
        Assets.drawString(batch, currentUpgrade.description, rectInfoBox.x + 5, rectInfoBox.y + rectInfoBox.height - 200,
                Config.COLOR_TEXT, 0.30f, Assets.font, rectInfoBox.width - 10, Align.center);
        Assets.drawString(batch, "Cost: " + currentUpgrade.cost, rectInfoBox.x, rectInfoBox.y + 80,
                Config.COLOR_TEXT, 0.25f, Assets.font, rectInfoBox.width, Align.center);

        purchaseUpgradeButton.enabled = true;
        if (currentUpgrade.cost > Statistics.getStatistics().getCurrentTurnStatistics().money) purchaseUpgradeButton.enabled = false;
        if(currentUpgrade.group == ItemGroups.Research) {

            if(storeManager.getResearchStatus(researchMap.get(currentUpgrade.name)) == StoreManager.ResearchStatus.RESEARCHED) {
                purchaseUpgradeButton.text = "Researched";
                purchaseUpgradeButton.enabled = false;
            } else {
                purchaseUpgradeButton.text = "Research";
            }

        } else if (currentUpgrade.group == ItemGroups.Trucks){
            if (currentUpgrade.truck != null && storeManager.getMaxTruck() == currentUpgrade.truck.truckType){
                purchaseUpgradeButton.text = "Upgraded";
                purchaseUpgradeButton.enable(false);
            } else if (currentUpgrade.truck == null) {
                purchaseUpgradeButton.text = "Purchase";
            } else {
                purchaseUpgradeButton.text = "Upgrade";
            }

        } else
        {
            purchaseUpgradeButton.text = "Purchase";
        }
        if (purchaseUpgradeButton.enabled) {
            batch.setColor(Config.COLOR_TEXT);
        } else {
            batch.setColor(Config.COLOR_TEXT.r, Config.COLOR_TEXT.g, Config.COLOR_TEXT.b, .7f);
        }
        purchaseUpgradeButton.render(batch);
    }

    private boolean checkTruckEnabled(UpgradeButton button)
    {
        DumpTruck.TruckType maxType = storeManager.getMaxTruck();
        int maxCost = storeManager.getTruckCost(maxType);
        if (button.truck == null) {
            button.cost = (button.truckIndex + 1) * (button.truckIndex + 1) * maxCost;
        } else {
            button.cost = maxCost - storeManager.getTruckCost(button.truck.truckType);
        }

        
        if (button.truck != null) return true;
        if (button.truckIndex == world.routes.trucks.size) return true;
        return false;
    }

    private void renderWorld(SpriteBatch batch) {
        batch.setColor(Color.WHITE);
        world.render(batch);
    }

    private void renderObjects(SpriteBatch batch) {
        // todo
    }

    private void renderHud(SpriteBatch batch) {
        Assets.drawString(batch, "$" + Statistics.getStatistics().getCurrentTurnStatistics().money, 0, Config.gameHeight - 20, Config.COLOR_GOLD, 0.5f, Assets.font, hudCamera.viewportWidth, Align.center);
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
        public int quantity = 0;
        public ItemGroups group;
        public DumpTruck truck;
        public int truckIndex;
    }
}