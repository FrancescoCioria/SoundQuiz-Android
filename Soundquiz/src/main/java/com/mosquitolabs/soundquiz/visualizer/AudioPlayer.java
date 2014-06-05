package com.mosquitolabs.soundquiz.visualizer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import com.mosquitolabs.soundquiz.R;

/**
 * Created by francesco on 3/15/14.
 */
public class AudioPlayer {
    private float VOLUME;

    private int WIN;
    private int WRONG;
    private int SELECT;
    private int REMOVE;
    private int FIREWORKS;
    private int COINS;

    private int res;
    private MediaPlayer.OnCompletionListener listener;

    private Context context;


    //    private Equalizer equalizer;
    private static AudioPlayer audioPlayer = new AudioPlayer();
    public SoundPool sounds = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
    public MediaPlayer player;
//    private byte[] bytes;

    public static AudioPlayer getIstance() {
        return audioPlayer;
    }

    public void initSounds(Context context) {
        this.context = context;
        WIN = sounds.load(context, R.raw.tada, 1);
        SELECT = sounds.load(context, R.raw.select, 1);
        WRONG = sounds.load(context, R.raw.wrong_answer, 1);
        REMOVE = sounds.load(context, R.raw.remove, 1);
        FIREWORKS = sounds.load(context, R.raw.fireworks, 1);
        COINS = sounds.load(context, R.raw.coins_table, 1);
    }

    public void createPlayer(Context context, int res) {
        if (player != null) {
//            stopPlayer();
            releasePlayer();
        }

        this.res = res;
        player = MediaPlayer.create(context, res);
    }

    public void linkOnCompletionListenerToPlayer(MediaPlayer.OnCompletionListener listener) {
        player.setOnCompletionListener(listener);
        this.listener = listener;
    }

    public void resetPlayer(Context context) {
        if (player != null) {
            stopPlayer();
            releasePlayer();
        }
        player = MediaPlayer.create(context, res);
        player.setOnCompletionListener(listener);
    }

    public void releasePlayer() {
        player.release();
        Log.d("AUDIO_PLAYER", "released");
    }

    public void startPlayer() {
        player.start();
        Log.d("AUDIO_PLAYER", "started");
    }

    public void pausePlayer() {
        player.pause();
        Log.d("AUDIO_PLAYER", "paused");
    }

    public void stopPlayer() {
        player.stop();
        Log.d("AUDIO_PLAYER", "stopped");
    }

    public boolean isPlayerPlaying() {
        return player.isPlaying();
    }

    public void invalidatePlayer() {
        if (player != null) {
            releasePlayer();
            player = null;
            Log.d("AUDIO_PLAYER", "invalidated");
        }
    }

    public void playWin() {
        updateVolume();
        sounds.play(WIN, VOLUME, VOLUME, 1, 0, 1.5f);
    }

    public void playRemove() {
        updateVolume();
        sounds.play(REMOVE, VOLUME, VOLUME, 1, 0, 1.5f);
    }

    public void playWrong() {
        updateVolume();
        sounds.play(WRONG, VOLUME, VOLUME, 1, 0, 1.5f);
    }

    public void playSelect() {
        updateVolume();
        sounds.play(SELECT, VOLUME, VOLUME, 1, 0, 1.5f);
    }

    public void playFireworks() {
        updateVolume();
        sounds.play(FIREWORKS, VOLUME, VOLUME, 1, 0, 1.5f);
    }

    public void playCoinDrop() {
        updateVolume();
        sounds.play(COINS, VOLUME, VOLUME, 1, 0, 1.5f);
    }

    private void updateVolume() {
        AudioManager mgr = (AudioManager) context.getSystemService(
                Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        final float streamVolumeMax = mgr
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        VOLUME = streamVolumeCurrent / streamVolumeMax;
    }

//    public void setData(byte[] bytes) {
//        this.bytes = bytes;
//    }
//
//    public byte[] getData() {
//        return bytes;
//    }

}
