package com.mosquitolabs.soundquiz.visualizer.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;


public class LineIndicatorSprite {

    private float left;
    private float stroke;
    private float right;
    private float topBrick;
    private long lastCallMillis;
    private int acceleration;
    private float ZERO;
    private final static int FIRST_ACCELERATION = 20;
    private static float VELOCITY = 20;
    private static float TIME = 300;
    private boolean isFreeToFall = false;
    private Paint paint = new Paint();


    public LineIndicatorSprite(float left, float right, float stroke, float zero, int color) {
        this.left = left;
        this.stroke = stroke;
        this.right = right;
        this.ZERO = zero;
        topBrick = ZERO;
        VELOCITY = 0.2f;


        setColor(color);
        lastCallMillis = 0;
    }


    private void setColor(int color) {
        paint.setColor(color);
    }

    private void update(float topBrickWithGap) {
        if (topBrick < topBrickWithGap) {
            startFall();
        } else {
            stopFall();
        }
        fall(topBrickWithGap);
    }

    private void fall(float topBrickWithGap) {
        if (isFreeToFall) {
            float timeGap = System.currentTimeMillis() - lastCallMillis;

            topBrick += timeGap * VELOCITY;
            if (topBrick > topBrickWithGap) {
                topBrick = topBrickWithGap;
            }
            if (topBrick > ZERO) {
                topBrick = ZERO;
            }
            acceleration = acceleration * 2;
            lastCallMillis = System.currentTimeMillis();


        } else {
            topBrick = topBrickWithGap;
        }


    }

    private void startFall() {
        isFreeToFall = true;
        acceleration = FIRST_ACCELERATION;
    }

    private void stopFall() {
        isFreeToFall = false;
        acceleration = FIRST_ACCELERATION;
    }

    public void draw(Canvas canvas, float newTopBrick) {
        int gap = 0;
        if (newTopBrick <= ZERO) {
            gap = (int) stroke / 3;
        }
        update(newTopBrick - gap);
        canvas.drawRect(left, topBrick - stroke, right, topBrick, paint);
    }


}
