package com.mosquitolabs.soundquiz;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class BackgroundFadingTransformer implements ViewPager.PageTransformer {
    private static float SCALE_RATE = 0.25f;
    private float DISTANCE = 0.0f;

    private BackgroundHandler backgroundHandler = new BackgroundHandler();

    public BackgroundFadingTransformer(PackageListActivity context) {
        backgroundHandler.initHandler(context);
    }

    @TargetApi(11)
    public void transformPage(View view, float position) {
        int index = (Integer) ((ViewGroup) view).getChildAt(0).getTag();
        Log.d("index", "index:   " + index);


//        INIT DISTANCE BETWEEN PAGES AND IMAGES
        if (DISTANCE == 0.0f && index == 1) {
            DISTANCE = position;
            backgroundHandler.compositeBackground(0,1f,true);
            backgroundHandler.compositeBackground(1,0f,true);
            backgroundHandler.compositeBackground(2,0f,false );
//            backgroundHandler.setBackgroundWithAlpha(0, 1f);
//            backgroundHandler.setBackgroundWithAlpha(1, 0f);
            Log.d("TRANSFORMATION", "distance: " + DISTANCE);
        }

        float absPosition = Math.abs(position);
        float offset = (absPosition / DISTANCE);

//        CHECK IF WE ARE IN THE VISIBLE AREA
//        if (absPosition < (DISTANCE - 0.001f) && DISTANCE > 0) {
//            float alpha = 1 - offset;
//            backgroundHandler.setBackgroundWithAlpha(index, alpha);
//        }

        backgroundHandler.compositeBackground(index, 1 - offset, absPosition < (DISTANCE - 0.001f) && DISTANCE > 0);

        if (absPosition <= (DISTANCE + 0.1f) && DISTANCE > 0 && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Scale the page down (between (1-SCALE_RATE) and 1)
            float scaleFactor = 1 - (offset * SCALE_RATE);
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        }

    }


    private static class BackgroundHandler {
        private PackageListActivity context;
        private RelativeLayout[] pages = {null, null, null};
        private ImageView view1 = null;
        private ImageView view2 = null;
        private ImageView[] views = {view1, view2};
        private int[] indexes = {9, 9};

        private void setBackgroundWithAlpha(int index, float alpha) {
//            controlla se esiste view già settata
            for (int i = 0; i < indexes.length; i++) {
                if (indexes[i] == index) {
                    context.setAlpha(views[i], alpha);
//                    Log.d("SET_ALPHA", "index: " + index + " alpha: " + alpha + " view: " + views[i]);
                    return;
                }
            }
//            scegli il non adiacente e settalo
            for (int i = 0; i < indexes.length; i++) {
                if (indexes[i] != index + 1 && indexes[i] != index - 1) {
                    context.setBackgroundImage(views[i], index);
                    context.setAlpha(views[i], alpha);
                    indexes[i] = index;
//                    Log.d("SET_BACKGROUND", "index: " + index + " alpha: " + alpha + " view: " + views[i]);
                    return;
                }
            }
        }

        private void initHandler(PackageListActivity context) {
            this.context = context;
            views[0] = (ImageView) this.context.findViewById(R.id.firstView);
            views[1] = (ImageView) this.context.findViewById(R.id.secondView);
            pages[0] = (RelativeLayout) context.findViewById(R.id.pageOne);
        }


        private void setAlpha(View view, float alpha) {
            AlphaAnimation alphaAnim = new AlphaAnimation(alpha, alpha);
            alphaAnim.setDuration(0); // Make animation instant
            alphaAnim.setFillAfter(true); // Tell it to persist after the animation ends
            // And then on your layout
            view.startAnimation(alphaAnim);
        }

        private void compositeBackground(int index, float alpha, boolean visible) {
            switch (index) {
                case 0:
                    if (visible) {
                        context.findViewById(R.id.pageOne).setVisibility(View.VISIBLE);
                        setAlpha(context.findViewById(R.id.pageOne), alpha);
                        context.findViewById(R.id.bodyOne).setVisibility(View.VISIBLE);
                        setAlpha(context.findViewById(R.id.bodyOne), alpha);
                    } else {
                        context.findViewById(R.id.pageOne).setVisibility(View.GONE);
                        context.findViewById(R.id.bodyOne).setVisibility(View.GONE);
                    }
                    break;

                case 1:
                    if (visible) {
                        context.findViewById(R.id.pageTwo).setVisibility(View.VISIBLE);
                        setAlpha(context.findViewById(R.id.pageTwo), alpha);
//                    context.findViewById(R.id.bodyOne).setVisibility(View.VISIBLE);
//                    context.findViewById(R.id.bodyOne).setAlpha(alpha);
                        Log.d("pageTwo", "2");

                    } else {
                        context.findViewById(R.id.pageTwo).setVisibility(View.GONE);
//                        context.findViewById(R.id.bodyOne).setVisibility(View.GONE);
                    }
                    break;

                case 2:
                    if (visible) {
                        context.findViewById(R.id.pageThree).setVisibility(View.VISIBLE);
                        setAlpha(context.findViewById(R.id.pageThree), alpha);
//                    context.findViewById(R.id.bodyOne).setVisibility(View.VISIBLE);
//                    context.findViewById(R.id.bodyOne).setAlpha(alpha);
                    } else {
                        context.findViewById(R.id.pageThree).setVisibility(View.GONE);
//                        context.findViewById(R.id.bodyOne).setVisibility(View.GONE);
                    }
                    break;
            }
        }

    }

}




