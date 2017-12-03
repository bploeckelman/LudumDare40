package lando.systems.ld40.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class Statistics {

    private static final int STARTING_MONEY = 100;

    private static Statistics stats;

    public static Statistics getStatistics(){
        if (stats == null){
            stats = new Statistics();
        }
        return stats;
    }

    private Array<TurnStatistics> turnStatistics;
    private TurnStatistics currentTurnStatistics;

    private Statistics(){
        turnStatistics = new Array<TurnStatistics>();
        currentTurnStatistics = new TurnStatistics(STARTING_MONEY);
    }

    public TurnStatistics getCurrentTurnStatistics() {
        return currentTurnStatistics;
    }

    public void onTurnComplete(int turnNumber) {
        // Archive the current term
        currentTurnStatistics.turnNumber = turnNumber;
        turnStatistics.insert(turnNumber, currentTurnStatistics);
        // New turn
        currentTurnStatistics = new TurnStatistics(
                currentTurnStatistics.money);
    }

    public void render(SpriteBatch batch){
        if (turnStatistics.size == 0) return;

    }



}
