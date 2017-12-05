package lando.systems.ld40.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld40.gameobjects.Bird;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.Config;

public class EndScreen extends BaseScreen{

    public Array<Bird> birds;

    private String heading = "Litter Burg";
    private String theme = "Made for Ludum Dare 40: The more you have, the worse it is";
    private String thanks = "Thanks for playing our game!";
    private String developers = "Developed by:\nDoug Graham\nBrian Ploeckelman\nIan McNamara\nBrian Rossman\nJeffrey Hwang\nBrandon Humboldt\nTim Polcyn";


    public EndScreen(){
        birds = new Array<Bird>();
        for (int i = 0; i < 25; i++){
            birds.add(new Bird());
        }
    }


    @Override
    public void update(float dt) {
        for (Bird bird : birds){
            bird.update(dt);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(Config.bgColor.r, Config.bgColor.g, Config.bgColor.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(Assets.titleScreenBackground, 0, 0,hudCamera.viewportWidth, hudCamera.viewportHeight);

        for (Bird bird : birds){
            bird.render(batch);
        }
        
        batch.end();
    }
}
