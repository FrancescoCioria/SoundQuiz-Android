package com.mosquitolabs.soundquiz.visualizer;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.mosquitolabs.soundquiz.QuizActivity;
import com.mosquitolabs.soundquiz.R;

import java.util.Random;

/**
 * Created by francesco on 4/18/14.
 */
public class CharacterVisualizerHandler {

    private ImageView mouth;
    private ImageView head;
    private ImageView body;

    private int mouthCyclesLeft = 0;
    private int headCyclesLeft = 0;

    private boolean isAnimating = false;

    private Drawable mouthShapes[] = {null, null, null, null, null, null, null};


    private Random random = new Random();


    public CharacterVisualizerHandler(final QuizActivity context) {
        mouth = (ImageView) context.findViewById(R.id.imageViewMouth);
        body = (ImageView) context.findViewById(R.id.imageViewCharacter);

        mouthShapes[0] = context.getResources().getDrawable(R.drawable.ah);
        mouthShapes[1] = context.getResources().getDrawable(R.drawable.eh);
        mouthShapes[2] = context.getResources().getDrawable(R.drawable.i);
        mouthShapes[3] = context.getResources().getDrawable(R.drawable.oh);
        mouthShapes[4] = context.getResources().getDrawable(R.drawable.l);
        mouthShapes[5] = context.getResources().getDrawable(R.drawable.jolly);
        mouthShapes[6] = context.getResources().getDrawable(R.drawable.mpb);

        body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.togglePlay();
            }
        });


    }


    public void refresh() {
        if (!isAnimating) {
            return;
        }

        mouthAnimation();

    }

    public void startAnimation() {
        isAnimating = true;
    }

    public void stopAnimation() {
        isAnimating = false;
        mouth.setImageDrawable(mouthShapes[6]);
    }

    public void setVisible(boolean visible) {
        mouth.setVisibility(visible ? View.VISIBLE : View.GONE);
        body.setVisibility(visible ? View.VISIBLE : View.GONE);
    }


    private void mouthAnimation() {
        if (mouthCyclesLeft == 0) {
            int nextImage = random.nextInt(7);
            mouthCyclesLeft = random.nextInt(3) + 4;
            mouth.setImageDrawable(mouthShapes[nextImage]);
        }
        mouthCyclesLeft--;
    }


}
