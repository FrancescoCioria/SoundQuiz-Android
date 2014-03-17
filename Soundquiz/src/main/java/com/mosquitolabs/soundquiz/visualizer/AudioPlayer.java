package com.mosquitolabs.soundquiz.visualizer;

import android.media.MediaPlayer;

/**
 * Created by francesco on 3/15/14.
 */
public class AudioPlayer {

    //    private Equalizer equalizer;
    public MediaPlayer player;
    private byte[] bytes;

    public static AudioPlayer getIstance = new AudioPlayer();

    public void setData(byte[] bytes) {
        this.bytes = bytes;
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

    public byte[] getData() {
        return bytes;
    }

}
