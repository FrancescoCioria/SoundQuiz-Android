package com.mosquitolabs.soundquiz.visualizer.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by francesco on 3/15/14.
 */
public class DigitalTileSprite {

    private float left;
    private float top;
    private float right;
    private float bottom;

    private boolean isFadingEnabled = true;

    private long lastCallMillis;

    private int ALPHA = 255;

    private Paint paint = new Paint();

    private final static float FADE_OUT_TIME = 300; //millis

    public DigitalTileSprite(float x, float y, float width, float height, int color) {
        this.left = x;
        this.top = y;
        this.right = width;
        this.bottom = height;

        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        lastCallMillis = System.currentTimeMillis();
        setColor(color);
    }

    private void setColor(int color) {
        paint.setColor(color);
    }


    private void update() {
        if (isFadingEnabled) {
            long timeGap = System.currentTimeMillis() - lastCallMillis;

            if (timeGap > 0) {
                ALPHA -= (int) ((timeGap / FADE_OUT_TIME) * 255);
            }


            if (ALPHA < 0) {
                ALPHA = 0;
                isFadingEnabled = false;
            }
            paint.setAlpha(ALPHA);

        }

        lastCallMillis = System.currentTimeMillis();

    }

    public void draw(Canvas canvas) {
        update();
        canvas.drawRect(left, top, right, bottom, paint);

    }

    public void stopAnimation() {
        isFadingEnabled = false;
        ALPHA = 255;
        paint.setAlpha(ALPHA);
    }

    public void startAnimation() {
        isFadingEnabled = true;
    }


}
