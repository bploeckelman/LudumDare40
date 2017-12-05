package lando.systems.ld40.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld40.gameobjects.Bird;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.utils.Config;

public class EndScreen extends BaseScreen{

    public Array<Bird> birds;

    private String heading = "Litter Burg";
    private String theme = "Made for Ludum Dare 40:\nTheme: The more you have, the worse it is";
    private String thanks = "Thanks for playing our game!";
    private String developers = "Developed by:\nDoug Graham\nBrian Ploeckelman\nBrian Rossman\nIan McNamara\nJeffrey Hwang\nBrandon Humboldt\nTim Polcyn";
    private String artists = "Art by:\nMatt Neumann\nLuke Bain";
    private String ligdx = "Made with <3 and LibGDX";

    public EndScreen(){
        birds = new Array<Bird>();
        for (int i = 0; i < 30; i++){
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

        Assets.drawString(batch, heading, 0, hudCamera.viewportHeight - 10, Config.COLOR_TEXT, .8f, Assets.font, hudCamera.viewportWidth, Align.center);
        Assets.drawString(batch, theme, 0, hudCamera.viewportHeight - 60, Config.COLOR_TEXT, .35f, Assets.font, hudCamera.viewportWidth, Align.center);
        Assets.drawString(batch, developers, 0, hudCamera.viewportHeight - 120, Config.COLOR_TEXT, .3f, Assets.font, hudCamera.viewportWidth/2, Align.center);
        Assets.drawString(batch, artists, hudCamera.viewportWidth/2, hudCamera.viewportHeight - 120, Config.COLOR_TEXT, .3f, Assets.font, hudCamera.viewportWidth/2, Align.center);
        Assets.drawString(batch, ligdx, hudCamera.viewportWidth/2, hudCamera.viewportHeight - 200, Config.COLOR_TEXT, .4f, Assets.font, hudCamera.viewportWidth/2, Align.center);


        Assets.drawString(batch, thanks, 0, 300, Config.COLOR_TEXT, .3f, Assets.font, hudCamera.viewportWidth, Align.center);


        batch.end();
    }
}
