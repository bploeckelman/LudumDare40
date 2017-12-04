package lando.systems.ld40.utils;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld40.utils.accessors.*;

/**
 * Created by Brian on 11/28/2017
 */
@SuppressWarnings("LibGDXStaticResource")
public class Assets {

    public static AssetManager mgr;
    public static TweenManager tween;
    public static SpriteBatch batch;
    public static ShapeRenderer shapes;
    public static GlyphLayout layout;
    public static BitmapFont font;
    public static BitmapFont eightBitFont;
    public static ShaderProgram fontShader;
    public static Array<ShaderProgram> randomTransitions;
    public static ShaderProgram blindsShader;
    public static ShaderProgram fadeShader;
    public static ShaderProgram radialShader;
    public static ShaderProgram doomShader;
    public static ShaderProgram pizelizeShader;
    public static ShaderProgram doorwayShader;
    public static ShaderProgram crosshatchShader;

    public static TextureAtlas atlas;

    public static NinePatch defaultNinePatch;
    public static NinePatch speechNinePatch;
    public static NinePatch tooltipNinePatch;
    public static NinePatch statsNinePatch;
    public static NinePatch whiteNinePatch;
    public static NinePatch transparentNinePatch;

    public static TextureRegion testTexture;
    public static TextureRegion whitePixel;

    public static TextureRegion compactorTexture;
    public static TextureRegion compactorCutoutTexture;
    public static TextureRegion incineratorTexture;
    public static TextureRegion incineratorCutoutTexture;
    public static TextureRegion leafTexture;
    public static TextureRegion leafCutoutTexture;
    public static TextureRegion dumpsterTexture;
    public static TextureRegion dumpsterCutoutTexture;
    public static TextureRegion recycleTexture;
    public static TextureRegion recycleCutoutTexture;
    public static TextureRegion tileCover;
    public static TextureRegion buttonBackgroundTexture;
    public static TextureRegion bird1;
    public static TextureRegion bird2;
    public static TextureRegion grass1;
    public static TextureRegion grass2;
    public static TextureRegion grass3;
    public static TextureRegion grass4;
    public static TextureRegion grass5;
    public static TextureRegion grass6;
    public static TextureRegion grass7;
    public static TextureRegion replayButton;
    public static TextureRegion buttonHighlightTexture;
    public static TextureRegion upgradeTexture;
    public static TextureRegion dumpTexture;
    public static TextureRegion commercialLowTexture;
    public static TextureRegion commercialMediumTexture;
    public static TextureRegion commercialHighTexture;
    public static TextureRegion industrialLowTexture;
    public static TextureRegion industrialMediumTexture;
    public static TextureRegion industrialHighTexture;
    public static TextureRegion residentialLowTexture;
    public static TextureRegion residentialMediumTexture;
    public static TextureRegion residentialHighTexture;
    public static TextureRegion trashBag;
    public static TextureRegion trashButton;
    public static TextureRegion trashButtonFull;
    public static TextureRegion whiteCircle;
    public static TextureRegion tier1Texture;
    public static TextureRegion tier2Texture;
    public static TextureRegion tier3Texture;
    public static TextureRegion tileHighlightTexture;
    public static TextureRegion moneyTexture;

    public static Array<TextureAtlas.AtlasRegion> truck1AtlasRegions;
    public static Array<TextureAtlas.AtlasRegion> truck2AtlasRegions;
    public static Array<TextureAtlas.AtlasRegion> truck3AtlasRegions;

    public static Array<TextureRegion> grassTiles;
    public static Array<TextureRegion> cloudTextures;

    public static Texture titleScreenBackground;
    public static Texture titleName;

    public static boolean initialized;

    public static void load() {
        initialized = false;

        final TextureLoader.TextureParameter linearParams = new TextureLoader.TextureParameter();
        linearParams.minFilter = Texture.TextureFilter.Linear;
        linearParams.magFilter = Texture.TextureFilter.Linear;

        final TextureLoader.TextureParameter nearestParams = new TextureLoader.TextureParameter();
        nearestParams.minFilter = Texture.TextureFilter.Nearest;
        nearestParams.magFilter = Texture.TextureFilter.Nearest;

        mgr = new AssetManager();

        mgr.load("sprites.atlas", TextureAtlas.class);
        mgr.load("images/titlebackground.png", Texture.class);
        mgr.load("images/titlename.png", Texture.class);

        if (tween == null) {
            tween = new TweenManager();
            Tween.setCombinedAttributesLimit(4);
            Tween.registerAccessor(Color.class, new ColorAccessor());
            Tween.registerAccessor(Rectangle.class, new RectangleAccessor());
            Tween.registerAccessor(Vector2.class, new Vector2Accessor());
            Tween.registerAccessor(Vector3.class, new Vector3Accessor());
            Tween.registerAccessor(OrthographicCamera.class, new CameraAccessor());
        }

        batch = new SpriteBatch();
        shapes = new ShapeRenderer();
        layout = new GlyphLayout();
    }

    public static float update() {
        if (!mgr.update()) return mgr.getProgress();
        if (initialized) return 1f;
        initialized = true;

        titleScreenBackground = mgr.get("images/titlebackground.png", Texture.class);
        titleName = mgr.get("images/titlename.png", Texture.class);

        atlas = mgr.get("sprites.atlas", TextureAtlas.class);
        testTexture = atlas.findRegion("badlogic");
        whitePixel = atlas.findRegion("white-pixel");

        compactorTexture = atlas.findRegion("compactor");
        compactorCutoutTexture = atlas.findRegion("compactor-cutout");
        incineratorTexture = atlas.findRegion("incinerator");
        incineratorCutoutTexture = atlas.findRegion("incinerator-cutout");
        leafTexture = atlas.findRegion("leaf-green");
        leafCutoutTexture = atlas.findRegion("leaf-green-cutout");
        dumpsterTexture = atlas.findRegion("newdumpster");
        dumpsterCutoutTexture = atlas.findRegion("newdumpster-cutout");
        recycleTexture = atlas.findRegion("recycle");
        recycleCutoutTexture = atlas.findRegion("recycle-cutout");
        tileCover = atlas.findRegion("coverTile");
        buttonBackgroundTexture = atlas.findRegion("button-background");
        bird1 = atlas.findRegion("bird1");
        bird2 = atlas.findRegion("bird2");
        grass1 = atlas.findRegion("grass1");
        grass2 = atlas.findRegion("grass2");
        grass3 = atlas.findRegion("grass3");
        grass4 = atlas.findRegion("grass4");
        grass5 = atlas.findRegion("grass5");
        grass6 = atlas.findRegion("grass6");
        grass7 = atlas.findRegion("grass7");
        grassTiles = new Array<TextureRegion>();
        grassTiles.add(grass1);
        grassTiles.add(grass2);
        grassTiles.add(grass3);
        grassTiles.add(grass4);
        grassTiles.add(grass5);
        grassTiles.add(grass6);
        grassTiles.add(grass7);
        replayButton = atlas.findRegion("replay_button");
        buttonHighlightTexture   = atlas.findRegion("button-highlight");
        upgradeTexture           = atlas.findRegion("upgrade");
        dumpTexture              = atlas.findRegion("dump");
        commercialLowTexture     = atlas.findRegion("com-low");
        commercialMediumTexture  = atlas.findRegion("com-med");
        commercialHighTexture    = atlas.findRegion("com-high");
        industrialLowTexture     = atlas.findRegion("ind-low");
        industrialMediumTexture  = atlas.findRegion("ind-med");
        industrialHighTexture    = atlas.findRegion("ind-high");
        residentialLowTexture    = atlas.findRegion("res-low");
        residentialMediumTexture = atlas.findRegion("res-med");
        residentialHighTexture   = atlas.findRegion("res-high");
        trashBag = atlas.findRegion("bag_o_trash");
        trashButton = atlas.findRegion("trash_button");
        trashButtonFull = atlas.findRegion("trash_button_danger");
        whiteCircle = atlas.findRegion("white-circle");
        tier1Texture = atlas.findRegion("tier1");
        tier2Texture = atlas.findRegion("tier2");
        tier3Texture = atlas.findRegion("tier3");
        tileHighlightTexture = atlas.findRegion("tile-highlight");

        cloudTextures = new Array<TextureRegion>();
        cloudTextures.add(atlas.findRegion("cloud1"));
        cloudTextures.add(atlas.findRegion("cloud2"));
        cloudTextures.add(atlas.findRegion("cloud3"));
        cloudTextures.add(atlas.findRegion("cloud4"));
        cloudTextures.add(atlas.findRegion("cloud5"));

        final Texture distText = new Texture(Gdx.files.internal("fonts/ubuntu.png"), true);
        distText.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        font = new BitmapFont(Gdx.files.internal("fonts/ubuntu.fnt"), new TextureRegion(distText), false);
        font.getData().setScale(.3f);
        font.setUseIntegerPositions(false);
        
        defaultNinePatch = new NinePatch(atlas.findRegion("ninepatch"), 6,6,6,6);
        speechNinePatch = new NinePatch(atlas.findRegion("speech"), 12, 12, 12, 12);
        tooltipNinePatch = new NinePatch(atlas.findRegion("tooltip-ninepatch"), 10, 10, 10, 10);
        statsNinePatch = new NinePatch(atlas.findRegion("stats-ninepatch"), 10, 10, 10, 10);
        whiteNinePatch = new NinePatch(atlas.findRegion("white-ninepatch"), 10, 10, 10, 10);
        transparentNinePatch = new NinePatch(atlas.findRegion("transparent-ninepatch"), 10, 10, 10, 10);

        fontShader = loadShader("shaders/dist.vert", "shaders/dist.frag");

        randomTransitions = new Array<ShaderProgram>();
        blindsShader = loadShader("shaders/default.vert", "shaders/blinds.frag");
        fadeShader = loadShader("shaders/default.vert", "shaders/dissolve.frag");
        radialShader = loadShader("shaders/default.vert", "shaders/radial.frag");
        doomShader = loadShader("shaders/default.vert", "shaders/doomdrip.frag");
        pizelizeShader = loadShader("shaders/default.vert", "shaders/pixelize.frag");
        doorwayShader = loadShader("shaders/default.vert", "shaders/doorway.frag");
        crosshatchShader = loadShader("shaders/default.vert", "shaders/crosshatch.frag");


//        randomTransitions.add(blindsShader);
        randomTransitions.add(fadeShader);
        randomTransitions.add(radialShader);
//        randomTransitions.add(pizelizeShader);

        truck1AtlasRegions = atlas.findRegions("truck1");
        truck2AtlasRegions = atlas.findRegions("truck2");
        truck3AtlasRegions = atlas.findRegions("truck3");

        moneyTexture = atlas.findRegion("money");


        return 1f;
    }

    public static void dispose() {
        batch.dispose();
        shapes.dispose();
        font.dispose();
        mgr.clear();
    }

    private static ShaderProgram loadShader(String vertSourcePath, String fragSourcePath) {
        ShaderProgram.pedantic = false;
        ShaderProgram shaderProgram = new ShaderProgram(
                Gdx.files.internal(vertSourcePath),
                Gdx.files.internal(fragSourcePath));
        ShaderProgram.pedantic = true;

        if (!shaderProgram.isCompiled()) {
            Gdx.app.error("LoadShader", "compilation failed:\n" + shaderProgram.getLog());
            throw new GdxRuntimeException("LoadShader: compilation failed:\n" + shaderProgram.getLog());
        } else {
            Gdx.app.debug("LoadShader", "ShaderProgram compilation log:\n" + shaderProgram.getLog());
        }

        return shaderProgram;
    }

    public static void drawString(SpriteBatch batch, String text, float x, float y, Color c, float scale, BitmapFont font){
        batch.setShader(fontShader);
        fontShader.setUniformf("u_scale", scale);
        font.getData().setScale(scale);
        font.setColor(c);
        font.draw(batch, text, x, y);
        font.getData().setScale(1f);
        fontShader.setUniformf("u_scale", 1f);
        font.getData().setScale(scale);
        batch.setShader(null);
    }

    public static void drawString(SpriteBatch batch, String text, float x, float y, Color c, float scale, BitmapFont font, float targetWidth, int halign){
        batch.setShader(fontShader);
        fontShader.setUniformf("u_scale", scale);
        font.getData().setScale(scale);
        font.setColor(c);
        font.draw(batch, text, x, y, targetWidth, halign, true);
        font.getData().setScale(1f);
        fontShader.setUniformf("u_scale", 1f);
        font.getData().setScale(scale);
        batch.setShader(null);
    }

}
