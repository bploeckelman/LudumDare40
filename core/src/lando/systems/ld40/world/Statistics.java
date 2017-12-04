package lando.systems.ld40.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld40.utils.Assets;

public class Statistics {

    private static final int STARTING_MONEY = 100;

    private static Statistics stats;


    private static float MODAL_X_MARGIN = 50;
    private static float MODAL_Y_MARGIN = 50;
    private static float ANIMATION_TIME = 3f;
    private static int TURN_HASH = 10;
    public static Color COLOR_MONEY = new Color(235/255f,208/255f,0,1);
    public static Color COLOR_BUILDINGS = new Color(90/255f, 178/255f, 23/255f, 1f);
    public static Color COLOR_GARBAGE_GENERATED = new Color(85/255f, 222/255f, 183/255f, 1f);

    private static Statistics STATS;
    public static Statistics getStatistics(){
        if (STATS == null){
            STATS = new Statistics();
        }
        return STATS;
    }


    public Rectangle modalBounds;
    public Rectangle graphBounds;
    public Rectangle tooltipBounds;
    private Array<TurnStatistics> turns;
    private TurnStatistics currentTurnStatistics;

    public float animationTimer;
    private Vector2 screenPos;
    private Vector3 worldPos;
    public boolean showMoney;
    public boolean showBuildings;
    public boolean showGarbageGenerated;
    public boolean showGarbageHauled;
    public boolean showGarbageInLandFills;
    public boolean showAddons;

    private Statistics(){
        turns = new Array<TurnStatistics>();
        currentTurnStatistics = new TurnStatistics(STARTING_MONEY);

        modalBounds = new Rectangle();
        graphBounds = new Rectangle();
        tooltipBounds = new Rectangle();
        animationTimer = 0;
        screenPos = new Vector2();
        worldPos = new Vector3();
        showMoney = true;
        showBuildings = true;
        showGarbageGenerated = true;
        showGarbageHauled = true;
        showGarbageInLandFills = true;
        showAddons = true;
    }

    public TurnStatistics getCurrentTurnStatistics() {
        return currentTurnStatistics;
    }

    public void onTurnComplete(int turnNumber) {
        // Archive the current term
        currentTurnStatistics.turnNumber = turnNumber;
        turns.insert(turnNumber, currentTurnStatistics);
        // New turn
        currentTurnStatistics = new TurnStatistics(
                currentTurnStatistics.money);
    }


    /**
     * Only Called on the end game stats screen to work through the animation
     * @param dt
     */
    public void update(float dt){
        animationTimer += dt;
        animationTimer = MathUtils.clamp(animationTimer, 0, ANIMATION_TIME);

    }

    public void render(SpriteBatch batch, OrthographicCamera camera){
        if (turns.size == 0) return;
        modalBounds.set((int)MODAL_X_MARGIN, (int)MODAL_Y_MARGIN,
                (int)(camera.viewportWidth - (2*MODAL_X_MARGIN)),
                (int)(camera.viewportHeight - (2*MODAL_Y_MARGIN)));
        graphBounds.set(modalBounds.x + 30, modalBounds.y + 100, modalBounds.width - 200, modalBounds.height - 200);
        batch.setColor(Color.WHITE);
        Assets.statsNinePatch.draw(batch, modalBounds.x, modalBounds.y, modalBounds.width, modalBounds.height);

        batch.end();
        Assets.shapes.setProjectionMatrix(camera.combined);
        Assets.shapes.begin(ShapeRenderer.ShapeType.Line);
        renderGraph();
        Assets.shapes.end();
        batch.begin();
    }

    private void renderGraph(){
        float dx = graphBounds.width / (turns.size - 1);
        ShapeRenderer sr = Assets.shapes;
        
        sr.setColor(.8f,.8f,.8f,1);
        sr.rect(graphBounds.x - 5, graphBounds.y - 5, graphBounds.width + 10, graphBounds.height+ 10);

        // Turn hashes
        sr.setColor(177/255f, 185/255f, 166/255f, 1f);
        for (int i = TURN_HASH; i < turns.size; i += TURN_HASH){
            drawDashedLine(graphBounds.x + (i * dx), graphBounds.y - 5,
                    graphBounds.x + (i * dx), graphBounds.y + graphBounds.height + 10,
                    20, .2f);
        }

        float maxStat = 1;
        for (TurnStatistics turn : turns){
            if (showMoney) maxStat = Math.max(maxStat, turn.money);
            if (showBuildings) maxStat = Math.max(maxStat, turn.buildings);
            if (showGarbageGenerated) maxStat = Math.max(maxStat, turn.garbageGenerated);
        }
        maxStat *= 1.1f;
        float graphPercent = animationTimer / ANIMATION_TIME;
        int lastIndex = (int)(turns.size * graphPercent) + 1;
        for (int i = 1; i <= lastIndex; i++){
            if (i == turns.size) break;
            float lerpPercent = 1f;
            if (i == lastIndex){
                float dt = 1f / turns.size;
                lerpPercent = (graphPercent % dt) / dt;
            }
            TurnStatistics lastTurn = turns.get(i-1);
            TurnStatistics currentTurn = turns.get(i);

            float x1 = graphBounds.x + (dx * (i - 1));
            float x2 = x1 + dx;
            float y1 = 0;
            float y2 = 0;

            if (showMoney) {
                // MONEY
                y1 = graphBounds.y + lastTurn.money / maxStat * graphBounds.height;
                y2 = graphBounds.y + currentTurn.money / maxStat * graphBounds.height;

                x2 = MathUtils.lerp(x1, x2, lerpPercent);
                y2 = MathUtils.lerp(y1, y2, lerpPercent);

                sr.setColor(COLOR_MONEY);
                sr.line(x1, y1, x2, y2);
            }

            if (showBuildings) {
                // Buildings
                y1 = graphBounds.y + lastTurn.buildings / maxStat * graphBounds.height;
                y2 = graphBounds.y + currentTurn.buildings / maxStat * graphBounds.height;

                x2 = MathUtils.lerp(x1, x2, lerpPercent);
                y2 = MathUtils.lerp(y1, y2, lerpPercent);

                sr.setColor(COLOR_BUILDINGS);
                sr.line(x1, y1, x2, y2);
            }

            if (showGarbageGenerated) {
                // Buildings
                y1 = graphBounds.y + lastTurn.garbageGenerated / maxStat * graphBounds.height;
                y2 = graphBounds.y + currentTurn.garbageGenerated / maxStat * graphBounds.height;

                x2 = MathUtils.lerp(x1, x2, lerpPercent);
                y2 = MathUtils.lerp(y1, y2, lerpPercent);

                sr.setColor(COLOR_GARBAGE_GENERATED);
                sr.line(x1, y1, x2, y2);
            }
        }
    }

    public void drawDashedLine(float x1, float y1, float x2, float y2, int numDashes, float dashSize) {
        for (int i = 0; i < numDashes; i++) {
            float start = (float)i / (float)numDashes;
            float end = (i + dashSize) / (float)numDashes;
            Assets.shapes.line(x1 + (x2 - x1) * start, y1 + (y2 - y1) * start,
                    x1 + (x2 - x1) * end, y1 + (y2 - y1) * end);
        }
    }

    public void drawTooltip(SpriteBatch batch, OrthographicCamera camera){
        if (animationTimer < ANIMATION_TIME) return;
        float dx = graphBounds.width / (turns.size - 1);
        worldPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(worldPos);
        screenPos.set(worldPos.x, worldPos.y);

        if (graphBounds.contains(screenPos)){
            int turn = (int)(((screenPos.x - graphBounds.x) / dx) + .5f);
            batch.setColor(Color.WHITE);
            batch.draw(Assets.whitePixel, graphBounds.x + (turn * dx) - 1, graphBounds.y -5,
                       2 , graphBounds.height + 10);
            float offset = 10;
            float left = screenPos.x + offset;
            float bottom = screenPos.y + offset;
            if (screenPos.x > camera.viewportWidth/2f){
                left -= 350 + (2 * offset);
            }
            if (screenPos.y > camera.viewportHeight/2f){
                bottom -= 200 + (2* offset);
            }
            TurnStatistics turnStats = turns.get(turn);
            tooltipBounds.set(left, bottom, 350, 200);
            batch.setColor(Color.WHITE);
            Assets.tooltipNinePatch.draw(batch, tooltipBounds.x, tooltipBounds.y, tooltipBounds.width, tooltipBounds.height);
            Assets.drawString(batch, "Turn " + (turn+1), tooltipBounds.x, tooltipBounds.y + tooltipBounds.height - 4,
                    Color.WHITE, .5f, Assets.font, tooltipBounds.width, Align.center);
            float yOffset = 45;
            if (showMoney){
                Assets.drawString(batch, "Money: " + turnStats.money, tooltipBounds.x + 10, tooltipBounds.y + tooltipBounds.height - yOffset,
                        Color.WHITE, .35f, Assets.font, tooltipBounds.width - 20, Align.left);
                yOffset += 20;
            }
            if (showBuildings){
                Assets.drawString(batch, "Buildings: " + turnStats.buildings, tooltipBounds.x + 10, tooltipBounds.y + tooltipBounds.height - yOffset,
                        Color.WHITE, .35f, Assets.font, tooltipBounds.width - 20, Align.left);
                yOffset += 20;
            }
            if (showAddons){
                Assets.drawString(batch, "Building Add-ons: " + turnStats.addons, tooltipBounds.x + 10, tooltipBounds.y + tooltipBounds.height - yOffset,
                        Color.WHITE, .35f, Assets.font, tooltipBounds.width - 20, Align.left);
                yOffset += 20;
            }
            if (showGarbageGenerated){
                Assets.drawString(batch, "Garbage Created: " + turnStats.garbageGenerated, tooltipBounds.x + 10, tooltipBounds.y + tooltipBounds.height - yOffset,
                        Color.WHITE, .35f, Assets.font, tooltipBounds.width - 20, Align.left);
                yOffset += 20;
            }
            if (showGarbageHauled){
                Assets.drawString(batch, "Garbage Hauled: " + turnStats.garbageHauled, tooltipBounds.x + 10, tooltipBounds.y + tooltipBounds.height - yOffset,
                        Color.WHITE, .35f, Assets.font, tooltipBounds.width - 20, Align.left);
                yOffset += 20;
            }
            if (showGarbageInLandFills){
                Assets.drawString(batch, "Garbage In Landfills: " + turnStats.garbageInLandFills, tooltipBounds.x + 10, tooltipBounds.y + tooltipBounds.height - yOffset,
                        Color.WHITE, .35f, Assets.font, tooltipBounds.width - 20, Align.left);
                yOffset += 20;
            }
        }

    }



}
