package lando.systems.ld40.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class Statistics {

    private static Statistics stats;

    public static Statistics getStatistics(){
        if (stats == null){
            stats = new Statistics();
        }
        return stats;
    }

    private Array<TurnStatistics> turns;

    private Statistics(){
        turns = new Array<TurnStatistics>();
    }

    public void addTurnStatistics(int turn, int money, int buildings){
        TurnStatistics turnStats = new TurnStatistics(turn, money, buildings);
        turns.insert(turn, turnStats);
    }

    public void render(SpriteBatch batch){
        if (turns.size == 0) return;

    }

}
