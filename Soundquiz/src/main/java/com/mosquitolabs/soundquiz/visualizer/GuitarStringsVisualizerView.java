package com.mosquitolabs.soundquiz.visualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

/**
 * Created by francesco on 4/15/14.
 */
public class GuitarStringsVisualizerView extends View {
    private final static float IDLE_AMPLITUDE = 0.1f;
    private final static float ANIMATION_AMPLITUDE = 1.0f;
    private final static float DAMPING_FACTOR = 0.86f;
    private final static float FREQUENCY = 1f;
    //    private final static float PHASE_SHIFT = -0.4f;
    private final static float[] PHASE_SHIFT = {-0.5f, -0.52f, -0.54f, -0.58f, -0.6f, -0.62f};
    private final static float IDLE_PHASE_SHIFT = 0.3f;
    private final static float DENSITY = 5.0f;
    private final static float SAFETY_PADDING = 0.9f;
    private final static int STRINGS = 6;

    private boolean firstVibration = true;


    private Paint paint;
    private Path path = new Path();

    private float compressionFactor = 1.0f;

    private int stroke = 2;
    private int lastPlayedString = -1;
    private Random random = new Random();

    private GuitarString[] guitarStrings = {new GuitarString(), new GuitarString(), new GuitarString(), new GuitarString(), new GuitarString(), new GuitarString()};


//    float amplitude = IDLE_AMPLITUDE;

    private boolean isAnimating = true;

    public GuitarStringsVisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint() {{
            setStyle(Paint.Style.STROKE);
            setAntiAlias(true);
        }};
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        randomStringVibrator();
        drawVisualizer(canvas);
    }

    public void refresh() {
        invalidate();
    }

    private void randomStringVibrator() {
        if (!isAnimating) {
            return;
        }

        int vibratingStrings = 0;

        for (GuitarString guitarString : guitarStrings) {
            vibratingStrings += guitarString.isVibrating() ? 1 : 0;
        }

        if (vibratingStrings < 3) {
            firstVibration = vibratingStrings == 0;

            int next;
            do {
                next = random.nextInt(STRINGS);
            }
            while (next == lastPlayedString || guitarStrings[next].isVibrating());

            int vibrationTime;
            if (!firstVibration) {
                int minVibrationTime = 300;
                int maxVibrationTime = 600;
                vibrationTime = minVibrationTime + random.nextInt(maxVibrationTime - minVibrationTime);
            } else {
                vibrationTime = 300 + vibratingStrings * 150;
                firstVibration = vibratingStrings != 2;
            }

            float cycleTime = 1000f / 35;
            int cycles = (int) (vibrationTime / cycleTime);

            guitarStrings[next].cyclesLeftOfVibration = cycles;

        }


    }


    private void drawVisualizer(Canvas canvas) {

        float baseAvailableWidth = getWidth() * SAFETY_PADDING / STRINGS;
        float topAvailableWidth = compressionFactor * getWidth() / STRINGS;

        float height = getHeight();
        float mid = height / 2.0f;

        final float maxAmplitude = baseAvailableWidth * 7 / 10 - 2 * stroke;

        // We draw multiple sinus waves translated.
        int counter = 0;
        for (GuitarString guitarString : guitarStrings) {
            path.reset();

            float normedAmplitude;
            if (guitarString.isVibrating()) {
                if (guitarString.amplitude < ANIMATION_AMPLITUDE) {
                    guitarString.amplitude *= (1 / DAMPING_FACTOR);
                }
                guitarString.cyclesLeftOfVibration -= ANIMATION_AMPLITUDE <= guitarString.amplitude ? 1 : 0;
                normedAmplitude = Math.min(ANIMATION_AMPLITUDE, guitarString.amplitude);
                guitarString.phase += PHASE_SHIFT[counter];
            } else {
                if (guitarString.amplitude > IDLE_AMPLITUDE) {
                    guitarString.amplitude *= DAMPING_FACTOR;
                }
                normedAmplitude = Math.max(IDLE_AMPLITUDE, guitarString.amplitude);
                guitarString.phase += IDLE_PHASE_SHIFT;
            }


            float leftPadding = getWidth() * (1 - SAFETY_PADDING) / 2;
            float baseTranslationX = baseAvailableWidth / 2 + baseAvailableWidth * counter;
            float topTranslationX = 3 * (baseAvailableWidth - topAvailableWidth) + (topAvailableWidth / 2 + topAvailableWidth * counter);

            float deltaTranslation = baseTranslationX - topTranslationX;

            for (float y = 0; y < height + DENSITY; y += DENSITY) {

                float translationX = y * deltaTranslation / height + baseTranslationX + leftPadding;
                // We use a parable to scale the sinus wave, that has its peak in the middle of the view.
                float scaling = (float) (-Math.pow(1 / mid * (y - mid), 2) + 1);

                float x = (float) (scaling * maxAmplitude * normedAmplitude * Math.sin(2 * Math.PI * (y / height) * FREQUENCY + guitarString.phase) + translationX);

                if (y == 0) path.moveTo(x, y);
                else path.lineTo(x, y);
            }
            canvas.drawPath(path, paint);
            counter++;
        }

    }

    public void setCompressionFactor(float compressionFactor) {
        this.compressionFactor = compressionFactor;
    }


    public void startAnimation() {
        isAnimating = true;
    }

    public void stopAnimation() {
        for (GuitarString guitarString : guitarStrings) {
            guitarString.cyclesLeftOfVibration = 0;
        }
        isAnimating = false;
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    public void setStroke(int stroke) {
        this.stroke = stroke;
        paint.setStrokeWidth(stroke);
    }

    private class GuitarString {
        private float amplitude = IDLE_AMPLITUDE;
        private int cyclesLeftOfVibration = 0;
        private float phase = 0;

        private boolean isVibrating() {
            return cyclesLeftOfVibration > 0;
        }
    }

}
