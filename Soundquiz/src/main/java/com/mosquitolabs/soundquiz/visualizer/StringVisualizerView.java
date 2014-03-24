package com.mosquitolabs.soundquiz.visualizer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class StringVisualizerView extends View {
    private final static float IDLE_WAVES = 1.0f;
    private final static float ANIMATION_WAVES = 5.0f;
    private final static float IDLE_AMPLITUDE = 0.03f;
    private final static float ANIMATION_AMPLITUDE = 1.0f;
    private final static float DAMPING_FACTOR = 0.86f;
    private final static float FREQUENCY = 1.5f;
    private final static float PHASE_SHIFT = -0.3f;
    private final static float DENSITY = 5.0f;

    private Activity context;
    private Paint paint;
    private Path path = new Path();


    float phase = 0;
    float amplitude = IDLE_AMPLITUDE;
    float waves = IDLE_WAVES;

    private boolean stopVisualizer = false;
    private Runnable handlerTask;

    private boolean isAnimating = false;

    private final static int FPS = 35;

    public StringVisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = (Activity) context;
        paint = new Paint() {{
            setStyle(Paint.Style.STROKE);
            setAntiAlias(true);
        }};
    }

    public StringVisualizerView(Context context) {
        this(context, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawVisualizer(canvas);
    }

    public void refresh() {
        phase += PHASE_SHIFT;
        invalidate();
    }


    private void drawVisualizer(Canvas canvas) {

        // We draw multiple sinus waves, with equal phases but altered amplitudes, multiplied by a parable function.
        for (int i = 0; i < waves + 1; i++) {

            path.reset();

            // The first wave is drawn with a 2px stroke width, all others a with 1px stroke width.
            paint.setStrokeWidth((i == 0) ? 2 : 1);

            float halfHeight = getHeight() / 2;
            float width = getWidth();
            float mid = width / 2.0f;

            final float maxAmplitude = halfHeight - 4; // 4 corresponds to twice the stroke width

            // Progress is a value between 1.0 and -0.5, determined by the current wave idx, which is used to alter the wave's amplitude.
            float progress = 1.0f - i / waves;
            float normedAmplitude;
            if (isAnimating) {
                if (amplitude < ANIMATION_AMPLITUDE) {
                    amplitude *= (1 / DAMPING_FACTOR);
                }
                normedAmplitude = (1.5f * progress - 0.5f) * Math.min(ANIMATION_AMPLITUDE, amplitude);
            } else {
                if (amplitude > IDLE_AMPLITUDE) {
                    amplitude *= DAMPING_FACTOR;
                }
                normedAmplitude = (1.5f * progress - 0.5f) * Math.max(IDLE_AMPLITUDE, amplitude);
            }

            // Choose the color based on the progress (that is, based on the wave idx)
            int alpha = (int) (255 * (progress / 3.0 * 2 + 1.0 / 3.0));
            paint.setAlpha(alpha);

            for (float x = 0; x < width + DENSITY; x += DENSITY) {

                // We use a parable to scale the sinus wave, that has its peak in the middle of the view.
                float scaling = (float) (-Math.pow(1 / mid * (x - mid), 2) + 1);

                float y = (float) (scaling * maxAmplitude * normedAmplitude * Math.sin(2 * Math.PI * (x / width) * FREQUENCY + phase) + halfHeight);

                if (x == 0) path.moveTo(x, y);
                else path.lineTo(x, y);
            }

            canvas.drawPath(path, paint);

        }
    }

    public void startLoop() {
        stopVisualizer = false;
        final Handler handler = new Handler();
        handlerTask = new Runnable() {
            @Override
            public void run() {
                refresh();
                if (!stopVisualizer) {
                    handler.postDelayed(handlerTask, 1000 / FPS);
                }
            }
        };
        handlerTask.run();
    }

    public void stopLoop() {
        stopVisualizer = true;
        stopAnimation();
    }

    public void startAnimation() {
        isAnimating = true;
        waves = ANIMATION_WAVES;
    }

    public void stopAnimation() {
        isAnimating = false;
        waves = IDLE_WAVES;
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

}