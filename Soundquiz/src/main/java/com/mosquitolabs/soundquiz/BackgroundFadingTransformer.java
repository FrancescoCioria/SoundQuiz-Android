package com.mosquitolabs.soundquiz;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class BackgroundFadingTransformer implements ViewPager.PageTransformer {
    private static float SCALE_RATE = 0.25f;
    private PackageListActivity context;
    private float DISTANCE = 0.0f;

    private ImageView view1 = null;
    private ImageView view2 = null;

    private ImageView[] views = {view1, view2};

    public BackgroundFadingTransformer(Context context) {
        this.context = (PackageListActivity) context;
        if (views[0] == null) {
            views[0] = (ImageView) this.context.findViewById(R.id.firstView);
            views[1] = (ImageView) this.context.findViewById(R.id.secondView);
        }
    }

    public void transformPage(View view, float position) {
        int index = (Integer) ((ViewGroup) view).getChildAt(0).getTag();


//        INIT DISTANCE BETWEEN PAGES AND IMAGES
        if (DISTANCE == 0.0f && index == 1) {
            DISTANCE = position;
            context.setBackgroundImage(views[0], 0);
            context.setAlpha(views[0], 1);
            context.setBackgroundImage(views[1], 1);
            context.setAlpha(views[1], 0);
            Log.d("TRANSFORMATION", "distance: " + DISTANCE);
        }

        float absPosition = Math.abs(position);
        float offset = (absPosition / DISTANCE);

//        CHECK IF WE ARE IN THE VISIBLE AREA
        if (absPosition < (DISTANCE - 0.001f) && DISTANCE > 0) {
            float alpha = 1 - offset;
            if (position <= 0) {
                context.setBackgroundImage(views[0], index);
                context.setAlpha(views[0], alpha);
                Log.d("SET_BACKGROUND", "index: " + index + " alpha: " + alpha + "   view:  " + views[0]);
            } else {
                context.setBackgroundImage(views[1], index);
                context.setAlpha(views[1], alpha);
                Log.d("SET_BACKGROUND", "index: " + index + " alpha: " + alpha + "   view:  " + views[1]);
            }
        }

        if (absPosition <= (DISTANCE + 0.1f) && DISTANCE > 0) {
            // Scale the page down (between (1-SCALE_RATE) and 1)
            float scaleFactor = 1 - (offset * SCALE_RATE);
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        }
    }

}




