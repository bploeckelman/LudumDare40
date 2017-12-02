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
    }

    public static HashMap<SoundOptions, Sound> soundMap = new HashMap<SoundOptions, Sound>();

    public static Music music;
    public static MutableFloat musicVolume;

    public static void load(boolean playMusic) {
        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/music-game.mp3"));//musicMap.get(MusicOptions.game);
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

    public static void playMusic(){//MusicOptions musicOption){
        // Stop currently running music
        if (music != null) music.stop();

        // Set specified music track as current and play it
        //music = musicMap.get(musicOption);
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
