package com.mosquitolabs.soundquiz.visualizer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

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


    //    private Equalizer equalizer;
    private static AudioPlayer audioPlayer = new AudioPlayer();
    public SoundPool sounds = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
    public MediaPlayer player;
    private byte[] bytes;

    public static AudioPlayer getIstance() {
        return audioPlayer;
    }

    public void resetEqualizer() {
//        equalizer = new Equalizer(9999999, player.getAudioSessionId());
//
//        int val = equalizer.setEnabled(true);
//        if (val != Equalizer.SUCCESS)
//            Log.v("A", "EQUALIZER NON ATTIVO" + val);
//        else
//            Log.v("A", "SUCCESS" + val);
    }

    public void initSounds(Context context) {
        AudioManager mgr = (AudioManager) context.getSystemService(
                Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        final float streamVolumeMax = mgr
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        VOLUME = streamVolumeCurrent / streamVolumeMax;
        WIN = sounds.load(context, R.raw.tada, 1);
        SELECT = sounds.load(context, R.raw.select, 1);
        WRONG = sounds.load(context, R.raw.error, 1);
        REMOVE = sounds.load(context, R.raw.block, 1);
    }

    public void playWin() {
        sounds.play(WIN, VOLUME, VOLUME, 1, 0, 1.5f);
    }

    public void playRemove() {
        sounds.play(REMOVE, VOLUME, VOLUME, 1, 0, 1.5f);
    }

    public void playWrong() {
        sounds.play(WRONG, VOLUME, VOLUME, 1, 0, 1.5f);
    }

    public void playSelect() {
        sounds.play(SELECT, VOLUME, VOLUME, 1, 0, 1.5f);
    }


    public void setData(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getData() {
        return bytes;
    }

}
