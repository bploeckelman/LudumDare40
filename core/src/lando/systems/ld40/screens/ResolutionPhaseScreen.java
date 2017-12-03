package lando.systems.ld40.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
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

    //Button bBuildings;
    //Button bAddons;
    //Button bResearch;

    Rectangle rectHeadBut1;
    Rectangle rectHeadBut2;
    Rectangle rectHeadBut3;

    Rectangle rectButBox;
    Rectangle rectInfoBox;

    Rectangle rectButContinueBox;

    Rectangle buttonHeaderRegion;
    Rectangle buttonRegion;

    Rectangle infoHeaderRegion;
    Rectangle infoRegion;

    Rectangle carRegion;

    //Button continueButton;
    //Button purchaseButton;

    public ResolutionPhaseScreen() {
        world = World.GetWorld();
        Gdx.input.setInputProcessor(this);
        camera.zoom = 2.5f;
        camera.position.x = 500;
        camera.position.y = 500;

        float fScreenWidth = hudCamera.viewportWidth;
        float fScreenHeight = hudCamera.viewportHeight;

        rectHeadBut1 = new Rectangle(20, 450, 240, fScreenHeight / 12);
        rectHeadBut2 = new Rectangle(280, 450, 240, fScreenHeight / 12);
        rectHeadBut3 = new Rectangle(540, 450, 240, fScreenHeight / 12);

        rectButBox = new Rectangle(20, 90, fScreenWidth * 0.55f, 330);
        rectInfoBox = new Rectangle(rectButBox.x + rectButBox.width + fScreenWidth * 0.05f, 90, fScreenWidth * 0.35f, 330);

        rectButContinueBox = new Rectangle(fScreenWidth * 0.75f, fScreenHeight / 30, fScreenWidth * 0.225f, fScreenHeight / 12);
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
        // todo
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
            // Draw screen background
            //batch.draw(Assets.whitePixel, 0, 0, hudCamera.viewportWidth, hudCamera.viewportHeight);

            // Draw section backgrounds
            //batch.setColor(64f / 255f, 64f / 255f, 64f / 255f, 0.9f);

            // Draw header buttons
            batch.draw(Assets.whitePixel, rectHeadBut1.x, rectHeadBut1.y, rectHeadBut1.width, rectHeadBut1.height);
            batch.draw(Assets.whitePixel, rectHeadBut2.x, rectHeadBut2.y, rectHeadBut2.width, rectHeadBut2.height);
            batch.draw(Assets.whitePixel, rectHeadBut3.x, rectHeadBut3.y, rectHeadBut3.width, rectHeadBut3.height);

            batch.draw(Assets.whitePixel, rectButBox.x, rectButBox.y, rectButBox.width, rectButBox.height);
            batch.draw(Assets.whitePixel, rectInfoBox.x, rectInfoBox.y, rectInfoBox.width, rectInfoBox.height);

            batch.draw(Assets.whitePixel, rectButContinueBox.x, rectButContinueBox.y, rectButContinueBox.width, rectButContinueBox.height);

            // Draw header text


            // Draw cash moneys


            // Draw upgrade item buttons


            // Draw info section details




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
    }
}

