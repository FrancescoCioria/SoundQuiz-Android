/**
 * Copyright 2011, Felix Palmer
 *
 * Licensed under the MIT license:
 * http://creativecommons.org/licenses/MIT/
 */
package com.mosquitolabs.soundquiz.visualizer.renderer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;

import com.mosquitolabs.soundquiz.visualizer.AudioPlayer;

import java.util.ArrayList;


public class BarRenderer {
    private ArrayList<Integer> lastValues = new ArrayList<Integer>();
    private ArrayList<LineIndicatorSprite> lineIndicatorSprites = new ArrayList<LineIndicatorSprite>();
    private boolean mCycleColor;

    private final static int COLUMNS = 10;
    private static int STROKE = 4;
    private final static float MIN = 0;
    private final static int INDICATOR_COLOR = Color.rgb(255, 165, 0);
    private final static int BRICK_COLOR = Color.rgb(66, 139, 202);
    private final static int BRICK = 0;
    private final static int DIGITAL = 1;
    private final static int GAP = 5;
    private final static int MAX_TILES = 10;

    private Activity context;

    private float sum;


    private int[] colors;


    private ArrayList<ArrayList<DigitalTileSprite>> digitalTiles = new ArrayList<ArrayList<DigitalTileSprite>>();

    private AudioPlayer audioPlayer = AudioPlayer.getIstance;


    public BarRenderer(Activity context) {
        this.context = context;
        colors = new int[MAX_TILES];
        for (int i = 0; i < MAX_TILES; i++) {
            switch (i) {
                case 0:
                    colors[i] = Color.rgb(0, 217, 47);
                    break;
                case 1:
                    colors[i] = Color.rgb(0, 222, 39);
                    break;
                case 2:
                    colors[i] = Color.rgb(100, 227, 29);
                    break;
                case 3:
                    colors[i] = Color.rgb(162, 231, 1);
                    break;
                case 4:
                    colors[i] = Color.rgb(255, 205, 7);
                    break;
                case 5:
                    colors[i] = Color.rgb(220, 232, 0);
                    break;
                case 6:
                    colors[i] = Color.rgb(236, 186, 0);
                    break;
                case 7:
                    colors[i] = Color.rgb(250, 130, 0);
                    break;
                case 8:
                    colors[i] = Color.rgb(255, 53, 0);
                    break;
                case 9:
                    colors[i] = Color.rgb(255, 0, 0);
                    break;

            }
        }
    }


    public void render(Canvas canvas, Rect rect) {
        if (audioPlayer.player != null) {
            if (audioPlayer.getData() != null && audioPlayer.player.isPlaying()) {
//            int range = audioPlayer.getData().length / COLUMNS;
//            ArrayList<Integer> values = new ArrayList<Integer>();
                AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

                for (int i = 0; i < COLUMNS; i++) {
                    double magnitude;

                    byte rfk = audioPlayer.getData()[2 * i];
                    byte ifk = audioPlayer.getData()[2 * i + 1];

                    magnitude = ((rfk * rfk + ifk * ifk));
                    int dbValue = (int) (10 * Math.log10(magnitude));
                    magnitude = Math.round(dbValue * 8);

//                float height = Math.abs(realHeight - smoothCorrection);
                    float height = (float) magnitude;

//                    if (currentVolume > 10) {
//                        height = height - (float) (Math.sqrt((double) height) * 10);
//                    } else if (currentVolume < 7 && currentVolume > 0) {
//                        height = height + (float) (Math.sqrt((double) height) * 10);
//                    }


                    if (i == 4) {
                        sum += height;
                    }

                    drawSprites(canvas, rect, i, (int) height, DIGITAL);
                }

//            lastValues.clear();
//            for (int value : values) {
//                lastValues.add(value);
//            }
            } else {
                drawSilentState(canvas, rect, DIGITAL);
                sum = 0.0f;
            }
        }
    }


    private void drawSilentState(Canvas canvas, Rect rect, int type) {
        float zero = rect.height() - 10;
        switch (type) {

            case BRICK:
                for (int i = 0; i < COLUMNS; i++) {
                    if (lineIndicatorSprites.size() > i) {
                        lineIndicatorSprites.get(i).draw(canvas, zero);
                    }
                }
                break;

            case DIGITAL:
                for (int i = 0; i < COLUMNS; i++) {
                    if (digitalTiles.size() > i && digitalTiles.get(i).size() > 0) {
                        int z = 0;
                        for (DigitalTileSprite digitalTileSprite : digitalTiles.get(i)) {
                            if (z == 0) {
                                digitalTileSprite.stopAnimation();
                            } else {
                                digitalTileSprite.startAnimation();
                            }
                            digitalTileSprite.draw(canvas);
                            z++;
                        }
                    }
                }
        }
    }


    private void drawSprites(Canvas canvas, Rect rect, int column, int height, int type) {
        float zero = rect.height() - 10;
//        float mediumHeight = zero / 2;
        float gap = rect.width() / COLUMNS / 5;
        float width = (rect.width() - gap * (COLUMNS + 1)) / COLUMNS;
        float left;
        float right;
        switch (type) {
            case BRICK:
                int brickHeight = (int) (width / 2.3);
                STROKE = brickHeight / 7;
                if (STROKE == 0) {
                    STROKE = 1;
                }

                left = width * column + gap * (column + 1);
                right = left + width;

                if (lineIndicatorSprites.size() == column) {
                    lineIndicatorSprites.add(new LineIndicatorSprite(left, right, STROKE, zero, INDICATOR_COLOR));
                }

                int numberOfBricks = height / (brickHeight + GAP);
                if (numberOfBricks > 0) {
                    for (int z = 0; z < numberOfBricks; z++) {
                        int brickGap = z * GAP;
                        float top = zero - (z + 1) * brickHeight - brickGap;
                        float bottom = zero - z * brickHeight - brickGap;

                        BrickSprite brickSprite = new BrickSprite(left, top, right, bottom, BRICK_COLOR);
                        brickSprite.draw(canvas);
                        if (z == numberOfBricks - 1) {
                            lineIndicatorSprites.get(column).draw(canvas, zero - height);
                        }
                    }
                } else {
                    if (height > 0) {
                        float top = zero - brickHeight;
                        float bottom = zero;

                        BrickSprite brickSprite = new BrickSprite(left, top, right, bottom, BRICK_COLOR);
                        brickSprite.draw(canvas);
                        lineIndicatorSprites.get(column).draw(canvas, top);
                    } else {
                        lineIndicatorSprites.get(column).draw(canvas, zero);
                    }
                }
                break;

            case DIGITAL:
                int tileHeight = (int) (width / 2);
                left = width * column + gap * (column + 1);
                right = left + width;

                if (digitalTiles.size() == column) {
                    ArrayList<DigitalTileSprite> columnSprites = new ArrayList<DigitalTileSprite>();
                    digitalTiles.add(columnSprites);
                }

                int numberOfTiles = height / (tileHeight + GAP);

                if (numberOfTiles == 0) {
                    numberOfTiles++;
                }

                int range = Math.max(numberOfTiles, digitalTiles.get(column).size());

                range = Math.min(range, MAX_TILES);

                for (int z = 0; z < range; z++) {

                    if (digitalTiles.get(column).size() == z) {
                        int brickGap = z * GAP;
                        float top = zero - (z + 1) * tileHeight - brickGap;
                        float bottom = zero - z * tileHeight - brickGap;
                        DigitalTileSprite digitalTileSprite = new DigitalTileSprite(left, top, right, bottom, colors[z]);
                        digitalTiles.get(column).add(digitalTileSprite);
                    }

                    if (z < numberOfTiles) {
                        digitalTiles.get(column).get(z).stopAnimation();
                    } else {
                        digitalTiles.get(column).get(z).startAnimation();
                    }

                    digitalTiles.get(column).get(z).draw(canvas);

                }

                break;

        }
    }

    private float colorCounter = 0;

    private void cycleColor() {
//        int r = (int) Math.floor(128 * (Math.sin(colorCounter) + 3));
//        int g = (int) Math.floor(128 * (Math.sin(colorCounter + 1) + 1));
//        int b = (int) Math.floor(128 * (Math.sin(colorCounter + 7) + 1));
//        mPaint.setColor(Color.argb(128, r, g, b));
//        colorCounter += 0.03;
    }

    public void clearSprites() {
        lineIndicatorSprites.clear();
        digitalTiles.clear();
    }

}
