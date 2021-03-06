package lando.systems.ld40.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import lando.systems.ld40.buildings.Building;
import lando.systems.ld40.gameobjects.DumpTruck;
import lando.systems.ld40.gameobjects.Routes;
import lando.systems.ld40.ui.ButtonGroup;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.Config;
import lando.systems.ld40.utils.SoundManager;
import lando.systems.ld40.world.World;

/**
 * Created by Brian on 12/3/2017.
 */
public class RouteManager extends ActionManager {

    public enum RouteState { NONE, START, PICK_ROUTE, PICK_SOURCES, PICK_DEST, DONE }
    public RouteState state = RouteState.START;
    public DumpTruck selectedTruck;

    private World world;
    private Routes routes;
    private IntArray newRoute;

    int remainingSelections = 0;
    boolean activated;
    ButtonGroup routeButtons = new ButtonGroup();

    private Rectangle hudBounds;
    private Array<TruckButton> truckButtons;
    private TruckButton hoveredTruck;

    public RouteManager(OrthographicCamera hudCamera, OrthographicCamera worldCamera) {

        super(hudCamera, worldCamera);

        world = World.GetWorld();
        routes = world.routes;

        init();
    }

    @Override
    public boolean isTileHighlightState() {
        return (state == RouteState.PICK_SOURCES || state == RouteState.PICK_DEST);
    }

    private void init() {
        hudBounds = new Rectangle(0, 0, hudCamera.viewportWidth, hudCamera.viewportHeight / 4);

        int count = routes.trucks.size;
        truckButtons = new Array<TruckButton>(count);

        TextureRegion image = routes.trucks.get(0).texture;
        float width = image.getRegionWidth();
        float gap = (hudBounds.width - (width * count)) / (count + 1);

        float x = gap;
        float y = hudBounds.y + hudBounds.height - image.getRegionHeight();

        for (DumpTruck truck : routes.trucks) {
            Rectangle bounds = new Rectangle(x, y, width, image.getRegionHeight());
            TruckButton button = new TruckButton(truck, bounds, hudCamera);
            routeButtons.add(button);
            truckButtons.add(button);
            x += (width + gap);
        }
    }

    @Override
    public void activate() {
        activated = true;
        setState(RouteState.PICK_ROUTE);
    }

    private void setState(RouteState state) {
        this.state = state;
        switch (state) {
            case PICK_ROUTE:
                routeButtons.clear();
                newRoute = null;
                world.setFilter(World.FilterType.None);
                break;
            case PICK_SOURCES:
                world.setFilter(World.FilterType.Source);
                break;
            case PICK_DEST:
                world.setFilter(World.FilterType.Desitination);
                break;
            default:
                world.setFilter(World.FilterType.None);
                break;
        }
    }

    @Override
    public void renderManager(SpriteBatch batch) {
        if (!activated) return;

        if (hoveredTruck != null){
            world.renderRoutes(batch, worldCamera, routes.routes.get(hoveredTruck.truck), truckButtons.indexOf(hoveredTruck, true));
        } else {
            world.renderRoutes(batch, worldCamera, newRoute, routeButtons.selectedIndex());
        }
        batch.setProjectionMatrix(hudCamera.combined);
        batch.setColor(Config.COLOR_BLACK);
        batch.draw(Assets.whitePixel, 0, 0, hudCamera.viewportWidth, hudCamera.viewportHeight/3);

        batch.setColor(Color.WHITE);
        for (TruckButton button : truckButtons) {
            button.render(batch, routes);
        }

        switch (state) {
            case PICK_ROUTE:
                drawHudText(batch, "Select a route");
                break;
            case PICK_SOURCES:
                drawHudText(batch, "Source selections: " + remainingSelections);
                break;
            case PICK_DEST:
                drawHudText(batch, "Select a dump");
                break;
        }

    }

    private void drawHudText(SpriteBatch batch, String text) {
        drawHudText(batch, text, 0.5f);
    }

    private void drawHudText(SpriteBatch batch, String text, float scale) {
        batch.setShader(Assets.fontShader);
        Assets.font.setColor(Config.COLOR_GOLD);
        Assets.font.getData().setScale(scale);
        Assets.fontShader.setUniformf("u_scale", scale);
        Assets.layout.setText(Assets.font, text);
        Assets.font.draw(batch, text,
                0, hudBounds.y + 50, hudBounds.width,
                Align.center, true);
        Assets.font.setColor(Color.WHITE);
        Assets.font.getData().setScale(1f);
        Assets.fontShader.setUniformf("u_scale", 1f);
        batch.setShader(null);
    }

    @Override
    public void updateManager(float dt) {
        hoveredTruck = null;
        for (TruckButton button : truckButtons) {
            button.update(dt);
            if (selectedTruck == null){
                if (button.checkForTouch(Gdx.input.getX(), Gdx.input.getY())){
                    hoveredTruck = button;
                }
            }
        }

        switch (state) {
            case PICK_SOURCES:
                remainingSelections = selectedTruck.speed - newRoute.size;
                if (remainingSelections == 0) {
                    setState(RouteState.PICK_DEST);
                }
                break;
            case PICK_DEST:
                break;
            case DONE:
                // nothing to see here
                break;
        }
    }

    public void selectTruck(TruckButton truck) {
        if (truck.selected) {
            setState(RouteState.PICK_ROUTE);
            SoundManager.playSound(SoundManager.SoundOptions.clickButton);
        } else {
            truck.select();
            selectedTruck = truck.truck;
            newRoute = new IntArray();
            remainingSelections = selectedTruck.speed;
            setState(RouteState.PICK_SOURCES);
            SoundManager.playSound(SoundManager.SoundOptions.clickButton);
        }
    }

    @Override
    public boolean handleTouch(float screenX, float screenY) {
        boolean handled = false;

        for (TruckButton button : truckButtons) {
            if (button.checkForTouch((int)screenX, (int)screenY)) {
                selectTruck(button);
                return true;
            }
        }


        Vector3 touchPosition = unprojectWorld(screenX, screenY);

        switch (state) {
            case PICK_SOURCES:
                handled = addToRoute(touchPosition);
                break;
            case PICK_DEST:
                handled = addToRoute(touchPosition);
                if (handled) {
                    routes.setRoute(selectedTruck, newRoute);
                    setState(RouteState.PICK_ROUTE);
                    selectedTruck = null;
                }
                SoundManager.playSound(SoundManager.SoundOptions.pickRouteWaypoint);
                break;
        }

        return handled;
    }

    private boolean addToRoute(Vector3 position) {
        int index = world.getSelectedObjectIndex(position.x, position.y);
        if (index == -1) return false;

        Building selected = world.buildings.get(index);
        if (selected.filtered) return false;

        int existingIndex = newRoute.indexOf(index);
        if (existingIndex != -1) {
            newRoute.removeIndex(existingIndex);
        }
        newRoute.add(index);
        routes.buildSpline(selectedTruck, newRoute);
        return true;
    }
}
