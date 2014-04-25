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

    private final static int NORMAL = 0;
    private final static int LEFT = 1;
    private final static int RIGHT = 2;

    private final static int OPEN = 0;
    private final static int SHUT = 1;


    private ImageView mouth;
    private ImageView head;
    private ImageView body;

    private int mouthCyclesLeft = 0;
    private int faceCyclesLeft = 0;
    private int eyesCyclesLeft = 0;

    private int currentFaceState = NORMAL;
    private int currentEyesState = SHUT;


    private boolean isAnimating = false;

    private Drawable mouthShapes[] = {null, null, null, null, null, null, null};
    private Drawable bodyShapes[] = {null, null, null};
    private Drawable bodyShapesShut[] = {null, null, null};


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

        bodyShapes[0] = context.getResources().getDrawable(R.drawable.guess_who_frame);
        bodyShapes[1] = context.getResources().getDrawable(R.drawable.guess_who_frame_left);
        bodyShapes[2] = context.getResources().getDrawable(R.drawable.guess_who_frame_right);

        bodyShapesShut[0] = context.getResources().getDrawable(R.drawable.guess_who_frame_shut);
        bodyShapesShut[1] = context.getResources().getDrawable(R.drawable.guess_who_frame_left_shut);
        bodyShapesShut[2] = context.getResources().getDrawable(R.drawable.guess_who_frame_right_shut);

        body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.togglePlay();
            }
        });

    }


    public void refresh() {
        eyesAnimation();
//        faceAnimation();

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
//        body.setImageDrawable(bodyShapes[NORMAL]);
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

    private void eyesAnimation() {
        if (eyesCyclesLeft == 0) {
            body.setImageDrawable(currentEyesState == OPEN ? bodyShapesShut[currentFaceState] : bodyShapes[currentFaceState]);
            eyesCyclesLeft = currentEyesState == OPEN ? (4) : (random.nextInt(60) + 60);
            currentEyesState = currentEyesState == OPEN ? SHUT : OPEN;
        }
        eyesCyclesLeft--;
    }

    private void faceAnimation() {
        if (faceCyclesLeft == 0) {
            int nextFace = currentFaceState == NORMAL ? (random.nextInt(2) + 1) : NORMAL;
            body.setImageDrawable(currentEyesState == OPEN ? bodyShapes[nextFace] : bodyShapesShut[nextFace]);
            faceCyclesLeft = nextFace != NORMAL ? (random.nextInt(18) + 18) : (random.nextInt(70) + 70);
            currentFaceState = nextFace;
        }
        faceCyclesLeft--;
    }


}
