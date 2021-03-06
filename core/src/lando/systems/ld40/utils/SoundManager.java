package lando.systems.ld40.utils;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Sine;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

public class SoundManager {

    public static final float MUSIC_VOLUME = 0.25f;

    public enum SoundOptions {
        buildAddon, clickButton, collectMoney, dumpTrash, pickRouteWaypoint, pickupTrash, spendMoney, startActionPhase
    }

    public enum MusicOptions {
        titleScreen, mainGame, musicGame
    }

    public static HashMap<SoundOptions, Sound> soundMap = new HashMap<SoundOptions, Sound>();
    public static HashMap<MusicOptions, Music> musicMap = new HashMap<MusicOptions, Music>();

    public static Music music;
    public static MutableFloat musicVolume;

    public static void load(boolean playMusic) {

        soundMap.put(SoundOptions.buildAddon, Gdx.audio.newSound(Gdx.files.internal("sounds/build-addon.wav")));
        soundMap.put(SoundOptions.clickButton, Gdx.audio.newSound(Gdx.files.internal("sounds/click-button.wav")));
        soundMap.put(SoundOptions.collectMoney, Gdx.audio.newSound(Gdx.files.internal("sounds/collect-money.wav")));
        soundMap.put(SoundOptions.dumpTrash, Gdx.audio.newSound(Gdx.files.internal("sounds/dump-trash.wav")));
        soundMap.put(SoundOptions.pickRouteWaypoint, Gdx.audio.newSound(Gdx.files.internal("sounds/pick-route-waypoint.wav")));
        soundMap.put(SoundOptions.pickupTrash, Gdx.audio.newSound(Gdx.files.internal("sounds/pickup-trash.wav")));
        soundMap.put(SoundOptions.spendMoney, Gdx.audio.newSound(Gdx.files.internal("sounds/spend-money.wav")));
        soundMap.put(SoundOptions.startActionPhase, Gdx.audio.newSound(Gdx.files.internal("sounds/start-action-phase.wav")));


//        musicMap.put(MusicOptions.titleScreen, Gdx.audio.newMusic(Gdx.files.internal("sounds/title-screen-music.mp3")));
//        musicMap.put(MusicOptions.mainGame, Gdx.audio.newMusic(Gdx.files.internal("sounds/main-game-music.mp3")));
//        musicMap.put(MusicOptions.musicGame, Gdx.audio.newMusic(Gdx.files.internal("sounds/music-game.mp3")));
        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/music-game.mp3"));//musicMap.get(MusicOptions.game);
        music.setVolume(0.5f);
        music.setLooping(true);

        musicVolume = new MutableFloat(0);
        if (playMusic) {
            music.play();
        }
        setMusicVolume(MUSIC_VOLUME, 2f);
    }

    public static void update(float dt){
        music.setVolume(musicVolume.floatValue());
    }

    public static void dispose() {
        SoundOptions[] allSounds = SoundOptions.values();
        for (SoundOptions allSound : allSounds) {
            soundMap.get(allSound).dispose();
        }
        music.dispose();
    }

    public static long playSound(SoundOptions soundOption) {
        return soundMap.get(soundOption).play(1f);
    }

    public static void playMusic(MusicOptions musicOption){
        // Stop currently running music
        if (music != null) music.stop();

        // Set specified music track as current and play it
        music = musicMap.get(musicOption);
        music.setLooping(true);
        music.play();
    }

    public static void stopSound(SoundOptions soundOption) {
        Sound sound = soundMap.get(soundOption);
        if (sound != null) {
            sound.stop();
        }
    }

    public static void stopAllSounds() {
        for (Sound sound : soundMap.values()) {
            if (sound != null) sound.stop();
        }
    }

    private static long currentLoopID;
    private static Sound currentLoopSound;

    public static void setMusicVolume(float level, float duration){
        Assets.tween.killTarget(musicVolume);
        Tween.to(musicVolume, 1, duration)
                .target(level)
                .ease(Sine.IN)
                .start(Assets.tween);
    }

}
