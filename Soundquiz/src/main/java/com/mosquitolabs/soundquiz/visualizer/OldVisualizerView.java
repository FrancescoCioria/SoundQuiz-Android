/**
 * Copyright 2011, Felix Palmer
 *
 * Licensed under the MIT license:
 * http://creativecommons.org/licenses/MIT/
 */
package com.mosquitolabs.soundquiz.visualizer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.view.View;

import com.mosquitolabs.soundquiz.visualizer.renderer.BarRenderer;

/**
 * A class that draws visualizations of data received from a
 * {@link Visualizer.OnDataCaptureListener#onWaveFormDataCapture } and
 * {@link Visualizer.OnDataCaptureListener#onFftDataCapture }
 */
public class OldVisualizerView extends View {
    private static final String TAG = "OldVisualizerView";

    private byte[] mBytes;
    private byte[] mFFTBytes;
    private Rect mRect = new Rect();
    private Visualizer mVisualizer;
    private BarRenderer barRenderer;
    private Paint mFadePaint = new Paint();
    private Visualizer.OnDataCaptureListener captureListener;

    private AudioPlayer audioPlayer = AudioPlayer.getIstance;
    private Activity context;

    private final static int COLUMNS = 10;

    int magnitudePoints[];


    public OldVisualizerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        this.context = (Activity) context;
        init();
    }

    // called to inflate XML
    public OldVisualizerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OldVisualizerView(Context context) {
        this(context, null, 0);
    }


    private void init() {
        mBytes = null;
        mFFTBytes = null;

        mFadePaint.setColor(Color.argb(75, 255, 255, 255)); // Adjust alpha to change how quickly the image fades
        mFadePaint.setXfermode(new PorterDuffXfermode(Mode.MULTIPLY));

        captureListener = new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                              int samplingRate) {
                ;
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] bytes,
                                         int samplingRate) {
                updateVisualizer(bytes);
            }
        };

    }

    public void link() {
        if (audioPlayer.player == null) {
            throw new NullPointerException("Cannot link to null MediaPlayer");
        }

        // Create Equalizer to trick the Visualizer into not counting device volume
//        Equalizer mEqualizer = new Equalizer(0, audioPlayer.player.getAudioSessionId());
//        mEqualizer.setEnabled(true);

        // Create the Visualizer object and attach it to our media player.

        mVisualizer = new Visualizer(audioPlayer.player.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

        // Pass through Visualizer data to OldVisualizerView


        mVisualizer.setDataCaptureListener(captureListener,
                Visualizer.getMaxCaptureRate() / 2, true, true);
        int z = mVisualizer.getScalingMode();
        mVisualizer.setEnabled(true);
        magnitudePoints = new int[COLUMNS];
    }

    public void initBarRenderer() {
//        Paint barPaint = new Paint();
//        barPaint.setAntiAlias(true);
//        barPaint.setColor(Color.argb(88, 0, 128, 255));
        barRenderer = new BarRenderer(context);
    }

    public void disableVisualizer() {
        mVisualizer.setEnabled(false);
    }

    public void release() {
        mVisualizer.release();
        captureListener = null;
        mVisualizer = null;
        barRenderer.clearSprites();
    }

    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        invalidate();
    }

    Bitmap mCanvasBitmap;
    Canvas mCanvas;


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Create canvas once we're ready to draw
        mRect.set(0, 0, getWidth(), getHeight());

        if (mCanvasBitmap == null) {
            mCanvasBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Config.ARGB_8888);
        }
        if (mCanvas == null) {
            mCanvas = new Canvas(mCanvasBitmap);
        }

        //clear canvas
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        // Fade out old contents
//        mCanvas.drawPaint(mFadePaint);

        audioPlayer.setData(mBytes);
        if (barRenderer != null) {
            barRenderer.render(mCanvas, mRect);
        }

        canvas.drawBitmap(mCanvasBitmap, new Matrix(), null);
    }
}